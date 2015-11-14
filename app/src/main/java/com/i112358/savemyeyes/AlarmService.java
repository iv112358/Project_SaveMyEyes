package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

public class AlarmService extends Service {
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        Alarm alarm = new Alarm();
        alarm.cancelAlarm(this);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE);
        boolean isChangeBrightnessEnable = preferences.getBoolean("changeBrightnessStatus", false);
        if ( isChangeBrightnessEnable ) {
            Bundle extras = intent.getExtras();
            if ( extras != null ) {
                if ( extras.containsKey("startScheduledChangeBrightness") ) {
                    boolean startScheduledChangeBrightness = extras.getBoolean("startScheduledChangeBrightness", false);
                    if ( startScheduledChangeBrightness ) {
                        BrightnessPoint point = BrightnessPointManager.getClosestTimePoint(preferences);
                        if ( point != null ) {
                            alarm.setAlarm(this, point);
                        }
                    }
                }
            }
        }

        stopSelf();
        return START_NOT_STICKY;
    }
}