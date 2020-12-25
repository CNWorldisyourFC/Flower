package com.example.flower.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ModifyPasswordActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{
    private EditText editText_modifyPassword_oldPassword;
    private EditText editText_modifyPassword_newPassword1;
    private EditText editText_modifyPassword_newPassword2;

    private Button button_modifyPassword_doneAll;
    private Button button_modifyPassword_oldPassword;
    private Button button_modifyPassword_newPassword1;
    private Button button_modifyPassword_newPassword2;

    private TextView textView_modifyPassword_tipText;
    private TextView textView_modifyPassword_emailText;

    ChangePassTask changePassTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifypassword_main);

        editText_modifyPassword_oldPassword=(EditText)findViewById(R.id.modifyPasswordMain_edit_oldPassword);
        editText_modifyPassword_newPassword1=(EditText)findViewById(R.id.modifyPasswordMain_edit_newPassword1);
        editText_modifyPassword_newPassword2=(EditText)findViewById(R.id.modifyPasswordMain_edit_newPassword2);

        button_modifyPassword_doneAll=(Button)findViewById(R.id.modifyPasswordMain_button_finish);
        button_modifyPassword_oldPassword=(Button)findViewById(R.id.modifyPasswordMain_button_oldPassword);
        button_modifyPassword_newPassword1=(Button)findViewById(R.id.modifyPasswordMain_button_newPassword1);
        button_modifyPassword_newPassword2=(Button)findViewById(R.id.modifyPasswordMain_button_newPassword2);

        textView_modifyPassword_emailText = (TextView)findViewById(R.id.modifyPasswordMain_text_email);
        textView_modifyPassword_tipText = (TextView)findViewById(R.id.modifyPasswordMain_text_tipText);

        textView_modifyPassword_emailText.setText(loginStateRead()[0].toString());

        button_modifyPassword_doneAll.setOnClickListener(this);
        button_modifyPassword_oldPassword.setOnTouchListener(this);
        button_modifyPassword_newPassword1.setOnTouchListener(this);
        button_modifyPassword_newPassword2.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.modifyPasswordMain_button_finish) {
            if (editText_modifyPassword_oldPassword.getText().toString().equals("") || editText_modifyPassword_newPassword1.getText().toString().equals("") || editText_modifyPassword_newPassword2.getText().toString().equals("")) {
                textView_modifyPassword_tipText.setText("存在空栏");
            } else if (!editText_modifyPassword_newPassword1.getText().toString().equals(editText_modifyPassword_newPassword2.getText().toString())) {
                textView_modifyPassword_tipText.setText("两次密码不一样");
            } else if (!RegexUtils.isMatch("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$", editText_modifyPassword_newPassword1.getText().toString())){
                textView_modifyPassword_tipText.setText("请输入6-20位包含大小写和数字的新密码");
            } else if (!RegexUtils.isMatch("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$", editText_modifyPassword_oldPassword.getText().toString())){
                textView_modifyPassword_tipText.setText("旧密码格式错误");
            }else if (editText_modifyPassword_newPassword1.getText().toString().equals(editText_modifyPassword_oldPassword.getText().toString())){
                textView_modifyPassword_tipText.setText("新密码不能与旧密码一致");
            }else {
                button_modifyPassword_doneAll.setEnabled(false);
                textView_modifyPassword_tipText.setText("请求发送中");
                //sendRequest_toModifyPassword();
                changePassTask = new ChangePassTask();
                changePassTask.execute();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //手指按下
                if(v.getId()==R.id.modifyPasswordMain_button_oldPassword)
                    editText_modifyPassword_oldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else if(v.getId()==R.id.modifyPasswordMain_button_newPassword1)
                    editText_modifyPassword_newPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else if(v.getId()==R.id.modifyPasswordMain_button_newPassword2)
                    editText_modifyPassword_newPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case MotionEvent.ACTION_UP: //手指抬起
                if(v.getId()==R.id.modifyPasswordMain_button_oldPassword)
                    editText_modifyPassword_oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else if(v.getId()==R.id.modifyPasswordMain_button_newPassword1)
                    editText_modifyPassword_newPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else if(v.getId()==R.id.modifyPasswordMain_button_newPassword2)
                    editText_modifyPassword_newPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
        }
        return true;
    }

    public void sendRequest_toModifyPassword(){
        //客户端不验证密码正确性
//        String responseData = null;
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "modifyPassword")
//                .add("username", textView_modifyPassword_emailText.getText().toString())
//                .add("oldPassword", editText_modifyPassword_oldPassword.getText().toString())
//                .add("newPassword", editText_modifyPassword_newPassword1.getText().toString())
//                .build();
//        Request request = new Request.Builder()
//                .url("http://42.194.219.209:8080//Flower//Register")
//                .post(requestBody)
//                .build();
//        Log.d("RegisterTest", "RegisterTest");
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//            responseData = response.body().string();
//            Log.d("RegisterTest", "onClick:" + responseData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(responseData=="true") {
            textView_modifyPassword_tipText.setText("修改密码成功");
            loginStateWrite_modifyPassword(textView_modifyPassword_emailText.getText().toString(), editText_modifyPassword_newPassword1.getText().toString());
//        }else{
//            textView_modifyPassword_tipText.setText("修改密码失败");
//            button_modifyPassword_doneAll.setEnabled(true);
//        }
    }



    private void loginStateWrite_modifyPassword(String email, String password){
        //修改邮箱和密码
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(email.equals(sharedPreferences.getString("email", null))) {
            editor.putString("password", password);
            editor.commit();
        }else {
            Log.d("modifyPasswordTest", "false - emailCantEquals");
            textView_modifyPassword_tipText.setText("修改密码失败-登记邮箱与请求中邮箱不一致");
        }
    }

    private void loginStateWritePassword(String password){
        //修改邮箱和密码
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.commit();
    }

    private String[] loginStateRead(){
        //读取邮箱和密码
        String[] message = new String[2];
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        message[0] = sharedPreferences.getString("email", null);
        message[1] = sharedPreferences.getString("password", null);
        return message;
    }

    private class ChangePassTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String email_j = sharedPreferences.getString("email", null);
            String psw_o = editText_modifyPassword_oldPassword.getText().toString();
            String psw_j = editText_modifyPassword_newPassword1.getText().toString();
            JSONObject json_object = new JSONObject();
            try {
                json_object.put("email",email_j);
                json_object.put("pwd",psw_j);
                json_object.put("old_pwd",psw_o);
                //json_object.put("check_code",checkcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json = json_object.toString();//发送的json

            Log.d("12345678", "onClick: "+json);
            final String[] response_json = {""};
            Connection connection = new Connection(json,"UpdatePwd");
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
            super.onPostExecute(s);
            Toast.makeText(ModifyPasswordActivity.this,s,Toast.LENGTH_SHORT).show();
        }
    }

}
