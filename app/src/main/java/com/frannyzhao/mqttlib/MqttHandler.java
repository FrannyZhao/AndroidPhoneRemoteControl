package com.frannyzhao.mqttlib;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhaofengyi on 9/19/17.
 */

public class MqttHandler implements MqttInterface, MqttCallback {
    private static final String TAG = MqttHandler.class.getSimpleName();
    private static final int qos = 1;
    private static MqttClient mqttClient = null;
    private static MqttHandler mMqttHandler = null;

    public MqttHandler() {

    }

    public static MqttHandler getInstance() {
        if (mMqttHandler == null) {
            mMqttHandler = new MqttHandler();
        }
        return mMqttHandler;
    }

    @Override
    public MqttClient connect(Context context, String broker, String userName, String password) {
        String clientId = MQTTSharePreference.getDeviceName(context);
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                connOpts.setUserName(userName);
                connOpts.setPassword(password.toCharArray());
            }
            connOpts.setCleanSession(true);
            connOpts.setWill("monitor", "mqtt.....test....".getBytes(), 2, true);
            connOpts.setConnectionTimeout(10);
            connOpts.setKeepAliveInterval(10);
            connOpts.setAutomaticReconnect(false);
            Log.d(TAG, "Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            if (mqttClient.isConnected()) {
                Log.d(TAG, "Connected");
                mqttClient.setCallback(this);
                BusEvent connectEvent = new BusEvent(MqttEventBusConfig.connected);
                EventBus.getDefault().post(connectEvent);
            } else {
                Log.d(TAG, "Connect fail");
                BusEvent disconnectEvent = new BusEvent(MqttEventBusConfig.disconnected);
                EventBus.getDefault().post(disconnectEvent);
            }
        } catch (MqttException me) {
            printMqttExceptionErrorLog(me);
        }
        return mqttClient;
    }

    private void reconnect(MqttClient mqttClient) {
        try {
            mqttClient.reconnect();
        } catch (MqttException me) {
            printMqttExceptionErrorLog(me);
        }
    }

    @Override
    public void publish(String topic, String message) {
        Log.d(TAG, "Publishing message: " + message + " to topic " + topic);
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        try {
            mqttClient.publish(topic, mqttMessage);
            Log.d(TAG, "Message " + message + " published");
            BusEvent publishEvent = new BusEvent(MqttEventBusConfig.published, message);
            EventBus.getDefault().post(publishEvent);
        } catch (MqttException me) {
            printMqttExceptionErrorLog(me);
        }
    }

    @Override
    public void disconnect() {
        try {
            mqttClient.disconnect();
            Log.d(TAG, "Disconnected");
            BusEvent disconnectEvent = new BusEvent(MqttEventBusConfig.disconnected);
            EventBus.getDefault().post(disconnectEvent);
        } catch (MqttException me) {
            printMqttExceptionErrorLog(me);
        }
    }

    @Override
    public void subscribe(String topic) {
        Log.d(TAG, "Subscribing to topic " + topic);
        try {
            mqttClient.subscribe(topic, qos);
            BusEvent subscribeEvent = new BusEvent(MqttEventBusConfig.subscribed, topic);
            EventBus.getDefault().post(subscribeEvent);
        } catch (MqttException me) {
            printMqttExceptionErrorLog(me);
        }
    }

    private void printMqttExceptionErrorLog(MqttException me) {
        Log.d(TAG, "MqttException ReasonCode " + me.getReasonCode());
        Log.d(TAG, "MqttException Message " + me.getMessage());
        Log.d(TAG, "MqttException LocalizedMessage " + me.getLocalizedMessage());
        Log.d(TAG, "MqttException Cause " + me.getCause());
        Log.d(TAG, "MqttException " + me);
        BusEvent disconnectEvent = new BusEvent(MqttEventBusConfig.disconnected);
        EventBus.getDefault().post(disconnectEvent);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost ", cause);
//        if (mqttClient != null) {
//            this.reconnect(mqttClient);
//            if (mqttClient.isConnected()) {
//                BusEvent connectEvent = new BusEvent(MqttEventBusConfig.connected);
//                EventBus.getDefault().post(connectEvent);
//                return;
//            }
//        }
        BusEvent disconnectEvent = new BusEvent(MqttEventBusConfig.disconnected);
        EventBus.getDefault().post(disconnectEvent);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        BusEvent msgArrivedEvent = new BusEvent(MqttEventBusConfig.messageArrived);
        msgArrivedEvent.setObj1(message.toString());
        msgArrivedEvent.setObj2(topic);
        EventBus.getDefault().post(msgArrivedEvent);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        String msg = "";
        try {
            MqttMessage mqttMessage = token.getMessage();
            if (mqttMessage != null) {
                msg = mqttMessage.toString();
            }
        } catch (MqttException me) {
            printMqttExceptionErrorLog(me);
        }
        BusEvent deliveredEvent = new BusEvent(MqttEventBusConfig.deliveryComplete, msg);
        EventBus.getDefault().post(deliveredEvent);
    }

    @Override
    public void executeCommand(Context context, String topic, String command) {
        if (TextUtils.isEmpty(command)) {
            return;
        }
        if (command.equals(MqttCustomCommands.CMD_TEST)) {
            publish(topic, MqttCustomCommands.CMD_DONE);
        } else if (command.equals(MqttCustomCommands.CMD_VOLUME_UP)) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int MUSIC_VOLUME = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, MUSIC_VOLUME + 1, AudioManager.FLAG_PLAY_SOUND);
            publish(topic, MqttCustomCommands.CMD_DONE);
        }
    }
}
