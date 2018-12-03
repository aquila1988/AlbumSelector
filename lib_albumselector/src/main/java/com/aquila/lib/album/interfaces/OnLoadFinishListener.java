package com.aquila.lib.album.interfaces;

import com.aquila.lib.album.AlbumFolderEntity;

import java.util.ArrayList;

/***
 * @date 创建时间 2018/11/17 11:22
 * @author 作者: W.YuLong
 * @description
 */
public interface OnLoadFinishListener {
    void onLoadFinish(ArrayList<AlbumFolderEntity> folderList);
}
