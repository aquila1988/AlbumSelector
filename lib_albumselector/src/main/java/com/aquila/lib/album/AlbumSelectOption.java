package com.aquila.lib.album;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.aquila.lib.album.interfaces.OnAlbumSelectCallback;
import com.aquila.lib.album.utils.AlbumTypeDefine;

/***
 * @date 创建时间 2018/11/17 17:50
 * @author 作者: W.YuLong
 * @description
 */
public class AlbumSelectOption implements Parcelable {
    @IntDef({AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO, AlbumTypeDefine.TYPE_PICTURE, AlbumTypeDefine.TYPE_VIDEO})
    public @interface AlbumSelectType {
    }

    private Context context;
    private boolean isMultipleSelectMode = false;
    private int max = 9;
    private String title;
    private ImageCropOption imageCropOption ;


    @AlbumSelectType
    private int selectType = AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO;


    //是否支持裁剪
    private boolean isCrop = false;

    /***********************************************************************************************************************/
    public AlbumSelectOption() {
    }

    public static AlbumSelectOption with() {
        return new AlbumSelectOption();
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public AlbumSelectOption(Context context) {
        this.context = context;
    }

    public static AlbumSelectOption with(Context context) {
        return new AlbumSelectOption(context);
    }

    public void doAlbumSelect(OnAlbumSelectCallback callback) {
        AlbumSelectorActivity.startAlbumSelectActivity(context, this, callback);
    }


    /***********************************************************************************************************************/


    public ImageCropOption getImageCropOption() {
        return imageCropOption;
    }

    public AlbumSelectOption setImageCropOption(ImageCropOption imageCropOption) {
        this.imageCropOption = imageCropOption;
        return this;
    }

    public boolean isCrop() {
        return isCrop;
    }

    public AlbumSelectOption setCrop(boolean crop) {
        isCrop = crop;
        //如果未设置裁剪的配置，就生成一个默认的
        if (imageCropOption == null){
            imageCropOption = ImageCropOption.get();
        }
        return this;
    }


    public String getTitle() {
        return title;
    }

    public AlbumSelectOption setTitle(String title) {
        this.title = title;
        return this;
    }


    public boolean isMultipleSelectMode() {
        return isMultipleSelectMode;
    }

    public AlbumSelectOption setMultipleSelectMode(boolean multipleSelectMode) {
        isMultipleSelectMode = multipleSelectMode;
        return this;
    }

    public int getMax() {
        return max;
    }

    public AlbumSelectOption setMax(int max) {
        this.max = max;
        return this;
    }

    public int getSelectType() {
        return selectType;
    }

    public AlbumSelectOption setSelectType(@AlbumSelectType int selectType) {
        this.selectType = selectType;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isMultipleSelectMode ? (byte) 1 : (byte) 0);
        dest.writeInt(this.max);
        dest.writeString(this.title);
        dest.writeParcelable(this.imageCropOption, flags);
        dest.writeInt(this.selectType);
        dest.writeByte(this.isCrop ? (byte) 1 : (byte) 0);
    }

    protected AlbumSelectOption(Parcel in) {
        this.isMultipleSelectMode = in.readByte() != 0;
        this.max = in.readInt();
        this.title = in.readString();
        this.imageCropOption = in.readParcelable(ImageCropOption.class.getClassLoader());
        this.selectType = in.readInt();
        this.isCrop = in.readByte() != 0;
    }

    public static final Creator<AlbumSelectOption> CREATOR = new Creator<AlbumSelectOption>() {
        @Override
        public AlbumSelectOption createFromParcel(Parcel source) {
            return new AlbumSelectOption(source);
        }

        @Override
        public AlbumSelectOption[] newArray(int size) {
            return new AlbumSelectOption[size];
        }
    };
}
