package com.frannyzhao.mqttlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaofengyi on 4/20/18.
 */

public class DeviceLocationInfo implements Parcelable {
    private String device;
    private double latitude;
    private double longitude;
    private float radius;
    private String coorType;
    private int errorCode;
    private float direction;

    public DeviceLocationInfo() {

    }

    protected DeviceLocationInfo(Parcel in) {
        device = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readFloat();
        coorType = in.readString();
        errorCode = in.readInt();
        direction = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(device);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(radius);
        dest.writeString(coorType);
        dest.writeInt(errorCode);
        dest.writeFloat(direction);
    }

    public static final Creator<DeviceLocationInfo> CREATOR = new Creator<DeviceLocationInfo>() {
        @Override
        public DeviceLocationInfo createFromParcel(Parcel in) {
            return new DeviceLocationInfo(in);
        }

        @Override
        public DeviceLocationInfo[] newArray(int size) {
            return new DeviceLocationInfo[size];
        }
    };

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getCoorType() {
        return coorType;
    }

    public void setCoorType(String coorType) {
        this.coorType = coorType;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "DeviceLocationInfo:" +
                "device='" + device + '\'' +
                ", (" + longitude + "," + latitude + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
