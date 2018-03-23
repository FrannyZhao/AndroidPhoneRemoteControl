package com.frannyzhao.mqttlib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.frannyzhao.mqttlib.BusEvent;
import com.frannyzhao.mqttlib.MqttEventBusConfig;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Created by zhaofengyi on 3/6/18.
 */

public class MessageHandler {
    private static final String TAG = MessageHandler.class.getSimpleName();
    private static final String MESSAGE_PREFIX = "mqtt://parse?";
    private static final String ACTION = "action";
    public static final String KEY_FROM_DEVICE = "from_device_name";
    public static final String KEY_TARGET_DEVICE = "target_device_name";
    public static final String KEY_ACTION = "action_name";
    public static final String KEY_RESULT = "action_result";
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_FAIL = "fail";

    public static final int ACTION_RESULT = 99;
    public static final int ACTION_FIRST_ONLINE = 100;
    public static final int ACTION_NOTIFY_NAME = 101;
    public static final int ACTION_DISCONNECT = 102;
    public static final int ACTION_OPEN_FLASH = 103;
    public static final int ACTION_CLOSE_FLASH = 104;


    private static Camera mCamera = null;
    private static Camera.Parameters mCameraParameters;

    public static String generateMessage(Context context, int action, HashMap<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(MessageHandler.KEY_FROM_DEVICE, MQTTSharePreference.getDeviceName(context));
        StringBuilder msg = new StringBuilder();
        msg.append(ACTION).append("=").append(action);
        if (!CollectionUtils.isNullOrEmpty(params)) {
            for (String key : params.keySet()) {
                msg.append("&").append(key).append("=").append(params.get(key));
            }
        }
        MLog.d(TAG, "generateMessage ", msg);
        return msg.toString();
    }

    public static void parseMessage(Activity activity, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        MLog.d(TAG, "parseMessage ", msg);
        String message = MESSAGE_PREFIX + msg;
        Uri uri = Uri.parse(message);
        int action = NumberUtils.parseInt(uri.getQueryParameter(ACTION));
        String fromDeviceName = uri.getQueryParameter(KEY_FROM_DEVICE);
        String targetDeviceName = "";
        switch (action) {
            case ACTION_RESULT:
                String actionName = uri.getQueryParameter(KEY_ACTION);
                String actionResult = uri.getQueryParameter(KEY_RESULT);
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    Toast.makeText(activity, "action " + actionName + " " + actionResult,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_FIRST_ONLINE: // todo 加上通知支持控制的功能
                if (!MQTTSharePreference.getDeviceName(activity).equals(fromDeviceName)) {
                    MLog.d(TAG, "device ", fromDeviceName, " is first time online");
                    String msg2 = MessageHandler.generateMessage(activity, MessageHandler.ACTION_NOTIFY_NAME,
                            null);
                    MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(activity), msg2);
                    BusEvent deviceConnectedEvent = new BusEvent(MqttEventBusConfig.deviceConnected, fromDeviceName);
                    EventBus.getDefault().post(deviceConnectedEvent);
                }
                break;
            case ACTION_NOTIFY_NAME: // todo 加上通知支持控制的功能
                if (!MQTTSharePreference.getDeviceName(activity).equals(fromDeviceName)) {
                    MLog.d(TAG, "device ", fromDeviceName, " is online");
                    BusEvent deviceConnectedEvent = new BusEvent(MqttEventBusConfig.deviceConnected, fromDeviceName);
                    EventBus.getDefault().post(deviceConnectedEvent);
                }
                break;
            case ACTION_DISCONNECT:
                if (!MQTTSharePreference.getDeviceName(activity).equals(fromDeviceName)) {
                    MLog.d(TAG, "device ", fromDeviceName, " is offline");
                    BusEvent deviceDisconnectedEvent = new BusEvent(MqttEventBusConfig.deviceDisconnected, fromDeviceName);
                    EventBus.getDefault().post(deviceDisconnectedEvent);
                }
                break;
            case ACTION_OPEN_FLASH:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "Going to open flash");
                    try {
                        mCamera = Camera.open();
                        SurfaceTexture mDummy = new SurfaceTexture(1); // any int argument will do
                        mCamera.setPreviewTexture(mDummy);
                        mCamera.startPreview();
                        mCameraParameters = mCamera.getParameters();
                        mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(mCameraParameters);
                        sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                    } catch (Exception ex) {
                        MLog.d(TAG, ex);
                        sendActionResult(activity, action, RESULT_FAIL, fromDeviceName);
                    }
                }
                break;
            case ACTION_CLOSE_FLASH:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "Going to close flash");
                    try {
                        mCameraParameters = mCamera.getParameters();
                        mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(mCameraParameters);
                        mCamera.stopPreview();
                        mCamera.release();
                        sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                    } catch (Exception ex) {
                        MLog.d(TAG, ex);
                        sendActionResult(activity, action, RESULT_FAIL, fromDeviceName);
                    }
                }
                break;
            default:
                break;
        }
    }

    private static void sendActionResult(Activity activity, int action, String result, String targetDevice) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TARGET_DEVICE, targetDevice);
        hashMap.put(KEY_ACTION, String.valueOf(action));
        hashMap.put(KEY_RESULT, result);
        String resultMsg = MessageHandler.generateMessage(activity, MessageHandler.ACTION_RESULT,
                hashMap);
        MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(activity), resultMsg);
    }
}
