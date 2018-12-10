package com.aquila.lib.album.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/***
 * @date 创建时间 2018/12/9 21:07
 * @author 作者: W.YuLong
 * @description
 */
public class ExpandListView extends ListView {
    public ExpandListView(Context context) {
        super(context);
    }

    public ExpandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置不滚动
     */

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
