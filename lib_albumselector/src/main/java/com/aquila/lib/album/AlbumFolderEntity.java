package com.aquila.lib.album;

import java.util.ArrayList;

/***
 * @date 创建时间 2018/11/17 10:28
 * @author 作者: W.YuLong
 * @description 相册的文件夹实体类
 */
public class AlbumFolderEntity {
    private String folderName;
    private String folderPath;
    private AlbumFileEntity coverEntity;
    private ArrayList<AlbumFileEntity> fileEntityList = new ArrayList<>();

    //选中的序号
    private int index = 0;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public AlbumFileEntity getCoverEntity() {
        return coverEntity;
    }

    public void setCoverEntity(AlbumFileEntity coverEntity) {
        this.coverEntity = coverEntity;
    }

    public ArrayList<AlbumFileEntity> getFileEntityList() {
        return fileEntityList;
    }

    public void setFileEntityList(ArrayList<AlbumFileEntity> fileEntityList) {
        this.fileEntityList = fileEntityList;
    }

    public void addFileEntity(AlbumFileEntity entity){
        fileEntityList.add(entity);
    }


    public String getFolderInfo(){
        return folderName +","+ folderPath ;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlbumFolderEntity{");
        sb.append("folderName='").append(folderName).append('\'');
        sb.append(", folderPath='").append(folderPath).append('\'');
        sb.append(", coverEntity=").append(coverEntity);
//        sb.append(", fileEntityList=").append(fileEntityList);
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
