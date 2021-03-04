package com.tokenbank.base;

import android.content.Context;
import android.text.TextUtils;

import com.android.jccdex.app.base.JCallback;
import com.android.jccdex.app.eos.EosWallet;
import com.android.jccdex.app.util.JCCJson;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.EthGasSettignDialog;
import com.tokenbank.net.api.GetTransactionDetailsRequest;
import com.tokenbank.net.api.GetTransactionRecordRequest;
import com.tokenbank.net.load.RequestPresenter;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.utils.Util;


public class EOSWalletBlockchain implements BaseWalletUtil {

    private final static String TAG = "EOSWalletBlockchain";

    @Override
    public void init() {
    }

    @Override
    public void createWallet(final WCallback callback) {
        //TODO
    }

    @Override
    public void importWallet(String privateKey, int type, final WCallback callback) {
        if (type == 2) {
            EosWallet.getInstance().importSecret(privateKey, new JCallback() {
                @Override
                public void completion(JCCJson json) {
                    String address = json.getString("address");
                    String secret = json.getString("secret");
                    if (address != null && secret != null) {
                        GsonUtil gsonUtil = new GsonUtil(json.toString());
                        callback.onGetWResult(0, gsonUtil);
                    } else {
                        callback.onGetWResult(-1, null);
                    }
                }
            });
        }
    }

    @Override
    public void toIban(String eosAddress, WCallback callback) {

    }

    @Override
    public void fromIban(String ibanAddress, WCallback callback) {

    }

    //gasPrice 以gwei为单位
    @Override
    public void gasPrice(final WCallback callback) {

    }

    @Override
    public void signedTransaction(GsonUtil data, final WCallback callback) {

        GsonUtil transaction = new GsonUtil("{}");
        String secret = data.getString("privateKey", "");
        transaction.putString("from", data.getString("senderAddress", ""));
        transaction.putString("value", Util.getEosValue(data.getString("symbol", ""), data.getDouble("tokencount", 0.0f)));
        transaction.putString("to", data.getString("receiverAddress", ""));
        transaction.putString("contract", data.getString("contactAddress", ""));
        transaction.putString("memo", data.getString("memo", ""));
        EosWallet.getInstance().sendTransaction(transaction.getObj(), secret, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String hash = json.getString("hash");
                if (hash == null) {
                    callback.onGetWResult(-1, null);
                } else {
                    callback.onGetWResult(0, null);
                }
            }
        });
    }

    @Override
    public void sendSignedTransaction(String rawTransaction, WCallback callback) {
        GsonUtil json = new GsonUtil("{}");
        json.putString("rawTransaction", rawTransaction);
    }

    @Override
    public boolean isWalletLegal(String pk, String address) {
        if (!TextUtils.isEmpty(pk) && !TextUtils.isEmpty(address) && pk.startsWith("5") && pk.length() == 51) {
            return true;
        }
        return false;
    }

    @Override
    public void generateReceiveAddress(final String walletAddress, final double amount, final String token, final WCallback callback) {
        if (TextUtils.isEmpty(walletAddress) || TextUtils.isEmpty(token)) {
            callback.onGetWResult(-1, new GsonUtil("{}"));
            return;
        }
        final double tmpAmount = amount < 0 ? 0.0f : amount;
        final GsonUtil address = new GsonUtil("{}");
        String receiveStr = String.format("eos:%s?amount=%f&token=%s", walletAddress, tmpAmount, token);
        address.putString("receiveAddress", receiveStr);
        callback.onGetWResult(0, address);
    }

    @Override
    public void calculateGasInToken(final double gas, double gasPrice, final boolean defaultToken, final WCallback callback) {

    }

    @Override
    public void gasSetting(Context context, double gasPrice, boolean defaultToken, final WCallback callback) {

    }

    @Override
    public double getRecommendGas(double gas, boolean defaultToken) {
        return 0.0;
    }

    @Override
    public String getDefaultTokenSymbol() {
        return "EOS";
    }


    @Override
    public int getDefaultDecimal() {
        return 4;
    }

    @Override
    public void getTokenInfo(String token, long blockChainId, final WCallback callback) {

    }

    @Override
    public void translateAddress(String sourceAddress, final WCallback callback) {

    }

    @Override
    public boolean checkWalletAddress(String receiveAddress) {
        if (receiveAddress.length() != 12) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkWalletPk(String privateKey) {
        if (!privateKey.startsWith("5") || privateKey.length() != 51) {
            return false;
        }
        return true;
    }

    @Override
    public void queryTransactionDetails(String hash, final WCallback callback) {
        if (TextUtils.isEmpty(hash)) {
            callback.onGetWResult(-1, new GsonUtil("{}"));
            return;
        }
        new RequestPresenter().loadData(new GetTransactionDetailsRequest(hash), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                if (ret == 0) {
                    GsonUtil data = json.getObject("data", "{}");
                    data.putDouble("real_value", getValue(data.getInt("decimal", getDefaultDecimal()), data.getDouble("token_value", 0.0f)));
                }
                callback.onGetWResult(ret, json);
            }
        });

    }

    @Override
    public void queryTransactionList(GsonUtil params, final WCallback callback) {
        int start = params.getInt("start", 0) + 1;
        int pagesize = params.getInt("pagesize", 10);
        final String contractAddress = params.getString("contract_address", "");
        new RequestPresenter().loadData(new GetTransactionRecordRequest(start, pagesize,
                WalletInfoManager.getInstance().getWAddress(), contractAddress), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                GsonUtil data = json.getArray("result", "[]");
                int len = data.getLength();
                for (int i = 0; i < len; i++) {
                    GsonUtil item = data.getObject(i, "{}");
                    if (TextUtils.isEmpty(item.getString("contractAddress", ""))) {
                        //原生币
                        item.putDouble("real_value", getValue(getDefaultDecimal(),
                                Util.parseDouble(item.getString("value", "0"))));
                        item.putString("tokenSymbol", "EOS");
                    } else {
                        item.putDouble("real_value", getValue(item.getInt("tokenDecimal", getDefaultDecimal()),
                                Util.parseDouble(item.getString("value", "0"))));
                    }
                }
                GsonUtil res = new GsonUtil("{}");
                res.put("data", data);
                callback.onGetWResult(0, res);
            }
        });
    }

    @Override
    public double getValue(int decimal, double originValue) {

        return Util.formatDouble(decimal, originValue);
    }

    @Override
    public void queryBalance(String account, int type, final WCallback callback) {
        EosWallet.getInstance().getBalance(account, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String balance = json.getString("balance");
                if (balance == null) {
                    balance = "0";
                }
                GsonUtil formatData = new GsonUtil("{}");
                GsonUtil arrays = new GsonUtil("[]");
                GsonUtil data = new GsonUtil("{}");
                data.putLong("blockchain_id", Long.parseLong("" + TBController.EOS_INDEX));
                data.putString("icon_url", Constant.EOS_ICON);
                data.putString("bl_symbol", "EOS");
                data.putInt("decimal", 4);
                data.putString("balance", balance);
                data.putString("asset", "0");
                arrays.put(data);
                formatData.put("data", arrays);
                callback.onGetWResult(0, formatData);
            }
        });
    }

    @Override
    public String getTransactionSearchUrl(String hash) {
        return Constant.eth_transaction_search_url + hash;
    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
        String data = FileUtil.getConfigFile(context, "eosTokens.json");
        return new GsonUtil(data);
    }
}
