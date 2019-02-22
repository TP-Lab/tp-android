package com.tokenbank.base;

import android.content.Context;
import android.text.TextUtils;

import com.tokenbank.config.Constant;
import com.tokenbank.dialog.EthGasSettignDialog;
import com.tokenbank.net.api.GetTransactionDetailsRequest;
import com.tokenbank.net.api.GetTransactionRecordRequest;
import com.tokenbank.net.load.RequestPresenter;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;



public class ETHWalletBlockchain implements BaseWalletUtil {

    private final static String TAG = "ETHWalletBlockchain";

    @Override
    public void init() {
    }

    @Override
    public void createWallet(final String walletName, final String walletPassword, int blockType, final WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putInt("blockType", blockType);
        JSUtil.getInstance().callJS("createWalletWithWord", json, callback);
    }

    @Override
    public void importWallet(String privateKey, int blockType, int type, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putInt("blockType", blockType);

        if (type == 1) {
            json.putString("words", privateKey);
            JSUtil.getInstance().callJS("importWalletWithWords", json, callback);
        } else if (type == 2) {
            json.putString("privateKey", privateKey);
            JSUtil.getInstance().callJS("importWalletWithPK", json, callback);
        }
    }

    @Override
    public void toIban(String ethAddress, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }

        GsonUtil json = new GsonUtil("{}");
        json.putString("ethAddress", ethAddress);
        JSUtil.getInstance().callJS("toIbanAddress", json, callback);
    }

    @Override
    public void fromIban(String ibanAddress, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }

        GsonUtil json = new GsonUtil("{}");
        json.putString("ibanAddress", ibanAddress);
        JSUtil.getInstance().callJS("toEthAddress", json, callback);
    }

    //gasPrice 以gwei为单位
    @Override
    public void gasPrice(final WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }

        GsonUtil json = new GsonUtil("{}");
        JSUtil.getInstance().callJS("getGasPrice", json, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                double gasPrice = 8.0f;
                if (ret == 0) {
                    double wei = Util.parseDouble(extra.getString("gasPrice", "8000000000"));
                    if (wei > 0) {
                        gasPrice = wei / 1000000000.0f;
                    }
                }
                GsonUtil gasPriceJson = new GsonUtil("{}");
                gasPriceJson.putDouble("gasPrice", gasPrice);
                callback.onGetWResult(0, gasPriceJson);
            }
        });
    }

    @Override
    public void signedTransaction(GsonUtil data, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }

        GsonUtil json = new GsonUtil("{}");
        GsonUtil transactionToSign = new GsonUtil("{}");
        json.putString("privateKey", data.getString("privateKey", ""));
        String abi = data.getString("abi", "");
        if (TextUtils.isEmpty(abi)) {
            transactionToSign.putString("from", data.getString("senderAddress", ""));
            transactionToSign.putString("value", Util.formatDoubleToStr(0, data.getDouble("tokencount", 0.0f)));
            transactionToSign.putString("to", data.getString("receiverAddress", ""));
            transactionToSign.putString("gas", Util.formatDoubleToStr(0, data.getDouble("gas", 0.0f)));
            transactionToSign.putString("gasPrice", Util.formatDoubleToStr(0,
                    Util.fromGweToWei(1, data.getDouble("gasPrice", 0.0f))));
        } else {
            transactionToSign.putString("from", data.getString("senderAddress", ""));
            transactionToSign.putString("value", Util.formatDoubleToStr(0, data.getDouble("tokencount", 0.0f)));
            transactionToSign.putString("to", data.getString("contactAddress", ""));
            transactionToSign.putString("gas", Util.formatDoubleToStr(0, data.getDouble("gas", 0.0f)));
            transactionToSign.putString("gasPrice", Util.formatDoubleToStr(0, Util.fromGweToWei(1, data.getDouble("gasPrice", 0.0f))));
            transactionToSign.put("abi", new GsonUtil(abi));
            transactionToSign.putString("toAddress", data.getString("receiverAddress", ""));
        }

        json.put("transactionToSign", transactionToSign);
        JSUtil.getInstance().callJS("accountSignTransaction", json, callback);
    }

    @Override
    public void sendSignedTransaction(String rawTransaction, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putString("rawTransaction", rawTransaction);
        JSUtil.getInstance().callJS("sendTransaction", json, callback);
    }

    @Override
    public boolean isWalletLegal(String pk, String address) {
        if (!TextUtils.isEmpty(pk) && !TextUtils.isEmpty(address) && pk.startsWith("0x") && pk.length() == 66) {
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
        toIban(walletAddress, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    String ibanAddress = extra.getString("ibanAddress", "");
                    if (TextUtils.isEmpty(ibanAddress)) {
                        callback.onGetWResult(-1, address);
                    } else {
                        String receiveStr = String.format("iban:%s?amount=%f&token=%s", ibanAddress, tmpAmount, token);
                        address.putString("receiveAddress", receiveStr);
                        callback.onGetWResult(0, address);
                    }
                } else {
                    callback.onGetWResult(-1, address);
                }
            }
        });
    }

    @Override
    public void calculateGasInToken(final double gas, double gasPrice, final boolean defaultToken, final WCallback callback) {
        if (gasPrice <= 0.0) {
            gasPrice(new WCallback() {
                @Override
                public void onGetWResult(int ret, GsonUtil extra) {
                    double gasPrice = 8.0f;
                    if (ret == 0) {
                        double gasPriceByEth = extra.getDouble("gasPrice", 8.0f);
                        if (gasPriceByEth > 0.0f) {
                            gasPrice = gasPriceByEth;
                        }
                    }
                    double totalGasInWei = gasPrice * 1000000000.0f * getRecommendGas(gas, defaultToken);
                    GsonUtil gas = new GsonUtil("{}");
                    gas.putString("gas", Util.formatDoubleToStr(5, Util.fromWei(TBController.ETH_INDEX, totalGasInWei)) + " " + "ETH");
                    callback.onGetWResult(0, gas);
                }
            });
        } else {
            double totalGasInWei = gasPrice * 1000000000.0f * getRecommendGas(gas, defaultToken);
            GsonUtil gasJson = new GsonUtil("{}");
            gasJson.putString("gas", Util.formatDoubleToStr(5, Util.fromWei(TBController.ETH_INDEX, totalGasInWei)) + " " + "ETH");
            callback.onGetWResult(0, gasJson);
        }
    }

    @Override
    public void gasSetting(Context context, double gasPrice, boolean defaultToken, final WCallback callback) {
        EthGasSettignDialog gasSettignDialog = new EthGasSettignDialog(context, new EthGasSettignDialog.OnSettingGasListener() {
            @Override
            public void onSettingGas(double gasPrice, double gasInToken) {
                GsonUtil gas = new GsonUtil("{}");
                gas.putString("gas", Util.formatDoubleToStr(5, gasInToken) + " " + "ETH");
                gas.putDouble("gasPrice", gasPrice);
                callback.onGetWResult(0, gas);
            }
        }, gasPrice, defaultToken);
        gasSettignDialog.show();
    }

    @Override
    public double getRecommendGas(double gas, boolean defaultToken) {
        if (gas <= 0.0f) {
            if (defaultToken) {
                return 25200;
            } else {
                return 60000;
            }
        }
        return gas;
    }

    @Override
    public String getDefaultTokenSymbol() {
        return "ETH";
    }

    @Override
    public int getDefaultDecimal() {
        return 18;
    }

    @Override
    public void getTokenInfo(String token, long blockChainId, final WCallback callback) {

    }

    @Override
    public void translateAddress(String sourceAddress, final WCallback callback) {
        if (TextUtils.isEmpty(sourceAddress)) {
            GsonUtil addressJson = new GsonUtil("{}");
            addressJson.putString("receive_address", "");
            callback.onGetWResult(0, addressJson);
            return;
        }
        fromIban(sourceAddress, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                GsonUtil addressJson = new GsonUtil("{}");
                if (ret == 0) {
                    addressJson.putString("receive_address", extra.getString("ethAddress", ""));
                } else {
                    addressJson.putString("receive_address", "");
                }
                callback.onGetWResult(0, addressJson);
            }
        });
    }

    @Override
    public boolean checkWalletAddress(String receiveAddress) {
        if (!receiveAddress.startsWith("0x") || receiveAddress.length() != 42) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkWalletPk(String privateKey) {
        if (!privateKey.startsWith("0x") || privateKey.length() != 66) {
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
                        item.putString("tokenSymbol", "ETH");
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
        if (decimal <= 0) {
            decimal = getDefaultDecimal();
        }
        return Util.formatDouble(5, Util.translateValue(decimal, originValue));
    }

    @Override
    public void queryBalance(String address, int type, final WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putString("address", address);
        JSUtil.getInstance().callJS("getBalance", json, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    GsonUtil formatData = new GsonUtil("{}");
                    GsonUtil arrays = new GsonUtil("[]");
                    GsonUtil data = new GsonUtil("{}");
                    data.putLong("blockchain_id", Long.parseLong("" + TBController.ETH_INDEX));
                    data.putString("icon_url", Constant.ETHER_ICON);
                    data.putString("bl_symbol", "ETH");
                    data.putInt("decimal", 18);
                    data.putString("balance", extra.getString("balance", "0"));
                    data.putString("asset", "0");
                    arrays.put(data);
                    formatData.put("data", arrays);
                    callback.onGetWResult(ret, formatData);
                }
            }
        });
    }

    @Override
    public String getTransactionSearchUrl(String hash) {
        return Constant.eth_transaction_search_url + hash;
    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
        String data = FileUtil.getConfigFile(context, "ethTokens.json");
        return new GsonUtil(data);
    }

    private boolean checkInit(WCallback callback) {
        return JSUtil.getInstance().checkInit(callback);
    }
}
