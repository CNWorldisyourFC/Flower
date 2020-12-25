package com.example.flower;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Connection {
    private String responseData="";
    private static OkHttpClient client = new OkHttpClient();
    public Connection(String json,String src) {
//        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.43.89:8080///Flower_Server//"+src)
//                .addHeader("connection","close")
                .post(RequestBody.create(json.getBytes()))
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            responseData = response.body().string();
            Log.d("Login", "API_onClick:" + responseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OkHttpClient getClient(){
        return client;
    }
    public String getResponseData()
    {
        return responseData;
    }
}
