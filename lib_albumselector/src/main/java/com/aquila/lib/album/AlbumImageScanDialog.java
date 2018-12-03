package com.aquila.lib.album;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aquila.lib.album.base.BasePagerAdapter;
import com.aquila.lib.album.interfaces.OnViewClickListener;
import com.aquila.lib.album.utils.ImageLoadUtil;
import com.aquila.lib.album.utils.OnPageChangeListenerImpl;
import com.aquila.lib.album.widget.PinchImageView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

/***
 * @date 创建时间 2018/5/19 18:10
 * @author 作者: W.YuLong
 * @description 图片浏览的Dialog
 */
class AlbumImageScanDialog extends Dialog implements OnViewClickListener, View.OnClickListener {
    public static final String TAG_IMAGE_ITEM_CLICK = "TAG_IMAGE_ITEM_CLICK";
    public static final String TAG_VIDEO_ITEM_CLICK = "TAG_VIDEO_ITEM_CLICK";

    ViewPager viewPager;
    RelativeLayout titleLayout;
    ImageView backImageView;
    TextView titleTextView;

    private ImagePageAdapter imagePageAdapter;

    private Builder builder;

    private AlbumImageScanDialog(@NonNull Builder builder) {
        super(builder.context);
        this.builder = builder;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_album_image_scan_layout);

        viewPager = findViewById(R.id.dialog_image_scan_ViewPager);
        titleLayout = findViewById(R.id.dialog_top_title_layout);
        titleTextView = findViewById(R.id.dialog_title_TextView);
        backImageView = findViewById(R.id.dialog_back_ImageView);

        imagePageAdapter = new ImagePageAdapter();
        imagePageAdapter.setOnViewClickListener(this);
        viewPager.setAdapter(imagePageAdapter);

        imagePageAdapter.setDataList(builder.dataList);

        updateTitle(builder.currentIndex);

        viewPager.addOnPageChangeListener(new OnPageChangeListenerImpl() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                builder.currentIndex = position;
                updateTitle(builder.currentIndex);
            }
        });
        viewPager.setCurrentItem(builder.currentIndex);

        backImageView.setOnClickListener(this);
        titleLayout.setVisibility(builder.isHideTitle ? View.GONE : View.VISIBLE);

        configDialog();
    }

    protected void configDialog() {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = Gravity.FILL;
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void updateTitle(int index) {
        titleTextView.setText(String.format("%d/%d", (index + 1), imagePageAdapter.getCount()));
    }

    public <T> AlbumImageScanDialog addDataList(List<T> list) {
        if (builder.dataList == null) {
            builder.dataList = list;
        } else {
            builder.dataList.addAll(list);
        }
        imagePageAdapter.setDataList(builder.dataList);
        updateTitle(builder.currentIndex);
        return this;
    }

    public <T> AlbumImageScanDialog setDataList(List<T> list) {
        builder.dataList = list;
        imagePageAdapter.setDataList(builder.dataList);
        updateTitle(builder.currentIndex);
        return this;
    }

    /*设置当前的索引*/
    public AlbumImageScanDialog setCurrentPosition(int position) {
        builder.currentIndex = position;
        viewPager.setCurrentItem(position);
        return this;
    }

    @Override
    public <T> void onClickAction(View v, String tag, T t) {
        if (builder.remoteViewClickListener != null) {
            builder.remoteViewClickListener.onClickAction(v, tag, t);
        }
        if (builder.isCLickDismiss) {
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == backImageView) {
            dismiss();
        }
    }

    /***
     *@date 创建时间 2018/8/17 11:27
     *@author 作者: W.YuLong
     *@description
     */
    public static class Builder<T> {
        private Context context;
        private OnViewClickListener remoteViewClickListener;
        private boolean isCLickDismiss = false;
        private boolean isHideTitle = false;
        private int currentIndex = 0;
        private CharSequence title;
        private List<T> dataList;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDataList(List<T> list) {
            this.dataList = list;
            return this;
        }

        public Builder setRemoteViewClickListener(OnViewClickListener remoteViewClickListener) {
            this.remoteViewClickListener = remoteViewClickListener;
            return this;
        }

        public Builder setCLickDismiss(boolean CLickDismiss) {
            isCLickDismiss = CLickDismiss;
            return this;
        }

        public Builder setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
            return this;
        }

        public Builder setHideTitle(boolean hideTitle) {
            isHideTitle = hideTitle;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public AlbumImageScanDialog builder() {
            return new AlbumImageScanDialog(this);
        }
    }

    /***
     *@date 创建时间 2018/8/17 11:27
     *@author 作者: W.YuLong
     *@description
     */
    private static class ImagePageAdapter extends BasePagerAdapter {
        public OnViewClickListener onViewClickListener;

        public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
            this.onViewClickListener = onViewClickListener;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = LayoutInflater.from(container.getContext()).inflate(R.layout.album_image_view_only, container, false);
            PinchImageView imageView = v.findViewById(R.id.only_ImageView);
            LinearLayout loadingLayout = v.findViewById(R.id.image_loading_prompt_layout);
            TextView titleTextView = v.findViewById(R.id.image_loading_title_TextView);
            ProgressBar progressBar = v.findViewById(R.id.image_loading_ProgressBar);

            if (((String) dataList.get(position)).contains(".mp4")) {
                loadingLayout.setVisibility(View.GONE);
                imageView.setOnClickListener(v1 -> {
                    if (onViewClickListener != null) {
                        onViewClickListener.onClickAction(imageView, TAG_VIDEO_ITEM_CLICK, dataList.get(position));
                    }
                });
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ImageLoadUtil.getBitmapFromUrl(imageView.getContext(), dataList.get(position), new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        loadingLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        progressBar.setVisibility(View.GONE);
                        titleTextView.setText("图片加载失败");
                    }
                });
                imageView.setOnClickListener(v1 -> {
                    if (onViewClickListener != null) {
                        onViewClickListener.onClickAction(imageView, TAG_IMAGE_ITEM_CLICK, dataList.get(position));
                    }
                });
            }

            container.addView(v);
            return v;
        }
    }


}
