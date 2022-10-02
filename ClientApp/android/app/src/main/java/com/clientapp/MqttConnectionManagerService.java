package com.clientapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

import javax.annotation.Nullable;

public class MqttConnectionManagerService extends Service {
    private final String TAG = "MqttConnectionManager";
    private final String CHANNEL_ID = "MqttConnectionChannel";
    private final String CLIENT_ID = String.format("%s_%s_%s",
            Build.MANUFACTURER,
            Build.PRODUCT,
            UUID.randomUUID().toString());
    private MqttAndroidClient client;
    private MqttConnectOptions options;

    @Override
    public void onCreate() {
        super.onCreate();
        options = createMqttConnectOptions();
        client = createMqttAndroidClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MQTT Client Service")
                .setContentText("Device connected to the MQTT broker.")
                .setSmallIcon(R.drawable.mqtt_logo)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();
        startForeground(20221001, notification);
        connect();
        return START_STICKY;
    }

    private MqttConnectOptions createMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(30);
        options.setUserName(BuildConfig.MQTT_USERNAME);
        options.setPassword(BuildConfig.MQTT_PASSWORD.toCharArray());
        options.setAutomaticReconnect(true);
        return options;
    }

    private MqttAndroidClient createMqttAndroidClient() {
        return new MqttAndroidClient(getApplicationContext(), BuildConfig.MQTT_BROKER_URI, CLIENT_ID);
    }

    public void connect() {
        try {
            if (!client.isConnected()) {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new MqttConnectionListener(client));
                client.setCallback(new MqttSubscriber(getApplicationContext()));
            }
        } catch (MqttException e) {
            Log.d(TAG, "Connection to MQTT broker failed");
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MqttClientChannel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for the MQTT client foreground service");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
