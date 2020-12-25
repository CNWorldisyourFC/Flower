package com.example.flower.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.flower.Database;
import com.example.flower.DiaryAdapter;
import com.example.flower.DiaryDatabase;
import com.example.flower.EditActivity;
import com.example.flower.MainActivity;
import com.example.flower.MyAdapter;
import com.example.flower.R;
import com.example.flower.model.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    TextView diary_context = null;
    private DiaryAdapter adapter;
    private ListView list_diary;
    private List<Diary> diaryList = new ArrayList<Diary>();
    private DiaryDatabase dbHelper;

    private final String TAG = "tag";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getmListView().observe(getViewLifecycleOwner(), new Observer<ListView>() {
            @Override
            public void onChanged(ListView listView) {
                refreshListView();
            }
        });
        WebView webview = (WebView)root.findViewById(R.id.webview);
        webview.loadUrl("file:///android_asset/Web/html/GrassLand.html");
        //
        setHasOptionsMenu(true);
        //
        list_diary = root.findViewById(R.id.list_diary);
        adapter = new DiaryAdapter(getActivity(), diaryList);
        refreshListView();
        list_diary.setAdapter(adapter);
        list_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.list_diary:
                        Diary curDiary = (Diary) parent.getItemAtPosition(position);
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("context", curDiary.getContent());
                        intent.putExtra("id", curDiary.getId());
                        intent.putExtra("time", curDiary.getTime());
                        intent.putExtra("mode", 3);     // MODE of 'click to edit'
                        intent.putExtra("tag", curDiary.getTag());
                        startActivityForResult(intent, 1);      //collect data from edit
                        Log.d(TAG, "onItemClick: " + position);
                        break;
                }
            }
        });
        refreshListView();
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), EditActivity.class);
                intent.putExtra("mode", 4);
//                intent.putExtra("input" , 0);
                startActivityForResult(intent, 1 );
            }
        });

        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        switch (requestCode){
//            case 1:
//                if (resultCode== RESULT_OK){
//                    String context = data.getStringExtra("context");
//                    String time = data.getStringExtra("time");
//                    Diary diary = new Diary(context , time , 1);
//                    Database op = new Database(mContext);
//                    op.open();
//                    op.addDiary(diary);
//                    op.close();
//                    refreshListView();
//                }
//                break;
//            default:
//                Log.d(TAG,"wrong");
//                break;
//        }
//        super.onActivityResult(requestCode,resultCode,data);

        int returnMode;
        long diary_Id;
        returnMode = data.getExtras().getInt("mode", -1);
        diary_Id = data.getExtras().getLong("id", 0);


        if (returnMode == 1) {  //update current diary

            String content = data.getExtras().getString("context");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);

            Diary newDiary = new Diary(content, time, tag);
            newDiary.setId(diary_Id);
            Database op = new Database(getActivity());
            op.open();
            op.updateDiary(newDiary);
            op.close();
        } else if (returnMode == 0) {  // create new diary
            String content = data.getExtras().getString("context");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);

            Diary newDiary = new Diary(content, time, tag);
            newDiary.setId(diary_Id);
            Database op = new Database(getActivity());
            op.open();
            op.addDiary(newDiary);
            op.close();
        } else if (returnMode == 2) { // delete
            Diary curDiary = new Diary();
            curDiary.setId(diary_Id);
            Database op = new Database(getActivity());
            op.open();
            op.updateDiary(curDiary);
            op.close();
        }
        else{

        }
        refreshListView();
        super.onActivityResult(requestCode, resultCode, data);

    }
    public void refreshListView(){
        Database op = new Database(getActivity());
        op.open();
        if(diaryList.size() > 0 ) diaryList.clear();
        diaryList.addAll(op.getAllDiaries());
        op.close();
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete:
                new AlertDialog.Builder(getActivity())
                        .setMessage("删除全部吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper = new DiaryDatabase(getActivity());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete("diaries", null, null);
                                db.execSQL("update sqlite_sequence set seq=0 where name='diaries'");
                                refreshListView();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

                break;

        }
//        refreshListView();
        return super.onOptionsItemSelected(item);
    }

//fragment中添加menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        //search setting
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

//        return super.onCreateOptionsMenu(menu);
    }


}