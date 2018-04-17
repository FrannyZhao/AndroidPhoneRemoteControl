package com.frannyzhao.mqttlib.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by zhaofengyi on 4/18/18.
 */

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();
    private Activity mActivity;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int FINE_LOCATION_CODE = 2;
    public static final int COARSE_LOCATION_CODE = 3;

    private static final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int[] requestCodes = new int[]{
            CAMERA_REQUEST_CODE,
            FINE_LOCATION_CODE,
            COARSE_LOCATION_CODE};

    public PermissionUtils(Activity activity) {
        mActivity = activity;
        for (int i = 0; i < permissions.length; i++) {
            requestPermission(permissions[i], requestCodes[i]);
        }
    }

    private void requestPermission(String permissionName, int requestCode) {
        if (ContextCompat.checkSelfPermission(mActivity, permissionName)
                != PackageManager.PERMISSION_GRANTED) {
            // 为了打开闪光灯, 申请相机权限
            ActivityCompat.requestPermissions(mActivity, new String[]{permissionName},
                    requestCode);
        }
    }

    public void handlePermissionResult(int requestCode, String permissions[], int[] grantResults) {
        // todo 完善支持/不支持的功能列表, 发送给对方
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the contacts-related task you need to do.
            MLog.d(TAG, "requestCode ", requestCode, " is granted");
        } else {
            // permission denied, boo! Disable the functionality that depends on this permission.
            MLog.d(TAG, "requestCode ", requestCode, " is denied");
        }
    }

}
