package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

import java.util.List;


public class ChangeWalletActivity extends BaseActivity {

    private TitleBar mTitleBar;
    private ListView mLsAccount;
    private WalletAdapter mAdapter;
    private List<WalletInfoManager.WData> mAllWallet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeaccount);
        mAllWallet = WalletInfoManager.getInstance().getAllWallet();
        if (mAllWallet == null && mAllWallet.size() == 0) {
            this.finish();
            return;
        }
        init();
    }

    public static void startChangeWalletActivity(Context context) {
        Intent intent = new Intent(context, ChangeWalletActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void init() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_switch_account));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                ChangeWalletActivity.this.finish();
            }
        });

        mLsAccount = findViewById(R.id.ls_account);
        mAdapter = new WalletAdapter();
        mLsAccount.setAdapter(mAdapter);

        mLsAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WalletInfoManager.WData walletData = mAllWallet.get(position);
                if (!walletData.isBaked) {
                    ViewUtil.showBakupDialog(ChangeWalletActivity.this, walletData, true, true, walletData.whash);
                } else {
                    WalletInfoManager.getInstance().setCurrentWallet(mAllWallet.get(position));
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    class WalletAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAllWallet == null ? 0 : mAllWallet.size();
        }

        @Override
        public Object getItem(int position) {
            return mAllWallet == null ? 0 : mAllWallet.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_account, false);
                holder = new ViewHolder();
                holder.mTvWalletName = convertView.findViewById(R.id.tv_wallet_name);
                holder.mImgSelectedIcon = convertView.findViewById(R.id.img_selected_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            bindItemData(holder, position);

            return convertView;
        }

        private void bindItemData(ViewHolder holder, int position) {
            WalletInfoManager.WData wallet = mAllWallet.get(position);
            holder.mTvWalletName.setText(wallet.wname);
            if (wallet.equals(WalletInfoManager.getInstance().getCurrentWallet())) {
                holder.mImgSelectedIcon.setVisibility(View.VISIBLE);
            } else {
                holder.mImgSelectedIcon.setVisibility(View.GONE);
            }
        }

        class ViewHolder {
            TextView mTvWalletName;
            ImageView mImgSelectedIcon;
        }
    }
}
