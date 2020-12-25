package com.example.flower.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.RegexUtils;
import com.bumptech.glide.Glide;
import com.example.flower.Connection;
import com.example.flower.R;
import com.google.gson.JsonObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//undone-验证邮箱真实性
public class RegisterLogin extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    //-=-=-=-=变量-登录页面-main=-=-=-=-
    //按钮
    private Button button_loginMain_showPlaintext;
    private Button button_loginMain_doneAll;
    //可编辑文本框
    private EditText editText_loginMain_email;
    private EditText editText_loginMain_password;
    //文本框
    private TextView textView_loginMain_toRegister;
    private TextView textView_loginMain_toForget;
    private TextView textView_loginMain_tipText;

    //-=-=-=-=变量-登出页面-main=-=-=-=-
    //按钮
    private Button button_userMain_logout;
    private Button button_userMain_modifyPassword;
    //文本框
    private TextView textView_userMain_emailText;
    private ImageView imageView_userMain_avatar;

    //-=-=-=-=变量-注册页面-main=-=-=-=-
    //按钮
    private Button button_registerMain_showPlaintext1;
    private Button button_registerMain_showPlaintext2;
    private Button button_registerMain_doneAll;
    //可编辑文本框
    private EditText editText_registerMain_email;
    private EditText editText_registerMain_password1;
    private EditText editText_registerMain_password2;
    //文本框
    private TextView textView_registerMain_tipText;
    private TextView textView_registerMain_toLogin;
    //账户/密码
    private String string_register_email = null;
    private String string_register_password = null;

    //-=-=-=-=变量-注册页面-next=-=-=-=-
    //文本框
    private TextView textView_registerNext_tipText;
    //可编辑文本框
    private EditText editText_registerNext_checkCode;
    //按钮
    private Button button_registerNext_sendEmailCheck;
    private Button button_registerNext_doneAll;
    //倒计时时长，默认倒计时时间60秒；
    private long length = 60 * 1000;
    //开始执行计时的类，可以在每秒实行间隔任务
    private Timer timer;
    //每秒时间到了之后所执行的任务
    private TimerTask timerTask;
    //在点击按钮之前按钮所显示的文字，默认是获取验证码
    private String beforeText = "获取验证码";
    //在开始倒计时之后那个秒数数字之后所要显示的字，默认是秒
    private String afterText = "秒";

    MyAsyncTask syncTask;//声明
    RegisterTask rgTask;
    LoginTask loginTask;

    //-=-=-=-=活动初始化=-=-=-=-
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loginStateWrite_login(null,null);

        //loginStateWrite_login("null", "null");


        if(loginStateIs()){
            setContentView(R.layout.user_main);
            toLogoutMain();
        }else {
            setContentView(R.layout.login_main);
            toLoginMain();
        }
    }

    //-=-=-=-=Touch监听器=-=-=-=-
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //手指按下
                if(v.getId()==R.id.registerMain_button_password_1)
                    editText_registerMain_password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else if(v.getId()==R.id.registerMain_button_password_2)
                    editText_registerMain_password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else if(v.getId()==R.id.loginMain_button_password)
                    editText_loginMain_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case MotionEvent.ACTION_UP: //手指抬起
                if(v.getId()==R.id.registerMain_button_password_1)
                    editText_registerMain_password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else if(v.getId()==R.id.registerMain_button_password_2)
                    editText_registerMain_password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else if(v.getId()==R.id.loginMain_button_password)
                    editText_loginMain_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
        }
        return true;
    }

    //-=-=-=-=注册页面-next-倒计时按钮实现=-=-=-=-
    //初始化时间
    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
    }
    //设置倒计时时长
    public void setLength(long length) {
        this.length = length;
    }
    //设置未点击时显示的文字
    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }
    //设置未点击后显示的文字
    public void setAfterText(String beforeText) {
        this.afterText = afterText;
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        final ImageView imageView_userMain_avatar = (ImageView)findViewById(R.id.userMain_image_avatar);
//        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
//        final String iconSrc = sharedPreferences.getString("icon", null);
//        if(iconSrc==null){Log.d("iconSrc", "null");}
//        else{Log.d("iconSrc", iconSrc);}
//        Glide.with(RegisterLogin.this).load(iconSrc).into(imageView_userMain_avatar);
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                Glide.with(RegisterLogin.this).load(iconSrc).into(imageView_userMain_avatar);
////            }
////        }).start();;
//    }

    //-=-=-=-=Click监听器=-=-=-=-
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerMain_button_finish) {
            //注册-main
            //检查不为空&&邮箱格式/密码格式/验证码格式是否正确
            if (editText_registerMain_password1.getText().toString().equals("") || editText_registerMain_password2.getText().toString().equals("") || editText_registerMain_email.getText().toString().equals("")) {
                textView_registerMain_tipText.setText("存在空栏");
            } else if (!RegexUtils.isEmail(editText_registerMain_email.getText().toString())) {
                textView_registerMain_tipText.setText("邮箱格式错误");
            } else if (!editText_registerMain_password1.getText().toString().equals(editText_registerMain_password2.getText().toString())) {
                textView_registerMain_tipText.setText("两次密码不一样");
            } else if (!RegexUtils.isMatch("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$", editText_registerMain_password1.getText().toString())){
                textView_registerMain_tipText.setText("请输入6-20位包含大小写和数字的密码");
            }else {
                toRegisterNext();
                //undone-改变xml页面，密码和邮箱保存为变量？
            }
        } else if(v.getId()==R.id.registerNext_button_checkCode){
            //注册-next-发送验证码
            sendRequest_checkCode();
            start();
        } else if(v.getId()==R.id.registerNext_button_finish){
            //注册-next
            if(editText_registerNext_checkCode.getText().toString().equals(""))
                textView_registerNext_tipText.setText("存在空栏");
            else if(editText_registerNext_checkCode.getText().toString().length()!=4)
                textView_registerNext_tipText.setText("请输入4位验证码");
            else {
                sendRequest_toRegister();
            }
        } else if(v.getId() == R.id.registerMain_text_toLogin){
            //注册-main-toLogin
            toLoginMain();
        } else if(v.getId() == R.id.loginMain_text_toForgetPassword){
            //注册-main-toLogin
            toForgetPassword();
        } else if(v.getId() == R.id.loginMain_text_toRegister){
            //注册-main-toLogin
            toRegisterMain();
        } else if(v.getId() == R.id.loginMain_button_finish){
            if (editText_loginMain_email.getText().toString().equals("") || editText_loginMain_password.getText().toString().equals("")) {
                textView_loginMain_tipText.setText("存在空栏");
            } else if (!RegexUtils.isEmail(editText_loginMain_email.getText().toString())) {
                textView_loginMain_tipText.setText("邮箱格式错误");
            } else if(!RegexUtils.isMatch("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$",editText_loginMain_password.getText().toString())) {
                textView_loginMain_tipText.setText("请输入6-20位包含大小写和数字的密码");
            } else{
                sendRequest_toLogin();
            }
        } else if(v.getId()==R.id.userMain_button_toLogout){
            loginStateWrite_logout();
            toLoginMain();
        } else if(v.getId()==R.id.userMain_button_toModifyPassword){
            toModifyPassword();
        }
        else if(v.getId()==R.id.userMain_image_avatar)
            toModifyAvatar();
    }

    public void start() {
        initTimer();
        button_registerNext_sendEmailCheck.setText(length / 1000 + afterText);
        button_registerNext_sendEmailCheck.setEnabled(false);
        timer.schedule(timerTask, 0, 1000);
    }

    //更新显示的文本
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            button_registerNext_sendEmailCheck.setText(length / 1000 + afterText);
            length -= 1000;
            if (length < 0) {
                button_registerNext_sendEmailCheck.setEnabled(true);
                button_registerNext_sendEmailCheck.setText(beforeText);
                clearTimer();
                length = 60 * 1000;
            }
        }
    };

    //清除倒计时
    public void clearTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        clearTimer();
        super.onDetachedFromWindow();
    }


    //-=-=-=-=OK HTTP=-=-=-=-
    public void sendRequest_checkCode(){
//        String responseData = null;
//        if (!TextUtils.isEmpty(button_registerNext_sendEmailCheck.getText())) {
//            beforeText = button_registerNext_sendEmailCheck.getText().toString().trim();
//        }
//        button_registerNext_sendEmailCheck.setText(beforeText);
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "askCheckCode")
//                .add("username", string_register_email)
//                .add("password", string_register_password)
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
//            textView_registerNext_tipText.setText("发送成功");
//        toLogoutMain();
//            //undone-回到主activity，登记为登录状态
//        }else{
//            textView_registerNext_tipText.setText("发送失败");
//        }
    }

    public void sendRequest_toRegister(){
//        String responseData = null;
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "register")
//                .add("checkcode", editText_registerNext_checkCode.getText().toString())
//                .add("username", string_register_email)
//                .add("password", string_register_password)
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
//            textView_registerNext_tipText.setText("注册成功");
            loginStateWrite_login(string_register_email, string_register_password);
            string_register_email=string_register_password=null;
            rgTask = new RegisterTask();
            rgTask.execute();
            toLogoutMain();
//            //undone-回到主activity，登记为登录状态
//        }else{
//            textView_registerNext_tipText.setText("注册失败");
//        }
    }

    public void sendRequest_toLogin(){
//        String responseData = null;
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "login")
//                .add("username", editText_loginMain_email.getText().toString())
//                .add("password", editText_loginMain_password.getText().toString())
//                .build();
//        Request request = new Request.Builder()
//                .url("http://42.194.219.209:8080//Flower//Register")
//                .post(requestBody)
//                .build();
//        Log.d("LoginTest", "LoginTest");
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//            responseData = response.body().string();
//            Log.d("LoginTest", "onClick:" + responseData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(responseData=="true") {
//            textView_loginMain_tipText.setText("登录成功");
            loginTask = new LoginTask();
            loginTask.execute();
            //loginStateWrite_login(editText_loginMain_email.getText().toString(), editText_loginMain_password.getText().toString());
            //toLogoutMain();
//            //undone-回到主activity，登记为登录状态
//        }else{
//            textView_loginMain_tipText.setText("登录失败");
//        }
    }

    //-=-=-=-=注册页面-next=-=-=-=-
    private void toRegisterNext(){
        string_register_email = editText_registerMain_email.getText().toString();
        string_register_password = editText_registerMain_password1.getText().toString();

        setContentView(R.layout.register_next);

        button_registerNext_sendEmailCheck = (Button) findViewById(R.id.registerNext_button_checkCode);
        button_registerNext_doneAll = (Button) findViewById(R.id.registerNext_button_finish);
        textView_registerNext_tipText = (TextView)findViewById(R.id.registerNext_text_tip);

        editText_registerNext_checkCode = (EditText) findViewById(R.id.registerNext_edit_checkCode);
        editText_registerNext_checkCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText_registerNext_checkCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});


        button_registerNext_sendEmailCheck.setOnClickListener(new View.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View v) {
                                                                        syncTask = new MyAsyncTask();
                                                                        syncTask.execute();
//                                                                      SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
//                                                                      String email_j = sharedPreferences.getString("email", null);
//                                                                      String psw_j = sharedPreferences.getString("password", null);
//                                                                      final String json = "{\"email\":\""+string_register_email+"\",\"pwd\":\""+string_register_password+"\"}";//发送的json
//                                                                      Log.d("12345678", "onClick: "+json);
//                                                                      final String[] response_json = {""};
//                                                                      Thread th = new Thread(new Runnable() {
//                                                                          @Override
//                                                                          public void run() {
//                                                                              Connection connection = new Connection(json,"PublishCode");
//                                                                              response_json[0] = connection.getResponseData();//返回的json
//                                                                          }
//                                                                      });
//                                                                      th.start();
//                                                                      try {
//                                                                          th.join();
//                                                                      } catch (InterruptedException e) {
//                                                                          e.printStackTrace();
//                                                                      }
//                                                                      try {
//                                                                          JSONObject jsonObject =  new JSONObject(response_json[0]);
//                                                                          String state = jsonObject.getString("state");
//                                                                          Toast.makeText(RegisterLogin.this,state,Toast.LENGTH_SHORT).show();
//                                                                      } catch (JSONException e) {
//                                                                          e.printStackTrace();
//                                                                      }


                                                                  }
                                                              }

        );
        button_registerNext_doneAll.setOnClickListener(this);
    }
    //异步线程
    private class MyAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String email_j =sharedPreferences.getString("email", null);
            String psw_j = sharedPreferences.getString("password", null);
            final String json = "{\"email\":\""+string_register_email+"\",\"pwd\":\""+string_register_password+"\"}";//发送的json
            Log.d("12345678", "onClick: "+json);
            final String[] response_json = {""};
            Connection connection = new Connection(json,"PublishCode");
            response_json[0] = connection.getResponseData();//返回的json

            try {
                JSONObject jsonObject =  new JSONObject(response_json[0]);
                String state = jsonObject.getString("state");
//                Toast.makeText(RegisterLogin.this,state,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class RegisterTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String email_j = sharedPreferences.getString("email", null);
            String psw_j = sharedPreferences.getString("password", null);
//            EditText textcode=findViewById(R.id.registerNext_edit_checkCode);
            String checkcode = editText_registerNext_checkCode.getText().toString();
            JSONObject json_object = new JSONObject();
            try {
                json_object.put("email",email_j);
                json_object.put("pwd",psw_j);
                json_object.put("check_code",checkcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json = json_object.toString();//发送的json
            Log.d("12345678", "onClick: "+json);
            final String[] response_json = {""};
            Connection connection = new Connection(json,"Activate");
            response_json[0] = connection.getResponseData();//返回的json

            try {
                JSONObject jsonObject =  new JSONObject(response_json[0]);
                String state = jsonObject.getString("result");
//                Toast.makeText(RegisterLogin.this,state,Toast.LENGTH_SHORT).show();
                if(!state.equals("注册成功"))return "验证码错误";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(RegisterLogin.this,s,Toast.LENGTH_SHORT).show();
        }
    }
    private class LoginTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String email_j = editText_loginMain_email.getText().toString();
            String psw_j = editText_loginMain_password.getText().toString();
            //String checkcode = editText_registerNext_checkCode.getText().toString();
            JSONObject json_object = new JSONObject();
            try {
                json_object.put("email",email_j);
                json_object.put("pwd",psw_j);
                Log.d("pwd",psw_j);
                //json_object.put("check_code",checkcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json = json_object.toString();//发送的json
            Log.d("12345678", "onClick: "+json);
            final String[] response_json = {""};
            Connection connection = new Connection(json,"Login");
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
            Toast.makeText(RegisterLogin.this,s,Toast.LENGTH_SHORT).show();
            if(!s.equals("登录成功")) {
                Toast.makeText(RegisterLogin.this, s, Toast.LENGTH_SHORT).show();

            }else{
                toLogoutMain();
                Log.d("psw", editText_loginMain_password.getText().toString());
                if(editText_loginMain_password.getText().toString()==null){
                    Log.d("psw", null);
                }else{
                    Log.d("psw", editText_loginMain_password.getText().toString());
                }

                loginStateWrite_login(editText_loginMain_email.getText().toString(), editText_loginMain_password.getText().toString());
                GetInfoAsyncTask getInfoAsyncTask = new GetInfoAsyncTask();
                getInfoAsyncTask.execute();
            }
        }
    }
    private class GetInfoAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
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
            Connection connection = new Connection(json,"GetUserInfo");
            response_json[0] = connection.getResponseData();//返回的json
            //src:图片src
            try {
                JSONObject responseJsonO = new JSONObject(response_json[0]);
                String iconSrc = responseJsonO.getString("iconSrc");//用户头像的网络地址
                SharedPreferences sharedPreferencesIcon = getSharedPreferences("loginState", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesIcon.edit();
                editor.putString("icon", iconSrc);
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
//            String iconSrc = sharedPreferences.getString("icon", null);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
    private void toRegisterMain(){
        setContentView(R.layout.register_main);

        button_registerMain_doneAll = (Button) findViewById(R.id.registerMain_button_finish);
        button_registerMain_showPlaintext1 = (Button) findViewById(R.id.registerMain_button_password_1);
        button_registerMain_showPlaintext2 = (Button) findViewById(R.id.registerMain_button_password_2);

        textView_registerMain_tipText = (TextView) findViewById(R.id.registerMain_text_tip);
        textView_registerMain_toLogin = (TextView) findViewById(R.id.registerMain_text_toLogin);

        editText_registerMain_email = (EditText) findViewById(R.id.registerMain_edit_email);
        editText_registerMain_password1 = (EditText) findViewById(R.id.registerMain_edit_password_1);
        editText_registerMain_password2 = (EditText) findViewById(R.id.registerMain_edit_password_2);

        button_registerMain_showPlaintext1.setOnTouchListener(this);
        button_registerMain_showPlaintext2.setOnTouchListener(this);
        button_registerMain_doneAll.setOnClickListener(this);
        textView_registerMain_toLogin.setOnClickListener(this);
    }
    private void toLoginMain(){
        setContentView(R.layout.login_main);

        button_loginMain_showPlaintext = (Button) findViewById(R.id.loginMain_button_password);
        button_loginMain_doneAll = (Button) findViewById(R.id.loginMain_button_finish);
        textView_loginMain_toRegister = (TextView) findViewById(R.id.loginMain_text_toRegister);
        textView_loginMain_toForget = (TextView)findViewById(R.id.loginMain_text_toForgetPassword);
        textView_loginMain_tipText = (TextView)findViewById(R.id.loginMain_text_tipText);
        editText_loginMain_email = (EditText) findViewById(R.id.loginMain_edit_email);
        editText_loginMain_password = (EditText) findViewById(R.id.loginMain_edit_password);

        button_loginMain_showPlaintext.setOnTouchListener(this);
        button_loginMain_doneAll.setOnClickListener(this);
        textView_loginMain_toRegister.setOnClickListener(this);
        textView_loginMain_toForget.setOnClickListener(this);
    }
    private void toLogoutMain(){
        setContentView(R.layout.user_main);

        final ImageView imageView_userMain_avatar = (ImageView)findViewById(R.id.userMain_image_avatar);
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        final String iconSrc = sharedPreferences.getString("icon", null);
        if(iconSrc==null){Log.d("iconSrc", "null");}
        else{Log.d("iconSrc", iconSrc);}
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("iconSrc", iconSrc);
                Glide.with(RegisterLogin.this).load(iconSrc).into(imageView_userMain_avatar);
            }
        }).run();

        button_userMain_logout = (Button)findViewById(R.id.userMain_button_toLogout);

        button_userMain_modifyPassword = (Button)findViewById(R.id.userMain_button_toModifyPassword);

        textView_userMain_emailText = (TextView)findViewById(R.id.userMain_text_email);
        textView_userMain_emailText.setText(loginStateReadEmail());

        imageView_userMain_avatar.setOnClickListener(this);
        button_userMain_logout.setOnClickListener(this);

        button_userMain_modifyPassword.setOnClickListener(this);
    }

    void toForgetPassword(){
        Intent intent = new Intent(this,ForgetPasswordActivity.class);
        startActivity(intent);
    }

    void toModifyPassword(){
        Intent intent=new Intent(this,ModifyPasswordActivity.class);
        startActivity(intent);
    }

    void toModifyAvatar(){
        Intent intent=new Intent(this,ModifyAvatarActivity.class);
        startActivity(intent);
    }

    //-=-=-=-=登陆状态-SharedPreference=-=-=-=-
    private void loginStateWrite_login(String email, String password){
        //修改邮箱和密码
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }
    private void loginStateWrite_logout(){
        //删去密码
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", null);
        editor.commit();
    }
    private void loginStateWrite_clear(){
        //删去邮箱和密码
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", null);
        editor.putString("password", null);
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

    private String loginStateReadEmail(){
        //读取邮箱和密码
        String message = new String();
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        message = sharedPreferences.getString("email", null);
        return message;
    }

    private boolean loginStateIs(){
        //读取邮箱和密码
        String[] message = new String[2];
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        message[0] = sharedPreferences.getString("email", null);
        message[1] = sharedPreferences.getString("password", null);
       // Log.d("psw",message[1]);
        if(message[0]==null || message[1]==null)
            return false;
        return true;
    }
}

