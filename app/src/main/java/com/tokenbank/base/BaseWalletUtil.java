package com.tokenbank.base;


import android.content.Context;

import com.tokenbank.utils.GsonUtil;


public interface BaseWalletUtil {

    void init();

    void createWallet(WCallback callback);

    void importWallet(String privateKey, int type, WCallback callback);

    void toIban(String address, WCallback callback);

    void fromIban(String ibanAddress, WCallback callback);

    void gasPrice(WCallback callback);

    void signedTransaction(GsonUtil data, WCallback callback);

    void sendSignedTransaction(String rawTransaction, WCallback callback);

    boolean isWalletLegal(String pk, String address);

    void generateReceiveAddress(String walletAddress, double amount, String token, WCallback callback);

    void calculateGasInToken(final double gas, double gasPrice, final boolean defaultToken, final WCallback callback);

    void gasSetting(Context context, double gasPrice, boolean defaultToken, WCallback callback);

    double getRecommendGas(double gas, boolean defaultToken);

    String getDefaultTokenSymbol();

    int getDefaultDecimal();

    void getTokenInfo(String token, long blockChainId, WCallback callback);

    void translateAddress(String sourceAddress, WCallback callback);

    boolean checkWalletAddress(String receiveAddress);

    boolean checkWalletPk(String privateKey);

    void queryTransactionDetails(String hash, WCallback callback);

    void queryBalance(String address, int type, WCallback callback);

    void queryTransactionList(GsonUtil params, WCallback callback);

    double getValue(int decimal, double originValue);

    GsonUtil loadTransferTokens(Context context);

    String getTransactionSearchUrl(String hash);
}
