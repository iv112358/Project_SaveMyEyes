package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ShakeService extends Service implements SensorEventListener {

    private final float ACCELERATION_STRENGTH = 13;
    private final float ACCELERATION_TRIGGER = 12;
    private float m_lastAcceleration = SensorManager.GRAVITY_EARTH;
    private ArrayList<Float> m_accelerationList = new ArrayList<Float>();
    private long m_lastChangeTime = -1;

    private int m_brightness = 255;
    private Timer m_timer = null;
    private TimerTask m_timerTask = null;

    private final Handler m_handler = new Handler();
    private boolean m_directionUp = false;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public ShakeService()
    {

    }

    public void onCreate()
    {
        super.onCreate();

    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float currentAcceleration = (float) Math.sqrt((double) (x*x + y*y + z*z));
        currentAcceleration = currentAcceleration * 0.9f + m_lastAcceleration * 0.1f;
        m_lastAcceleration = currentAcceleration;

        if ( currentAcceleration > ACCELERATION_TRIGGER ) {
            if ( m_lastChangeTime < 0 ) {
                m_lastChangeTime = System.currentTimeMillis();
            }
        }

        if ( m_lastChangeTime > 0 ) {
            m_accelerationList.add(currentAcceleration);
            if ( System.currentTimeMillis() - m_lastChangeTime > 1000 ) {
                float averegeShake = 0.0f;
                for ( Float elem : m_accelerationList ) {
                    averegeShake += elem.floatValue();
                }
                averegeShake /= m_accelerationList.size();

                if ( averegeShake > ACCELERATION_STRENGTH ) {
                    /// With handler
                    try {
                        m_brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                        m_directionUp = (m_brightness <= 10);
                    } catch ( Settings.SettingNotFoundException e ) {
                        e.printStackTrace();
                    }
                    m_handler.removeCallbacks(changeBrightness);
                    m_handler.post(changeBrightness);
                    /// With handler

                    /// With Timer
                    //ChangeBrightness();
                    /// With Timer
                } else {
                    Log.i("info", "Shake harder. " + (ACCELERATION_STRENGTH - averegeShake) + " m/s left");
                }
                m_lastChangeTime = -1;
                m_accelerationList.clear();
            }
        }
    }

    public void ChangeBrightness() {
        Log.i("info", "ShakeService ChangeBrightness called");

        try {
            m_brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            final boolean directionUp = (m_brightness <= 10);

            m_timer = new Timer();
            m_timerTask = new TimerTask() {
                @Override
                public void run() {
                    if ( directionUp ) {
                        ++m_brightness;
                    } else {
                        --m_brightness;
                    }

                    android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, m_brightness);

                    if ( m_brightness <= 0 || m_brightness >= 255 ) {
                        Log.i("info", "stop timer");
                        m_timer.cancel();
                        m_timerTask = null;
                        m_timer = null;
                    }
                }
            };
            m_timer.scheduleAtFixedRate(m_timerTask, 50, (long) (1000 / 60));
//            m_timer.schedule(m_timerTask, 50, (long) (1000 / 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable changeBrightness = new Runnable() {
        @Override
        public void run() {
            if ( m_directionUp ) {
                ++m_brightness;
            } else {
                --m_brightness;
            }

            try {
                android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, m_brightness);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ( m_brightness <= 0 || m_brightness >= 255 ) {
                Log.i("info", "stop handler");
                m_handler.removeCallbacks(changeBrightness);
            } else {
                m_handler.postDelayed(this, 11);
            }
        }
    };

}
