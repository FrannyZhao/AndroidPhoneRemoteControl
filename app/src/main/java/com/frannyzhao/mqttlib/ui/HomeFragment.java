package com.frannyzhao.mqttlib.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frannyzhao.mqttlib.BusEvent;
import com.frannyzhao.mqttlib.MqttEventBusConfig;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.R;
import com.frannyzhao.mqttlib.jobqueue.JobManager;
import com.frannyzhao.mqttlib.ui.view.DeviceOperationView;
import com.frannyzhao.mqttlib.utils.MLog;
import com.frannyzhao.mqttlib.utils.MessageHandler;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static boolean mIsConnected = false;
    private boolean mShouldRunAnimation = false;
    private int mAnimationDuration = 300;
    private MqttHandler mMqttHandler;
    private TextView mConnectTv;
    private TransitionDrawable mStatusTransition;
    private LinearLayout mDevicePanel;

    private static TextToSpeech mTts;
    private static final int CAMERA_REQUEST_CODE = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        MLog.d(TAG, "newInstance");
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        MLog.d(TAG, "onResume");
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 为了打开闪光灯, 申请相机权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }
        enableTTS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    // TODO 支持相机功能
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    // todo 不支持相机功能
                }
                break;
            default:
                break;
        }
    }

    public static TextToSpeech getSpeaker() {
        return mTts;
    }

    private void enableTTS() {
        if(mTts != null) {
            mTts.stop();
            mTts.shutdown();
            MLog.d(TAG, "TTS Destroyed");
        }
        final Locale defaultLocal = Locale.getDefault();
        MLog.d(TAG, "defaultLocal = " + defaultLocal);
        mTts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                MLog.d(TAG, "onInit status = " + status);
                if (status == TextToSpeech.SUCCESS) {
                    if (null != mTts) {
                        mTts.setSpeechRate(0.5f);
                        MLog.d(TAG, "mTts.isLanguageAvailable = " + mTts.isLanguageAvailable(defaultLocal));
                        if (mTts.isLanguageAvailable(defaultLocal) >= 0) {
                            mTts.setLanguage(defaultLocal);
                        } else if (mTts.isLanguageAvailable(Locale.ENGLISH) >= 0) {
                            mTts.setLanguage(Locale.ENGLISH);
                        } else {
                            // todo 不支持语音功能
                        }
                    } else {
                        MLog.e(TAG, "Cann't create TextToSpeech object");
                        // todo 不支持语音功能
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        MLog.d(TAG, "onDestroy");
        if (mIsConnected) {
            String msg = MessageHandler.generateMessage(getActivity(),
                    MessageHandler.ACTION_DISCONNECT, null);
            mMqttHandler.publish(MQTTSharePreference.getTopic(getActivity()), msg);
            mMqttHandler.disconnect();
        }
        EventBus.getDefault().unregister(this);
        if(mTts != null) {
            mTts.stop();
            mTts.shutdown();
            MLog.d(TAG, "TTS Destroyed");
        }
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean visibility) {
        super.setUserVisibleHint(visibility);
        MLog.d(TAG, "setUserVisibleHint " + visibility);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        MLog.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mMqttHandler = MqttHandler.getInstance();
        mConnectTv = rootView.findViewById(R.id.tv_connect);
        mConnectTv.setOnClickListener(this);
        mStatusTransition = (TransitionDrawable) mConnectTv.getBackground();
        mDevicePanel = rootView.findViewById(R.id.device_panel);
        return rootView;
    }
/*
    private void repeatAnimation() {
        mStatusTransition.startTransition(mAnimationDuration);
        new CountDownTimer(mAnimationDuration, mAnimationDuration / 2) {
            public void onTick(long millisUntilFinished){
//                MLog.d(TAG, "onTick " + millisUntilFinished);
            }

            public void onFinish() {
//                MLog.d(TAG, "onFinish mIsConnected " + mIsConnected);
                if (mShouldRunAnimation) {
                    mStatusTransition.resetTransition();
                    repeatAnimation();
                }
            }
        }.start();
    }
*/
    private void repeatAnimation() {
        JobManager.post(new Runnable() {
            @Override
            public void run() {
                try {
                    while (mShouldRunAnimation) {
                        mStatusTransition.startTransition(mAnimationDuration);
                        Thread.sleep(mAnimationDuration);
                        if (mShouldRunAnimation) {
                            mStatusTransition.resetTransition();
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MLog.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MLog.d(TAG, "onDetach");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_connect) {
            if (TextUtils.isEmpty(MQTTSharePreference.getDeviceName(getActivity()))) {
                Toast.makeText(getActivity(), getString(R.string.unique_name_toast), Toast.LENGTH_LONG).show();
            } else {
                if (!mIsConnected) {
                    mShouldRunAnimation = true;
//                    JobManager.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            repeatAnimation();
//                        }
//                    });
                    repeatAnimation();
                    mMqttHandler.connect(getActivity(),
                            MQTTSharePreference.getServer(getActivity()) + ":" + MQTTSharePreference.getPort(getActivity()),
                            MQTTSharePreference.getLoginName(getActivity()), MQTTSharePreference.getPassword(getActivity()));
                } else {
                    String msg = MessageHandler.generateMessage(getActivity(), MessageHandler.ACTION_DISCONNECT, null);
                    mMqttHandler.publish(MQTTSharePreference.getTopic(getActivity()), msg);
                    mMqttHandler.disconnect();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BusEvent event) {
        MLog.d(TAG, "onEventMainThread " + event);
        if (event.getWhat() == MqttEventBusConfig.connected) {
            MLog.d(TAG, "onEventMainThread connected");
            mMqttHandler.subscribe(MQTTSharePreference.getTopic(getActivity()));
        } else if (event.getWhat() == MqttEventBusConfig.disconnected) {
            MLog.d(TAG, "onEventMainThread disconnected");
            mIsConnected = false;
            updateConnectBtn();
        } else if (event.getWhat() == MqttEventBusConfig.published) {
            MLog.d(TAG, "onEventMainThread published");
        } else if (event.getWhat() == MqttEventBusConfig.subscribed) {
            MLog.d(TAG, "onEventMainThread subscribed");
            String msg = MessageHandler.generateMessage(getActivity(), MessageHandler.ACTION_FIRST_ONLINE, null);
            mMqttHandler.publish(MQTTSharePreference.getTopic(getActivity()), msg);
            mIsConnected = true;
            updateConnectBtn();
        } else if (event.getWhat() == MqttEventBusConfig.deliveryComplete) {
            MLog.d(TAG, "onEventMainThread deliveryComplete " + event.getObj().toString());
        } else if (event.getWhat() == MqttEventBusConfig.messageArrived) {
            String message = event.getObj1().toString();
            String topic = event.getObj2().toString();
            MLog.d(TAG, "onEventMainThread messageArrived " + message);
            MessageHandler.parseMessage(getActivity(), message);
        } else if (event.getWhat() == MqttEventBusConfig.deviceConnected) {
            String deviceName = event.getObj().toString();
            MLog.d(TAG, "onEventMainThread deviceConnected ", deviceName);
            mDevicePanel.setVisibility(View.VISIBLE);
            boolean deviceAlreadyAdded = false;
            for (int i = 1; i < mDevicePanel.getChildCount(); i++) {
                DeviceOperationView deviceOperationView = (DeviceOperationView)mDevicePanel.getChildAt(i);
                if (deviceName.equals(deviceOperationView.getDeviceName())) {
                    deviceAlreadyAdded = true;
                    break;
                }
            }
            if (!deviceAlreadyAdded) {
                DeviceOperationView deviceOperationView = new DeviceOperationView(getActivity());
                deviceOperationView.setDeviceName(deviceName);
                mDevicePanel.addView(deviceOperationView);
                MQTTSharePreference.addOnlineDevice(getActivity(), deviceName);
            }
            MQTTSharePreference.setLastOnlineTime(getActivity(), deviceName, System.currentTimeMillis());
        } else if (event.getWhat() == MqttEventBusConfig.deviceDisconnected) {
            String deviceName = event.getObj().toString();
            for (int i = 1; i < mDevicePanel.getChildCount(); i++) {
                DeviceOperationView deviceOperationView = (DeviceOperationView)mDevicePanel.getChildAt(i);
                if (deviceName.equals(deviceOperationView.getDeviceName())) {
                    mDevicePanel.removeViewAt(i);
                    MQTTSharePreference.removeOnlineDevice(getActivity(), deviceName);
                    MQTTSharePreference.setLastOnlineTime(getActivity(), deviceName, 0);
                    if (mDevicePanel.getChildCount() == 1) {
                        mDevicePanel.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
    }

    private void updateConnectBtn() {
        mShouldRunAnimation = false;
        if (mIsConnected) {
            mConnectTv.setText(getText(R.string.btn_txt_disconnect));
            mStatusTransition.startTransition(mAnimationDuration);
        } else {
            mConnectTv.setText(getText(R.string.btn_txt_connect));
            mStatusTransition.reverseTransition(mAnimationDuration);
            mDevicePanel.setVisibility(View.GONE);
        }
    }

    public static boolean isConnected() {
        return mIsConnected;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
