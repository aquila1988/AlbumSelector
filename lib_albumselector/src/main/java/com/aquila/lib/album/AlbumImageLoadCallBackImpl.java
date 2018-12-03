package com.aquila.lib.album;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.aquila.lib.album.interfaces.OnLoadFinishListener;
import com.aquila.lib.album.utils.AlbumTypeDefine;
import com.aquila.lib.album.utils.CLog;

import java.io.File;
import java.util.ArrayList;

/***
 * @date 创建时间 2018/11/17 11:15
 * @author 作者: W.YuLong
 * @description
 */
public class AlbumImageLoadCallBackImpl implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String[] fileParams = {
            FileColumns._ID,
            FileColumns.DATA,
            FileColumns.MEDIA_TYPE,
            FileColumns.DISPLAY_NAME,
            FileColumns.DATE_ADDED,
            FileColumns.SIZE,
            FileColumns.MIME_TYPE,
    };

    private Context context;

    private OnLoadFinishListener onLoadFinishListener;

    public AlbumImageLoadCallBackImpl(Context context, OnLoadFinishListener onLoadFinishListener) {
        this.context = context;
        this.onLoadFinishListener = onLoadFinishListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(String.format(" %s > 0 AND ", FileColumns.SIZE));
        switch (id) {
            case AlbumTypeDefine.TYPE_VIDEO:
                sqlFilter.append(String.format(" %s = %s ", FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_VIDEO));
                break;
            case AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO:
                sqlFilter.append(String.format(" %s = %s OR %s = %s ",
                        FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_IMAGE,
                        FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_VIDEO));
                break;
            default:
                sqlFilter.append(String.format(" %s = %s ", FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_IMAGE));
                break;
        }

        CursorLoader loader = new CursorLoader(context,
                MediaStore.Files.getContentUri("external"),
                fileParams, sqlFilter.toString(),
                null, FileColumns.DATE_MODIFIED+ " DESC");
        return loader;
    }

    /*获取本地的视频时长*/
    private long getVideoDuration(AlbumFileEntity fileEntity) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DURATION},
                String.format("%s = %s and %s = '%s' and %s = '%s'",
                        MediaStore.Video.Media.SIZE, fileEntity.getSize(),
                        MediaStore.Video.Media.DATA, fileEntity.getPath(),
                        MediaStore.Video.Media.DISPLAY_NAME, fileEntity.getName()), null, null);
        long duration = -1;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            cursor.close();
        }
        return duration;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            ArrayList<AlbumFolderEntity> folderList = new ArrayList<>();
            if (cursor.getCount() > 0) {
                ArrayList<AlbumFileEntity> allFileList = new ArrayList<>();
                cursor.moveToFirst();
                do {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(FileColumns.DATA));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(FileColumns.DISPLAY_NAME));
                    long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(FileColumns.DATE_ADDED));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(FileColumns.SIZE));
                    int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(FileColumns.MEDIA_TYPE));

                    File file = new File(path);
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(path) || !file.exists()) {
                        CLog.d(String.format("文件错误：name = %s,time= %d, path = %s", name, dateTime, path));
                        continue;
                    }

                    AlbumFileEntity fileEntity = new AlbumFileEntity(path, name, dateTime);
                    fileEntity.setSize(size);
                    fileEntity.setMediaType(mediaType);

                    if (mediaType == FileColumns.MEDIA_TYPE_VIDEO) {
                        long duration = getVideoDuration(fileEntity);
                        fileEntity.setDuration(duration);
                    }
                    allFileList.add(fileEntity);

                    //读取文件夹信息
                    File folderFile = file.getParentFile();
                    if (folderFile != null && folderFile.exists()) {
                        AlbumFolderEntity folder = getExistFolder(folderList, folderFile);
                        if (folder == null) {
                            folder = initFolderEntity(folderFile, fileEntity);
                            folderList.add(folder);
                        } else {
                            folder.addFileEntity(fileEntity);
                        }
                    }
                } while (cursor.moveToNext());

                if (onLoadFinishListener != null) {
                    if (allFileList.size() > 0) {

                        AlbumFileEntity entity = allFileList.get(0);

                        AlbumFolderEntity firstAllFolder = new AlbumFolderEntity();
                        File firstParentFile = new File(entity.getPath()).getParentFile();
                        firstAllFolder.setFolderName("全部相册");
                        firstAllFolder.setFolderPath(firstParentFile.getAbsolutePath());
                        firstAllFolder.setCoverEntity(entity);
                        firstAllFolder.getFileEntityList().addAll(allFileList);
                        folderList.add(0, firstAllFolder);
                    }
                    onLoadFinishListener.onLoadFinish(folderList);
                }

            }
        }
    }

    private AlbumFolderEntity getExistFolder(ArrayList<AlbumFolderEntity> folderList, File folderFile) {
        if (folderList != null && folderList.size() > 0) {
            for (AlbumFolderEntity entity : folderList) {
                if (entity.getFolderPath().equals(folderFile.getAbsolutePath())) {
                    return entity;
                }
            }
        }
        return null;
    }

    /*初始化文件夹的相关信息*/
    private AlbumFolderEntity initFolderEntity(File folderFile, AlbumFileEntity fileEntity) {
        AlbumFolderEntity folderEntity = new AlbumFolderEntity();
        folderEntity.setFolderName(folderFile.getName());
        folderEntity.setFolderPath(folderFile.getAbsolutePath());
        folderEntity.setCoverEntity(fileEntity);
        folderEntity.addFileEntity(fileEntity);
        return folderEntity;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
