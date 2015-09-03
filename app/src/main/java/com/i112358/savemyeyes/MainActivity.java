package com.i112358.savemyeyes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static MainActivity activity;
    private int m_brightness = 255;
    private ContentResolver m_contentResolver;
    private Timer m_timer = null;
    private TimerTask m_timerTask = null;
    Intent m_shakeService = null;

    public static MainActivity Get() { return activity;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("info", "MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        m_shakeService = new Intent(this, ShakeService.class);
        m_contentResolver = getContentResolver();

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.main_layout);
        TextView txt1 = new TextView(MainActivity.this);
        txt1.setText("niggers like butter");
        txt1.setRotation(1.8f);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.addView(txt1);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
    }

    @Override
    public void onDestroy()
    {
        Log.w("info", "MainActivity onDestroy");
        super.onDestroy();
        this.stopService(m_shakeService);
    }

    @Override
    public void onResume( ) {
        Log.w("info", "MainActivity onResume");
        super.onResume();
//        this.startService(m_shakeService);
        this.startService(new Intent(this, ShakeService.class));

        m_brightness = 0;
    }

    @Override
    public void onPause()
    {
        Log.w("info", "MainActivity onPause");
        super.onPause();
        this.stopService(new Intent(this, ShakeService.class));
    }

    @Override
    public void onStop()
    {
        Log.w("info", "MainActivity onStop");
        super.onStop();
        this.stopService(new Intent(this, ShakeService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ChangeBrightness()
    {
        Log.i("info", "MainActivity ChangeBrightness");
        try
        {
            m_brightness = android.provider.Settings.System.getInt(m_contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        Log.i("info", "Brightness is " + m_brightness);
        final boolean directionUp = ( m_brightness <= 10 ) ? true : false;

        try {
            m_timer = new Timer();
            m_timerTask= new TimerTask() {
                @Override
                public void run() {
                    android.provider.Settings.System.putInt(m_contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, m_brightness);
                    if ( directionUp ) {
                        m_brightness++;
                        if ( m_brightness >= 255 ) {
                            Log.i("info", "stop timer");
                            m_timerTask.cancel();
                        }
                    } else {
                        m_brightness--;
                        if ( m_brightness <= 0 ) {
                            Log.i("info", "stop timer");
                            m_timerTask.cancel();
                        }
                    }
                }
            };
            m_timer.schedule(m_timerTask, 50, (long)(1000 / 60) );
        } catch ( Exception e ) {

        }
    }
}
