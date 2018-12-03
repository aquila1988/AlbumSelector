package com.aquila.lib.album.interfaces;

import com.aquila.lib.album.AlbumFileEntity;

import java.util.List;

/***
 * @date 创建时间 2018/11/17 18:08
 * @author 作者: W.YuLong
 * @description 多选模式下的选择回调
 */
public interface OnMultipleItemSelectCallback {
    void onMultipleItemSelect(List<AlbumFileEntity> selectList);
}
