package com.frannyzhao.mqttlib.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by zhaofengyi on 10/12/17.
 */

public class ActivityUtils {
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    /**
     * 保存切换前状态, 按下返回键可以返回切换前页面
     * @param fragmentManager
     * @param fragment
     * @param frameId
     */
    public static void addFragmentToActivitySaveBackStack(@NonNull FragmentManager fragmentManager,
            @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static Activity getActivity(Object obj) {
        Activity activity;
        if (obj instanceof Fragment) {
            activity = ((Fragment) obj).getActivity();
        } else if (obj instanceof Activity) {
            activity = (Activity) obj;
        } else {
            activity = null;
        }
        return activity;
    }
}
