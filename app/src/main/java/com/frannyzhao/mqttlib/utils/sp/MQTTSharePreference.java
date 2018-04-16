package com.frannyzhao.mqttlib.utils.sp;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArraySet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaofengyi on 10/11/17.
 */

public class MQTTSharePreference extends SharePreferenceBase {
    public static final String MQTT_SP_NAME = "mqtt_share_pref";
    private static MQTTSharePreference instance = new MQTTSharePreference(MQTT_SP_NAME);

    private MQTTSharePreference(String spName) {
        super(spName);
    }

    public static MQTTSharePreference getInstance() {
        return instance;
    }

    private static final String DEVICE_NAME = "device_name";
    private static final String SERVER = "server";
    private static final String PORT = "port";
    private static final String LOGIN_NAME = "login_name";
    private static final String PASSWORD = "password";
    private static final String TOPIC = "topic";
    private static final String DEVICE_ONLINE_TIME = "_online_time";
    private static final String ONLINE_DEVICES = "online_devices";

    public static String getDeviceName(Context context) {
        return getInstance().getString(context, DEVICE_NAME, "");
    }

    public static void setDeviceName(Context context, String deviceName) {
        getInstance().putString(context, DEVICE_NAME, deviceName);
    }

    public static String getServer(Context context) {
        return getInstance().getString(context, SERVER, "");
    }

    public static void setServer(Context context, String deviceName) {
        getInstance().putString(context, SERVER, deviceName);
    }

    public static String getPort(Context context) {
        return getInstance().getString(context, PORT, "");
    }

    public static void setPort(Context context, String deviceName) {
        getInstance().putString(context, PORT, deviceName);
    }

    public static String getLoginName(Context context) {
        return getInstance().getString(context, LOGIN_NAME, "");
    }

    public static void setLoginName(Context context, String deviceName) {
        getInstance().putString(context, LOGIN_NAME, deviceName);
    }

    public static String getPassword(Context context) {
        return getInstance().getString(context, PASSWORD, "");
    }

    public static void setPassword(Context context, String deviceName) {
        getInstance().putString(context, PASSWORD, deviceName);
    }

    public static String getTopic(Context context) {
        return getInstance().getString(context, TOPIC, "");
    }

    public static void setTopic(Context context, String deviceName) {
        getInstance().putString(context, TOPIC, deviceName);
    }

    public static long getLastOnlineTime(Context context, String deviceName) {
        if (!TextUtils.isEmpty(deviceName)) {
            return getInstance().getLong(context, deviceName + DEVICE_ONLINE_TIME, 0);
        }
        return 0;
    }

    public static void setLastOnlineTime(Context context, String deviceName, long time) {
        if (!TextUtils.isEmpty(deviceName)) {
            getInstance().putLong(context, deviceName + DEVICE_ONLINE_TIME, time);
        }
    }

    public static Set<String> getOnlineDevices(Context context) {
        return getInstance().getStringSet(context, ONLINE_DEVICES, null);
    }

    public static void addOnlineDevice(Context context, String deviceName) {
        Set<String> onlineDevices = getOnlineDevices(context);
        if (onlineDevices == null) {
            onlineDevices = new HashSet<>();
        }
        onlineDevices.add(deviceName);
        getInstance().putStringSet(context, ONLINE_DEVICES, onlineDevices);
    }

    public static void removeOnlineDevice(Context context, String deviceName) {
        Set<String> onlineDevices = getOnlineDevices(context);
        if (onlineDevices != null) {
            onlineDevices.remove(deviceName);
        }
        getInstance().putStringSet(context, ONLINE_DEVICES, onlineDevices);
    }
}
