package com.aquila.lib.album.interfaces;

import com.aquila.lib.album.AlbumFolderEntity;

/***
 * @date 创建时间 2018/11/17 15:01
 * @author 作者: W.YuLong
 * @description 文件夹选择的接口
 */
public interface OnFolderSelectListener {
    void onFolderSelect(AlbumFolderEntity folderEntity);
}
