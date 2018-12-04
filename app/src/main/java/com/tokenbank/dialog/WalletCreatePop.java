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
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.activity.CreateWalletActivity;
import com.tokenbank.activity.ImportWalletActivity;
import com.tokenbank.base.BlockChainData;



public class WalletCreatePop extends PopupWindow implements View.OnClickListener {

    private Context context;
    private BlockChainData.Block block;
    private TextView tips1, tips2;

    public WalletCreatePop(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_wallet_create, null);

        tips1 = view.findViewById(R.id.pop_wallet_token1);
        tips2 = view.findViewById(R.id.pop_wallet_token2);

        view.findViewById(R.id.wallet_create).setOnClickListener(this);
        view.findViewById(R.id.wallet_import).setOnClickListener(this);
        view.findViewById(R.id.pop_wallet_cancel).setOnClickListener(this);
        view.findViewById(R.id.pop_wallet_view).setOnClickListener(this);

        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        //设置动画
        setAnimationStyle(R.style.Pop_up_anim);
        //设置背景颜色
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));//不加背景低版本无法取消
        //设置可以获取焦点
        setFocusable(true);
        //设置可以触摸弹出框以外的区域
        setOutsideTouchable(true);
    }


    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.wallet_import) {
            ImportWalletActivity.startImportWalletActivity(context);
        } else if (view.getId() == R.id.wallet_create) {
            CreateWalletActivity.navToActivity(context, block, -1);
        }
    }

    public void setData(BlockChainData.Block block) {
        this.block = block;
        tips1.setText(String.format(tips1.getText().toString(), block.desc));
        tips2.setText(String.format(tips2.getText().toString(), block.desc, block.desc));
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
