package com.i112358.savemyeyes;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

public class ShakeService extends Service implements SensorEventListener {
    private float m_lastAcceleration = SensorManager.GRAVITY_EARTH;
    private ArrayList<Float> m_accelerationList = new ArrayList<Float>();
    private long m_lastChangeTime = -1;

    private int m_brightness = 255;
    private int m_delay = 0;
    private int m_direction = 0;

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

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        Toast.makeText(this, "ShakeService Stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

        Toast.makeText(this, "ShakeService Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        float[] sensorValues = event.values.clone();
        float currentAcceleration = (float) Math.sqrt((Math.pow(sensorValues[0], 2) + Math.pow(sensorValues[1], 2) + Math.pow(sensorValues[2], 2)));
        currentAcceleration = currentAcceleration * 0.9f + m_lastAcceleration * 0.1f;
        m_lastAcceleration = currentAcceleration;

        if ( currentAcceleration > Float.valueOf(getResources().getString(R.string.ACCELERATION_TRIGGER)) ) {
            if ( m_lastChangeTime < 0 ) {
                m_lastChangeTime = System.currentTimeMillis();
            }
        }

        if ( m_lastChangeTime > 0 ) {
            m_accelerationList.add(currentAcceleration);
            if ( System.currentTimeMillis() - m_lastChangeTime > 1000 ) {
                float averageShake = 0.0f;
                for ( Float elem : m_accelerationList ) {
                    averageShake += elem;
                }
                averageShake /= m_accelerationList.size();

                if ( averageShake > Float.valueOf(getResources().getString(R.string.ACCELERATION_STRENGTH)) ) {
                    Log.i("info", "ShakeService start change brightness");
                    ChangeBrightness();
                } else {
                    Log.i("info", "Shake harder. " + (Float.valueOf(getResources().getString(R.string.ACCELERATION_STRENGTH)) - averageShake) + " m/s left");
                }
                m_lastChangeTime = -1;
                m_accelerationList.clear();
            }
        }
    }

    public void ChangeBrightness() {
        m_brightness = BrightnessController.getCurrentBrightness(getContentResolver());
        m_delay = BrightnessController.changeToValueInTime(0, 60);
        m_directionUp = (m_brightness <= 10);
        m_direction = 1;
        if ( !m_directionUp ) m_direction = -1;
        m_handler.removeCallbacks(changeBrightness);
        m_handler.post(changeBrightness);
    }

    private Runnable changeBrightness = new Runnable() {
        @Override
        public void run() {
            m_brightness += m_direction;

            boolean continueChange = true;
            if ( m_brightness >= 255 ) {
                m_brightness = 255;
                continueChange = false;
            } else if ( m_brightness <= 0 ) {
                m_brightness = 0;
                continueChange = false;
            }

            BrightnessController.setBrightness(getContentResolver(), m_brightness);
            if ( continueChange ) {
                m_handler.postDelayed(this, m_delay);
            } else {
                Log.i("info", "stop handler");
                m_handler.removeCallbacks(changeBrightness);
            }
        }
    };

}
