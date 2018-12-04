package com.tokenbank.adapter;

import android.support.v7.widget.RecyclerView;

import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;



public abstract class BaseRecycleAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    private final static String TAG = "BaseRecycleAdapter";

    public interface OnDataLodingFinish{
        <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore);
    }

    protected OnDataLodingFinish mDataLoadingListener;

    private GsonUtil mData = new GsonUtil("[]");

    @Override
    public int getItemCount() {
        TLog.e(TAG, "getLength" + getLength());
        return getLength();
    }

    public void setData(GsonUtil data) {
        if(data != null && data.isValid() && data.isArray()) {
            mData = data;
        }else{
            mData = null;
        }
        notifyDataSetChanged();
    }

    public void addData(GsonUtil data) {
        if(data != null && data.isValid() && data.isArray() && data.getLength() > 0) {
            mData = mData.add(data);
            notifyDataSetChanged();
        }
    }

    public void refresh() {
        loadData(null, false);
    }

    public void loadmore(T parmas) {
        loadData(parmas, true);
    }

    public void setDataLoadingListener(OnDataLodingFinish dataLoadingListener) {
        mDataLoadingListener = dataLoadingListener;
    }

    public abstract void loadData(T params, boolean loadmore);

    public int getLength() {
        if(mData == null) {
            return 0;
        }
        return mData.getLength();
    }

    public GsonUtil getItem(int postion) {
        return mData.getObject(postion, "{}");
    }
}
