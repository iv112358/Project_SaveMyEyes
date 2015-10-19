package com.i112358.savemyeyes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmService = new Intent(context, AlarmService.class);
        alarmService.putExtra("startNextAlarm", true);
        context.startService(alarmService);
    }
}