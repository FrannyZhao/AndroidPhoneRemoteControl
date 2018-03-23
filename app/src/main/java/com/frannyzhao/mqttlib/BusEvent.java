package com.frannyzhao.mqttlib;

public class BusEvent {
    private MqttEventBusConfig what;
    private Object obj;
    private Object obj1;
    private Object obj2;

    public BusEvent(MqttEventBusConfig what) {
        this.what = what;
    }

    public BusEvent(MqttEventBusConfig what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public Object getObj1() {
        return obj1;
    }

    public BusEvent setObj1(Object obj1) {
        this.obj1 = obj1;
        return this;
    }

    public Object getObj2() {
        return obj2;
    }

    public BusEvent setObj2(Object obj2) {
        this.obj2 = obj2;
        return this;
    }

    public MqttEventBusConfig getWhat() {
        return what;
    }

    public BusEvent setWhat(MqttEventBusConfig what) {
        this.what = what;
        return this;
    }

    public Object getObj() {
        return obj;
    }

    public BusEvent setObj(Object obj) {
        this.obj = obj;
        return this;
    }

}
