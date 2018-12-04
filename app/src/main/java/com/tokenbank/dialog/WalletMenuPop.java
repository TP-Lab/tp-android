package com.tokenbank.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.activity.CreateWalletActivity;
import com.tokenbank.adapter.BaseListViewAdapter;
import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;


public class WalletMenuPop extends PopupWindow implements View.OnClickListener {

    private Context context;
    private ListView listView;
    private WalletAdapter mAdapter;
    private List<WalletInfoManager.WData> wallets = new ArrayList<>();

    public WalletMenuPop(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_wallet_view, null);
        listView = view.findViewById(R.id.listview);
        mAdapter = new WalletAdapter(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                WalletInfoManager.WData walletData = wallets.get(i);
                if (walletData.isBaked) {
                    WalletInfoManager.getInstance().setCurrentWallet(wallets.get(i));
                    dismiss();
                } else {
                    ViewUtil.showBakupDialog(context, wallets.get(i), true, true, wallets.get(i).whash);
                }
            }
        });
        listView.setAdapter(mAdapter);
        view.findViewById(R.id.create_wallet).setOnClickListener(this);
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

    public void setData() {
        wallets.clear();
        List<WalletInfoManager.WData> walletList = WalletInfoManager.getInstance().getAllWallet();
        if (walletList == null || walletList.isEmpty()) {
            return;
        }
        int count = walletList.size();
        if (count == 1) {
            mAdapter.setIndex(0);
        } else {
            WalletInfoManager.WData currentWallet = WalletInfoManager.getInstance().getCurrentWallet();
            if (currentWallet != null) {
                for (int i = 0; i < count; i++) {
                    if (walletList.get(i).wid == currentWallet.wid) {
                        mAdapter.setIndex(i);
                        break;
                    }
                }
            }
        }
        wallets.addAll(walletList);
        mAdapter.setList(wallets);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.create_wallet) {
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

    /**
     * 钱包菜单列表
     */
    class WalletAdapter extends BaseListViewAdapter<WalletInfoManager.WData> {

        private int index = 0;
        private Context context;

        public WalletAdapter(Context ctx) {
            super(ctx);
            this.context = ctx;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.pop_wallet_item_view, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            BlockChainData.Block block = BlockChainData.getInstance().getBolckByHid(mList.get(position).type);
            if (block != null) {
                holder.name.setText(mList.get(position).wname +
                        "(" + block.desc + ")");
            } else {
                holder.name.setText("");
            }

            if (index == position) {
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.color_theme));
            } else {
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.color_text_3));
            }
            return view;
        }

        private class ViewHolder {
            public ViewHolder(View view) {
                this.name = view.findViewById(R.id.wallet_item_name);
            }

            TextView name;
        }
    }
}
