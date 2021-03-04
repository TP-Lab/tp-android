package com.tokenbank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;



import com.tokenbank.R;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.MsgDialog;
import com.tokenbank.web.WebActivity;

public class DappFragment extends BaseFragment {

    private EditText mEt_url;
    private TextView mTv_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dapp, container, false);
        mEt_url = view.findViewById(R.id.et_url);
        mEt_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                }
                return false;
            }
        });
        mTv_search = view.findViewById(R.id.tv_search);
        mTv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static DappFragment newInstance() {
        DappFragment dappFragment = new DappFragment();
        return dappFragment;
    }

    private void search() {
        String searchUrl = mEt_url.getText().toString().trim();
        if (!TextUtils.isEmpty(searchUrl)) {
            if (searchUrl.startsWith("http://") || searchUrl.startsWith("https://")) {
                if(searchUrl.equals("http://test")){
                    searchUrl = "file:///android_asset/DappTestPage.html";//测试
                }
                startActivity(new Intent(getActivity(), WebActivity.class)
                        .putExtra(Constant.LOAD_URL, searchUrl));
            } else {
                new MsgDialog(getContext(), "err").show();
            }
        }
    }
}


