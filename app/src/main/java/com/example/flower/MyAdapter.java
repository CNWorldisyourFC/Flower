package com.example.flower;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.flower.model.Diary;

import java.util.ArrayList;
import java.util.List;

public abstract class MyAdapter<T> extends BaseAdapter {
    private ArrayList<T> mData;
    private int mLayoutRes; // layout id


    public MyAdapter(){

    }

    public MyAdapter(ArrayList<T> mData ){
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, getmLayoutRes()
                , position);
        bindView(holder, getItem(position));
        return holder.getItemView();
    }

    public abstract void bindView(ViewHolder holder, T obj);

    public abstract int getmLayoutRes();

    public static class ViewHolder{

        private SparseArray<View> mViews;   //存储ListView 的 item中的View
        private View item;                  //存放convertView
        private int position;               //游标
        private Context context;            //Context上下文

        //构造方法，完成相关初始化
        private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
            mViews = new SparseArray<>();
            this.context = context;
            View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
            item = convertView;
        }

        //绑定ViewHolder与item
        public static ViewHolder bind(Context context, View convertView, ViewGroup parent,
                                      int layoutRes, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, layoutRes);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int id) {
            T t = (T) mViews.get(id);
            if (t == null) {
                t = (T) item.findViewById(id);
                mViews.put(id, t);
            }
            return t;
        }

        public View getItemView() {
            return item;
        }

        public int getItemPosition() {
            return position;
        }

        public ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }
    }








}
