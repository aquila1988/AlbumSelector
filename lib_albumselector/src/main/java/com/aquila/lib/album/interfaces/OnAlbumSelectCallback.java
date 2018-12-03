package com.aquila.lib.album.interfaces;

import com.aquila.lib.album.AlbumFileEntity;

import java.util.List;

/***
 * @date 创建时间 2018/11/17 18:16
 * @author 作者: W.YuLong
 * @description
 */
public interface OnAlbumSelectCallback {
    void onSingleSelect(AlbumFileEntity albumFileEntity);
    void onMultipleSelect(List<AlbumFileEntity> selectList);
}
