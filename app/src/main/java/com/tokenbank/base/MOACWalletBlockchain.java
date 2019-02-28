package com.tokenbank.base;

import android.content.Context;
import android.text.TextUtils;

import com.tokenbank.utils.GsonUtil;

public class MOACWalletBlockchain implements BaseWalletUtil {
    @Override
    public void init() {

    }

    @Override
    public void createWallet(String walletName, String walletPassword, int blockType, WCallback callback) {

        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putInt("blockType", blockType);
        JSUtil.getInstance().callJS("createMoacWallet", json, callback);
    }

    @Override
    public void importWallet(String privateKey, int blockType, int type, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putInt("blockType", blockType);

        if (type == 2) {
            json.putString("privateKey", privateKey);
            JSUtil.getInstance().callJS("importMoacSecret", json, callback);
        }
    }

    @Override
    public void toIban(String address, WCallback callback) {

    }

    @Override
    public void fromIban(String ibanAddress, WCallback callback) {

    }

    @Override
    public void gasPrice(WCallback callback) {

    }

    @Override
    public void signedTransaction(GsonUtil data, WCallback callback) {

    }

    @Override
    public void sendSignedTransaction(String rawTransaction, WCallback callback) {

    }

    @Override
    public boolean isWalletLegal(String pk, String address) {
        if (!TextUtils.isEmpty(pk) && !TextUtils.isEmpty(address)) {
            return true;
        }
        return false;
    }

    @Override
    public void generateReceiveAddress(String walletAddress, double amount, String token, WCallback callback) {

    }

    @Override
    public void calculateGasInToken(double gas, double gasPrice, boolean defaultToken, WCallback callback) {

    }

    @Override
    public void gasSetting(Context context, double gasPrice, boolean defaultToken, WCallback callback) {

    }

    @Override
    public double getRecommendGas(double gas, boolean defaultToken) {
        return 0;
    }

    @Override
    public String getDefaultTokenSymbol() {
        return null;
    }

    @Override
    public int getDefaultDecimal() {
        return 0;
    }

    @Override
    public void getTokenInfo(String token, long blockChainId, WCallback callback) {

    }

    @Override
    public void translateAddress(String sourceAddress, WCallback callback) {

    }

    @Override
    public boolean checkWalletAddress(String receiveAddress) {
        return false;
    }

    @Override
    public boolean checkWalletPk(String privateKey) {
        if (!privateKey.startsWith("0x") || privateKey.length() != 66) {
            return false;
        }
        return true;
    }

    @Override
    public void queryTransactionDetails(String hash, WCallback callback) {

    }

    @Override
    public void queryBalance(String address, int type, WCallback callback) {

    }

    @Override
    public void queryTransactionList(GsonUtil params, WCallback callback) {

    }

    @Override
    public double getValue(int decimal, double originValue) {
        return 0;
    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
        return null;
    }

    @Override
    public String getTransactionSearchUrl(String hash) {
        return null;
    }

    private boolean checkInit(WCallback callback) {
        return JSUtil.getInstance().checkInit(callback);
    }

}
