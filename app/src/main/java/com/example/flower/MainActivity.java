package com.example.flower;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.flower.model.Diary;
import com.example.flower.user.RegisterLogin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private DiaryDatabase dbHelper;

    private Context mContext ;
    private AppBarConfiguration mAppBarConfiguration;
    private final String TAG = "tag";
    TextView diary_context = null;
    private ListView list_diary;
    private DiaryAdapter adapter;
    private List<Diary> diaryList = new ArrayList<Diary>();
    private GetInfoAsyncTask getInfoAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headView;
        headView = navigationView.getHeaderView(0);
        ImageView avatarView;
        avatarView = headView.findViewById(R.id.imageView);
        avatarView.setOnClickListener(this);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //setContentView(R.layout.register_main);
                switch(item.getItemId()){
                    case R.id.nav_register_login:
                        Intent aaa = new Intent(MainActivity.this, RegisterLogin.class);
                        startActivity(aaa);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        getInfoAsyncTask = new GetInfoAsyncTask();
        getInfoAsyncTask.execute();
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, RegisterLogin.class);
        startActivity(intent);
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
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}