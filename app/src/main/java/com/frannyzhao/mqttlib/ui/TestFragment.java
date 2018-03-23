package com.frannyzhao.mqttlib.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.frannyzhao.mqttlib.BusEvent;
import com.frannyzhao.mqttlib.MqttEventBusConfig;
import com.frannyzhao.mqttlib.MqttHandler;
import com.frannyzhao.mqttlib.R;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TestFragment extends Fragment implements View.OnClickListener {
    private TextView buttonConnect, buttonSubscribe, buttonPublisher, buttonDisconnect, tvConsole;
    private EditText etServer, etPort, etUserName, etPassword, etTopic, etMessage;
    private MqttHandler mMqttHandler;
    private MqttClient mMqttClient;

    public TestFragment() {
        // Required empty public constructor
    }

    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mMqttHandler = new MqttHandler();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        buttonConnect = rootView.findViewById(R.id.btn_connect);
        buttonConnect.setOnClickListener(this);
        buttonSubscribe = rootView.findViewById(R.id.btn_subscribe);
        buttonSubscribe.setOnClickListener(this);
        buttonPublisher = rootView.findViewById(R.id.btn_publish);
        buttonPublisher.setOnClickListener(this);
        buttonDisconnect = rootView.findViewById(R.id.btn_disconnect);
        buttonDisconnect.setOnClickListener(this);
        etServer = rootView.findViewById(R.id.et_server);
        etPort = rootView.findViewById(R.id.et_port);
        etUserName = rootView.findViewById(R.id.et_username);
        etPassword = rootView.findViewById(R.id.et_password);
        etTopic = rootView.findViewById(R.id.et_topic);
        etMessage = rootView.findViewById(R.id.et_message);
        tvConsole = rootView.findViewById(R.id.txt_console);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_connect) {
            tvConsole.setText(getString(R.string.status_connecting));
            mMqttClient = mMqttHandler.connect(getActivity(), getEditTextContent(etServer)+":"+getEditTextContent(etPort),
                    getEditTextContent(etUserName), getEditTextContent(etPassword));
        } else if (id == R.id.btn_publish) {
            mMqttHandler.publish(getEditTextContent(etTopic), getEditTextContent(etMessage));
        } else if (id == R.id.btn_subscribe) {
            mMqttHandler.subscribe(getEditTextContent(etTopic));
        } else if (id == R.id.btn_disconnect) {
            mMqttHandler.disconnect();
        }
    }

    private String getEditTextContent(EditText editText) {
        String content = editText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            content = editText.getHint().toString();
        }
        return content;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BusEvent event) {
        if (event.getWhat() == MqttEventBusConfig.connected) {
            tvConsole.setText(genConsoleText(getString(R.string.status_connected)));
        } else if (event.getWhat() == MqttEventBusConfig.disconnected) {
            tvConsole.setText(genConsoleText(getString(R.string.status_disconnected)));
        } else if (event.getWhat() == MqttEventBusConfig.published) {
            tvConsole.setText(genConsoleText(getString(R.string.status_published, event.getObj().toString())));
        } else if (event.getWhat() == MqttEventBusConfig.subscribed) {
            tvConsole.setText(genConsoleText(getString(R.string.status_subscribe, event.getObj().toString())));
        } else if (event.getWhat() == MqttEventBusConfig.deliveryComplete) {
            tvConsole.setText(genConsoleText("Delivery \"" + event.getObj().toString() + "\"complete."));
        } else if (event.getWhat() == MqttEventBusConfig.messageArrived) {
            String message = event.getObj1().toString();
            String topic = event.getObj2().toString();
            tvConsole.setText(genConsoleText("Get message \"" + message + "\" from " + topic));
            mMqttHandler.executeCommand(getActivity(), topic, message);
        }
    }

    private String genConsoleText(String text) {
        String originConsoleText = tvConsole.getText().toString();
        long timeInMs = System.currentTimeMillis();
        String timeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(timeInMs);
        return "[" + timeStr + "] " + text + "\n" + originConsoleText;
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
