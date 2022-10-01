package com.clientapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

// Start MqttConnectionManagerService on system boot up
public class MqttServiceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            // For Android API >= 26
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context,
                        MqttConnectionManagerService.class));
            } else {
                context.startService(new Intent(context, MqttConnectionManagerService.class));
            }
        }
    }
}