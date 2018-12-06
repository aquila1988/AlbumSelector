package com.aquila.lib.album;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/***
 * @date 创建时间 2018/12/6 21:44
 * @author 作者: W.YuLong
 * @description 图片裁剪的相关配置
 */
public class ImageCropOption implements Parcelable {
    private int outputX = 720;
    private int outputY= 720;

    /****aspectX裁剪的宽高比例*/
    private int aspectX = 1;
    /****aspectY裁剪的宽高比例*/
    private int aspectY = 1;

    private boolean isScale = true;

    private boolean isCircleCrop = false;

    private String outputFormat = Bitmap.CompressFormat.JPEG.toString();

    public static ImageCropOption get(){
        return new ImageCropOption();
    }

    public int getOutputX() {
        return outputX;
    }

    public ImageCropOption setOutputX(int outputX) {
        this.outputX = outputX;
        return this;
    }

    public int getOutputY() {
        return outputY;
    }

    public ImageCropOption setOutputY(int outputY) {
        this.outputY = outputY;
        return this;
    }

    public int getAspectX() {
        return aspectX;
    }

    public ImageCropOption setAspectX(int aspectX) {
        this.aspectX = aspectX;
        return this;
    }

    public int getAspectY() {
        return aspectY;
    }

    public ImageCropOption setAspectY(int aspectY) {
        this.aspectY = aspectY;
        return this;
    }

    public boolean isScale() {
        return isScale;
    }

    public ImageCropOption setScale(boolean scale) {
        isScale = scale;
        return this;
    }

    public boolean isCircleCrop() {
        return isCircleCrop;
    }

    public ImageCropOption setCircleCrop(boolean circleCrop) {
        isCircleCrop = circleCrop;
        return this;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public ImageCropOption setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.outputX);
        dest.writeInt(this.outputY);
        dest.writeInt(this.aspectX);
        dest.writeInt(this.aspectY);
        dest.writeByte(this.isScale ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCircleCrop ? (byte) 1 : (byte) 0);
        dest.writeString(this.outputFormat);
    }

    public ImageCropOption() {
    }

    protected ImageCropOption(Parcel in) {
        this.outputX = in.readInt();
        this.outputY = in.readInt();
        this.aspectX = in.readInt();
        this.aspectY = in.readInt();
        this.isScale = in.readByte() != 0;
        this.isCircleCrop = in.readByte() != 0;
        this.outputFormat = in.readString();
    }

    public static final Parcelable.Creator<ImageCropOption> CREATOR = new Parcelable.Creator<ImageCropOption>() {
        @Override
        public ImageCropOption createFromParcel(Parcel source) {
            return new ImageCropOption(source);
        }

        @Override
        public ImageCropOption[] newArray(int size) {
            return new ImageCropOption[size];
        }
    };
}

