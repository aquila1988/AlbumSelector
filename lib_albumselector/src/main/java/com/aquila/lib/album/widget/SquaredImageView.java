package com.aquila.lib.album.widget;

import android.content.Context;
import android.util.AttributeSet;

/***
 *@date 创建时间 2018/11/16 16:36
 *@author 作者: W.YuLong
 *@description  正方形的ImageView
 */
public class SquaredImageView extends android.support.v7.widget.AppCompatImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
