package com.frannyzhao.mqttlib.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.frannyzhao.mqttlib.BusEvent;
import com.frannyzhao.mqttlib.MqttEventBusConfig;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.R;
import com.frannyzhao.mqttlib.entity.DeviceLocationInfo;
import com.frannyzhao.mqttlib.utils.MLog;
import com.frannyzhao.mqttlib.utils.MessageHandler;
import com.frannyzhao.mqttlib.utils.MyLocation;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MapActivity.class.getSimpleName();
    public static final String KEY_DEVICE_NAME = "key_device_name";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc = true; // 是否首次定位
    private TextView mInfoTv;
    private InfoWindow mInfoWindow;
    private List<LatLng> mPoints = new ArrayList<>();
    private LatLng myPoint, mDevicePoint;
    private MyLocationListener mLocationListener = new MyLocationListener();
    private String mDeviceName;
    private TextView mFocusOnDeviceTv, mFocusOnMyselfTv, mNavigateTv;
    private enum Operations {
        focusOnDevice,
        focusOnMyself
    }
    private Operations currentOperation = Operations.focusOnDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        MyLocation.getInstance(this).setLocationListener(mLocationListener);
        MyLocation.getInstance(this).startLocation();
        setContentView(R.layout.activity_map);
        mInfoTv = new TextView(MapActivity.this);
        mInfoTv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        mMapView = findViewById(R.id.myMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setIndoorEnable(true);
        mBaiduMap.setMyLocationEnabled(true);
        mFocusOnDeviceTv = findViewById(R.id.focus_on_device);
        mFocusOnDeviceTv.setOnClickListener(this);
        mFocusOnMyselfTv = findViewById(R.id.focus_on_myself);
        mFocusOnMyselfTv.setOnClickListener(this);
        mNavigateTv = findViewById(R.id.navigate);
        mNavigateTv.setOnClickListener(this);
        BaiduMapRoutePlan.setSupportWebRoute(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, mDeviceName);
        String msg = MessageHandler.generateMessage(this, MessageHandler.ACTION_STOP_LOCATION,
                mHashMap);
        MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(this), msg);
        MyLocation.getInstance(this).stopLocation();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mDeviceName = getIntent().getStringExtra(KEY_DEVICE_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.focus_on_device) {
            currentOperation = Operations.focusOnDevice;
            focusOnPoint(mDevicePoint);
        } else if (id == R.id.focus_on_myself) {
            currentOperation = Operations.focusOnMyself;
            focusOnPoint(myPoint);
        } else if (id == R.id.navigate) {
            if (mDevicePoint != null) {
                if (myPoint == null) {
                    myPoint = new LatLng(31.235882, 121.355818);
                }
                RouteParaOption para = new RouteParaOption().startPoint(myPoint).endPoint(mDevicePoint)
                        .startName(MQTTSharePreference.getDeviceName(this)).endName(mDeviceName);
                try {
                    BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
                } catch (BaiduMapAppNotSupportNaviException e) {
                    MLog.printStackTrace(TAG, e);
                    Toast.makeText(this, "调起百度地图失败 =,=", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void focusOnPoint(LatLng point) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            MLog.d(TAG, "onReceiveLocation (", location.getLongitude(), ",",
                    location.getLatitude(), ")");
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            myPoint = new LatLng(location.getLatitude(), location.getLongitude());
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc && currentOperation == Operations.focusOnMyself) {
                isFirstLoc = false;
                focusOnPoint(myPoint);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BusEvent event) {
        if (event.getWhat() == MqttEventBusConfig.locationInfo) {
            DeviceLocationInfo deviceLocationInfo = (DeviceLocationInfo) event.getObj();
            if (deviceLocationInfo.getDevice().equals(mDeviceName)) {
                MLog.d(TAG, "onEventMainThread ", deviceLocationInfo);
                mDevicePoint = new LatLng(deviceLocationInfo.getLatitude(),
                        deviceLocationInfo.getLongitude());
                mInfoTv.setText(deviceLocationInfo.getDevice());
                mInfoWindow = new InfoWindow(mInfoTv, mDevicePoint, -80);
                OverlayOptions option = new MarkerOptions().position(mDevicePoint)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_device));
                mPoints.clear();
                mPoints.add(mDevicePoint);
                mBaiduMap.clear();
                mBaiduMap.addOverlay(option);
                if (myPoint != null) {
                    mPoints.add(myPoint);
                    OverlayOptions ooPolyline = new PolylineOptions().width(10).color(
                            0xAAFF0000).points(mPoints);
                    mBaiduMap.addOverlay(ooPolyline);
                }
                mBaiduMap.showInfoWindow(mInfoWindow);
                if (isFirstLoc && currentOperation == Operations.focusOnDevice) {
                    isFirstLoc = false;
                    focusOnPoint(mDevicePoint);
                }
            }
        }
    }
}
