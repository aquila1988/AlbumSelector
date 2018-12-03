package com.aquila.lib.album.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/***
 *@date 创建时间 2018/11/16 16:36
 *@author 作者: W.YuLong
 *@description  正方形的RelativeLayout
 */
public class SquareRelativeLayout extends RelativeLayout {
    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
