package com.frannyzhao.mqttlib.entity;

/**
 * Created by zhaofengyi on 4/12/18.
 */

public class MyAccessibilityEvent {
    public static final int TYPE_DOUBAN_FM = 100;

    private int type;
    private String device;

    public MyAccessibilityEvent(int type, String device) {
        this.type = type;
        this.device = device;
    }

    public int getType() {
        return type;
    }

    public String getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "MyAccessibilityEvent{" +
                "type=" + type +
                ", device='" + device + '\'' +
                '}';
    }
}
