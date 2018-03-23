package com.frannyzhao.mqttlib;

/**
 * Created by zhaofengyi on 9/20/17.
 */

public enum MqttEventBusConfig {
    connected,
    disconnected,
    published,
    subscribed,
    connectionLost,
    messageArrived,
    deliveryComplete,
    deviceConnected,
    deviceDisconnected
}
