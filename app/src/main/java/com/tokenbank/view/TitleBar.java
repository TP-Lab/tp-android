package com.tokenbank.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.utils.ViewUtil;


public class TitleBar extends LinearLayout implements View.OnClickListener {
    private TextView mTvLeft;
    private TextView mTvTitle;
    private TextView mTvRight;

    private ImageView mImgLeft;
    private ImageView mImgTitle;
    private ImageView mImgRight;

    private FrameLayout mLayoutLeft;
    private FrameLayout mLayoutTitle;
    private FrameLayout mLayoutRight;

    private View mViewSplit;

    private TitleBarClickListener mTitleBarClickListener;

    public interface TitleBarClickListener {
        void onLeftClick(View view);

        void onRightClick(View view);

        void onMiddleClick(View view);
    }

    public static class TitleBarListener implements TitleBarClickListener {

        @Override
        public void onLeftClick(View view) {
        }

        @Override
        public void onRightClick(View view) {
        }

        @Override
        public void onMiddleClick(View view) {
        }
    }

    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = ViewUtil.inflatView(getContext(), this, R.layout.layout_view_titlebar, true);
        mTvLeft = view.findViewById(R.id.tv_left);
        mLayoutLeft = view.findViewById(R.id.layout_left);
        mImgLeft = view.findViewById(R.id.img_left);
        mLayoutLeft.setOnClickListener(this);

        mTvTitle = view.findViewById(R.id.tv_title);
        mLayoutTitle = view.findViewById(R.id.layout_title);
        mImgTitle = view.findViewById(R.id.img_title);
        mLayoutTitle.setOnClickListener(this);

        mTvRight = view.findViewById(R.id.tv_right);
        mLayoutRight = view.findViewById(R.id.layout_right);
        mImgRight = view.findViewById(R.id.img_right);
        mLayoutRight.setOnClickListener(this);

        mViewSplit = view.findViewById(R.id.view_split);
    }

    @Override
    public void onClick(View view) {
        if (mTitleBarClickListener == null) {
            return;
        }
        if (view == mLayoutLeft) {
            mTitleBarClickListener.onLeftClick(view);
        } else if (view == mLayoutTitle) {
            mTitleBarClickListener.onMiddleClick(view);
        } else if (view == mLayoutRight) {
            mTitleBarClickListener.onRightClick(view);
        }

    }

    public void setLeftDrawable(int resId) {
        setLeftVisble(View.VISIBLE);
        mTvLeft.setVisibility(View.GONE);
        mImgLeft.setVisibility(View.VISIBLE);
        mImgLeft.setImageResource(resId);
    }

    public void setLeftText(String text) {
        setLeftVisble(View.VISIBLE);

        if (TextUtils.isEmpty(text)) {
            return;
        }
        mTvLeft.setVisibility(View.VISIBLE);
        mTvLeft.setText(text);
        mImgLeft.setVisibility(View.GONE);
    }

    public void setLeftText(int id) {
        String text = getResources().getString(id);
        setLeftText(text);
    }

    public void setLeftTextColor(int colorId) {
        mTvLeft.setTextColor(getResources().getColor(colorId));
    }

    public void setTitle(String text) {
        setTitleVisible(View.VISIBLE);
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(text);
        mImgTitle.setVisibility(View.GONE);
    }

    public void setTitle(int id) {
        String text = getResources().getString(id);
        setTitle(text);
    }

    public void setTitleDrawable(int resId) {
        setTitleVisible(View.VISIBLE);

        mTvTitle.setVisibility(View.GONE);
        mImgTitle.setVisibility(View.VISIBLE);
        mImgTitle.setImageResource(resId);
    }

    public void setTitleTextColor(int colorId) {
        mTvTitle.setTextColor(getResources().getColor(colorId));
    }


    public void setRightText(String text) {
        setRighttVisble(View.VISIBLE);
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText(text);
        mImgRight.setVisibility(View.GONE);

    }

    public void setRightText(int id) {
        String text = getResources().getString(id);
        setRightText(text);
    }

    public void setRightDrawable(int resId) {
        setRighttVisble(View.VISIBLE);

        mTvRight.setVisibility(View.GONE);
        mImgRight.setVisibility(View.VISIBLE);
        mImgRight.setImageResource(resId);
    }

    public void setRightTextColor(int colorId) {
        mTvRight.setTextColor(getResources().getColor(colorId));
    }


    public void setTitleBarClickListener(TitleBarClickListener listener) {
        mTitleBarClickListener = listener;
    }

    private void setLeftVisble(int visible) {
        mLayoutLeft.setVisibility(visible);
    }

    private void setRighttVisble(int visible) {
        mLayoutRight.setVisibility(visible);
    }

    private void setTitleVisible(int visible) {
        mLayoutTitle.setVisibility(visible);
    }

    public void setSplitVisible(int visible) {
        mViewSplit.setVisibility(visible);
    }
}
