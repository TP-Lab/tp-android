package com.tokenbank.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public abstract class NetUtil {

    public static boolean isWifi(Context c) {
        boolean bRet = false;
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (null != wifiInfo && wifiInfo.isConnectedOrConnecting()) {
                bRet = true;
            }
        }
        return bRet;
    }

    public static boolean isWifiConnected(Context c) {
        boolean bRet = false;
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (null != wifiInfo && wifiInfo.isConnected()) {
                bRet = true;
            }
        }
        return bRet;
    }

    public static boolean isMobileNet(final Context c) {
        boolean ret = false;
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != mobileInfo && mobileInfo.isConnectedOrConnecting()) {
                ret = true;
            }
        }
        return ret;
    }

    public static boolean isNetworkAvailable(Context context) {
        Context ct = context.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return false;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (null != info) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // 以网络编码格式返回，如 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
    @SuppressLint("DefaultLocale")
    public static String getNetTypeName(Context context) {
        String typeName = "null";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (null == info) {
            typeName = "null";
        } else if (info.getTypeName() != null) {
            typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE    

            if (!typeName.equals("wifi")) {
                if (info.getExtraInfo() != null) {
                    typeName = info.getExtraInfo().toLowerCase(); //3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
                }

                if (typeName.equals("#777") && info.getSubtypeName() != null)
                    typeName = info.getSubtypeName();
            }
        }

        return typeName;
    }

    // wuwenhua add: 以wifi、2g、3g、4g作为返回格式
    public static String getNetTypeNameEx(Context context) {

        String type = "null";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (null != mobileInfo && mobileInfo.isConnected()) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int netType = tm.getNetworkType();
            switch (netType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    type = "2g";
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    type = "3g";
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    type = "4g";
                    break;
                default:
                    type = "null";
                    break;
            }

        }
        if (null != wifiInfo && wifiInfo.isConnected()) {
            type = "wifi";
        }
        return type;
    }


    public static int getNetType(Context context) {
        int type = -1;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (null != mobileInfo && mobileInfo.isConnected()) {
            type = ConnectivityManager.TYPE_MOBILE;
        }
        if (null != wifiInfo && wifiInfo.isConnected()) {
            type = ConnectivityManager.TYPE_WIFI;
        }

        return type;
    }

    public static int getCurrentNetType(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni == null ? -1 : ni.getType();
    }

    public static String getNetworkSubType(int subType) {
        String type = null;
        switch (subType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
                // 2G网络
                type = "2g";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_UMTS:
                // 3G网络
                type = "3g";
                break;
            default:
                type = "other";
                break;
        }

        return type;
    }


    public static String getIPAddress(Context context) throws SocketException {
        String ret = null;
        if ((ret = getIPAddressPrivate(context)) != null) {
            return ret;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {

                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } else {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {

                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        return null;
    }


    private static String getIPAddressPrivate(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if (dhcp != null) {
            int ipAddress = dhcp.ipAddress;
            return (ipAddress & 0xff) + "." + ((ipAddress >> 8) & 0xff) + "." +
                    ((ipAddress >> 16) & 0xff) + "." + ((ipAddress >> 24) & 0xff);
        }

        return null;
    }


    public static String getSsid(Context ctx) {
        WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.getConnectionInfo() != null) {
            return mWifiManager.getConnectionInfo().getSSID();
        }
        return null;
    }


    public static boolean isSSIDSame(Context context, String ssid) {
        boolean ret = false;
        if (context != null && ssid != null) {
            if (ssid != null && !ssid.equals("")) {
                ret = ssid.equals(getSsid(context));
            } else {
                ret = true;
            }
        } else {
            if (ssid == null || ssid.equals("")) {
                ret = true;
            }
        }
        return ret;
    }


    @SuppressLint("DefaultLocale")
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

}
