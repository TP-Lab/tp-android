package com.tokenbank.base;


import java.util.ArrayList;
import java.util.List;



public class TBController {

    private final static String TAG = "TBController";

    public final static int ETH_INDEX = 1;
    public final static int SWT_INDEX = 2;

    private BaseWalletUtil mWalletUtil;

    private BaseWalletUtil mEthWalletUtil;
    private BaseWalletUtil mSwtWalletUtil;
    private TestWalletBlockchain mNullWalletUtil;

    private static TBController sInstance = new TBController();
    private  List<Integer> mSupportType = new ArrayList<>();

    private TBController() {

    }

    public static TBController getInstance() {
        return sInstance;
    }

    public void init() {
        mSupportType.add(ETH_INDEX);
        mSupportType.add(SWT_INDEX);

        mEthWalletUtil = new ETHWalletBlockchain();
        mEthWalletUtil.init();

        mSwtWalletUtil = new SWTWalletBlockchain();
        mSwtWalletUtil.init();

        mNullWalletUtil = new TestWalletBlockchain();
    }

    public BaseWalletUtil getWalletUtil(int type) {
        if (type == ETH_INDEX) {
            mWalletUtil = mEthWalletUtil;
        } else if(type == SWT_INDEX) {
            mWalletUtil = mSwtWalletUtil;
        } else {
            mWalletUtil = mNullWalletUtil;// do nothing
        }
        return mWalletUtil;
    }

    public List<Integer> getSupportType() {
        return mSupportType;
    }

}
