package com.tokenbank.base;

import android.content.Context;
import android.text.TextUtils;

import com.android.jccdex.app.base.JCallback;
import com.android.jccdex.app.moac.MoacWallet;
import com.android.jccdex.app.util.JCCJson;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.EthGasSettignDialog;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;

public class MOACWalletBlockchain implements BaseWalletUtil {
    @Override
    public void init() {

    }

    @Override
    public void createWallet(final WCallback callback) {

        MoacWallet.getInstance().createWallet(new JCallback() {
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
            MoacWallet.getInstance().importWords(privateKey, new JCallback() {
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
            MoacWallet.getInstance().importSecret(privateKey, new JCallback() {
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
    public void toIban(String address, final WCallback callback) {
        MoacWallet.getInstance().toIban(address, new JCallback() {
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
        MoacWallet.getInstance().fromIban(ibanAddress, new JCallback() {
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

    @Override
    public void gasPrice(final WCallback callback) {
        MoacWallet.getInstance().gasPrice(new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String gas = json.getString("gasPrice");
                if (gas == null) {
                    gas = "20000000000";
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
        }

        MoacWallet.getInstance().sign(transactionToSign.getObj(), secret, new JCallback() {
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
        MoacWallet.getInstance().sendSignedTransaction(rawTransaction, new JCallback() {
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
        if (!TextUtils.isEmpty(pk) && !TextUtils.isEmpty(address)) {
            return true;
        }
        return false;
    }

    @Override
    public void generateReceiveAddress(String walletAddress, double amount, final String token, final WCallback callback) {
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
    public void calculateGasInToken(final double gas, final double gasPrice, final boolean defaultToken, final WCallback callback) {
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
                    gas.putString("gas", Util.formatDoubleToStr(5, Util.fromWei(TBController.MOAC_INDEX, totalGasInWei)) + " " + "MOAC");
                    callback.onGetWResult(0, gas);
                }
            });
        } else {
            double totalGasInWei = gasPrice * 1000000000.0f * getRecommendGas(gas, defaultToken);
            GsonUtil gasJson = new GsonUtil("{}");
            gasJson.putString("gas", Util.formatDoubleToStr(5, Util.fromWei(TBController.MOAC_INDEX, totalGasInWei)) + " " + "MOAC");
            callback.onGetWResult(0, gasJson);
        }
    }

    @Override
    public void gasSetting(Context context, double gasPrice, boolean defaultToken, final WCallback callback) {
        EthGasSettignDialog gasSettignDialog = new EthGasSettignDialog(context, new EthGasSettignDialog.OnSettingGasListener() {
            @Override
            public void onSettingGas(double gasPrice, double gasInToken) {
                GsonUtil gas = new GsonUtil("{}");
                gas.putString("gas", Util.formatDoubleToStr(5, gasInToken) + " " + "MOAC");
                gas.putDouble("gasPrice", gasPrice);
                callback.onGetWResult(0, gas);
            }
        }, gasPrice, defaultToken, TBController.MOAC_INDEX);
        gasSettignDialog.show();
    }

    @Override
    public double getRecommendGas(double gas, boolean defaultToken) {
        if (gas <= 0.0f) {
            if (defaultToken) {
                return 2000;
            } else {
                return 6000;
            }
        }
        return gas;
    }

    @Override
    public String getDefaultTokenSymbol() {
        return "MOAC";
    }

    @Override
    public int getDefaultDecimal() {
        return 18;
    }

    @Override
    public void getTokenInfo(String token, long blockChainId, WCallback callback) {

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
    public void queryTransactionDetails(String hash, WCallback callback) {

    }

    @Override
    public void queryBalance(String address, int type, final WCallback callback) {
        MoacWallet.getInstance().getBalance(address, new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String balance = json.getString("balance");
                if (balance == null) {
                    balance = "0";
                }
                GsonUtil formatData = new GsonUtil("{}");
                GsonUtil arrays = new GsonUtil("[]");
                GsonUtil data = new GsonUtil("{}");
                data.putLong("blockchain_id", Long.parseLong("" + TBController.MOAC_INDEX));
                data.putString("icon_url", Constant.MOAC_ICON);
                data.putString("bl_symbol", "MOAC");
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
    public void queryTransactionList(GsonUtil params, WCallback callback) {

    }

    @Override
    public double getValue(int decimal, double originValue) {
        return Util.formatDouble(5, originValue);
    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
        String data = FileUtil.getConfigFile(context, "moacTokens.json");
        return new GsonUtil(data);
    }

    @Override
    public String getTransactionSearchUrl(String hash) {
        return null;
    }
}
