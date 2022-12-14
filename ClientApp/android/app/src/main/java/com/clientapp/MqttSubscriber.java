package com.clientapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSubscriber implements MqttCallbackExtended {
    private final String TAG = "MqttSubscriber";
    private Context context;

    MqttSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.d(TAG, "Connected to broker " + serverURI);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Connection to broker lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG,"Incoming message: " + new String(message.getPayload()));
        Intent headlessJsIntent = new Intent(context, MqttEventHeadlessJsService.class);
        Bundle bundle = new Bundle();
        bundle.putString("payload", message.toString());
        bundle.putInt("id", message.getId());
        headlessJsIntent.putExtras(bundle);
        context.startService(headlessJsIntent);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
