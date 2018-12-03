package com.aquila.lib.album;

import android.provider.MediaStore;

/***
 * @date 创建时间 2018/11/16 18:20
 * @author 作者: W.YuLong
 * @description 相册的相关属性
 */
public class AlbumFileEntity {

    private String path;
    private String name;
    private long time;
    private long size;
    //文件类型，图片还是视频
    private int mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

    private long duration;

    public AlbumFileEntity() {
    }

    public AlbumFileEntity(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int fileType) {
        this.mediaType = fileType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
