package com.example.flower;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.flower.model.Diary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DiaryAdapter extends BaseAdapter implements Filterable {
    private MyFilter mFilter;
    private List<Diary> backList;
    private List<Diary> diaryList;
    private Context mContext;

    public DiaryAdapter(Context mContext, List<Diary> diaryList) {
        this.mContext = mContext;
        this.diaryList = diaryList;
        backList = diaryList;
    }

    @Override
    public int getCount() {
        return diaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return diaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //mContext.setTheme(R.style.DayTheme);
        View v = View.inflate(mContext, R.layout.mydiary_layout, null);
        TextView tv_content = (TextView)v.findViewById(R.id.diary_content);
        TextView tv_time = (TextView)v.findViewById(R.id.diary_time);

        //Set text for TextView
        String allText = diaryList.get(position).getContent();
        /*if (sharedPreferences.getBoolean("noteTitle" ,true))
            tv_content.setText(allText.split("\n")[0]);*/
        tv_content.setText(allText);
        tv_time.setText(diaryList.get(position).getTime());

        //Save note id to tag
        v.setTag(diaryList.get(position).getId());

        return v;
    }
    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<Diary> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Diary diary : backList) {
                    if (diary.getContent().contains(charSequence)) {
                        list.add(diary);
                    }

                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }
        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            diaryList = (List<Diary>)filterResults.values;
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
            }else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }





}