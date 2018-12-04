package com.tokenbank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;



public abstract class BaseListViewAdapter<T> extends BaseAdapter {

    protected List<T> mList;

    protected Context ctx;

    protected LayoutInflater mInflater;

    public BaseListViewAdapter(Context ctx) {
        this.ctx = ctx;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList == null ? 0 : position;
    }

    public void setList(List<T> list) {
        mList = new ArrayList<T>();
        this.mList = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return this.mList;
    }

    public void add(T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(t);
        notifyDataSetChanged();
    }

    public void add(int location, T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(location, t);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(int position, List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(position, list);
        notifyDataSetChanged();
    }

    public void addAllToFirst(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mList != null) {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeAll() {
        if (mList != null) {
            mList.removeAll(mList);
            notifyDataSetChanged();
        }
    }
}
