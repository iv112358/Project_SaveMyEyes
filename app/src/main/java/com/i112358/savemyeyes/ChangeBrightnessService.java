package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ChangeBrightnessService extends Service {

    private final Handler m_handler = new Handler();
    private int m_direction = 0;
    private int m_delay = 0;
    private int m_brightnessCurrent = 0;
    private int m_brightnessToSet = 0;

    public ChangeBrightnessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        m_brightnessCurrent = Utilites.getCurrentBrightness(getContentResolver());
        m_brightnessToSet = (intent.getIntExtra("brightness", 100) * 255) / 100;
        m_delay = Utilites.evaluateChangeDelay(m_brightnessCurrent, m_brightnessToSet);
        m_direction = Utilites.evaluateChangeDirection(m_brightnessCurrent, m_brightnessToSet);

        Log.i("info", "Value current is " + m_brightnessCurrent);
        Log.i("info", "Value to set is " + m_brightnessToSet);
        Log.i("info", "Value change delay is " + m_delay);
        Log.i("info", "Value change value is " + m_direction);
        Toast.makeText(this, "Set brightness to " + m_brightnessToSet, Toast.LENGTH_LONG).show();

        m_handler.removeCallbacks(changeBrightness);
        m_handler.post(changeBrightness);

        return START_STICKY;
    }

    private Runnable changeBrightness = new Runnable() {
        @Override
        public void run() {
            m_brightnessCurrent += m_direction;
            BrightnessController.setBrightness(getContentResolver(), m_brightnessCurrent);

            if ( m_brightnessCurrent != m_brightnessToSet ) {
                m_handler.postDelayed(this, m_delay);
            } else {
                m_handler.removeCallbacks(changeBrightness);
                gotoNextPoint();
            }
        }
    };

    private void gotoNextPoint()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES), MODE_PRIVATE);
        BrightnessPoint point = BrightnessPointManager.getClosestTimePoint(preferences);
        if ( point != null ) {
            Alarm alarm = new Alarm();
            alarm.setAlarm(this, point);
        }
        stopSelf();
    }
}
