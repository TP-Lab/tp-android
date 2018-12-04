package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.adapter.BaseRecycleAdapter;
import com.tokenbank.adapter.BaseRecyclerViewHolder;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.utils.TokenImageLoader;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

public class ChooseTokenTransferActivity extends BaseActivity implements BaseRecycleAdapter.OnDataLodingFinish{

    private final static String TAG = "ChooseTokenTransferActivity";

    private TitleBar mTitleBar;

    private RecyclerView mRecyclerViewChooseToken;
    private ChooseTokenAdapter mAdapter;

    private View mEmptyView;
    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_token_transfer);
        initView();
    }

    @Override
    public <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore) {
        if (!loadmore) {
            if (end) {
                if (mAdapter.getLength() <= 0) {
                    mRecyclerViewChooseToken.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerViewChooseToken.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    }


    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle("选择代币");

        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener(){
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);

        mRecyclerViewChooseToken = findViewById(R.id.recyclerview_choose_token_transfer);
        mAdapter = new ChooseTokenAdapter();
        mAdapter.setDataLoadingListener(this);
        mRecyclerViewChooseToken.setLayoutManager(new LinearLayoutManager(ChooseTokenTransferActivity.this));
        mRecyclerViewChooseToken.setAdapter(mAdapter);
        mRecyclerViewChooseToken.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });
        mAdapter.refresh();
    }

    public static void startChooseTokenTransferActivity(Context context, int from) {
        Intent intent = new Intent(context, ChooseTokenTransferActivity.class);
        intent.putExtra("From", from);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecyclerViewChooseToken.getChildAdapterPosition(
                    mRecyclerViewChooseToken.getChildAt(mRecyclerViewChooseToken.getChildCount() - 1));
            if (lastVisiblePosition >= mRecyclerViewChooseToken.getAdapter().getItemCount() - 1) {
                return mRecyclerViewChooseToken.getChildAt(mRecyclerViewChooseToken.getChildCount() - 1)
                        .getBottom() <= mRecyclerViewChooseToken.getBottom();
            }
        } catch (Throwable e) {
        }

        return false;
    }
    class ChooseTokenAdapter extends BaseRecycleAdapter<String, RecyclerView.ViewHolder> {

        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;

        private BaseRecyclerViewHolder.ItemClickListener mItemClickListener =
                new BaseRecyclerViewHolder.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        GsonUtil item = getItem(position);
                        Intent data = new Intent();
                        data.putExtra("Contact_Address", item.getString("contract", ""));
                        data.putExtra("Token_Symbol", item.getString("value", ""));
                        data.putExtra("TOKEN_DECIMAL", item.getInt("decimals", 0));
                        setResult(RESULT_OK, data);
                        ChooseTokenTransferActivity.this.finish();
                    }
                };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ViewUtil.inflatView(ChooseTokenTransferActivity.this, parent, R.layout.wallet_token_item_view, false);
            return new ChooseTokenAdapter.TokenViewHolder(view, mItemClickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GsonUtil itemData = getItem(position);
            fillTokenData((ChooseTokenAdapter.TokenViewHolder) holder, itemData);
        }

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
            int type = WalletInfoManager.getInstance().getWalletType();
            String address = WalletInfoManager.getInstance().getWAddress();
            GsonUtil json = TBController.getInstance().getWalletUtil(type).loadTransferTokens(ChooseTokenTransferActivity.this);
            handleTokenRequestResult(params, loadmore, json);
        }

        private void handleTokenRequestResult(final String params, final boolean loadmore, GsonUtil json) {
            TLog.d(TAG, "token list:" + json);
            GsonUtil tokens = json.getArray("data", "");
            if (!loadmore) {
                //第一页
                setData(tokens);
            } else {
                if (tokens.getLength() > 0) {
                    addData(tokens);
                }
            }
            mHasMore = false;
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillTokenData(ChooseTokenAdapter.TokenViewHolder holder, GsonUtil data) {
            TokenImageLoader.displayImage(data.getString("icon_url", ""), holder.mImgTokenIcon,
                    TokenImageLoader.imageOption(R.drawable.ic_images_common_loading, R.drawable.ic_images_asset_eth,
                            R.drawable.ic_images_asset_eth));
            holder.mTvTokenName.setText(data.getString("value", "ETH"));
        }


        class TokenViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgTokenIcon;
            TextView mTvTokenName;
            TextView mTvTokenCount;
            TextView mTvTokenAsset;

            public TokenViewHolder(View itemView, ItemClickListener onItemClickListener) {
                super(itemView, onItemClickListener);
                mImgTokenIcon = itemView.findViewById(R.id.token_icon);
                mTvTokenName = itemView.findViewById(R.id.token_name);
                mTvTokenCount = itemView.findViewById(R.id.token_count);
                mTvTokenAsset = itemView.findViewById(R.id.token_asset);
            }
        }
    }


}
