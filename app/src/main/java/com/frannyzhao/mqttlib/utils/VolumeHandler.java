package com.frannyzhao.mqttlib.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by zhaofengyi on 4/6/17.
 */

public class VolumeHandler {
    private static final String TAG = "VolumeHandler";
    // 调音量
    private static int MUSIC_VOLUME;
    private static boolean needRestoreVolume = true;

    public static void turnVolumeUpToMax(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 保存原先的音量值
        MUSIC_VOLUME = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (am.isWiredHeadsetOn() || am.isBluetoothA2dpOn()) {
            needRestoreVolume = false;
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC,
                    am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
            needRestoreVolume = true;
        }
    }

    public static void turnVolumeDownToMin(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 保存原先的音量值
        MUSIC_VOLUME = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (am.isWiredHeadsetOn() || am.isBluetoothA2dpOn()) {
            needRestoreVolume = false;
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
            needRestoreVolume = true;
        }
    }

    public void restoreVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 音量恢复到原本的值
        if (needRestoreVolume) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, MUSIC_VOLUME, AudioManager.FLAG_PLAY_SOUND);
        }
    }

}
