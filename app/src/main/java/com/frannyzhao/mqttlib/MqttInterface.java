package com.frannyzhao.mqttlib;

import android.app.Activity;
import android.content.Context;

import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Created by zhaofengyi on 9/19/17.
 */

public interface MqttInterface {
    MqttClient connect(Context context, String broker, String userName, String password);
    void publish(String topic, String message);
    void disconnect();
    void subscribe(String topic);
    void executeCommand(Context context, String topic, String command);
}
