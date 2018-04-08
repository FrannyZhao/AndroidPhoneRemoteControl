package com.frannyzhao.mqttlib.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zhaofengyi on 3/5/18.
 */

public class MLog {
    private static String defaultTag = "MQTT";
    private static boolean isDebugMode = true;

    public static boolean isDebug() {
        return isDebugMode;
    }

    public static int v(String tag, Object... msg) {
        return isDebug() && msg != null ? v("[" + tag + "] " + getLogMessage(msg)) : -1;
    }

    public static int d(String tag, Object... msg) {
        return isDebug() && msg != null ? d("[" + tag + "] " + getLogMessage(msg)) : -1;
    }

    public static int i(String tag, Object... msg) {
        return isDebug() && msg != null ? i("[" + tag + "] " + getLogMessage(msg)) : -1;
    }

    public static int w(String tag, Object... msg) {
        return isDebug() && msg != null ? w("[" + tag + "] " + getLogMessage(msg)) : -1;
    }

    public static int e(String tag, Object... msg) {
        return isDebug() && msg != null ? e("[" + tag + "] " + getLogMessage(msg)) : -1;
    }

    private static int i(String m) {
        return isDebug() && m != null ? Log.i(defaultTag, m) : -1;
    }

    private static int d(String m) {
        return isDebug() && m != null ? Log.d(defaultTag, m) : -1;
    }

    private static int e(String m) {
        return isDebug() && m != null && Log.isLoggable(defaultTag, Log.ERROR) ? Log.e(defaultTag, m) : -1;
    }

    private static int w(String m) {
        return isDebug() && m != null ? Log.w(defaultTag, m) : -1;
    }

    private static int v(String m) {
        return isDebug() && m != null ? Log.v(defaultTag, m) : -1;
    }

    private static String getLogMessage(Object... msg) {
        if (msg != null && msg.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object s : msg) {
                if (s != null) sb.append(s.toString());
            }
            return sb.toString();
        }
        return "";
    }

    public static void printStackTrace(Exception e) {
        if (e != null && e.getMessage() != null) {
            d(defaultTag, e.getMessage());
        }
    }

    public static void printStackTrace(String customTag,Exception e){
        if (e != null && e.getMessage() != null) {
            if(!TextUtils.isEmpty(customTag)){
                d(customTag, e.getMessage());
            }else{
                d(defaultTag, e.getMessage());
            }
        }
    }

    public static void printStackTrace(Error e) {
        if (e != null && e.getMessage() != null) {
            d(defaultTag, e.getMessage());
        }
    }

    public static void printStackTrace(Throwable e){
        if (e != null && e.getMessage() != null) {
            d(defaultTag, e.getMessage());
        }
    }
}
