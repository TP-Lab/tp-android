package com.tokenbank.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;

public class LanguageUtil {
    private static final String TAG = "LanguageUtil";

    /**
     * language key
     */
    private static final String LANGUAGE = "LANGUAGE";

    /**
     * select key
     */
    private static final String SELECT = "SELECT";

    /**
     *  get user language
     *  @param pContext
     *  @return Locale
     */
    public static Locale getUserLocale(Context pContext) {
        String fileName = pContext.getPackageName() + "_" + "LANGUAGE";
        SharedPreferences preferences = pContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String select = preferences.getString(SELECT, "auto");
        if (TextUtils.equals(select, "auto")) {
            Locale locale;
            Configuration configuration = Resources.getSystem().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = configuration.getLocales().get(0);
            } else {
                locale = configuration.locale;
            }
            return locale;
        }
        String localeLan = preferences.getString(LANGUAGE, Locale.getDefault().getLanguage());
        return new Locale(localeLan);
    }

    /**
     *  get user language
     *  @param pContext
     *  @return Locale
     */
    public static String getUserSelect(Context pContext) {
        String fileName = pContext.getPackageName() + "_" + "LANGUAGE";
        SharedPreferences preferences = pContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String select = preferences.getString(SELECT, "auto");
        return select;
    }

    /**
     * get app language
     *
     * @param pContext
     * @return Locale
     *    
     */
    public static Locale getCurrentLocale(Context pContext) {
        Locale Locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale = pContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            Locale = pContext.getResources().getConfiguration().locale;
        }
        return Locale;
    }

    /**
     * save the language
     *
     * @param pContext
     * @param pUserLocale    
     */
    public static void saveUserLocale(Context pContext, Locale pUserLocale) {
        String fileName = pContext.getPackageName() + "_" + LANGUAGE;
        SharedPreferences preferences = pContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor Edit = preferences.edit();
        Edit.putString(LANGUAGE, pUserLocale.getLanguage());
        Edit.apply();
        if (needUpdateLocale(pContext, pUserLocale)) {
            EventBus.getDefault().post("EVENT_REFRESH_LANGUAGE");
        }
    }

    /**
     * save the select
     *
     * @param pContext
     * @param pSelect     
     */
    public static void saveUserSelect(Context pContext, String pSelect) {
        String fileName = pContext.getPackageName() + "_" + LANGUAGE;
        SharedPreferences preferences = pContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor Edit = preferences.edit();
        Edit.putString(SELECT, pSelect);
        Edit.apply();
    }

    /**
     * change language
     *
     * @param pContext
     * @param pNewUserLocale    
     */
    public static Context updateLocale(Context pContext, Locale pNewUserLocale) {
        Context updateContext;
        Configuration configuration = pContext.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(pNewUserLocale);
            updateContext = pContext.createConfigurationContext(configuration);
        } else {
            configuration.locale = pNewUserLocale;
            updateContext = pContext;
        }
        pContext.getResources().updateConfiguration(configuration, pContext.getResources().getDisplayMetrics());
        return updateContext;
    }

    /**
     * need change or not
     *
     * @param pContext
     * @param pNewUserLocale
     * @return true / false
     *    
     */
    public static boolean needUpdateLocale(Context pContext, Locale pNewUserLocale) {
        return pNewUserLocale != null && !TextUtils.equals(getCurrentLocale(pContext).getLanguage(), pNewUserLocale.getLanguage());
    }
}
