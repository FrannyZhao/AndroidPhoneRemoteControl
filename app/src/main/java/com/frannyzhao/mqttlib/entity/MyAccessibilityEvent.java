package com.frannyzhao.mqttlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaofengyi on 4/12/18.
 */

public class MyAccessibilityEvent implements Parcelable {
    public static final int TYPE_DOUBAN_FM = 100;

    private int type;
    private String device;

    public MyAccessibilityEvent(int type, String device) {
        this.type = type;
        this.device = device;
    }

    protected MyAccessibilityEvent(Parcel in) {
        type = in.readInt();
        device = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(device);
    }

    public static final Creator<MyAccessibilityEvent> CREATOR =
            new Creator<MyAccessibilityEvent>() {
                @Override
                public MyAccessibilityEvent createFromParcel(Parcel in) {
                    return new MyAccessibilityEvent(in);
                }

                @Override
                public MyAccessibilityEvent[] newArray(int size) {
                    return new MyAccessibilityEvent[size];
                }
            };

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

    @Override
    public int describeContents() {
        return 0;
    }
}
