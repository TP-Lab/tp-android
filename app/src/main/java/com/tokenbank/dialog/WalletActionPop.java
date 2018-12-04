package com.tokenbank.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.tokenbank.R;
import com.tokenbank.activity.CreateWalletActivity;


public class WalletActionPop extends PopupWindow implements View.OnClickListener {


    public interface ScanClickListener {
        void onScanClick();
    }

    private Context context;
    private ScanClickListener mScanClickListener;

    public WalletActionPop(Context context, ScanClickListener scanClickListener) {
        super(context);
        this.context = context;
        this.mScanClickListener = scanClickListener;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_action_view, null);
        view.findViewById(R.id.pop_item_scan).setOnClickListener(this);
        view.findViewById(R.id.create_wallet).setOnClickListener(this);
        setContentView(view);
        setWidth(context.getResources().getDimensionPixelSize(R.dimen.dimen_wallet_action_width));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置动画
        setAnimationStyle(R.style.Pop_up_anim);
        //设置背景颜色
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));//不加背景低版本无法取消
//        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.shape_dialog_bg));
        //设置可以获取焦点
        setFocusable(true);
        //设置可以触摸弹出框以外的区域
        setOutsideTouchable(true);
    }


    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.pop_item_scan) {
            if(mScanClickListener != null) {
                mScanClickListener.onScanClick();
            }
        }  else if (view.getId() == R.id.create_wallet) {
            CreateWalletActivity.navToActivity(context, -1);
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }
}
