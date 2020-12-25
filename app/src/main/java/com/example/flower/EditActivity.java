package com.example.flower;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.flower.user.RegisterLogin;
import com.widemouth.library.toolitem.WMToolImage;
import com.widemouth.library.wmview.WMTextEditor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends BaseActivity {

//    @Override
//    protected void onCreate(Bundle saveInstanceState){
//        super.onCreate(saveInstanceState);
//        setContentView(R.layout.edit_layout);
//        et = findViewById(R.id.et);
//    }
//

    private Toolbar myToolbar;
    private String old_context = "";
    private String old_time = "";
    private int old_Tag = 1;
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    private boolean tagChange = false;


    EditText et,et_receive;
    WMTextEditor textEditor;
    public Intent intent = new Intent();
    private final String TAG = "tag";
    MyTask mTask;//新编辑日记异步线程
    ReceiveTask rTask;
    ArrayList<View> aList;
    MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_viewpager_layout);/////////////////修改了
        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSetMessage();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        /////////////////////////
        ViewPager vpager_one =findViewById(R.id.viewPager);
        aList = new ArrayList<>();
        LayoutInflater li = getLayoutInflater();
        View li1=getLayoutInflater().inflate(R.layout.edit_layout,null,false);
        //View li2=getLayoutInflater().inflate(R.layout.receive_layout,null,false);
        aList.add(li1);
        //aList.add(li2);
        //aList.add(li.inflate(R.layout.receive_layout,null,false));
        //aList.add(li.inflate(R.layout.page_3,null,false));
        mAdapter = new MyPagerAdapter(aList);
        vpager_one.setAdapter(mAdapter);

        ////////////////////////
        textEditor = findViewById(R.id.textEditor);
        et = li1.findViewById(R.id.et);
        Intent getIntent = getIntent();
        openMode = getIntent.getIntExtra("mode", 0);

        if (openMode == 3) {//打开已存在的note
            View li2=getLayoutInflater().inflate(R.layout.receive_layout,null,false);
            et_receive = li2.findViewById(R.id.et_receive);
            aList.add(li2);
            mAdapter.notifyDataSetChanged();

            id = getIntent.getLongExtra("id", 0);
            old_context = getIntent.getStringExtra("context");
            old_time = getIntent.getStringExtra("time");
            old_Tag = getIntent.getIntExtra("tag", 1);
            et.setText(old_context);
            et.setSelection(old_context.length());
            //展示绑定的日记内容
            rTask = new ReceiveTask();
            rTask.execute(old_time);//获取内容

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        if(openMode == 3){   //如果是打开已有的日记，则把发送按钮隐藏
            MenuItem itemToHide = menu.findItem(R.id.send_out);
            itemToHide.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete:
                new AlertDialog.Builder(EditActivity.this)
                        .setMessage("删除吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (openMode == 4){ // new note
                                    intent.putExtra("mode", -1);
                                    setResult(RESULT_OK, intent);
                                }
                                else { // existing note
                                    intent.putExtra("mode", 2);
                                    intent.putExtra("id", id);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.send_out://当点击发送按钮后
                //excute AsyncTask
                if(et.getText().toString().length() == 0){  //如果内容为空，则不发送
                    Toast.makeText(EditActivity.this,"日记内容为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    View li2=getLayoutInflater().inflate(R.layout.receive_layout,null,false); //展示接收日记页面
                    et_receive = li2.findViewById(R.id.et_receive);
                    aList.add(li2);
                    mAdapter.notifyDataSetChanged();
                    mTask = new MyTask();
                    mTask.execute();//执行异步线程
                }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WMToolImage.ALBUM_CHOOSE && resultCode == RESULT_OK) {
            textEditor.onActivityResult(data);
        }
    }


    private String dateToStr() {
        Date date =new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK){
            autoSetMessage();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void autoSetMessage(){
        if(openMode == 4){
            if(et.getText().toString().length() == 0){
                intent.putExtra("mode", -1); //nothing new happens.
            }
            else{
                intent.putExtra("mode", 0); // new one note;
                intent.putExtra("context", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", tag);
            }
        }
        else {
            if (et.getText().toString().equals(old_context) && !tagChange)
                intent.putExtra("mode", -1); // edit nothing
            else {
                intent.putExtra("mode", 1); //edit the content
                intent.putExtra("context", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }
    }

    //异步处理
    private class MyTask extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //发送日记到服务器
            if(et.getText().toString().length() == 0){
                //do not send
            }
            else{
                //to send
                String diaryContent=et.getText().toString();
                String date=dateToStr();
                String name="";
                String title="";
                //final String json = "{\"name\":\""+name+"\",\"title\":\""+title+"\",\"context\":\""+diaryContent+"\",\"time\":\""+date+"\"}";
                JSONObject json_object = new JSONObject();
                try {
                    json_object.put("from",name);
                    json_object.put("title",title);
                    json_object.put("context",diaryContent);
                    json_object.put("time",date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String json = json_object.toString();//发送的json

                Log.d("2020", "onClick: "+json);
                final String[] response_json = {""};
                Connection connection = new Connection(json,"PublishCode");
                response_json[0] = connection.getResponseData();//返回的json
                try {
                    JSONObject jsonObject =  new JSONObject(response_json[0]);
                    String title_r=jsonObject.getString("title");
                    String context_r = jsonObject.getString("context");
                    String time_r = jsonObject.getString("time");
                    String from_r = jsonObject.getString("from");
                    return context_r;//返回结果，将内容传给onPostExecute
                    //et_receive.setText(context_r);
                   // String state = jsonObject.getString("state");
                    //Toast.makeText(RegisterLogin.this,state,Toast.LENGTH_SHORT).show();
                    //publishProgress(context_r);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            et_receive.setText(result);//更改UI
            et_receive.setFocusableInTouchMode(false);//设为不可编辑状态
        }

    }

    private class ReceiveTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            //to send
            //String diaryContent=et.getText().toString();
            String date;
            String name="";
            //String title="";
            //final String json = "{\"name\":\""+name+"\",\"title\":\""+title+"\",\"context\":\""+diaryContent+"\",\"time\":\""+date+"\"}";
            JSONObject json_object = new JSONObject();
            try {
                json_object.put("from",name);
                //json_object.put("title",title);
                //json_object.put("context",diaryContent);
                json_object.put("time",params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json = json_object.toString();//发送的json
            final String[] response_json = {""};
            Connection connection = new Connection(json,"PublishCode");
            response_json[0] = connection.getResponseData();//返回的json
            try {
                JSONObject jsonObject =  new JSONObject(response_json[0]);
                String title_r=jsonObject.getString("title");
                String context_r = jsonObject.getString("context");
                String time_r = params[0];
                return context_r;//返回结果，将内容传给onPostExecute

                // String state = jsonObject.getString("state");
                //Toast.makeText(RegisterLogin.this,state,Toast.LENGTH_SHORT).show();
                //publishProgress(context_r);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            et_receive.setText(s);//更改UI
            et_receive.setFocusableInTouchMode(false);//设为不可编辑状态
        }
    }


}
