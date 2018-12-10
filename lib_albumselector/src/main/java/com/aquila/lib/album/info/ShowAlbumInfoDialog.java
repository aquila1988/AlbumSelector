package com.aquila.lib.album.info;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.aquila.lib.album.AlbumFileEntity;
import com.aquila.lib.album.R;
import com.aquila.lib.album.base.BaseListAdapter;
import com.aquila.lib.album.base.BaseViewHolder;
import com.aquila.lib.album.widget.SideJustifyTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * @date 创建时间 2018/12/8 12:01
 * @author 作者: W.YuLong
 * @description
 */
public class ShowAlbumInfoDialog extends Dialog {
    public ShowAlbumInfoDialog(@NonNull Context context) {
        super(context);
    }

    private ListView dataListView;
    private InfoListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_album_info_layout);
        dataListView = findViewById(R.id.info_data_ListView);
        listAdapter = new InfoListAdapter();
        dataListView.setAdapter(listAdapter);
        configDialog();
    }

    protected void configDialog() {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = Gravity.CENTER;
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public void showDialogInfo(AlbumFileEntity entity) {
        show();
        listAdapter.setDataList(initAlbumInfo(entity));
    }


    private List<AlbumInfoEntity> initAlbumInfo(AlbumFileEntity entity) {
        List<AlbumInfoEntity> infoList = new ArrayList<>();
        File file = new File(entity.getPath());
        infoList.add(new AlbumInfoEntity("名称:", file.getName()));
        infoList.add(new AlbumInfoEntity("路径:", file.getAbsolutePath()));
        infoList.add(new AlbumInfoEntity("大小:", getFileSizeFormat(file.length())));
        infoList.add(new AlbumInfoEntity("日期:", getFormatDate(file.lastModified())));
        switch (entity.getMediaType()) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                initMultipleInfo(entity.getPath(), infoList);
                break;
            default:
                initPictureInfo(entity.getPath(), infoList);
        }
        return infoList;
    }

    private void initPictureInfo(String path, List<AlbumInfoEntity> infoList) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        infoList.add(new AlbumInfoEntity("分辨率:", String.format("%dx%d", options.outHeight, options.outWidth)));

    }


    /***
     * 加载多媒体的信息
     * @param path
     */
    private void initMultipleInfo(String path, List<AlbumInfoEntity> infoList) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(path);
            String width = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String date = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);

            infoList.add(new AlbumInfoEntity("分辨率:", String.format("%sx%s", width, height)));
            infoList.add(new AlbumInfoEntity("时长:", formatTime(Long.parseLong(duration))));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            metadataRetriever.release();
        }
    }


    private static String formatTime(long duration) {
        duration /= 1000;
        String time;
        if (duration < 60) {
            time = String.format("00:%02d", duration);
        } else if (duration < 3600) {
            time = String.format("%02d:%02d", duration / 60, duration % 60);
        } else {
            time = String.format("%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60, duration % 60);
        }
        return time;
    }

    /**
     * 获取格式化时间
     */
    private String getFormatDate(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);

    }


    public static void main(String[] args) {
        System.out.println(formatTime(130));
        System.out.println(formatTime(13000));
        System.out.println(formatTime(65000));
        System.out.println(formatTime(3700000));
        System.out.println(formatTime(100000000L));
//        System.out.println(getFileSizeFormat((long) (1024 * 1.23)));
//        System.out.println(getFileSizeFormat((long) (1024 * 1230)));
//        System.out.println(getFileSizeFormat(1024 * 123));
//        System.out.println(getFileSizeFormat(1024 * 1024 * 345));
//        System.out.println(getFileSizeFormat((long) (1024 * 1024 * 34.000)));
//        System.out.println(getFileSizeFormat((long) (1024 * 1024 * 1024* 312.3)));

    }

    /***
     * 格式化文件大小
     * @param size
     * @return
     */
    private String getFileSizeFormat(long size) {
        String formatSize;
        String unit;
        if (size < 1024) {
            formatSize = String.valueOf(size);
            unit = "B";
        } else if (size < 1048576) {
            formatSize = String.format("%.1f", (double) size / 1024);
            unit = "K";
        } else if (size < 1073741824) {
            formatSize = String.format("%.1f", (double) size / 1048576);
            unit = "M";
        } else if (size < 1099511627776L) {
            formatSize = String.format("%.1f", (double) size / 1073741824);
            unit = "G";
        } else {
            formatSize = String.format("%.1f", (double) size / 1099511627776L);
            unit = "T";
        }
        return trimDotEndZero(formatSize) + unit;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     */
    public String trimDotEndZero(String oriStr) {
        if (oriStr.indexOf(".") > 0) {
            //去掉多余的0
            oriStr = oriStr.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            oriStr = oriStr.replaceAll("[.]$", "");
        }
        return oriStr;
    }


    private static class InfoListAdapter extends BaseListAdapter {
        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new InfoItemViewHolder(parent);
        }
    }


    private static class InfoItemViewHolder extends BaseViewHolder {
        private SideJustifyTextView titleTextView;
        private TextView contentTextView;

        public InfoItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.list_item_album_info_layout);
            titleTextView = itemView.findViewById(R.id.item_title_TextView);
            contentTextView = itemView.findViewById(R.id.item_content_TextView);
        }

        @Override
        public <T> void initUIData(T t) {
            AlbumInfoEntity entity = (AlbumInfoEntity) t;
            titleTextView.setText(entity.getTitle());
            contentTextView.setText(entity.getContent());
        }
    }

}
