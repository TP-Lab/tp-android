package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.adapter.BaseRecycleAdapter;
import com.tokenbank.adapter.BaseRecyclerViewHolder;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.TBController;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.utils.Util;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;


public class TokenDetailsActivity extends BaseActivity implements BaseRecycleAdapter.OnDataLodingFinish, View.OnClickListener {

    private static final String TAG = "TokenDetailsActivity";
    private static final String TOKEN = "Token";
    private static final String UNIT_KEY = "Unit_Key";

    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;
    private TokenDetailsActivity.RecyclerViewAdapter mAdapter;
    private View mEmptyView;

    private LinearLayout mLayoutTranster;
    private LinearLayout mLayoutReceive;

    private GsonUtil mItem;
    private String mContractAddress;
    private WalletInfoManager.WData mWalletData;
    private long mBlockChainId;
    private BaseWalletUtil mWalletUtil;
    private TextView mBrowser;
    private String mUnit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_details_activity);
        initData();
        initView();
    }

    @Override
    public void onClick(View v) {
        if (v == mLayoutTranster) {
            if (WalletInfoManager.getInstance().getWalletType() == TBController.EOS_INDEX) {
                TLog.d(TAG, "getWalletType = " + WalletInfoManager.getInstance().getWalletType());
                EosTokenTransferActivity.startTokenTransferActivity(TokenDetailsActivity.this, "", "eosio.token", 0,
                        mWalletUtil.getDefaultTokenSymbol(), mWalletUtil.getDefaultDecimal());
            } else {
                TokenTransferActivity.startTokenTransferActivity(TokenDetailsActivity.this, "",
                        mContractAddress, 0.0f, mItem.getString("bl_symbol", ""), mItem.getInt("decimal", 18), 0);
            }
        } else if (v == mLayoutReceive) {
            TokenReceiveActivity.startTokenReceiveActivity(TokenDetailsActivity.this, mItem.getString("bl_symbol", ""));
        } else if (v == mBrowser) {
            if (mWalletData.type == TBController.MOAC_INDEX) {
                WebBrowserActivity.startWebBrowserActivity(TokenDetailsActivity.this, getString(R.string.moac_browser), Constant.MOAC_BROWSER + mWalletData.waddress);
            } else if (mWalletData.type == TBController.EOS_INDEX) {
                WebBrowserActivity.startWebBrowserActivity(TokenDetailsActivity.this, getString(R.string.eos_browser), Constant.EOS_BROWSER + mWalletData.waddress);
            }
        }
    }

    private void initData() {
        if (getIntent() != null) {
            mItem = new GsonUtil(getIntent().getStringExtra(TOKEN));
            mBlockChainId = mItem.getLong("blockchain_id", 0l);
        }
        if (mBlockChainId <= 0l || mItem == null) {
            this.finish();
            return;
        }
        mWalletData = WalletInfoManager.getInstance().getCurrentWallet();
        if (mWalletData == null) {
            this.finish();
            return;
        }
        mWalletUtil = TBController.getInstance().getWalletUtil((int) mBlockChainId);
        if (mWalletUtil == null) {
            this.finish();
            return;
        }
        mContractAddress = mItem.getString("address", "");
    }

    @Override
    public <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore) {
        if (!loadmore) {
            if (end) {
                if (mAdapter.getLength() <= 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    public static void NavToActivity(Context context, String item, String unit) {
        Intent intent = new Intent(context, TokenDetailsActivity.class);
        intent.putExtra(TOKEN, item);
        intent.putExtra(UNIT_KEY, unit);
        context.startActivity(intent);
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                TokenDetailsActivity.this.finish();
            }
        });

        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);

        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new RecyclerViewAdapter();
        mAdapter.setDataLoadingListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(TokenDetailsActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });

        mLayoutTranster = findViewById(R.id.wallet_action_transfer);
        mLayoutTranster.setOnClickListener(this);
        mBrowser = findViewById(R.id.go_browser);
        if (mWalletData.type == TBController.MOAC_INDEX) {
            mBrowser.setText(getString(R.string.moac_browser));
        } else if (mWalletData.type == TBController.EOS_INDEX) {
            mBrowser.setText(getString(R.string.eos_browser));
        } else {
            mBrowser.setVisibility(View.GONE);
        }
        mBrowser.setOnClickListener(this);
        mLayoutReceive = findViewById(R.id.wallet_action_receive);
        mLayoutReceive.setOnClickListener(this);

        mTitleBar.setTitle(mItem.getString("bl_symbol", ""));

        TextView tvBalance = findViewById(R.id.token_balance);
        TextView tvAsset = findViewById(R.id.token_asset);
        mUnit = getIntent().getStringExtra(UNIT_KEY);
        if (TextUtils.isEmpty(mUnit)) {
            mUnit = "$";
        }
        tvBalance.setText("" + mWalletUtil.getValue(mItem.getInt("decimal", 0), Util.parseDouble(mItem.getString("balance", "0"))));
        tvAsset.setText(String.format("≈ %1s %2s", mUnit, Util.formatDoubleToStr(2, Util.strToDouble(
                mItem.getString("asset", "0")))));

        mAdapter.refresh();
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecyclerView.getChildAdapterPosition(
                    mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
            if (lastVisiblePosition >= mRecyclerView.getAdapter().getItemCount() - 1) {
                return mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1)
                        .getBottom() <= mRecyclerView.getBottom();
            }
        } catch (Throwable e) {
        }

        return false;
    }

    class RecyclerViewAdapter extends BaseRecycleAdapter<String, RecyclerViewAdapter.ViewHolder> {

        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;

        private BaseRecyclerViewHolder.ItemClickListener mItemClickListener = new BaseRecyclerViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GsonUtil item = getItem(position);
                gotoTransactionDetail(item);
            }
        };

        @Override
        public void loadData(final String params, final boolean loadmore) {
            if (!loadmore) {
                mPageIndex = 0;
            } else {
                mPageIndex++;
            }
            if (loadmore && !mHasMore) {
                return;
            }

            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, false, loadmore);
            }

            GsonUtil requestParams = new GsonUtil("{}");
            requestParams.putInt("start", mPageIndex * PAGE_SIZE);
            requestParams.putInt("pagesize", PAGE_SIZE);
            if (!TextUtils.isEmpty(mContractAddress)) {
                requestParams.putString("contract_address", mContractAddress);
            }
            requestParams.putString("token", mItem.getString("bl_symbol", ""));

            mWalletUtil.queryTransactionList(requestParams, new WCallback() {
                @Override
                public void onGetWResult(int ret, GsonUtil extra) {
                    if (ret == 0) {
                        handleTransactioRecordResult(params, loadmore, extra);
                    }
                }
            });
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewAdapter.ViewHolder holder = new RecyclerViewAdapter.ViewHolder(ViewUtil.inflatView(parent.getContext(),
                    parent, R.layout.layout_item_transaction, false), mItemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int position) {
            fillData(viewHolder, getItem(position));
        }

        private void handleTransactioRecordResult(final String params, final boolean loadmore, GsonUtil json) {
            TLog.d(TAG, "transaction list:" + json);
            GsonUtil transactionRecord = json.getArray("data", "[]");
            if (!loadmore) {
                //第一页
                setData(transactionRecord);
            } else {
                if (transactionRecord.getLength() > 0) {
                    addData(transactionRecord);
                }
            }

            if (transactionRecord.getLength() < PAGE_SIZE) {
                //最后一页了
                mHasMore = false;
            } else {
                mHasMore = true;
            }


            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillData(final ViewHolder holder, final GsonUtil item) {
            if (item == null || TextUtils.equals(item.toString(), "{}")) {
                return;
            }

//            int type = item.getInt("type", 0);
            double value = item.getDouble("real_value", 0.0f);
            String toAddress = item.getString("to", "");
//            if (type == 1) {
//                //代币
//                value = item.getString("token_value", "0.0");
//                toAddress = item.getString("addr_token", "");
//            } else {
//                value = item.getString("value", "0.0");
//                toAddress = item.getString("to", "");
//            }

            String fromAddress = item.getString("from", "");
            String currentAddress = WalletInfoManager.getInstance().getWAddress().toLowerCase();
            boolean in = false;
            holder.mTvTransactionTime.setText(Util.formatTime(item.getLong("timeStamp", 0l)));
            String label = "";
            if (TextUtils.equals(currentAddress, fromAddress)) {
                label = "-";
                in = false;
            }
            if (TextUtils.equals(currentAddress, toAddress)) {
                label = "+";
                in = true;
            }
            if (in) {
                holder.mImgIcon.setImageResource(R.drawable.ic_transaction_in);
                holder.mTvTransactionAddress.setText(fromAddress);
                holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_blue));
            } else {
                holder.mTvTransactionAddress.setText(toAddress);
                holder.mImgIcon.setImageResource(R.drawable.ic_transaction_out);
                holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_red));
            }

//            holder.mTvTransactionCount.setText(label + Util.formatDouble(5, Util.fromWei(1, value)) + item.getString("symbol", ""));
            holder.mTvTransactionCount.setText(label + value + item.getString("tokenSymbol", ""));
        }

        private void gotoTransactionDetail(GsonUtil json) {
            TransactionDetailsActivity.startTransactionDetailActivity(TokenDetailsActivity.this, json);
        }

        class ViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            TextView mTvTransactionTime;
            TextView mTvTransactionCount;

            public ViewHolder(View itemView, ItemClickListener itemClickListener) {
                super(itemView, itemClickListener);
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTransactionAddress = itemView.findViewById(R.id.tv_transaction_address);
                mTvTransactionTime = itemView.findViewById(R.id.tv_transaction_time);
                mTvTransactionCount = itemView.findViewById(R.id.tv_transaction_count);
            }
        }
    }
}
