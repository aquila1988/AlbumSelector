package com.aquila.lib.album.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/***
 * @date 创建时间 2018/8/18 21:21
 * @author 作者: W.YuLong
 * @description 权限申请的封装
 */
public class PermissionsUtil extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 64;

    private static final int REQUEST_SETTING_CODE = 121;

    private String[] permission;
    private String key;
    private boolean isShowTip;
    private PermissionTipInfo tipInfo;
    private static Map<String, OnPermissionCallback> listenersMap = new HashMap<>();


    /***
     * 权限的申请
     * @param context 请求权限的上下文，如果在Activity或fragment中最好传当前Activity实例
     * @param permission 需要申请的权限
     * @param onPermissionCallback 权限申请后的回调接口
     */
    public static void requestPermission(Context context, String[] permission, OnPermissionCallback onPermissionCallback) {
        requestPermission(context, null, permission, true, onPermissionCallback);
    }

    /***
     * 权限的申请方法
     * @param context 请求权限的上下文，如果在Activity或fragment中最好传当前Activity实例
     * @param permission 需要申请的权限
     * @param isShowTip 是否显示对话框(当权限被拒绝时)
     * @param onPermissionCallback 权限申请后的回调接口
     */
    public static void requestPermission(Context context, String[] permission, boolean isShowTip, OnPermissionCallback onPermissionCallback) {
        requestPermission(context, null, permission, isShowTip, onPermissionCallback);
    }

    /***
     * 权限的申请方法
     * @param context 请求权限的上下文，如果在Activity或fragment中最好传当前Activity实例
     * @param tipInfo 拒绝权限或者权限被关闭显示的对话框类型信息
     * @param permission 需要申请的权限
     * @param isShowTip 是否显示对话框(当权限被拒绝时)
     * @param onPermissionCallback 权限申请后的回调接口
     */
    public static void requestPermission(Context context, PermissionTipInfo tipInfo, String[] permission, boolean isShowTip, OnPermissionCallback onPermissionCallback) {
        if (hasPermission(context, permission)) {
            onPermissionCallback.onSuccess(permission);
        } else {
            if (Build.VERSION.SDK_INT < 23) {
                onPermissionCallback.onRefused(permission);
                return;
            }

            Intent intent = new Intent(context, PermissionsUtil.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (tipInfo != null) {
                intent.putExtra("tipInfo", tipInfo);
            }

            String key = System.currentTimeMillis() + "";
            intent.putExtra("key", key);
            listenersMap.put(key, onPermissionCallback);

            intent.putExtra("permission", permission);
            intent.putExtra("isShowTip", isShowTip);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            }
        }

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataFromIntent(getIntent());
        //直接申请权限
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        Log.d("PermissionsUtil", "onCreate");
    }

    private void initDataFromIntent(Intent intent) {
        key = intent.getStringExtra("key");
        tipInfo = intent.getParcelableExtra("tipInfo");
        permission = intent.getStringArrayExtra("permission");
        isShowTip = intent.getBooleanExtra("isShowTip", false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断
        if (requestCode == PERMISSION_REQUEST_CODE && isGranted(grantResults)
                && hasPermission(this, permissions)) {
            OnPermissionCallback listener = listenersMap.remove(key);
            if (listener != null) {
                listener.onSuccess(permission);
            }
            finish();
            Log.d("PermissionsUtil", "success");

        } else if (isShowTip) {
            showMissingPermissionDialog();
        } else { //不需要提示用户
            permissionsRefused();
            Log.d("PermissionsUtil", "refuse");
        }
    }

    //显示权限被拒绝的对话框
    private void showMissingPermissionDialog() {
        if (tipInfo == null) {
            tipInfo = PermissionTipInfo.newInstance();
        }

        new AlertDialog.Builder(this).setTitle(tipInfo.getTitle()).setMessage(tipInfo.getMessage())
                .setNegativeButton(tipInfo.getCancelText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionsRefused();
                    }
                })
                .setPositiveButton(tipInfo.getOkText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_SETTING_CODE);

                    }
                }).setCancelable(false).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //去设置页面设置完成之后再次请求看是否已打开权限
        if (requestCode == REQUEST_SETTING_CODE) {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        }
    }

    // 判断权限是否已打开
    public static boolean hasPermission(Context context, String... permissions) {
        if (permissions.length == 0) {
            return false;
        }
        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 权限被拒绝的回调
    private void permissionsRefused() {
        OnPermissionCallback listener = listenersMap.remove(key);
        if (listener != null) {
            listener.onRefused(permission);
        }
        finish();
    }

    private boolean isGranted(@NonNull int... grantResult) {
        if (grantResult.length == 0) {
            return false;
        }
        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当前的Activity被销毁时再次做一些移除传过来的接口，防止内存泄露
        listenersMap.remove(key);
        Log.d("PermissionsUtil", "onDestory");
    }

    @Override
    public void finish() {
        super.finish();
        //去掉页面的过度动画
        overridePendingTransition(0, 0);
        Log.d("PermissionsUtil", "finish");
    }

    /***
     *@date 创建时间 2018/8/18 21:37
     *@author 作者: W.YuLong
     *@description 权限被拒绝封装的内容实体类
     */
    public static class PermissionTipInfo implements Parcelable {
        private String title = "温馨提示";
        private String message = "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
        private String cancelText = "取消";
        private String okText = "打开权限";

        public PermissionTipInfo(String title, String message, String cancelText, String okText) {
            this.title = title;
            this.message = message;
            this.cancelText = cancelText;
            this.okText = okText;
        }

        public PermissionTipInfo() {
        }

        public static PermissionTipInfo newInstance() {
            return new PermissionTipInfo();
        }

        public String getTitle() {
            return title;
        }

        public PermissionTipInfo setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public PermissionTipInfo setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getCancelText() {
            return cancelText;
        }

        public PermissionTipInfo setCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public String getOkText() {
            return okText;
        }

        public PermissionTipInfo setOkText(String okText) {
            this.okText = okText;
            return this;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.message);
            dest.writeString(this.cancelText);
            dest.writeString(this.okText);
        }

        protected PermissionTipInfo(Parcel in) {
            this.title = in.readString();
            this.message = in.readString();
            this.cancelText = in.readString();
            this.okText = in.readString();
        }

        public static final Creator<PermissionTipInfo> CREATOR = new Creator<PermissionTipInfo>() {
            @Override
            public PermissionTipInfo createFromParcel(Parcel source) {
                return new PermissionTipInfo(source);
            }

            @Override
            public PermissionTipInfo[] newArray(int size) {
                return new PermissionTipInfo[size];
            }
        };
    }


    /***
     *@date 创建时间 2018/8/18 21:27
     *@author 作者: W.YuLong
     *@description 回调接口的实现类，可以防止每次都要实现接口的抽象方法
     */
    public static class OnPermissionCallbackImpl implements OnPermissionCallback {
        @Override
        public void onSuccess(String[] permission) {
        }

        @Override
        public void onRefused(String[] permission) {
        }
    }

    /***
     *@date 创建时间 2018/8/18 21:27
     *@author 作者: W.YuLong
     *@description 回调接口
     */
    public interface OnPermissionCallback {
        void onSuccess(String[] permission);

        void onRefused(String[] permission);
    }
}
