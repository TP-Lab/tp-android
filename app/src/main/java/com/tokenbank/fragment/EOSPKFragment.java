package com.tokenbank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.activity.MainActivity;
import com.tokenbank.activity.WebBrowserActivity;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.ViewUtil;

import java.util.List;


public class EOSPKFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = "PKFragment";
    private EditText mEdtWalletPrivateKey;
    private RelativeLayout mLayoutSelectBlockChain;
    private TextView mTvBlockChain;
    private EditText mEdtWalletPwd;
    private EditText mEdtWalletPwdRepeat;
    private EditText mEdtWalletPwdTips;
    private ImageView mImgboxTerms;
    private TextView mTvTerms;
    private TextView mTvImportWallet;
    private TextView mTvAboutPrivateKey;
    private BaseWalletUtil walletblockchain;
    public static final String BLOCK = "Block";
    private BlockChainData.Block mBlock;

    private int flag = 1;
    private final static String FLAG = "Flag";

    public static EOSPKFragment newInstance(int flag, BlockChainData.Block block) {

        Bundle args = new Bundle();

        EOSPKFragment fragment = new EOSPKFragment();
        args.putInt(FLAG, flag);
        args.putParcelable(BLOCK, block);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBlock = getArguments().getParcelable(BLOCK);
        }
        if (mBlock == null) {
            getActivity().finish();
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return ViewUtil.inflatView(inflater, container, R.layout.eos_fragment_pk_importwallet, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onClick(View view) {
        if (view == mTvTerms) {
            gotoServiceTermPage();
        } else if (view == mTvImportWallet) {
            checkPrivateKey();
            if (paramCheck()) {
                importWallet();
            }
        } else if (view == mTvAboutPrivateKey) {
            gotoPrivateKeyIntroPage();
        } else if (view == mImgboxTerms) {
            mImgboxTerms.setSelected(!mImgboxTerms.isSelected());
        }
    }

    private void initView(View view) {

        mEdtWalletPrivateKey = view.findViewById(R.id.edt_wallet_privatekey);

        mLayoutSelectBlockChain = view.findViewById(R.id.layout_block_chain);
        mLayoutSelectBlockChain.setOnClickListener(this);
        mTvBlockChain = view.findViewById(R.id.tv_block_chain);

        mEdtWalletPwd = view.findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwdRepeat = view.findViewById(R.id.edt_wallet_pwd_repeat);
        mEdtWalletPwdTips = view.findViewById(R.id.edt_pwd_tips);


        mImgboxTerms = view.findViewById(R.id.img_service_terms);
        mImgboxTerms.setOnClickListener(this);

        mTvTerms = view.findViewById(R.id.tv_service_terms);
        mTvTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvTerms.setOnClickListener(this);

        mTvImportWallet = view.findViewById(R.id.tv_import_wallet);
        mTvImportWallet.setOnClickListener(this);

        mTvAboutPrivateKey = view.findViewById(R.id.tv_about_privatekey);
        mTvAboutPrivateKey.setOnClickListener(this);

        mTvBlockChain.setText(mBlock.desc);
        walletblockchain = TBController.getInstance().getWalletUtil((int) (mBlock.hid));
    }

    private void checkPrivateKey() {
        if (mBlock.hid == TBController.EOS_INDEX) {
            String privateKey = mEdtWalletPrivateKey.getText().toString();
            if (!TextUtils.isEmpty(privateKey)) {
                mEdtWalletPrivateKey.setText(privateKey);
            }
        }
    }

    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_user_agreement), Constant.service_term_url);
    }

    private boolean paramCheck() {
        String walletPrivateKey = mEdtWalletPrivateKey.getText().toString();
        String walletPwd = mEdtWalletPwd.getText().toString();
        String walletPwdRepeat = mEdtWalletPwdRepeat.getText().toString();
        boolean readedTerms = mImgboxTerms.isSelected();
        if (TextUtils.isEmpty(walletPrivateKey)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_title_no_private_key), "OK");
            return false;
        }

        if (!walletblockchain.checkWalletPk(walletPrivateKey)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_title_key_format_incorrect), "OK");
            return false;
        }
        if (TextUtils.isEmpty(walletPwd)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_no_password), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletPwdRepeat)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_no_verify_password), "OK");
            return false;
        }

        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_passwords_unmatch), "OK");
            return false;
        }
        if (walletPwd.length() < 8) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_short_password), "OK");
            return false;
        }
        if (!readedTerms) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_no_read_service), "OK");
            return false;
        }
        return true;
    }

    private void importWallet() {
        final String privateKey = mEdtWalletPrivateKey.getText().toString();
        final String password = mEdtWalletPwd.getText().toString();
        walletblockchain.importWallet(privateKey, 2, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    String address = extra.getString("address", "");
                    if (isWalletExsit(address)) {
                        if (flag == 1) {
                            //导入钱包
                            ToastUtil.toast(getActivity(), getString(R.string.toast_wallet_exists));
                            return;
                        } else if (flag == 2) {
                            //重置密码
                            WalletInfoManager.getInstance().updateWalletHash(address, FileUtil.getStringContent(password));
                            return;
                        }

                    }
                    uploadWallet(address, (int) mBlock.hid, FileUtil.getStringContent(password),
                            privateKey, address);
                } else {
                    ToastUtil.toast(getActivity(), getString(R.string.toast_import_wallet_failed));
                }
            }
        });
    }

    private void uploadWallet(final String name, final int walletType, final String hash, final String privateKey,
                              final String address) {
        long walletId = System.currentTimeMillis();
        storeWallet(walletId, walletType, name, address, hash, privateKey);
        gotoMainActivity();
    }

    private void gotoMainActivity() {
        // 添加资产时，进入创建钱包
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void storeWallet(long walletId, int walletType, String walletName, String address, String walletHash, String privatekey) {
        WalletInfoManager.WData wallet = new WalletInfoManager.WData();
        wallet.wid = walletId;
        wallet.wname = walletName;
        wallet.waddress = address;
        wallet.whash = walletHash;
        wallet.wpk = privatekey;
        wallet.type = walletType;
        wallet.words = "";
        wallet.isBaked = true;
        WalletInfoManager.getInstance().insertWallet(wallet);
    }

    private void gotoPrivateKeyIntroPage() {
        WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_private_key), Constant.privatekey_intro_url);
    }

    private boolean isWalletExsit(String address) {
        List<WalletInfoManager.WData> allWallet = WalletInfoManager.getInstance().getAllWallet();
        if (allWallet == null || allWallet.size() <= 0) {
            return false;
        }
        for (WalletInfoManager.WData walletData : allWallet) {
            if (TextUtils.equals(walletData.waddress, address)) {
                return true;
            }
        }
        return false;
    }
}
