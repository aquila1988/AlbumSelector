package com.aquila.lib.album.info;

/***
 * @date 创建时间 2018/12/9 21:39
 * @author 作者: W.YuLong
 * @description
 */
public class AlbumInfoEntity {
    private String title;
    private String content;


    public AlbumInfoEntity() {
    }

    public AlbumInfoEntity(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public AlbumInfoEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AlbumInfoEntity setContent(String content) {
        this.content = content;
        return this;
    }
}
