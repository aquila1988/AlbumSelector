package com.aquila.lib.album;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aquila.lib.album.interfaces.OnAlbumSelectCallback;
import com.aquila.lib.album.interfaces.OnFolderSelectListener;
import com.aquila.lib.album.interfaces.OnLoadFinishListener;
import com.aquila.lib.album.interfaces.OnNotifySelectCountListener;
import com.aquila.lib.album.interfaces.OnSingleItemSelectCallback;
import com.aquila.lib.album.utils.PermissionsUtil;
import com.aquila.lib.album.utils.SPSingleton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @date 创建时间 2018/11/16 15:37
 * @author 作者: W.YuLong
 * @description 相册选择的页面
 */
public class AlbumSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CROP_IMAGE = 123;

    private ImageView backImageView;
    private Button finishButton;
    private TextView titleTextView;
    private TextView previewTextView;
    private TextView folderTextView;
    private TextView resetTextView;
    private LinearLayout folderLayout;
    private GridView dataGridView;

    private AlbumGridAdapter gridAdapter;

    private FolderListPopupWindow folderPopupWindow;

    private static Map<String, OnAlbumSelectCallback> listenersMap = new HashMap<>();

    private AlbumSelectOption option;
    private String key;

    private long lastClickTime;

    /*静态的进入方法*/
    public static void startAlbumSelectActivity(Context context, AlbumSelectOption option, OnAlbumSelectCallback callback) {
        String key = System.currentTimeMillis() + "";

        Intent intent = new Intent(context, AlbumSelectorActivity.class);
        intent.putExtra("AlbumSelectOption", option);
        intent.putExtra("key", key);
        context.startActivity(intent);

        listenersMap.put(key, callback);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_selector_layout);
        StatusBarUtil.setColorNoTranslucent(this, 0xff222222);

        //因为这个是在Lib库的单例，需要Application的Context，因此在这里进行初始化获取Application的Context
        SPSingleton.init(getApplicationContext());

        key = getIntent().getStringExtra("key");
        option = getIntent().getParcelableExtra("AlbumSelectOption");


        initViewFromXML();
        setViewListeners();

        initUI();

    }

    private void initUI() {
        if (!TextUtils.isEmpty(option.getTitle())) {
            titleTextView.setText(option.getTitle());
        }

        finishButton.setText(String.format("完成(%d/%d)", 0, option.getMax()));

        //单选模式隐藏部分按钮
        if (!option.isMultipleSelectMode()) {
            finishButton.setVisibility(View.GONE);
            resetTextView.setVisibility(View.GONE);
            previewTextView.setVisibility(View.GONE);
        }

        gridAdapter = new AlbumGridAdapter();

        gridAdapter.setMultipleSelectMode(option.isMultipleSelectMode());
        gridAdapter.setMax(option.getMax());
        gridAdapter.setOnNotifySelectCountListener(onNotifySelectCountListener);
        gridAdapter.setOnSingleItemSelectCallback(onSingleItemSelectCallback);
        dataGridView.setAdapter(gridAdapter);

        folderPopupWindow = new FolderListPopupWindow(this, onFolderSelectListener);

        PermissionsUtil.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                , new PermissionsUtil.OnPermissionCallbackImpl() {
                    @Override
                    public void onSuccess(String[] permission) {
                        AlbumImageLoadCallBackImpl imageLoadCallBack = new AlbumImageLoadCallBackImpl(AlbumSelectorActivity.this, onLoadFinishListener);
                        getSupportLoaderManager().initLoader(option.getSelectType(), null, imageLoadCallBack);

                    }
                });

    }

    private void setViewListeners() {
        backImageView.setOnClickListener(this);
        previewTextView.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        folderLayout.setOnClickListener(this);
        titleTextView.setOnClickListener(this);
        resetTextView.setOnClickListener(this);
    }

    private OnFolderSelectListener onFolderSelectListener = new OnFolderSelectListener() {
        @Override
        public void onFolderSelect(AlbumFolderEntity folderEntity) {
            gridAdapter.setDataList(folderEntity.getFileEntityList());
            dataGridView.smoothScrollToPosition(0);

            folderTextView.setText(folderEntity.getFolderName());
            folderPopupWindow.dismiss();
        }
    };

    private OnSingleItemSelectCallback onSingleItemSelectCallback = new OnSingleItemSelectCallback() {
        @Override
        public void onSingleItemSelect(AlbumFileEntity entity) {
            //如果是图片切为裁剪模式
            if (option.isCrop() && MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE == entity.getMediaType()) {
                cropAlbumFileEntity = entity;
                cropForPhoto(new File(entity.getPath()));
            } else {
                OnAlbumSelectCallback callBack = listenersMap.remove(key);
                if (callBack != null) {
                    callBack.onSingleSelect(entity);
                }
                onBackPressed();

            }
        }
    };

    private AlbumFileEntity cropAlbumFileEntity;
    private String cropPath;

    private void cropForPhoto(File file) {
        try {
            Uri uri = parseUri(file);
            Intent intent = new Intent("com.android.camera.action.CROP");
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "image/*");
            //直接裁剪
            //设置裁剪之后的图片路径文件
            File cutFile = new File(file.getParent() + File.separator + "crop_" + file.getName());
            if (cutFile.exists()) {
                cutFile.delete();
            }
            cutFile.createNewFile();
            cropPath = cutFile.getAbsolutePath();

            //初始化 uri
            intent.putExtra("crop", true);
            ImageCropOption imageCropOption = option.getImageCropOption();
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX", imageCropOption.getAspectX())
                    .putExtra("aspectY", imageCropOption.getAspectY());
            //设置要裁剪的宽高
            intent.putExtra("outputX", imageCropOption.getOutputX())
                    .putExtra("outputY", imageCropOption.getOutputY())
                    .putExtra("scale", imageCropOption.isScale())
                    .putExtra("outputFormat", imageCropOption.isCircleCrop())
                    //如果图片过大，会导致oom，这里设置为false
                    .putExtra("return-data", false);

            Uri outputUri = Uri.fromFile(cutFile);
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            startActivityForResult(intent, CROP_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从裁剪图片回来的
        if (resultCode == RESULT_OK && requestCode == CROP_IMAGE) {
            cropAlbumFileEntity.setPath(cropPath);
            OnAlbumSelectCallback callBack = listenersMap.remove(key);
            if (callBack != null) {
                callBack.onSingleSelect(cropAlbumFileEntity);
            }
            onBackPressed();
        }
    }

    protected Uri parseUri(File cameraFile) {
        Uri imageUri;
        String authority = getPackageName() + ".fileprovider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imageUri = FileProvider.getUriForFile(this, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }

    OnLoadFinishListener onLoadFinishListener = new OnLoadFinishListener() {
        /*表示图片或视频加载完成*/
        @Override
        public void onLoadFinish(ArrayList<AlbumFolderEntity> folderList) {
            AlbumFolderEntity entity = null;
            if (folderList == null || folderList.size() == 0) {
                return;
            }
            int defaultPosition = 0;
            int i = 0;

            String lastFolderPath = SPSingleton.get().getString(AlbumFolderAdapter.SP_selectFolderNameAndPath, "");

            for (AlbumFolderEntity folderEntity : folderList) {
                if (folderEntity.getFolderInfo().equals(lastFolderPath)) {
                    defaultPosition = i;
                    break;
                }
                i++;
            }
            entity = folderList.get(defaultPosition);

            gridAdapter.setDataList(entity.getFileEntityList());
            folderTextView.setText(entity.getFolderName());

            folderPopupWindow.setDataList(folderList);
            folderPopupWindow.setDefaultFolder(defaultPosition);
        }
    };


    private void initViewFromXML() {
        backImageView = findViewById(R.id.album_selector_back_ImageView);
        finishButton = findViewById(R.id.album_selector_finish_Button);
        titleTextView = findViewById(R.id.album_selector_title_TextView);
        previewTextView = findViewById(R.id.album_selector_preview_TextView);
        folderTextView = findViewById(R.id.album_selector_folder_name_TextView);
        resetTextView = findViewById(R.id.album_selector_reset_TextView);
        folderLayout = findViewById(R.id.album_selector_folder_name_layout);
        dataGridView = findViewById(R.id.album_selector_data_GridView);
    }


    private OnNotifySelectCountListener onNotifySelectCountListener = new OnNotifySelectCountListener() {
        @Override
        public void onSelectCount(int count) {
            finishButton.setText(String.format("完成(%d/%d)", count, option.getMax()));
        }
    };

    @Override
    public void onClick(View v) {
        if (v == backImageView) {
            onBackPressed();
        } else if (v == previewTextView) {
            showPreviewSelectFiles();
        } else if (v == resetTextView) {
            gridAdapter.unSelectAll();
        } else if (v == finishButton) {
            if (gridAdapter.getSelectList() == null && gridAdapter.getSelectList().size() == 0) {
                Toast.makeText(this, "您还未选择图片", Toast.LENGTH_SHORT).show();
                return;
            }

            OnAlbumSelectCallback callBack = listenersMap.remove(key);
            if (callBack != null) {
                callBack.onMultipleSelect(gridAdapter.getSelectList());
            }
            onBackPressed();
        } else if (v == folderLayout) {
            folderPopupWindow.showAtLocation(folderLayout, Gravity.LEFT | Gravity.BOTTOM, 0, 0);

        } else if (v == titleTextView) { //双击回到顶部
            if (System.currentTimeMillis() - lastClickTime < 500) {
                dataGridView.smoothScrollToPosition(0);
            }
            lastClickTime = System.currentTimeMillis();
        }
    }

    private void showPreviewSelectFiles() {
        if (gridAdapter.getSelectList() != null && gridAdapter.getSelectList().size() > 0) {
            List<String> imagePathList = new ArrayList<>();
            boolean needShowToast = false;
            for (AlbumFileEntity entity : gridAdapter.getSelectList()) {
                if (isImageFile(entity.getPath())) {
                    imagePathList.add(entity.getPath());
                } else {
                    needShowToast = true;
                }
            }
            AlbumImageScanDialog.with(this)
                    .setCLickDismiss(true).
                    setDataList(imagePathList).builder().show();

            if (needShowToast) {
                Toast.makeText(this, "您选择的文件中包含视频，目前暂不支持视频的预览！", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "您还未选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isImageFile(String path) {
        if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith("jpeg") || path.endsWith(".bmp")) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenersMap.remove(key);
    }
}
