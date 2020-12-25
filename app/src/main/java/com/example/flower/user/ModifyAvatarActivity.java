package com.example.flower.user;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.flower.Connection;
import com.example.flower.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyAvatarActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_modifyAvatar_takeFromAlbum;

    private TextView textView_modifyAvatar_emailText;

    private ImageView imageView_modifyAvatar_avatar;
    ChangeAvatar changeAvatar;

    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    OkHttpClient client = Connection.getClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifyavatar_main);

        imageView_modifyAvatar_avatar=(ImageView)findViewById(R.id.modifyAvatarMain_image_avatar);
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        final String iconSrc = sharedPreferences.getString("icon", null);
        if(iconSrc==null){Log.d("iconSrc", "null");}
        else{Log.d("iconSrc", iconSrc);}
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("iconSrc", iconSrc);
                Glide.with(ModifyAvatarActivity.this).load(iconSrc).into(imageView_modifyAvatar_avatar);
            }
        }).run();

        button_modifyAvatar_takeFromAlbum=(Button)findViewById(R.id.modifyAvatarMain_button_takeFromAlbum);
        textView_modifyAvatar_emailText=(TextView)findViewById(R.id.modifyAvatarMain_text_email);
        imageView_modifyAvatar_avatar.setOnClickListener(this);
        button_modifyAvatar_takeFromAlbum.setOnClickListener(this);

        if(loginStateReadEmail()!=null) {
            textView_modifyAvatar_emailText.setText(loginStateReadEmail());
        }else
            textView_modifyAvatar_emailText.setText("");

        //setAvatarToImageView(imageView_modifyAvatar_avatar);

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.modifyAvatarMain_button_takeFromAlbum){
            modifyheadFromAlbum();
//            changeAvatar = new ChangeAvatar();
//            changeAvatar.execute();
        }
    }


    public void modifyheadFromAlbum() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());//裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(head);//保存在SD卡中
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, 3);
    }

    private void setPicToView(Bitmap mBitmap) {
        mBitmap = getCroppedBitmap(mBitmap);
        String sdStatus = Environment.getExternalStorageState();
        String filename = "output_image.png";
        String filename2 = loginStateReadEmail()+".png";

        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            textView_modifyAvatar_emailText.setText("SdCantUse");
            return;
        }
        FileOutputStream b = null;
        FileOutputStream b2 = null;
        File file = new File(getExternalFilesDir(null), filename);
        File file2 = new File(getExternalFilesDir(null), filename2);

        try {
            b = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
            b2 = new FileOutputStream(file2);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, b2);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
                b2.flush();
                b2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //uploadAvatar(file2);
        changeAvatar = new ChangeAvatar();
        Log.d("12345678", "setPicToView: bablablablalbalbla");
        changeAvatar.execute();
        setAvatarToImageView(imageView_modifyAvatar_avatar);

    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    //----处理好的头像图片output_image.png，上传到服务器----
    private void uploadAvatar(File avatarPNG){
        String responseData="";
        File file = new File(getExternalFilesDir(null), loginStateReadEmail()+".png");
        long size = file.length();//文件长度
        MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
        RequestBody requestBody = RequestBody.Companion.create(file,mediaType);//把文件与类型放入请求体

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//添加表单数据
                .addFormDataPart("file", file.getName(), requestBody)//文件名,请求体里的文件
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Bearer d3e63518-1ba7-4342-b94c-63c8b9b9046b")//添加请求头的身份认证Token
                .url("http://192.168.43.89:8080//Flower_Server//UploadImage")
//                .url("http://192.168.43.89:8080//HappyLearning_Server//UpdateIcon")
                .post(multipartBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            responseData = response.body().string();
            Log.d("LoginTest", "onClick:" + responseData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //----头像图片output_image.png从服务器下载----
    private boolean downloadAvatar(){
        return false;
    }

    //----读取头像文件地址，设置头像----
    public void setAvatarToImageView(ImageView imageView){
        String filename = "output_image.png";
        File file = new File(getExternalFilesDir(null), filename);
        imageView_modifyAvatar_avatar.setImageResource(R.drawable.user_avatar);
        if(file.exists()){
            imageView_modifyAvatar_avatar.setImageURI(Uri.fromFile(file));
        }
    }



    private String loginStateReadEmail(){
        //读取邮箱
        String message = new String();
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        message = sharedPreferences.getString("email", null);
        return message;
    }

    private class ChangeAvatar extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            String responseData="";
            File file = new File(getExternalFilesDir(null), loginStateReadEmail()+".png");
            long size = file.length();//文件长度
            MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
            RequestBody requestBody = RequestBody.Companion.create(file,mediaType);//把文件与类型放入请求体

            SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
            String email_j = sharedPreferences.getString("email", null);
            JSONObject json_object = new JSONObject();
            try {
                //json_object.put("email",email_j);
                json_object.put("email",email_j);
                json_object.put("path",file.getPath());
                Log.d("path",file.getPath());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String json_string = json_object.toString();//发送的json

            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)//添加表单数据
                    .addFormDataPart("json", json_string)//json字符串
                    .addFormDataPart("file", file.getName(), requestBody)//文件名,请求体里的文件
                    .build();

            Request request = new Request.Builder()
                    .header("Authorization", "Bearer d3e63518-1ba7-4342-b94c-63c8b9b9046b")//添加请求头的身份认证Token
                    .url("http://192.168.43.89:8080//Flower_Server//UploadImage")
//                .url("http://192.168.43.89:8080//HappyLearning_Server//UpdateIcon")
                    .post(multipartBody)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                responseData = response.body().string();
                Log.d("LoginTest", "onClick:" + responseData);
                JSONObject jsonObject = new JSONObject(responseData);
                String iconSrc = jsonObject.getString("iconSrc");
                SharedPreferences sharedPreferencesIcon = getSharedPreferences("loginState", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesIcon.edit();
                editor.putString("icon", iconSrc);
                editor.commit();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            try{
                file.delete();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
