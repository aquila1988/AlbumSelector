package com.aquila.lib.album.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/***
 *@date 创建时间 2018/3/23 11:02
 *@author 作者: yulong
 *@description 图片的加载工具类
 */
public class ImageLoadUtil {


    public static void loadImageWithOption(final ImageView imageView, String url, RequestOptions options) {
        System.out.println("url = " + url);
        Glide.with(imageView.getContext()).load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options).into(imageView);
    }

    public static <T> void loadImageWithDefault(final ImageView imageView, T imgUrl, int defaultId) {
        if (imgUrl != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(defaultId);
            Glide.with(imageView.getContext()).load(imgUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.4f)
                    .apply(requestOptions).into(imageView);
        }
    }

    public static <T> void getBitmapFromUrl(Context context,T t, SimpleTarget<Drawable> simpleTarget) {
        RequestOptions options = new RequestOptions();
        Glide.with(context).load(t).apply(options).into(simpleTarget);
    }

    public static <T> void loadImage(final ImageView imageView, T t) {
        Glide.with(imageView.getContext()).load(t)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static void loadBitMapImage(final ImageView imageView, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(imageView.getContext()).load(bytes)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }


    public static void loadImageByUrl(final ImageView imageView, String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(imageView.getContext()).load(imgUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    public static void loadCircularImageByUrl(final ImageView imageView, String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            RoundedCorners roundedCorners = new RoundedCorners(10);
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
            Glide.with(imageView.getContext()).load(imgUrl).apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }



    public static void loadImageByUrl(final ImageView imageView, String imgUrl, int defaultResId) {
        RequestOptions options = new RequestOptions();
        options.placeholder(defaultResId);
        Glide.with(imageView.getContext()).load(imgUrl)
                .transition(DrawableTransitionOptions.withCrossFade()).apply(options)
                .into(imageView);
    }

    public static <T> void loadCircleImageWithDefault(final ImageView imageView, T imgUrl, int defaultResId) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transform(new CenterCrop());
        Glide.with(imageView.getContext()).load(imgUrl).apply(requestOptions).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                loadCircleImage(imageView, imgUrl);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                loadCircleImage(imageView, defaultResId);
            }
        });
    }

    /*加载图片转换为圆形*/
    public static <T> void loadCircleImage(final ImageView imageView, T path) {
        RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop());
        Glide.with(imageView.getContext()).load(path).apply(options).into(imageView);
    }



    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*将Bitmap写入到文件中*/
    public static File writeBitmapToFile(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            if (bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }
}
