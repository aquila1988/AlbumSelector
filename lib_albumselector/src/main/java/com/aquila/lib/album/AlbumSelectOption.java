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

    private boolean isMultipleSelectMode = false;
    private int max = 9;
    private String title;
    private Context context;
    private OnAlbumSelectCallback onAlbumSelectCallback;



    public void doAlbumSelect() {
        AlbumSelectorActivity.startAlbumSelectActivity(context, this, onAlbumSelectCallback);
    }

    private @AlbumSelectType int selectType = AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO;

    @IntDef({AlbumTypeDefine.TYPE_PICTURE_AND_VIDEO, AlbumTypeDefine.TYPE_PICTURE, AlbumTypeDefine.TYPE_VIDEO})
    public @interface AlbumSelectType {
    }

    public AlbumSelectOption setOnAlbumSelectCallback(OnAlbumSelectCallback onAlbumSelectCallback) {
        this.onAlbumSelectCallback = onAlbumSelectCallback;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AlbumSelectOption setTitle(String title) {
        this.title = title;
        return this;
    }

    public static AlbumSelectOption with(Context context) {
        return new AlbumSelectOption(context);
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

    public AlbumSelectOption(Context context) {
        this.context = context;
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
        dest.writeInt(this.selectType);
    }

    protected AlbumSelectOption(Parcel in) {
        this.isMultipleSelectMode = in.readByte() != 0;
        this.max = in.readInt();
        this.title = in.readString();
        this.selectType = in.readInt();
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
