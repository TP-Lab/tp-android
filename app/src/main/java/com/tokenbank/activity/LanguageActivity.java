package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tokenbank.R;
import com.tokenbank.utils.LanguageUtil;
import com.tokenbank.view.TitleBar;

import java.util.Locale;

public class LanguageActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    private RelativeLayout mLayoutAuto;
    private RelativeLayout mLayoutLanguageZh;
    private RelativeLayout mLayoutLanguageEn;
    private ImageView mImageAuto;
    private ImageView mImageChinese;
    private ImageView mImageEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.title_languages));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mLayoutAuto = (RelativeLayout) findViewById(R.id.layout_auto);
        mLayoutAuto.setOnClickListener(this);
        mImageAuto = (ImageView) mLayoutAuto.findViewById(R.id.img_auto);
        mLayoutLanguageZh = (RelativeLayout) findViewById(R.id.layout_chinese);
        mImageChinese = (ImageView) mLayoutLanguageZh.findViewById(R.id.img_chinese);
        mLayoutLanguageZh.setOnClickListener(this);
        mLayoutLanguageEn = (RelativeLayout) findViewById(R.id.layout_english);
        mImageEnglish = (ImageView) mLayoutLanguageEn.findViewById(R.id.img_english);
        mLayoutLanguageEn.setOnClickListener(this);

        String select = LanguageUtil.getUserSelect(this);
        imageShow(select);

    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutLanguageZh) {
            LanguageUtil.saveUserSelect(this, Locale.CHINESE.getLanguage());
            imageShow(Locale.CHINESE.getLanguage());
            LanguageUtil.saveUserLocale(this, Locale.CHINESE);
        } else if (view == mLayoutLanguageEn) {
            LanguageUtil.saveUserSelect(this, Locale.ENGLISH.getLanguage());
            imageShow(Locale.ENGLISH.getLanguage());
            LanguageUtil.saveUserLocale(this, Locale.ENGLISH);
        } else {
            LanguageUtil.saveUserSelect(this, "auto");
            imageShow("auto");
            LanguageUtil.saveUserLocale(this, Locale.getDefault());
        }
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }


    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onMiddleClick(View v) {

    }

    public static void startLanguageActivity(Context from) {
        Intent intent = new Intent(from, LanguageActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    private void imageShow(String select) {
        switch (select) {
            case "zh":
                mImageAuto.setVisibility(View.GONE);
                mImageChinese.setVisibility(View.VISIBLE);
                mImageEnglish.setVisibility(View.GONE);
                break;
            case "en":
                mImageAuto.setVisibility(View.GONE);
                mImageChinese.setVisibility(View.GONE);
                mImageEnglish.setVisibility(View.VISIBLE);
                break;
            case "auto":
                mImageAuto.setVisibility(View.VISIBLE);
                mImageChinese.setVisibility(View.GONE);
                mImageEnglish.setVisibility(View.GONE);
                break;
        }
    }
}
