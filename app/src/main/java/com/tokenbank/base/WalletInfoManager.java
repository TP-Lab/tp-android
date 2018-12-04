package com.tokenbank.base;

import android.content.Context;
import android.text.TextUtils;

import com.tokenbank.activity.SplashActivity;
import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class WalletInfoManager {

    private static WalletInfoManager instance = new WalletInfoManager();
    private WData mCurrentWallet;

    private WalletInfoManager() {

    }

    public static WalletInfoManager getInstance() {
        return instance;
    }

    public void init() {
        WData wallet = new WData();
        if (validWalletData(Constant.wallet_def_file)) {
            wallet.wid = FileUtil.getLongFromSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wid);
            wallet.type = FileUtil.getIntFromSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wtype);
            wallet.wname = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wname);
            wallet.waddress = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.waddress);
            wallet.whash = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.whash);
            wallet.wpk = FileUtil.getStringFromSp(AppConfig.getContext(),
                    Constant.wallet_def_file, Constant.wpk);
            wallet.words = FileUtil.getStringFromSp(AppConfig.getContext(),
                    Constant.wallet_def_file, Constant.words);
            wallet.isBaked = FileUtil.getBooleanFromSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.baked);
            mCurrentWallet = wallet;
        } else {
            FileUtil.deleteFile(FileUtil.getSharedPrefDir(AppConfig.getContext()) + Constant.wallet_def_file + ".xml");
            File spFileDir = new File(FileUtil.getSharedPrefDir(AppConfig.getContext()));
            if (!spFileDir.exists()) {
                return;
            } else {
                if (spFileDir.isDirectory()) {
                    File[] spFiles = spFileDir.listFiles();
                    if (spFiles != null && spFiles.length > 0) {
                        for (File spFile : spFiles) {
                            String spFileName = spFile.getName().substring(0, spFile.getName().lastIndexOf("."));
                            if (validWalletData(spFileName)) {
                                WData dwallet = new WData();
                                dwallet.wid = FileUtil.getLongFromSp(AppConfig.getContext(), spFileName, Constant.wid);
                                dwallet.type = FileUtil.getIntFromSp(AppConfig.getContext(), spFileName, Constant.wtype);
                                dwallet.wname = FileUtil.getStringFromSp(AppConfig.getContext(), spFileName, Constant.wname);
                                dwallet.waddress = FileUtil.getStringFromSp(AppConfig.getContext(), spFileName, Constant.waddress);
                                dwallet.whash = FileUtil.getStringFromSp(AppConfig.getContext(), spFileName, Constant.whash);
                                dwallet.wpk = FileUtil.getStringFromSp(AppConfig.getContext(),
                                        spFileName, Constant.wpk);
                                dwallet.words = FileUtil.getStringFromSp(AppConfig.getContext(),
                                        spFileName, Constant.words);
                                wallet.isBaked = FileUtil.getBooleanFromSp(AppConfig.getContext(), spFileName, Constant.baked);

                                setCurrentWallet(dwallet);
                                return;

                            }
                        }
                    }
                }
            }
        }
    }

    public boolean hasWallet() {
        File spFileDir = new File(FileUtil.getSharedPrefDir(AppConfig.getContext()));
        if (!spFileDir.exists()) {
            return false;
        } else {
            if (spFileDir.isDirectory()) {
                File[] spFiles = spFileDir.listFiles();
                if (spFiles != null && spFiles.length > 0) {
                    for (File spFile : spFiles) {
                        if (spFile.getName().startsWith("wallet")) {
                            if (validWalletData(spFile.getName().substring(0, spFile.getName().lastIndexOf(".")))) {
                                return true;
                            } else {
                                FileUtil.deleteFile(spFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
            FileUtil.deleteFile(FileUtil.getSharedPrefDir(AppConfig.getContext()) + Constant.wallet_def_file + ".xml");
        }

        return false;
    }

    public boolean hasWallet(int walletType) {
        List<WData> allWalletData = getAllWallet();
        for (WData walletData : allWalletData) {
            if (walletData.type == walletType) {
                return true;
            }
        }
        return false;
    }

    public void insertWallet(WData wallet) {
        if (wallet == null || TextUtils.isEmpty(wallet.wname) ||
                TextUtils.isEmpty(wallet.waddress) || TextUtils.isEmpty(wallet.whash) ||
                TextUtils.isEmpty(wallet.wpk) ||
                wallet.wid <= 0l ||
                wallet.type <= 0) {
            return;
        }


        String spFileName = Constant.wallet_prefs_prefix + wallet.waddress;
        FileUtil.putLongToSp(AppConfig.getContext(), spFileName, Constant.wid, wallet.wid);
        FileUtil.putIntToSp(AppConfig.getContext(), spFileName, Constant.wtype, wallet.type);
        FileUtil.putStringToSp(AppConfig.getContext(), spFileName, Constant.wname, wallet.wname);
        FileUtil.putStringToSp(AppConfig.getContext(), spFileName, Constant.waddress, wallet.waddress);
        FileUtil.putStringToSp(AppConfig.getContext(), spFileName, Constant.whash, wallet.whash);
        FileUtil.putStringToSp(AppConfig.getContext(), spFileName, Constant.wpk,
                wallet.wpk);
        FileUtil.putStringToSp(AppConfig.getContext(), spFileName, Constant.words,
                wallet.words);
        FileUtil.putBooleanToSp(AppConfig.getContext(), spFileName, Constant.baked, wallet.isBaked);

        setCurrentWallet(wallet);
    }

    public void deleteWallet(Context context, WData wallet) {
        if (wallet == null || TextUtils.isEmpty(wallet.waddress)) {
            return;
        }

        FileUtil.deleteFile(FileUtil.getSharedPrefDir(AppConfig.getContext()) + Constant.wallet_prefs_prefix + wallet.waddress + ".xml");

        //如果删除的是当前钱包，则要选择一个新的钱包为当前默认钱包
        List<WData> walletDataList = getAllWallet();

        if (TextUtils.equals(wallet.waddress, getWAddress())) {
            FileUtil.deleteFile(FileUtil.getSharedPrefDir(AppConfig.getContext()) + Constant.wallet_def_file + ".xml");
            if (walletDataList != null && walletDataList.size() > 0) {
                setCurrentWallet(walletDataList.get(0));
            }
        }

        //删除钱包后，如果当前无钱包了，则进入创建钱包引导页
        if (!hasWallet()) {
            SplashActivity.startSplashActivity(context);
            return;
        }
    }

    public void updateCurrentWalletName(String walletName) {
        if (mCurrentWallet == null) {
            return;
        }
        if (!TextUtils.isEmpty(walletName)) {
            mCurrentWallet.wname = walletName;
            FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wname,
                    walletName);
            FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + mCurrentWallet.waddress, Constant.wname,
                    walletName);
        }
    }

    public void updateCHash(String walletHash) {
        if (mCurrentWallet == null) {
            return;
        }
        if (!TextUtils.isEmpty(walletHash)) {
            mCurrentWallet.whash = walletHash;
            FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.whash,
                    walletHash);
            FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + mCurrentWallet.waddress, Constant.whash,
                    walletHash);
        }
    }

    public void updateCurrentBak(boolean baked) {
        if (mCurrentWallet == null) {
            return;
        }
        mCurrentWallet.isBaked = baked;
        FileUtil.putBooleanToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.baked,
                baked);
        FileUtil.putBooleanToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + mCurrentWallet.waddress, Constant.baked,
                baked);
    }

    public void updateCurrentWords(String words) {
        if (mCurrentWallet == null) {
            return;
        }
        mCurrentWallet.words = words;
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.words,
                words);
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + mCurrentWallet.waddress, Constant.words,
                words);
    }

    public void updateWalletName(String walletAddress, String walletName) {
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + walletAddress, Constant.wname, walletName);
        if (mCurrentWallet != null && TextUtils.equals(walletAddress, mCurrentWallet.waddress)) {
            updateCurrentWalletName(walletName);
        }
    }

    public void updateWalletHash(String walletAddress, String walletHash) {
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + walletAddress, Constant.whash, walletHash);
        if (mCurrentWallet != null && TextUtils.equals(walletAddress, mCurrentWallet.waddress)) {
            updateCHash(walletHash);
        }
    }

    public void updateWalletBaked(String walletAddress, boolean baked) {
        FileUtil.putBooleanToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + walletAddress, Constant.baked, baked);
        if (mCurrentWallet != null && TextUtils.equals(walletAddress, mCurrentWallet.waddress)) {
            updateCurrentBak(baked);
        }
    }

    public void updateWalletWords(String walletAddress, String words) {
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_prefs_prefix + walletAddress, Constant.words,
                words);
        if (mCurrentWallet != null && TextUtils.equals(walletAddress, mCurrentWallet.waddress)) {
            updateCurrentWords(words);
        }
    }

    public WData getWData(String walletAddress) {
        String spFileName = Constant.wallet_prefs_prefix + walletAddress;
        if (!validWalletData(spFileName)) {
            FileUtil.deleteFile(FileUtil.getSharedPrefDir(AppConfig.getContext()) + spFileName + ".xml");
            return null;
        }
        WData walletData = new WData();
        walletData.wid = FileUtil.getLongFromSp(AppConfig.getContext(), spFileName, Constant.wid);
        walletData.type = FileUtil.getIntFromSp(AppConfig.getContext(), spFileName, Constant.wtype);
        walletData.wname = FileUtil.getStringFromSp(AppConfig.getContext(), spFileName, Constant.wname);
        walletData.waddress = FileUtil.getStringFromSp(AppConfig.getContext(), spFileName, Constant.waddress);
        walletData.whash = FileUtil.getStringFromSp(AppConfig.getContext(), spFileName, Constant.whash);
        walletData.wpk = FileUtil.getStringFromSp(AppConfig.getContext(),
                spFileName, Constant.wpk);
        walletData.words = FileUtil.getStringFromSp(AppConfig.getContext(),
                spFileName, Constant.words);
        walletData.isBaked = FileUtil.getBooleanFromSp(AppConfig.getContext(), spFileName, Constant.baked);
        return walletData;
    }

    public WData getCurrentWallet() {
        return mCurrentWallet;
    }


    public String getWname() {
        if (mCurrentWallet == null) {
            return "";
        } else {
            return mCurrentWallet.wname;
        }
    }

    public String getWAddress() {
        if (mCurrentWallet == null) {
            return "";
        } else {
            return mCurrentWallet.waddress;
        }
    }


    public long getWalletId() {
        if (mCurrentWallet == null) {
            return 0l;
        } else {
            return mCurrentWallet.wid;
        }
    }

    public int getWalletType() {
        if (mCurrentWallet == null) {
            return 0;
        } else {
            return mCurrentWallet.type;
        }
    }

    public String getWalletWord() {
        if (mCurrentWallet == null) {
            return "";
        }
        return mCurrentWallet.words;
    }

    public boolean isWalletBakup() {
        if (mCurrentWallet == null) {
            return false;
        } else {
            return mCurrentWallet.isBaked;
        }
    }

    public boolean setCurrentWallet(int walletType) {
        List<WData> walletDataList = WalletInfoManager.getInstance().getAllWallet();
        for (WData walletData : walletDataList) {
            if (walletData.type == walletType) {
                WalletInfoManager.getInstance().setCurrentWallet(walletData);
                return true;
            }
        }
        return false;
    }

    /**
     * @param currentWallet 可以为null，null时表示用户选择的全部钱包
     * @return
     */
    public boolean setCurrentWallet(final WData currentWallet) {
        if (currentWallet == null) {
            this.mCurrentWallet = null;
            updateWalletDefaultSp(null);
            return false;
        }
        if (!TextUtils.isEmpty(currentWallet.wname) &&
                !TextUtils.isEmpty(currentWallet.waddress)
                && !TextUtils.isEmpty(currentWallet.whash) &&
                !TextUtils.isEmpty(currentWallet.wpk) &&
                currentWallet.type > 0 &&
                currentWallet.wid > 0l) {

            this.mCurrentWallet = currentWallet;
            updateWalletDefaultSp(currentWallet);
            return true;
        } else {
            return false;
        }
    }

    public List<WData> getAllWallet() {
        List<WData> allWallet = new ArrayList<>();
        File spFileDir = new File(FileUtil.getSharedPrefDir(AppConfig.getContext()));
        if (!spFileDir.exists()) {
            return allWallet;
        } else {
            if (spFileDir.isDirectory()) {
                File[] spFiles = spFileDir.listFiles();
                if (spFiles != null && spFiles.length > 0) {
                    for (File spFile : spFiles) {
                        String fileName = spFile.getName().substring(0, spFile.getName().lastIndexOf("."));
                        if (validWalletData(fileName) && !TextUtils.equals(fileName, Constant.wallet_def_file)) {
                            WData dwallet = new WData();
                            dwallet.type = FileUtil.getIntFromSp(AppConfig.getContext(), fileName, Constant.wtype);
                            dwallet.wid = FileUtil.getLongFromSp(AppConfig.getContext(), fileName, Constant.wid);
                            dwallet.wname = FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.wname);
                            dwallet.waddress = FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.waddress);
                            dwallet.whash = FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.whash);
                            dwallet.wpk = FileUtil.getStringFromSp(AppConfig.getContext(),
                                    fileName, Constant.wpk);
                            dwallet.words = FileUtil.getStringFromSp(AppConfig.getContext(),
                                    fileName, Constant.words);
                            dwallet.isBaked = FileUtil.getBooleanFromSp(AppConfig.getContext(), fileName, Constant.baked);
                            allWallet.add(dwallet);
                        }
                    }
                    return allWallet;
                }
            }
        }
        return allWallet;
    }

    private boolean validWalletData(String fileName) {
        if (!TextUtils.isEmpty(FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.wname)) &&
                !TextUtils.isEmpty(FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.waddress))
                && !TextUtils.isEmpty(FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.whash)) &&
                !TextUtils.isEmpty(FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.wpk)) &&
                FileUtil.getIntFromSp(AppConfig.getContext(), fileName, Constant.wtype) > 0 &&
                FileUtil.getLongFromSp(AppConfig.getContext(), fileName, Constant.wid) > 0) {
            return true;
        }
        return false;
    }

    private void updateWalletDefaultSp(WData wallet) {
        if (wallet == null) {
            FileUtil.deleteFile(FileUtil.getSharedPrefDir(AppConfig.getContext()) + Constant.wallet_def_file + ".xml");
            return;
        }
        if (TextUtils.isEmpty(wallet.wname) ||
                TextUtils.isEmpty(wallet.waddress) || TextUtils.isEmpty(wallet.whash) ||
                TextUtils.isEmpty(wallet.wpk) ||
                wallet.wid <= 0 ||
                wallet.type <= 0) {
            ToastUtil.toast(AppConfig.getContext(), "更新默认钱包失败");
            return;
        }
        FileUtil.putIntToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wtype, wallet.type);
        FileUtil.putLongToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wid, wallet.wid);
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wname, wallet.wname);
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.waddress, wallet.waddress);
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.whash, wallet.whash);
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.wpk, wallet.wpk);
        FileUtil.putStringToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.words, wallet.words);
        FileUtil.putBooleanToSp(AppConfig.getContext(), Constant.wallet_def_file, Constant.baked, wallet.isBaked);
    }


    public static class WData {
        public long wid;
        public String wname;
        public String waddress;
        public String whash;
        public String wpk;
        public int type;
        public boolean isBaked = false;
        public String words = "";

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof WData) {
                WData wallet = (WData) obj;
                if (TextUtils.equals(wallet.waddress, this.waddress) && wallet.type == this.type) {
                    return true;
                }
            }
            return false;
        }
    }
}
