package com.example.flower.ui.home;

import android.content.Context;
import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flower.MyAdapter;
import com.example.flower.R;
import com.example.flower.model.Diary;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {


    private MutableLiveData<ListView> mListView;
    public HomeViewModel() {
        mListView = new MutableLiveData<>();
//        mText.setValue("This is home fragment");

    }
    public MutableLiveData<ListView> getmListView(){
        if(mListView==null){
            mListView=new MutableLiveData<>();
        }
        return mListView;
    }

//    public LiveData<ListView> getText() {
//        return mListView;
//    }
}