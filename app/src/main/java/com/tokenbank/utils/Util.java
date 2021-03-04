package com.tokenbank.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Util {

    private final static String TAG = "Util";

    public static void clipboard(Context context, CharSequence label, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    public static String formatTime(long time) {
        if (time <= 0) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date = new Date(time * 1000);
            String str = sdf.format(date);
            return str;
        }
    }

    //TODO 要转成科学计数法
    public static double getValueByWeight(String valueinWeight) {
        try {
            double value = Double.parseDouble(valueinWeight) / 1000000000000000000.0f;
            BigDecimal bg = new BigDecimal(value);
            double result = bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    //将wei转成token value, 保存len位小数， 返回double
    public static double formatDouble(long blockChain, int len, double wei) {
        try {
            double value = fromWei(blockChain, wei);
            BigDecimal bg = new BigDecimal(value);
            double result = bg.setScale(len, BigDecimal.ROUND_HALF_UP).doubleValue();
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static double formatDouble(int len, double value) {
        try {
            BigDecimal bg = new BigDecimal(value);
            double result = bg.setScale(len, BigDecimal.ROUND_HALF_UP).doubleValue();
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    //将wei转成token value, 保存len位小数， 返回string
    public static String formatDoubleToStr(int len, double value) {
        return String.format("%." + len + "f", value).toString();
    }

    public static double strToDouble(String str) {
        try {
            double result = Double.parseDouble(str);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }


    //单位为gwei
    public static double getMinGweiGas(long blockChain, boolean defaultToken) {
        if (blockChain == 1) {
            if (defaultToken) {

            }
            return 32000;
        }
        return 0;
    }

    //单位为gwei
    public static double getMaxGweiGas(long blockChain, boolean defaultToken) {
        if (blockChain == 1) {
            if (defaultToken) {
                return 30000;
            } else {
                return 60000;
            }
        }
        return 0;
    }

    //单位为gwei
    public static double getRecommendGweiGas(long blockChain, boolean defaultToken) {
        if (blockChain == 1) {
            if (defaultToken) {
                return 25200;
            } else {
                return 60000;
            }
        } else if (blockChain == 3) {
            if (defaultToken) {
                return 2000;
            } else {
                return 6000;
            }
        }
        return 0;
    }

    public static String getSymbolByBlockChain(long blockChain) {
        if (blockChain == 1) {
            return "ETH";
        } else if (blockChain == 2) {
            return "SWT";
        } else if (blockChain == 3) {
            return "MOAC";
        }
        return "";
    }

    //wei to token value
    public static double fromWei(long blockChain, double wei) {
        if (blockChain == 1 || blockChain == 3) {
            if (wei <= 0) {
                return 0;
            }
            return wei / 1000000000000000000.0f;
        }
        return 0.0f;
    }

    //wei string to tokenvalue
    public static double fromWei(long blockChain, String wei) {
        try {
            double dwei = Double.parseDouble(wei);
            return fromWei(blockChain, dwei);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static double translateValue(int decimal, double value) {
        double divider = 1.0f;
        for (int i = 0; i < decimal; i++) {
            divider *= 10;
        }
        return value / divider;
    }

    //tokenvalue to wei
    public static double toWei(long blockChain, double tokenvalue) {
        if (blockChain == 1 || blockChain == 3) {
            if (tokenvalue <= 0) {
                return 0;
            }
            return tokenvalue * 1000000000000000000.0f;
        }
        return 0.0f;
    }

    public static double tokenToWei(long blockChain, double tokenValue, int dec) {
        if (blockChain == 1 || blockChain == 3) {
            String decimal = "1";
            for (int i = 0; i < dec; i++) {
                decimal = decimal + "0";
            }
            TLog.e(TAG, "decimal:" + decimal);
            return tokenValue * parseDouble(decimal);
        }
        return 0.0f;
    }

    public static double fromGweToWei(long blockChain, double gwei) {
        if (blockChain == 1 || blockChain == 3) {
            if (gwei <= 0) {
                return 0.0f;
            }
            return gwei * 1000000000.0f;
        }
        return 0.0f;
    }

    public static double fromWeiToGwei(long blockChain, double wei) {
        if (blockChain == 1 || blockChain == 3) {
            if (wei <= 0) {
                return 0.0f;
            }
            return wei / 1000000000.0f;
        }
        return 0.0f;
    }


    public static double parseDouble(String doubStr) {
        if (TextUtils.isEmpty(doubStr)) {
            return 0.0f;
        }
        try {
            return Double.parseDouble(doubStr);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static String getEosValue(String symbol, double value) {
        return new BigDecimal(value).setScale(4, BigDecimal.ROUND_DOWN).toPlainString() + " " + symbol.toUpperCase();
    }
}
