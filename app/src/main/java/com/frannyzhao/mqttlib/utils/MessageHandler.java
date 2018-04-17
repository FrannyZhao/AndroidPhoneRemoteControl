package com.frannyzhao.mqttlib.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.widget.Toast;

import com.frannyzhao.mqttlib.BusEvent;
import com.frannyzhao.mqttlib.MqttEventBusConfig;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.MyAccessibilityService;
import com.frannyzhao.mqttlib.entity.DeviceLocationInfo;
import com.frannyzhao.mqttlib.entity.MyAccessibilityEvent;
import com.frannyzhao.mqttlib.ui.HomeFragment;
import com.frannyzhao.mqttlib.ui.MapActivity;
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
    public static final String KEY_WORDS = "words";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";

    public static final int ACTION_PING = 88;
    public static final int ACTION_PINGBACK = 89;
    public static final int ACTION_RESULT = 99;
    public static final int ACTION_FIRST_ONLINE = 100;
    public static final int ACTION_NOTIFY_NAME = 101;
    public static final int ACTION_DISCONNECT = 102;
    public static final int ACTION_OPEN_FLASH = 103;
    public static final int ACTION_CLOSE_FLASH = 104;
    public static final int ACTION_SAY_LOUDLY = 105;
    public static final int ACTION_OPEN_DOUBAN_FM = 106;
    public static final int ACTION_CLOSE_MUSIC = 107;
    public static final int ACTION_VOLUME_MAX = 108;
    public static final int ACTION_VOLUME_MIN = 109;
    public static final int ACTION_START_LOCATION = 110;
    public static final int ACTION_STOP_LOCATION = 111;
    public static final int ACTION_LOCATION_INFO = 112;

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

    public static void parseMessage(final Activity activity, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        String message = MESSAGE_PREFIX + msg;
        Uri uri = Uri.parse(message);
        int action = NumberUtils.parseInt(uri.getQueryParameter(ACTION));
        String fromDeviceName = uri.getQueryParameter(KEY_FROM_DEVICE);
        String targetDeviceName = "";
        final AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        switch (action) {
            case ACTION_PING:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "get ping from ", targetDeviceName);
                    BusEvent deviceConnectedEvent = new BusEvent(MqttEventBusConfig.deviceConnected, fromDeviceName);
                    EventBus.getDefault().post(deviceConnectedEvent);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(KEY_TARGET_DEVICE, fromDeviceName);
                    String pingbackMsg = MessageHandler.generateMessage(activity,
                            MessageHandler.ACTION_PINGBACK,
                            hashMap);
                    MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(activity), pingbackMsg);
                }
                break;
            case ACTION_PINGBACK:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "get pingback from ", targetDeviceName);
                    BusEvent deviceConnectedEvent = new BusEvent(MqttEventBusConfig.deviceConnected, fromDeviceName);
                    EventBus.getDefault().post(deviceConnectedEvent);
                }
                break;
            case ACTION_RESULT:
                String actionName = uri.getQueryParameter(KEY_ACTION);
                String actionResult = uri.getQueryParameter(KEY_RESULT);
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "action " + actionName + " " + actionResult);
                    Toast.makeText(activity, "action " + actionName + " " + actionResult,
                            Toast.LENGTH_SHORT).show();
                    if (String.valueOf(ACTION_START_LOCATION).equals(actionName)) {
                        Intent intent = new Intent(activity, MapActivity.class);
                        intent.putExtra(MapActivity.KEY_DEVICE_NAME, fromDeviceName);
                        activity.startActivity(intent);
                    }
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
                    MLog.d(TAG, "Going to open flash on ", targetDeviceName);
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
                    MLog.d(TAG, "Going to close flash on ", targetDeviceName);
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
            case ACTION_SAY_LOUDLY:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                String words = uri.getQueryParameter(KEY_WORDS);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName) && !TextUtils.isEmpty(words)) {
                    MLog.d(TAG, "say loudly on ", targetDeviceName);
                    VolumeHandler.turnVolumeUpToMax(activity);
                    HomeFragment.getSpeaker().speak(words, TextToSpeech.QUEUE_ADD, null);
//                    VolumeHandler.restoreVolume(); todo 希望能检测到说完了之后把音量改回来
                    sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                }
                break;
            case ACTION_OPEN_DOUBAN_FM:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "open douban fm on ", targetDeviceName);
                    MyAccessibilityService.MyAccessibility_EVENT =
                            new MyAccessibilityEvent(MyAccessibilityEvent.TYPE_DOUBAN_FM, fromDeviceName);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName cn = new ComponentName("com.douban.radio",
                            "com.douban.radio.ui.WelcomeActivity");
                    intent.setComponent(cn);
                    activity.startActivity(intent);
                }
                break;
            case ACTION_CLOSE_MUSIC:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "close music on ", targetDeviceName);
                    if (am != null) {
                        // Request audio focus for playback focusChangeListener
                        int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN);
                        MLog.d(TAG, "am.requestAudioFocus result ", result);
                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            // other app had stopped playing song now , so u can do u stuff now .
                            sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                        } else {
                            sendActionResult(activity, action, RESULT_FAIL, fromDeviceName);
                        }
                    }
                }
                break;
            case ACTION_VOLUME_MAX:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "volume max on ", targetDeviceName);
                    VolumeHandler.turnVolumeUpToMax(activity);
                    sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                }
                break;
            case ACTION_VOLUME_MIN:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "volume min on ", targetDeviceName);
                    VolumeHandler.turnVolumeDownToMin(activity);
                    sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                }
                break;
            case ACTION_START_LOCATION:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "start location on ", targetDeviceName);
                    MyLocation.getInstance(activity).setTargetDevice(fromDeviceName);
                    MyLocation.getInstance(activity).startLocation();
                    sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                }
                break;
            case ACTION_STOP_LOCATION:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "stop location on ", targetDeviceName);
                    MyLocation.getInstance(activity).stopLocation();
                    sendActionResult(activity, action, RESULT_SUCCESS, fromDeviceName);
                }
                break;
            case ACTION_LOCATION_INFO:
                targetDeviceName = uri.getQueryParameter(KEY_TARGET_DEVICE);
                if (MQTTSharePreference.getDeviceName(activity).equals(targetDeviceName)) {
                    MLog.d(TAG, "get location info from ", fromDeviceName);
                    String longitude = uri.getQueryParameter(KEY_LONGITUDE);
                    String latitude = uri.getQueryParameter(KEY_LATITUDE);
                    DeviceLocationInfo deviceLocationInfo = new DeviceLocationInfo();
                    deviceLocationInfo.setDevice(fromDeviceName);
                    deviceLocationInfo.setLongitude(NumberUtils.parseFloat(longitude));
                    deviceLocationInfo.setLatitude(NumberUtils.parseFloat(latitude));
                    BusEvent locationInfo = new BusEvent(MqttEventBusConfig.locationInfo, deviceLocationInfo);
                    EventBus.getDefault().post(locationInfo);
                }
                break;
            default:
                break;
        }
    }

    public static void sendActionResult(Context context, int action, String result, String targetDevice) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TARGET_DEVICE, targetDevice);
        hashMap.put(KEY_ACTION, String.valueOf(action));
        hashMap.put(KEY_RESULT, result);
        String resultMsg = MessageHandler.generateMessage(context, MessageHandler.ACTION_RESULT,
                hashMap);
        MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(context), resultMsg);
    }
}
