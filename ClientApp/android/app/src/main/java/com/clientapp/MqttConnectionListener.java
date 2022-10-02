package com.clientapp;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttConnectionListener implements IMqttActionListener {
    private final String TAG = "MqttConnectionListener";
    private final MqttAndroidClient client;
    private final String TOPIC = BuildConfig.MQTT_TOPIC;

    MqttConnectionListener(MqttAndroidClient client) {
        this.client = client;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        client.setBufferOpts(disconnectedBufferOptions);
        subscribeToTopic();
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e(TAG, "Failed to connect to " + BuildConfig.MQTT_BROKER_URI);
        exception.printStackTrace();
    }

    private void subscribeToTopic() {
        try {
            client.subscribe(TOPIC, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed to " + TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe to " + TOPIC);
                }
            });
        } catch (MqttException e){
            Log.d(TAG, "Error while subscribing to topic");
            e.printStackTrace();
        }
    }
}
