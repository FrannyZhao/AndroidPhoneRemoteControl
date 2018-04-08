package com.frannyzhao.mqttlib;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.frannyzhao.mqttlib.entity.MyAccessibilityEvent;
import com.frannyzhao.mqttlib.jobqueue.JobManager;
import com.frannyzhao.mqttlib.ui.MainActivity;
import com.frannyzhao.mqttlib.utils.CollectionUtils;
import com.frannyzhao.mqttlib.utils.MLog;
import com.frannyzhao.mqttlib.utils.MessageHandler;

import java.util.List;

/**
 * Created by zhaofengyi on 4/8/18.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = MyAccessibilityService.class.getSimpleName();
    public static MyAccessibilityEvent MyAccessibility_EVENT = null;

    private static final int RETRY_COUNT = 3;
    private static final int[] TIME_INTERVAL = new int[]{3, 3, 5};

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        MLog.d(TAG, "onServiceConnected");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MLog.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        MLog.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        MLog.d(TAG, "onAccessibilityEvent AccessibilityEvent ", MyAccessibility_EVENT);
        if (MyAccessibility_EVENT != null) {
            int eventType = MyAccessibility_EVENT.getType();
            String device = MyAccessibility_EVENT.getDevice();
            switch (eventType) {
                case MyAccessibilityEvent.TYPE_DOUBAN_FM:
                    playDoubanFm(device);
                    break;
                default:
                    break;
            }
        }
    }

    public void playDoubanFm(final String device) {
        MLog.d(TAG, "playDoubanFm");
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (am.isMusicActive()) {
                MessageHandler.sendActionResult(MyAccessibilityService.this,
                        MessageHandler.ACTION_OPEN_DOUBAN_FM, MessageHandler.RESULT_SUCCESS,
                        device);
            } else {
                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void onActionDone() {
                        reset();
                        Intent intent = new Intent(MyAccessibilityService.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyAccessibilityService.this.startActivity(intent);
                        if (am.isMusicActive()) {
                            MessageHandler.sendActionResult(MyAccessibilityService.this,
                                    MessageHandler.ACTION_OPEN_DOUBAN_FM,
                                    MessageHandler.RESULT_SUCCESS,
                                    device);
                        } else {
                            MessageHandler.sendActionResult(MyAccessibilityService.this,
                                    MessageHandler.ACTION_OPEN_DOUBAN_FM,
                                    MessageHandler.RESULT_FAIL,
                                    device);
                        }
                    }

                    @Override
                    public boolean needRetry() {
                        return !am.isMusicActive();
                    }

                    @Override
                    public String getNodeName() {
                        return "com.douban.radio:id/btn_player_next";
                    }
                };
                retryAction(actionListener);
            }
        }
    }

    private void retryAction(final ActionListener actionListener) {
        JobManager.post(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int retryCount = 0; retryCount < RETRY_COUNT; retryCount ++) {
                        MLog.d(TAG, "going to wait ", TIME_INTERVAL[retryCount], " s");
                        Thread.sleep(TIME_INTERVAL[retryCount] * 1000);
                        if (actionListener.needRetry()) {
                            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                            MLog.d(TAG, "nodeInfo ", nodeInfo);
                            if (nodeInfo != null) {
                                final List<AccessibilityNodeInfo> nodeInfos =
                                        nodeInfo.findAccessibilityNodeInfosByViewId(
                                                actionListener.getNodeName());
                                MLog.d(TAG, "nodeInfos ", nodeInfos);
                                if (!CollectionUtils.isNullOrEmpty(nodeInfos)) {
                                    reset();
                                    for (AccessibilityNodeInfo accessibilityNodeInfo : nodeInfos) {
                                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    actionListener.onActionDone();
                } catch (InterruptedException e) {
                    MLog.printStackTrace(e);
                }
            }
        });
    }

    private void reset() {
        MyAccessibility_EVENT = null;
    }

    @Override
    public void onInterrupt() {
        MLog.d(TAG, "onInterrupt");
    }

    interface ActionListener {
        void onActionDone();
        boolean needRetry();
        String getNodeName();
    }
}
