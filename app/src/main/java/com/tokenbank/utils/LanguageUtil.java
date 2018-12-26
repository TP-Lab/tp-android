package com.tokenbank.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(pNewUserLocale);
            DisplayMetrics displayMetrics = pContext.getResources().getDisplayMetrics();
            pContext.getResources().updateConfiguration(configuration, displayMetrics);
            updateContext = pContext.createConfigurationContext(configuration);
        } else {
            configuration.locale = pNewUserLocale;
            updateContext = pContext;
            DisplayMetrics displayMetrics = pContext.getResources().getDisplayMetrics();
            pContext.getResources().updateConfiguration(configuration, displayMetrics);
        }
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
        return pNewUserLocale != null && !getCurrentLocale(pContext).equals(pNewUserLocale);
    }


//    public static Context selectLanguage(Context context, String language) {
//        Context updateContext;
//        //设置语言类型
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            updateContext = createConfigurationResources(context, language);
//        } else {
//            applyLanguage(context, language);
//            updateContext = context;
//        }
//        //保存设置语言的类型
//        return updateContext;
//    }

//    @TargetApi(Build.VERSION_CODES.N)
//    private static Context createConfigurationResources(Context context, String language) {
//        //设置语言类型
//        Resources resources = context.getResources();
//        Configuration configuration = resources.getConfiguration();
//        Locale locale = new Locale(language);
//        configuration.setLocale(locale);
//        return context.createConfigurationContext(configuration);
//    }
//
//    private static void applyLanguage(Context context, String language) {
//        Resources resources = context.getResources();
//        Configuration configuration = resources.getConfiguration();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        Locale locale = new Locale(language);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            configuration.setLocale(locale);
//        } else {
//            configuration.locale = locale;
//        }
//        resources.updateConfiguration(configuration, dm);
//    }
}
