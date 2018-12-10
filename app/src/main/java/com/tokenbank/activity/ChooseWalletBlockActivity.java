package com.tokenbank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.adapter.BaseListViewAdapter;
import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.TokenImageLoader;
import com.tokenbank.view.TitleBar;


public class ChooseWalletBlockActivity extends BaseActivity {
    private static final int REQUEST_CODE = 1007;
    public final static String TAG = "ChooseWalletBlockActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_util);
        initView();
    }

    private void initView() {
        TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_select_block));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightDrawable(R.drawable.ic_walletutil_help);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }

            @Override
            public void onRightClick(View view) {
                ToastUtil.toast(ChooseWalletBlockActivity.this, "帮助");
            }
        });

        ListView listView = findViewById(R.id.listview);
        final WalletUtilAdapter adapter = new WalletUtilAdapter(this);
        listView.setAdapter(adapter);
        adapter.setList(BlockChainData.getInstance().getBlock());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 本地无钱包时，直接返回
                if (!WalletInfoManager.getInstance().hasWallet()) {
                    setResult(adapter.getList().get(position));
                    return;
                }

                int hid = (int) adapter.getList().get(position).hid;
                if (hid == -1) {
                    ToastUtil.toast(ChooseWalletBlockActivity.this, getString(R.string.toast_abnormal_data) + hid);
                    return;
                }

                boolean has = false;
//                List<WalletInfoManager.WData> wallets = WalletInfoManager.getInstance().getAllWallet();
//                for (WalletInfoManager.WData walletData : wallets) {
//                    if (walletData.type == hid) {
//                        ToastUtil.toast(ChooseWalletBlockActivity.this, "该体系已创建，请选择其他体系");
//                        has = true;
//                        break;
//                    }
//                }

                if (!has) {
                    setResult(adapter.getList().get(position));
                }
            }
        });
    }

    private void setResult(BlockChainData.Block block) {
        setResult(RESULT_OK, new Intent().putExtra(CreateWalletActivity.BLOCK, block));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra(CreateWalletActivity.BLOCK)) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void navToActivity(Context context, int requestCode) {
        Intent intent = new Intent(context, ChooseWalletBlockActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 货币体系列表
     */
    class WalletUtilAdapter extends BaseListViewAdapter<BlockChainData.Block> {

        public WalletUtilAdapter(Context ctx) {
            super(ctx);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.token_item_view, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            BlockChainData.Block block = mList.get(position);
            holder.name.setText(block.title
            );
            holder.des.setText(block.desc);
            TokenImageLoader.displayImage(block.symbol, holder.icon,
                    TokenImageLoader.imageOption(R.drawable.ic_images_common_loading, R.drawable.ic_images_asset_eth, R.drawable.ic_images_asset_eth));
            return view;
        }

        private class ViewHolder {
            public ViewHolder(View view) {
                this.icon = view.findViewById(R.id.token_icon);
                this.name = view.findViewById(R.id.token_name);
                this.des = view.findViewById(R.id.token_des);
            }

            ImageView icon;
            TextView name, des;
        }
    }
}
