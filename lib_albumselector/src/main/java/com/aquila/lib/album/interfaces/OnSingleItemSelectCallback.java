package com.aquila.lib.album.interfaces;

import com.aquila.lib.album.AlbumFileEntity;

/***
 * @date 创建时间 2018/11/17 18:07
 * @author 作者: W.YuLong
 * @description 单选模式下的选择回调
 */
public interface OnSingleItemSelectCallback {
    void onSingleItemSelect(AlbumFileEntity albumFileEntity);
}
