package com.tokenbank.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.tokenbank.R;
import com.tokenbank.config.AppConfig;


public class HookIcon extends View {

    private final static float DP = AppConfig.getContext().getResources().getDisplayMetrics().density;

    private final static float mCircleRadius = DP * 12;
    private final static float mPaintWidth = DP * 3;
    private Paint mPaint;
    private Path mPath;
    private PathMeasure mPathMeasure;
    private ValueAnimator mValueAnimator;
    private float mDrawProgress;

    public HookIcon(Context context) {
        this(context, null);
    }

    public HookIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HookIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAnim();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mPaintWidth);
        Path path = new Path();
        //钩的左边起点
        float startPointX = mCircleRadius * 0.6F;
        float startPointY = mCircleRadius * 1.1F;
        path.moveTo(startPointX, startPointY);
        //左边终点
        float leftEndPointX = mCircleRadius * 0.9F;
        float leftEndPointY = startPointY + mCircleRadius * 0.3F;
        path.lineTo(leftEndPointX, leftEndPointY);
        //右边终点
        path.lineTo(mCircleRadius * 1.5F, mCircleRadius * 0.8F);
        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(path, false);
        mPath = new Path();
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(800);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDrawProgress = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    public void startAnim() {
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint != null) {
            int mWidth = getWidth();
            int mHeight = getHeight();
            //绘制圆
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mWidth / 2, mHeight / 2, mCircleRadius, mPaint);

            mPaint.setColor(getContext().getResources().getColor(R.color.color_detail_receive));
            mPaint.setStyle(Paint.Style.STROKE);
            float stop = mPathMeasure.getLength() * mDrawProgress;
            mPath.reset();
            mPathMeasure.getSegment(0, stop, mPath, true);
            canvas.drawPath(mPath, mPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
        mValueAnimator = null;
    }
}
