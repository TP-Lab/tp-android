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


public class CrossIcon extends View {

    private static final float DP = AppConfig.getContext().getResources().getDisplayMetrics().density;

    private final float mCircleRadius = DP * 12;
    private final float mPaintWidth = DP * 3;
    private Paint mPaint;
    private Path mPath1;
    private Path mPath2;
    private PathMeasure mPathMeasure1;
    private PathMeasure mPathMeasure2;
    private ValueAnimator mValueAnimator;
    private float mDrawProgress;

    public CrossIcon(Context context) {
        this(context, null);
    }

    public CrossIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossIcon(Context context, AttributeSet attrs, int defStyleAttr) {
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
        //第一条线起点
        float startPointX = mCircleRadius * 0.65F;
        float startPointY = mCircleRadius * 0.65F;
        path.moveTo(startPointX, startPointY);
        //第一条线终点
        float leftEndPointX = mCircleRadius * 1.45F;
        float leftEndPointY = mCircleRadius * 1.45F;
        path.lineTo(leftEndPointX, leftEndPointY);
        mPathMeasure1 = new PathMeasure();
        mPathMeasure1.setPath(path, false);
        mPath1 = new Path();

        Path path2 = new Path();
        //第二条线起点
        startPointX = mCircleRadius * 1.45F;
        startPointY = mCircleRadius * 0.65F;
        path2.moveTo(startPointX, startPointY);
        //第二条线终点
        leftEndPointX = mCircleRadius * 0.65F;
        leftEndPointY = mCircleRadius * 1.45F;
        path2.lineTo(leftEndPointX, leftEndPointY);
        mPathMeasure2 = new PathMeasure();
        mPathMeasure2.setPath(path2, false);
        mPath2 = new Path();
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

            mPaint.setColor(getContext().getResources().getColor(R.color.color_detail_send));
            mPaint.setStyle(Paint.Style.STROKE);
            float stop = mPathMeasure1.getLength() * mDrawProgress;
            mPath1.reset();
            mPathMeasure1.getSegment(0, stop, mPath1, true);
            canvas.drawPath(mPath1, mPaint);
            stop = mPathMeasure2.getLength() * mDrawProgress;
            mPath2.reset();
            mPathMeasure2.getSegment(0, stop, mPath2, true);
            canvas.drawPath(mPath2, mPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
        mValueAnimator = null;
    }
}
