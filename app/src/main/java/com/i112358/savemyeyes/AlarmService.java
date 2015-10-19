package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

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
//        Toast.makeText(this, "AlarmService Stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        boolean startNextAlarm = intent.getBooleanExtra("startNextAlarm", false);

        Alarm alarm = new Alarm();
        if ( startNextAlarm ) {
            SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES), MODE_PRIVATE);
            BrightnessPoint point = BrightnessPointManager.getClosestTimePoint(preferences);
            if ( point != null ) {
                alarm.setAlarm(this, point);
            }
        } else {
            alarm.cancelAlarm(this);
        }

        stopSelf();
        return START_NOT_STICKY;
    }
}
