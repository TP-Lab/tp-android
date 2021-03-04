package com.tokenbank.base;

import android.content.Context;
import android.text.TextUtils;

import com.android.jccdex.app.base.JCallback;
import com.android.jccdex.app.ethereum.EthereumWallet;
import com.android.jccdex.app.util.JCCJson;
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
    public void createWallet(final WCallback callback) {

        EthereumWallet.getInstance().createWallet(new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String address = json.getString("address");
                String secret = json.getString("secret");
                String words = json.getString("words");
                if (address != null && secret != null && words != null) {
                    GsonUtil gsonUtil = new GsonUtil(json.toString());
                    callback.onGetWResult(0, gsonUtil);
                } else {
                    callback.onGetWResult(-1, null);
                }
            }
        });
    }

    @Override
    public void importWallet(String privateKey, int type, final WCallback callback) {
        if (type == 1) {
            EthereumWallet.getInstance().importWords(privateKey, new JCallback() {
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
        } else if (type == 2) {
            EthereumWallet.getInstance().importSecret(privateKey, new JCallback() {
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
    public void toIban(String ethAddress, final WCallback callback) {
        EthereumWallet.getInstance().toIban(ethAddress, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String iban = json.getString("iban");
                if (iban == null) {
                    callback.onGetWResult(-1, null);
                } else {
                    GsonUtil gsonUtil = new GsonUtil(json.toString());
                    callback.onGetWResult(0, gsonUtil);
                }
            }
        });
    }

    @Override
    public void fromIban(String ibanAddress, final WCallback callback) {
        EthereumWallet.getInstance().fromIban(ibanAddress, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String address = json.getString("address");
                if (address == null) {
                    callback.onGetWResult(-1, null);
                } else {
                    GsonUtil gsonUtil = new GsonUtil(json.toString());
                    callback.onGetWResult(0, gsonUtil);
                }
            }
        });
    }

    //gasPrice 以gwei为单位
    @Override
    public void gasPrice(final WCallback callback) {

        EthereumWallet.getInstance().gasPrice(new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String gas = json.getString("gasPrice");
                if (gas == null) {
                    gas = "8000000000";
                }
                double gasPrice = 8.0f;
                double wei = Util.parseDouble(gas);
                if (wei > 0) {
                    gasPrice = wei / 1000000000.0f;
                }
                GsonUtil gasPriceJson = new GsonUtil("{}");
                gasPriceJson.putDouble("gasPrice", gasPrice);
                callback.onGetWResult(0, gasPriceJson);
            }
        });
    }

    @Override
    public void signedTransaction(GsonUtil data, final WCallback callback) {

        GsonUtil transactionToSign = new GsonUtil("{}");
        String secret = data.getString("privateKey", "");
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

        EthereumWallet.getInstance().sign(transactionToSign.getObj(), secret, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String rawTransaction = json.getString("rawTransaction");
                if (rawTransaction == null) {
                    callback.onGetWResult(-1, null);
                } else {
                    callback.onGetWResult(0, new GsonUtil(json.toString()));
                }
            }
        });
    }

    @Override
    public void sendSignedTransaction(String rawTransaction, final WCallback callback) {
        EthereumWallet.getInstance().sendSignedTransaction(rawTransaction, new JCallback() {
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
                    String ibanAddress = extra.getString("iban", "");
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
        }, gasPrice, defaultToken, TBController.ETH_INDEX);
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
                    addressJson.putString("receive_address", extra.getString("address", ""));
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
        EthereumWallet.getInstance().getBalance(address, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String balance = json.getString("balance");
                if (balance == null) {
                    balance = "0";
                }
                GsonUtil formatData = new GsonUtil("{}");
                GsonUtil arrays = new GsonUtil("[]");
                GsonUtil data = new GsonUtil("{}");
                data.putLong("blockchain_id", Long.parseLong("" + TBController.ETH_INDEX));
                data.putString("icon_url", Constant.ETHER_ICON);
                data.putString("bl_symbol", "ETH");
                data.putInt("decimal", 18);
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
        String data = FileUtil.getConfigFile(context, "ethTokens.json");
        return new GsonUtil(data);
    }
}
