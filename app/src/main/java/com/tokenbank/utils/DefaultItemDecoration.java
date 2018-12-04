package com.tokenbank.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;



public class DefaultItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public DefaultItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = space;
    }

}
