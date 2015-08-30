package com.i112358.savemyeyes;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private int m_brightness = 255;
    private ContentResolver m_contentResolver;
    private Window m_window;
    private Timer m_timer = null;
    private TimerTask m_timerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_contentResolver = getContentResolver();
        m_window = getWindow();

        try
        {
            m_brightness = android.provider.Settings.System.getInt(m_contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }

//        m_brightness = System.getInt(m_contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        Log.i("info", "Brightness is " + m_brightness);
//        android.provider.Settings.System.putInt(m_contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, 100);


        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.main_layout);
        TextView txt1 = new TextView(MainActivity.this);
        txt1.setText("niggers like butter");
        txt1.setRotation(1.8f);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.addView(txt1);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
    }

    @Override
    public void onResume( ) {
        super.onResume();
        m_brightness = 0;

        try {
            m_timer = new Timer();
            m_timerTask= new TimerTask() {
                @Override
                public void run() {
                    m_brightness++;
                    android.provider.Settings.System.putInt(m_contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, m_brightness);
                    Log.i("info", " new brightness is " + m_brightness);
                    if ( m_brightness >= 255 ) {
                        Log.i("info", "stop timer");
                        m_timerTask.cancel();
                    }
                }
            };
            m_timer.schedule(m_timerTask, 150, 50);
        } catch ( Exception e ){

        }
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
}
