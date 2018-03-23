package com.frannyzhao.mqttlib.utils;

import android.text.TextUtils;

public class NumberUtils {

    private static final String TAG = "NumberUtils";

    public static long parseLong(String numStr) {
        long l = 0L;
        if (!TextUtils.isEmpty(numStr)) {
            try {
                l = Long.parseLong(numStr);
            } catch (NumberFormatException e) {
                MLog.d(TAG, "NumberFormatException e = ", e.toString());
            }
        }
        return l;
    }

    public static int parseInt(String numStr) {
        int n = 0;
        if (!TextUtils.isEmpty(numStr)) {
            try {
                n = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                MLog.d(TAG, "NumberFormatException e = ", e.toString());
            }
        }
        return n;
    }

    public static float parseFloat(String numStr) {
        float f = 0.0F;
        if (!TextUtils.isEmpty(numStr)) {
            try {
                f = Float.parseFloat(numStr);
            } catch (NumberFormatException e) {
                MLog.d(TAG, "NumberFormatException e = ", e.toString());
            }
        }
        return f;
    }

    public static boolean parseBoolean(String str) {
        boolean b = false;
        if (!TextUtils.isEmpty(str)) {
            if (str.equals("true") || str.equals("TRUE")) {
                b = true;
            }
        }
        return b;
    }

}
