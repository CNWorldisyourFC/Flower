package com.example.flower.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.RegexUtils;
import com.example.flower.Connection;
import com.example.flower.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private EditText editText_forgetMain_email;
    private EditText editText_forgetNext_checkcode;
    private EditText editText_forgetNext_newPassword1;
    private EditText editText_forgetNext_newPassword2;

    private Button button_forgetMain_sendCheckcode;
    private Button button_forgetNext_doneAll;
    private Button button_forgetNext_newPassword1;
    private Button button_forgetNext_newPassword2;

    private TextView textView_forgetMain_tipText;
    private TextView textView_forgetNext_tipText;
    private TextView textView_forgetNext_email;

    private String email;

    ForgetPassTask forgetPassTask;


    SendEmail sendEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toForgetPasswordMain();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.forgetPasswordMain_button_finish) {
            if (editText_forgetMain_email.getText().toString().equals("")) {
                textView_forgetMain_tipText.setText("邮箱不可为空");
            }else if (!RegexUtils.isEmail(editText_forgetMain_email.getText().toString())) {
                    textView_forgetMain_tipText.setText("邮箱格式错误");
            }else {
                email = editText_forgetMain_email.getText().toString();
                textView_forgetMain_tipText.setText("请求发送中");
                button_forgetMain_sendCheckcode.setEnabled(false);
                //sendRequest_toSendEmail();
                sendEmail =new SendEmail();
                sendEmail.execute();
                //跳到wait页面，直到返回了请求，在选择跳到失败页面，或next页面
            }
        }else if(v.getId() == R.id.forgetPasswordNext_button_finish) {
            if (editText_forgetNext_checkcode.getText().toString().equals("")|| editText_forgetNext_newPassword1.getText().toString().equals("") || editText_forgetNext_newPassword2.getText().toString().equals("")) {
                textView_forgetNext_tipText.setText("存在空栏");
            }else if(editText_forgetNext_checkcode.getText().toString().length()!=4)
                textView_forgetNext_tipText.setText("请输入4位验证码");
            else if (!editText_forgetNext_newPassword1.getText().toString().equals(editText_forgetNext_newPassword2.getText().toString())) {
                textView_forgetNext_tipText.setText("两次密码不一样");
            } else if (!RegexUtils.isMatch("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$", editText_forgetNext_newPassword1.getText().toString())) {
                textView_forgetNext_tipText.setText("请输入6-20位包含大小写和数字的密码");
            }else {
                textView_forgetNext_tipText.setText("请求发送中");
                button_forgetNext_doneAll.setEnabled(false);
                //sendRequest_toModifyPassword();
                forgetPassTask = new ForgetPassTask();
                forgetPassTask.execute();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //手指按下
                if(v.getId()==R.id.forgetPasswordNext_button_newPassword1)
                    editText_forgetNext_newPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else if(v.getId()==R.id.forgetPasswordNext_button_newPassword2)
                    editText_forgetNext_newPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case MotionEvent.ACTION_UP: //手指抬起
                if(v.getId()==R.id.forgetPasswordNext_button_newPassword1)
                    editText_forgetNext_newPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else if(v.getId()==R.id.forgetPasswordNext_button_newPassword2)
                    editText_forgetNext_newPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
        }
        return true;
    }


    //----------HTTP----------
    //发送邮箱，服务器确认邮箱是否已注册，是则返回true且发送验证码，否则返回fail
    public void sendRequest_toSendEmail(){

//        String responseData = null;
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "askCheckCode_Forget")
//                .add("username", email)
//                .build();
//        Request request = new Request.Builder()
//                //.url("http://42.194.219.209:8080//Flower//Register")
//                .post(requestBody)
//                .build();
//        Log.d("ForgetTest1", "ForgetTest1");
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//            responseData = response.body().string();
//            Log.d("ForgetTest1", "onClick:" + responseData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(responseData=="true") {
            //toForgetPasswordNext();
//        }else{
//            textView_forgetMain_tipText.setText("此邮箱未注册，请确认邮箱是否输入正确");
//            button_forgetMain_sendCheckcode.setEnabled(true);
//        }
    }

    //发送邮箱、验证码、新密码，服务器确认邮箱与验证码是否匹配，是则返回true且发送验证码，否则返回fail
    public void sendRequest_toModifyPassword(){
//
//        String responseData = null;
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "forgetPassword")
//                .add("username", email)
//                .add("password",editText_forgetNext_newPassword1.getText().toString())
//                .add("checkcode",editText_forgetNext_checkcode.getText().toString())
//                .build();
//        Request request = new Request.Builder()
//                //.url("http://42.194.219.209:8080//Flower//Register")
//                .post(requestBody)
//                .build();
//        Log.d("ForgetTest2", "ForgetTest2");
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//            responseData = response.body().string();
//            Log.d("ForgetTest2", "onClick:" + responseData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(responseData=="true") {
//            textView_forgetNext_tipText.setText("密码修改成功");
//        }else{
            textView_forgetNext_tipText.setText("密码修改失败");
            button_forgetNext_doneAll.setEnabled(true);
//        }
    }


    //----------跳转方法（改变xml）----------
    private void toForgetPasswordMain(){

        setContentView(R.layout.forgetpassword_main);

        editText_forgetMain_email = (EditText) findViewById(R.id.forgetPasswordMain_edit_email);
        button_forgetMain_sendCheckcode = (Button) findViewById(R.id.forgetPasswordMain_button_finish);
        textView_forgetMain_tipText = (TextView) findViewById(R.id.forgetPasswordMain_text_tipText);

        button_forgetMain_sendCheckcode.setOnClickListener(this);

        if(loginStateReadEmail()!=null) {
            editText_forgetMain_email.setText(loginStateReadEmail());
        }else
            editText_forgetMain_email.setText("");
    }

    private void toForgetPasswordNext(){
        setContentView(R.layout.forgetpassword_next);

        editText_forgetNext_checkcode=(EditText) findViewById(R.id.forgetPasswordNext_edit_checkCode);
        editText_forgetNext_newPassword1=(EditText) findViewById(R.id.forgetPasswordNext_edit_newPassword1);
        editText_forgetNext_newPassword2=(EditText)findViewById(R.id.forgetPasswordNext_edit_newPassword2);
        button_forgetNext_doneAll=(Button)findViewById(R.id.forgetPasswordNext_button_finish);
        button_forgetNext_newPassword1=(Button)findViewById(R.id.forgetPasswordNext_button_newPassword1);
        button_forgetNext_newPassword2=(Button)findViewById(R.id.forgetPasswordNext_button_newPassword2);
        textView_forgetNext_tipText=(TextView) findViewById(R.id.forgetPasswordNext_text_tipText);
        textView_forgetNext_email=(TextView)findViewById(R.id.forgetPasswordNext_text_email);


        button_forgetNext_doneAll.setOnClickListener(this);
        button_forgetNext_newPassword1.setOnTouchListener(this);
        button_forgetNext_newPassword2.setOnTouchListener(this);

        editText_forgetNext_checkcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        if(loginStateReadEmail()!=null) {
            textView_forgetNext_email.setText(loginStateReadEmail());
        }else
            textView_forgetNext_email.setText("");

    }

    private void loginStateWritePassword(String password){
        //修改邮箱和密码
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.commit();
    }

    private String loginStateReadEmail(){
        //读取邮箱和密码
        String message = new String();
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        message = sharedPreferences.getString("email", null);
        return message;
    }

    //点击发送时发送邮件
    private class SendEmail extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String email_j = sharedPreferences.getString("email", null);

            JSONObject json_object = new JSONObject();
            try {
                json_object.put("email",email_j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json = json_object.toString();//发送的json
            final String[] response_json = {""};
            Connection connection = new Connection(json,"ForgetPwd");
            response_json[0] = connection.getResponseData();//返回的json

            try {
                JSONObject jsonObject =  new JSONObject(response_json[0]);
                String state = jsonObject.getString("state");
//                Toast.makeText(RegisterLogin.this,state,Toast.LENGTH_SHORT).show();
                if(!state.equals("发送成功"))return "发送失败";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(ForgetPasswordActivity.this,s,Toast.LENGTH_SHORT).show();
            if(!s.equals("邮箱不存在")){
                //跳转到下一个页面
                toForgetPasswordNext();
            }
            super.onPostExecute(s);
        }
    }
    //点击确定之后
    private class ForgetPassTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            editText_forgetNext_newPassword1.setEnabled(false);
            editText_forgetNext_newPassword2.setEnabled(false);
            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String psw_j = editText_forgetNext_newPassword1.getText().toString();
                    // String email_j = sharedPreferences.getString("email", null);
            //String psw_j = sharedPreferences.getString("password", null);
//            EditText textcode=findViewById(R.id.registerNext_edit_checkCode);
            String checkcode = editText_forgetNext_checkcode.getText().toString();
            JSONObject json_object = new JSONObject();
            try {
                //json_object.put("email",email_j);
                json_object.put("pwd",psw_j);
                json_object.put("check_code",checkcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json = json_object.toString();//发送的json

            Log.d("12345678", "onClick: "+json);
            final String[] response_json = {""};
            Connection connection = new Connection(json,"UpdatePwdByCode");
            response_json[0] = connection.getResponseData();//返回的json

            try {
                JSONObject jsonObject =  new JSONObject(response_json[0]);
                String state = jsonObject.getString("state");
                return state;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(ForgetPasswordActivity.this,s,Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }

}
