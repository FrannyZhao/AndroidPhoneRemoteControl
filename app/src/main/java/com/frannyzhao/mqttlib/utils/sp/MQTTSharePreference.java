package com.frannyzhao.mqttlib.utils.sp;

import android.content.Context;

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
}
