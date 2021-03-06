package com.frannyzhao.mqttlib.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.R;
import com.frannyzhao.mqttlib.utils.MessageHandler;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import java.util.HashMap;

/**
 * Created by zhaofengyi on 3/21/18.
 */

public class DeviceOperationView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = DeviceOperationView.class.getSimpleName();
    private HashMap<String, String> mHashMap = new HashMap<>();
    private Context mContext;
    private RelativeLayout mOperationRl;
    private LinearLayout mOperationLl;
    private TextView mDeviceNameTv;
    private ImageView mExpandIv;
    private TextView mOperationOpenFlash, mOperationCloseFlash, mOperationOpenMusic, mOperationCloseMusic,
            mOperationVolumeMax, mOperationVolumeMin, mOperationStartLocation, mOperationStopLocation;
    private RelativeLayout mOperationSay;
    private EditText mSayWords;
    public DeviceOperationView(Context context) {
        this(context, null);
    }

    public DeviceOperationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceOperationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.device_operation, this);
        mOperationRl = findViewById(R.id.operation_rl);
        mOperationLl = findViewById(R.id.operations_ll);
        mOperationRl.setOnClickListener(this);
        mDeviceNameTv = findViewById(R.id.device_name_tv);
        mExpandIv = findViewById(R.id.expand_iv);
        mExpandIv.setEnabled(true);
        mOperationOpenFlash = findViewById(R.id.op_open_flash);
        mOperationOpenFlash.setOnClickListener(this);
        mOperationCloseFlash = findViewById(R.id.op_close_flash);
        mOperationCloseFlash.setOnClickListener(this);
        mOperationOpenMusic = findViewById(R.id.op_open_music);
        mOperationOpenMusic.setOnClickListener(this);
        mOperationCloseMusic = findViewById(R.id.op_close_music);
        mOperationCloseMusic.setOnClickListener(this);
        mOperationSay = findViewById(R.id.op_say);
        mOperationSay.setOnClickListener(this);
        mSayWords = findViewById(R.id.et_say);
        mOperationVolumeMax = findViewById(R.id.op_volume_max);
        mOperationVolumeMax.setOnClickListener(this);
        mOperationVolumeMin = findViewById(R.id.op_volume_min);
        mOperationVolumeMin.setOnClickListener(this);
        mOperationStartLocation = findViewById(R.id.op_start_location);
        mOperationStartLocation.setOnClickListener(this);
        mOperationStopLocation = findViewById(R.id.op_stop_location);
        mOperationStopLocation.setOnClickListener(this);
    }

    public void setDeviceName(String name) {
        mDeviceNameTv.setText(name);
    }

    public String getDeviceName() {
        return mDeviceNameTv.getText().toString();
    }

    private void toggleExpand() {
        if (mExpandIv.isEnabled()) {
            mExpandIv.setEnabled(false);
            mOperationLl.setVisibility(VISIBLE);
        } else {
            mExpandIv.setEnabled(true);
            mOperationLl.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.operation_rl) {
            toggleExpand();
        } else if (id == R.id.op_open_flash) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_OPEN_FLASH,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_close_flash) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_CLOSE_FLASH,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_say) {
            String words = mSayWords.getText().toString();
            if (!TextUtils.isEmpty(words)) {
                mHashMap.clear();
                mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
                mHashMap.put(MessageHandler.KEY_WORDS, words);
                String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_SAY_LOUDLY,
                        mHashMap);
                MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
            }
        } else if (id == R.id.op_open_music) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_OPEN_DOUBAN_FM,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_close_music) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_CLOSE_MUSIC,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_volume_max) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_VOLUME_MAX,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_volume_min) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_VOLUME_MIN,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_start_location) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_START_LOCATION,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        } else if (id == R.id.op_stop_location) {
            mHashMap.clear();
            mHashMap.put(MessageHandler.KEY_TARGET_DEVICE, getDeviceName());
            String msg = MessageHandler.generateMessage(mContext, MessageHandler.ACTION_STOP_LOCATION,
                    mHashMap);
            MqttHandler.getInstance().publish(MQTTSharePreference.getTopic(mContext), msg);
        }
    }
}
