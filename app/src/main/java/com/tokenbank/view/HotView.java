package com.tokenbank.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tokenbank.R;

import java.util.ArrayList;
import java.util.List;


public class HotView extends ViewGroup implements OnClickListener {

    /**
     * 最大行数
     */
    private int maxLines = 3;

    private OnClickListener l;

    private List<Rect> rects = new ArrayList<Rect>();

    public HotView(Context context) {
        this(context, null);
    }

    public HotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public void setOnItemClickListener(OnClickListener l) {
        this.l = l;
    }

    public void setData(List<String> hots) {
        removeAllViews();
        if (hots != null && hots.size() > 0) {
            for (int i = 0; i < hots.size(); i++) {
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.hot_item_view, this, false);
                tv.setOnClickListener(this);
                try {
                    tv.setText(hots.get(i));
                    tv.setTag(i);
//                    GradientDrawable gd = (GradientDrawable) tv.getBackground();
//                    gd.setColor(Color.parseColor(olb.getColor()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addView(tv);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = getPaddingLeft();
        int height = getPaddingTop();

        // 行宽，行高
        int lineWidth = getPaddingLeft();
        int lineHeight = getPaddingTop();

        // 行数
        int lines = 1;

        int count = getChildCount();
        rects.clear();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            Rect rect = new Rect();
            // 换行
            if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                lineWidth = getPaddingLeft() + childWidth;
                height += lineHeight;
                lineHeight = childHeight;
                lines++;
            } else { // 未换行
                lineWidth += childWidth;
                lineHeight = Math.max(childHeight, lineHeight);
            }

            rect.top = height + lp.topMargin;
            rect.bottom = height + lineHeight - lp.bottomMargin;
            rect.right = lineWidth - lp.rightMargin;
            rect.left = lineWidth - childWidth + lp.leftMargin;

            if (lines > maxLines) {
                break;
            } else {
                rects.add(rect);
            }

            // 最后 一个
            if (i == count - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        height += getPaddingBottom();
        width += getPaddingRight();
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i < rects.size()) {
                Rect loc = rects.get(i);
                if (loc != null) {
                    View child = getChildAt(i);
                    child.layout(loc.left, loc.top, loc.right, loc.bottom);
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void onClick(View v) {
        if (l != null) {
            l.onClick(v);
        }
    }
}
