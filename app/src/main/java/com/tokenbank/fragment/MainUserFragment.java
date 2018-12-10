package com.tokenbank.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tokenbank.R;
import com.tokenbank.activity.AboutActivity;
import com.tokenbank.activity.ManageWalletActivity;
import com.tokenbank.activity.TransactionRecordActivity;
import com.tokenbank.activity.WebBrowserActivity;
import com.tokenbank.config.Constant;


public class MainUserFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout mLayoutManageWallet;
    private RelativeLayout mLayoutRecordTransaction;
    private RelativeLayout mLayoutNotifcation;
    private RelativeLayout mLayoutHelp;
    private RelativeLayout mLayoutAbout;

    public static MainUserFragment newInstance() {
        Bundle args = new Bundle();
        MainUserFragment fragment = new MainUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mainuser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    @Override
    public void onClick(View view) {
        if (view == mLayoutManageWallet) {
            ManageWalletActivity.startModifyWalletActivity(getActivity());
        } else if (view == mLayoutRecordTransaction) {
            TransactionRecordActivity.startTransactionRecordActivity(getActivity(), 2);
        } else if (view == mLayoutNotifcation) {
            TransactionRecordActivity.startTransactionRecordActivity(getActivity(), 1);
        } else if (view == mLayoutHelp) {
            WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_help_center), Constant.help_url);
        } else if (view == mLayoutAbout) {
            AboutActivity.startAboutActivity(getActivity());
        }
    }

    private void initView(View view) {

        mLayoutManageWallet = view.findViewById(R.id.layout_manage_wallet);
        mLayoutRecordTransaction = view.findViewById(R.id.layout_transaction_record);
        mLayoutNotifcation = view.findViewById(R.id.layout_notication);
        mLayoutHelp = view.findViewById(R.id.layout_help);
        mLayoutAbout = view.findViewById(R.id.layout_about);

        mLayoutManageWallet.setOnClickListener(this);
        mLayoutRecordTransaction.setOnClickListener(this);
        mLayoutNotifcation.setOnClickListener(this);
        mLayoutHelp.setOnClickListener(this);
        mLayoutAbout.setOnClickListener(this);
    }

}
