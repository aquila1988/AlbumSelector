package com.aquila.lib.album.interfaces;

import android.view.View;

/***
 * @date 创建时间 2018/12/3 21:36
 * @author 作者: W.YuLong
 * @description
 */
public interface OnViewClickListener {
    /***
     * 通用的点击事件接管
     * @param v 被点击的View
     * @param tag Tag标记
     */
    <T>void onClickAction(View v, String tag, T t);
}
