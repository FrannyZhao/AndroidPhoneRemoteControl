package com.frannyzhao.mqttlib.utils;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import java.util.HashMap;

/**
 * Created by zhaofengyi on 4/18/18.
 */

public class MyLocation {
    private static final String TAG = MyLocation.class.getSimpleName();
    private static LocationClient mLocationClient = null;
    private MyLocationListener mLocationListener = new MyLocationListener();
    private BDAbstractLocationListener customLocationListener = null;
    private static MyLocation sMyLocation = null;
    private HashMap<String, String> mHashMap = new HashMap<>();
    private Context mContext;
    private String mTargetDevice = null;

    public static MyLocation getInstance(Context context) {
        if (sMyLocation == null) {
            sMyLocation = new MyLocation(context);
        }
        return sMyLocation;
    }

    private MyLocation(Context context) {
        mContext = context;
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
    }

    public void startLocation() {
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    public void stopLocation() {
        customLocationListener = null;
        mTargetDevice = null;
        mLocationClient.stop();
    }

    public void setLocationListener(BDAbstractLocationListener customLocationListener) {
        this.customLocationListener = customLocationListener;
    }

    public void setTargetDevice(String targetDevice) {
        mTargetDevice = targetDevice;
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (customLocationListener != null) {
                customLocationListener.onReceiveLocation(location);
            }
            if (!TextUtils.isEmpty(mTargetDevice)) {
                mHashMap.clear();
                mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, mTargetDevice);
                mHashMap.put(MessageHandler.KEY_LATITUDE, String.valueOf(location.getLatitude()));
                mHashMap.put(MessageHandler.KEY_LONGITUDE, String.valueOf(location.getLongitude()));
                String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_LOCATION_INFO,
                        mHashMap);
                MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
                MLog.d(TAG, "send location to ", mTargetDevice, " (", location.getLongitude(), ",", location.getLatitude(), ")");
            }
        }
    }
}
