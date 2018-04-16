package com.frannyzhao.mqttlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.frannyzhao.mqttlib.jobqueue.JobManager;
import com.frannyzhao.mqttlib.ui.HomeFragment;
import com.frannyzhao.mqttlib.utils.CollectionUtils;
import com.frannyzhao.mqttlib.utils.MLog;
import com.frannyzhao.mqttlib.utils.MessageHandler;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by zhaofengyi on 4/16/18.
 */

public class HeartbeatService extends Service {
    private static final String TAG = HeartbeatService.class.getSimpleName();
    private boolean mIsRunning;
    private static final int INTERVAL = 10000;
    private static final int MAX_FAIL_COUNT = 3;
    private int tryConnectCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand");
        mIsRunning = true;
        tryConnectCount = 0;
        JobManager.post(new Runnable() {
            @Override
            public void run() {
                try {
                    while (mIsRunning) {
                        Thread.sleep(INTERVAL);
                        if (!mIsRunning) {
                            break;
                        }
                        MLog.d(TAG, "isConnected: ", HomeFragment.isConnected());
                        if (HomeFragment.isConnected()) {
                            tryConnectCount = 0;
                            Set<String> onlineDevices = MQTTSharePreference.getOnlineDevices(
                                    HeartbeatService.this);
                            MLog.d(TAG, "onlineDevices: ", onlineDevices);
                            if (!CollectionUtils.isNullOrEmpty(onlineDevices)) {
                                for (String device : onlineDevices) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put(MessageHandler.KEY_TARGET_DEVICE, device);
                                    String pingMsg = MessageHandler.generateMessage(
                                            HeartbeatService.this,
                                            MessageHandler.ACTION_PING, hashMap);
                                    MqttHandler.getInstance().publish(
                                            MQTTSharePreference.getTopic(HeartbeatService.this),
                                            pingMsg);
                                    long lastOnlineTime = MQTTSharePreference.getLastOnlineTime(
                                            HeartbeatService.this, device);
                                    if (System.currentTimeMillis() - lastOnlineTime
                                            > INTERVAL * 2) {
                                        BusEvent deviceDisconnectedEvent = new BusEvent(
                                                MqttEventBusConfig.deviceDisconnected, device);
                                        EventBus.getDefault().post(deviceDisconnectedEvent);
                                    }
                                }
                            }
                        } else {
                            if (tryConnectCount < MAX_FAIL_COUNT) {
                                tryConnectCount ++;
                                MqttHandler.getInstance().connect(HeartbeatService.this,
                                        MQTTSharePreference.getServer(HeartbeatService.this) + ":"
                                                + MQTTSharePreference.getPort(
                                                HeartbeatService.this),
                                        MQTTSharePreference.getLoginName(HeartbeatService.this),
                                        MQTTSharePreference.getPassword(HeartbeatService.this));

                            }
                        }
                    }
                } catch(Exception e) {
                    MLog.printStackTrace(TAG, e);
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MLog.d(TAG, "onDestroy");
        mIsRunning = false;
        super.onDestroy();
    }
}
