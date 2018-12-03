package com.aquila.lib.album.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/***
 * @date 创建时间 2018/8/13 12:22 PM
 * @author 作者: liweifeng
 * @description 处理屏幕相关的参数
 */
public class ScreenUtil {
    private static int screenWidth, screenHeight;

    public static int getScreenHeight(Context context) {
        if (screenHeight <= 0) {
            initScreenSize(context);
        }
        return screenHeight;
    }

    public static int getScreenWidth(Context context) {
        if (screenWidth <= 0) {
            initScreenSize(context);
        }
        return screenWidth;
    }


    private static void initScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
    }
}
