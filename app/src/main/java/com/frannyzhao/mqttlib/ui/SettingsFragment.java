package com.frannyzhao.mqttlib.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.frannyzhao.mqttlib.BusEvent;
import com.frannyzhao.mqttlib.MqttEventBusConfig;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.R;
import com.frannyzhao.mqttlib.utils.sp.MQTTSharePreference;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private EditText mDeviceName, mServer, mPort, mLoginName, mPassword, mTopic;
    private TextView mSaveBtn;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean visibility) {
        super.setUserVisibleHint(visibility);
        Log.d(TAG, "setUserVisibleHint " + visibility);
        if (visibility) {
            mDeviceName.setText(MQTTSharePreference.getDeviceName(getActivity()));
            mServer.setText(MQTTSharePreference.getServer(getActivity()));
            mPort.setText(MQTTSharePreference.getPort(getActivity()));
            mLoginName.setText(MQTTSharePreference.getLoginName(getActivity()));
            mPassword.setText(MQTTSharePreference.getPassword(getActivity()));
            mTopic.setText(MQTTSharePreference.getTopic(getActivity()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        mDeviceName = rootView.findViewById(R.id.et_device_name);
        mServer = rootView.findViewById(R.id.et_server);
        mPort = rootView.findViewById(R.id.et_port);
        mLoginName = rootView.findViewById(R.id.et_login_name);
        mPassword = rootView.findViewById(R.id.et_password);
        mTopic = rootView.findViewById(R.id.et_topic);
        mSaveBtn = rootView.findViewById(R.id.tv_save);
        mSaveBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_save) {
            MQTTSharePreference.setDeviceName(getActivity(), mDeviceName.getText().toString());
            MQTTSharePreference.setServer(getActivity(), mServer.getText().toString());
            MQTTSharePreference.setPort(getActivity(), mPort.getText().toString());
            MQTTSharePreference.setLoginName(getActivity(), mLoginName.getText().toString());
            MQTTSharePreference.setPassword(getActivity(), mPassword.getText().toString());
            MQTTSharePreference.setTopic(getActivity(), mTopic.getText().toString());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BusEvent event) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
