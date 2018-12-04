package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.adapter.BaseRecycleAdapter;
import com.tokenbank.adapter.BaseRecyclerViewHolder;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.TBController;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.utils.Util;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;


public class TransactionRecordActivity extends BaseActivity implements BaseRecycleAdapter.OnDataLodingFinish,
        TitleBar.TitleBarClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "TransactionRecordActivity";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TitleBar mTitleBar;

    private RecyclerView mRecyclerViewTransactionRecord;
    private TransactionRecordAdapter mAdapter;
    private BaseWalletUtil mWalletUtil;

    private View mEmptyView;
    private int mFrom = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);
        if (getIntent() != null) {
            mFrom = getIntent().getIntExtra("From", 2);
        }

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int blockId = WalletInfoManager.getInstance().getWalletType();
        mWalletUtil = TBController.getInstance().getWalletUtil(blockId);
        if (mWalletUtil == null) {
            this.finish();
            return;
        }
        if (mAdapter != null) {
            mAdapter.refresh();
            mSwipeRefreshLayout.setRefreshing(true);
        }
        if (mFrom == 1) {
            mTitleBar.setTitle("消息中心");

        } else {
            mTitleBar.setTitle(WalletInfoManager.getInstance().getWname());
        }

    }

    @Override
    public <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore) {
        if (!loadmore) {
            if (end) {
                if (mAdapter.getLength() <= 0) {
                    mRecyclerViewTransactionRecord.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerViewTransactionRecord.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onLeftClick(View view) {
        this.finish();
    }

    @Override
    public void onRightClick(View view) {
        ChangeWalletActivity.startChangeWalletActivity(this);
    }

    @Override
    public void onMiddleClick(View view) {
    }

    private void initView() {

        mSwipeRefreshLayout = findViewById(R.id.root_view);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.white);
        if (mFrom == 2) {
            mTitleBar.setRightDrawable(R.drawable.ic_changewallet);
        }
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitleBarClickListener(this);

        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);

        mRecyclerViewTransactionRecord = findViewById(R.id.recyclerview_transaction_record);
        mAdapter = new TransactionRecordAdapter();
        mAdapter.setDataLoadingListener(this);
        mRecyclerViewTransactionRecord.setLayoutManager(new LinearLayoutManager(TransactionRecordActivity.this));
        mRecyclerViewTransactionRecord.setAdapter(mAdapter);
        mRecyclerViewTransactionRecord.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });
    }

    public static void startTransactionRecordActivity(Context context, int from) {
        Intent intent = new Intent(context, TransactionRecordActivity.class);
        intent.putExtra("From", from);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mAdapter.refresh();
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecyclerViewTransactionRecord.getChildAdapterPosition(
                    mRecyclerViewTransactionRecord.getChildAt(mRecyclerViewTransactionRecord.getChildCount() - 1));
            if (lastVisiblePosition >= mRecyclerViewTransactionRecord.getAdapter().getItemCount() - 1) {
                return mRecyclerViewTransactionRecord.getChildAt(mRecyclerViewTransactionRecord.getChildCount() - 1)
                        .getBottom() <= mRecyclerViewTransactionRecord.getBottom();
            }
        } catch (Throwable e) {
        }

        return false;
    }

    class TransactionRecordAdapter extends BaseRecycleAdapter<String, TransactionRecordAdapter.TransactionRecordViewHolder> {

        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;
        private String mMarker; //jt 需要

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
                mMarker = "";
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
            requestParams.putInt("start", mPageIndex);
            requestParams.putInt("pagesize", PAGE_SIZE);
            requestParams.putString("marker", mMarker);

            mWalletUtil.queryTransactionList(requestParams, new WCallback() {
                @Override
                public void onGetWResult(int ret, GsonUtil extra) {
                    if (ret == 0) {
                        handleTransactioRecordResult(params, loadmore, extra);
                        mMarker = extra.getString("marker", "");
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public TransactionRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TransactionRecordViewHolder holder = new TransactionRecordViewHolder(ViewUtil.inflatView(parent.getContext(),
                    parent, R.layout.layout_item_transaction, false), mItemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(TransactionRecordViewHolder transactionRecordViewHolder, int position) {
            TLog.e(TAG, "itemcount:" + getItemCount());
            fillData(transactionRecordViewHolder, getItem(position));
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

        private void fillData(final TransactionRecordViewHolder holder, final GsonUtil item) {
            if (item == null || TextUtils.equals(item.toString(), "{}")) {
                return;
            }

            double value = item.getDouble("real_value", 0.0f);
            String toAddress = item.getString("to", "");

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

            holder.mTvTransactionCount.setText(label + value + item.getString("tokenSymbol", ""));
        }

        private void gotoTransactionDetail(GsonUtil json) {
            TransactionDetailsActivity.startTransactionDetailActivity(TransactionRecordActivity.this, json);
        }

        class TransactionRecordViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            TextView mTvTransactionTime;
            TextView mTvTransactionCount;

            public TransactionRecordViewHolder(View itemView, ItemClickListener itemClickListener) {
                super(itemView, itemClickListener);
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTransactionAddress = itemView.findViewById(R.id.tv_transaction_address);
                mTvTransactionTime = itemView.findViewById(R.id.tv_transaction_time);
                mTvTransactionCount = itemView.findViewById(R.id.tv_transaction_count);
            }
        }
    }

}
