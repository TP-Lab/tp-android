package com.tokenbank.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.tokenbank.R;


public class MethodCompat {

    public static void setBackgroundBitmap(View view, Bitmap bitmap) {
        setBackgroundDrawable(view, new BitmapDrawable(view.getResources(), bitmap));
    }

    public static void setBackgroundDrawable(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    public static void setLeftDrawableWithBounds(Context context, TextView textView, int resId, int width, int height) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, ViewUtil.dip2px(context, width), ViewUtil.dip2px(context, height));
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    public static void setRightDrawableWithBounds(Context context, TextView textView, int resId, int width, int height) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, ViewUtil.dip2px(context, width), ViewUtil.dip2px(context, height));
        textView.setCompoundDrawables(null, null, drawable, null);
    }

    public static void setTopDrawableWithBounds(Context context, TextView textView, int resId, int width, int height) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, ViewUtil.dip2px(context, width), ViewUtil.dip2px(context, height));
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    public static void setBottomDrawableWithBounds(Context context, TextView textView, int resId, int width, int height) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, ViewUtil.dip2px(context, width), ViewUtil.dip2px(context, height));
        textView.setCompoundDrawables(null, null, null, drawable);
    }

    public static Drawable getDrawable(View view, int id) {
        return getDrawable(view.getResources(), id);
    }

    public static Drawable getDrawable(Context context, int id) {
        return getDrawable(context.getResources(), id);
    }

    @TargetApi(21)
    public static Drawable getDrawable(Resources resources, int id) {
        try {
            return resources.getDrawable(id);
        } catch (Throwable e) {
        }

        try {
            return resources.getDrawable(id, null);
        } catch (Throwable e) {
        }

        return null;
    }

    @TargetApi(21)
    public static void setBackgroundColor(View view, Resources resources, int color) {
        try {
            view.setBackgroundColor(resources.getColor(color));
        } catch (Throwable e) {
        }

        try {
            view.setBackgroundColor(resources.getColor(color, null));
        } catch (Throwable e) {
        }
    }
}
