package com.i112358.savemyeyes;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.i112358.savemyeyes.Graphics.Line;
import com.i112358.savemyeyes.Shake.ShakeService;
import com.i112358.savemyeyes.Shake.ShakeServiceSettings;
import com.i112358.savemyeyes.Shake.ShakeServiceSettingsActivity;

import java.security.Permissions;

public class MainActivity extends Activity {

    private static final int REQUEST_WRITE_SETTINGS = 0;
    private View mLayout = null;

    public static MainActivity Get() { return activity; }
    private static MainActivity activity;
    private SharedPreferences m_preferences = null;
    private TextView m_brightnessPointsText = null;
    private TextView m_brightnessChangePeriodText = null;
    private Switch m_changeBrightnessSwitcher = null;

    private int m_changeBrightnessPeriod = 0;

    public boolean isChangeBrightnessEnable() { return m_isChangeBrightnessEnable; }
    private boolean m_isChangeBrightnessEnable = false;

    public BrightnessPoint getNextBrightnessPoint() { return m_nextBrightnessPoint; }
    public void setNextBrightnessPoint() { this.m_nextBrightnessPoint = BrightnessPointManager.getClosestTimePoint(m_preferences); }
    private BrightnessPoint m_nextBrightnessPoint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("info", "MainActivity onCreate");
        activity = this;
        mLayout = findViewById(R.id.main_layout);
        setContentView(R.layout.activity_main);
        Utilites.setDensity(getApplicationContext());

        m_preferences = getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE);
        Log.i("info", "Settings is " + m_preferences.getAll());
        BrightnessPointManager.loadSavedPoints(m_preferences);

        m_isChangeBrightnessEnable = m_preferences.getBoolean("changeBrightnessStatus", false);
        m_changeBrightnessSwitcher = (Switch)findViewById(R.id.changeBrightnessSwitcher);
        m_changeBrightnessSwitcher.setChecked(m_isChangeBrightnessEnable);
        m_changeBrightnessSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeBrightnessState(isChecked);
            }
        });

        m_changeBrightnessPeriod = m_preferences.getInt("changeBrightnessPeriod", 0);
        m_brightnessChangePeriodText = (TextView)findViewById(R.id.setBRightnessPointSmoothSwitch);

        SeekBar seekBar = (SeekBar)findViewById(R.id.smooth_switcher);
        seekBar.setMax(290);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("info", "new seconds is " + (progress + 10));
                m_changeBrightnessPeriod = progress;
                m_brightnessChangePeriodText.setText("Change in " + (m_changeBrightnessPeriod + 10) + " seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("info", "save seconds");
                SharedPreferences.Editor editor = m_preferences.edit();
                editor.putInt("changeBrightnessPeriod", m_changeBrightnessPeriod);
                editor.apply();
            }
        });
        seekBar.setProgress(m_changeBrightnessPeriod);
    }

    @Override
    public void onDestroy()
    {
        Log.w("info", "MainActivity onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume( ) {
        super.onResume();
        Log.w("info", "MainActivity onResume");

        if ( m_brightnessPointsText == null )
            m_brightnessPointsText = (TextView)findViewById(R.id.setBrightnessPointsText);

        int pointsCount = BrightnessPointManager.getPointsCount();
        if ( pointsCount > 0 ) {
            m_brightnessPointsText.setText(pointsCount + " " + getString(R.string.main_menu_set_brightness_points_text));
        } else {
            m_brightnessPointsText.setText("Brightness points doesn't exists");
        }
    }

    @Override
    public void onPause() {
        Log.w("info", "MainActivity onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.w("info", "MainActivity onStop");
        super.onStop();
    }

    ////////////////////////////////

    public void changeBrightnessState( final boolean isStart )
    {
        if ( isPermissonGranted() ) {
            m_isChangeBrightnessEnable = isStart;
            SharedPreferences.Editor editor = m_preferences.edit();
            editor.putBoolean("changeBrightnessStatus", m_isChangeBrightnessEnable);
            editor.apply();

            Intent alarmService = new Intent(this, AlarmService.class);
            alarmService.putExtra("startScheduledChangeBrightness", isStart);
            startService(alarmService);
        }
        m_changeBrightnessSwitcher.setChecked(m_isChangeBrightnessEnable);
    }

    public void onViewPointsClick( View view )
    {
        if ( isPermissonGranted() ) {
            Intent intent = new Intent(this, SetPointsActivity.class);
            startActivity(intent);
        }
    }

    private boolean isPermissonGranted()
    {
        boolean isGranted = Utilites.isPermissionGranted(getApplicationContext());
        if ( !isGranted ) {
            new AlertDialog.Builder(activity).setMessage("For change screen brightness automatically please grant WRITE SETTINGS permission.")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e("MainActivity", "error starting permission intent", e);
                            }
                        }
                    })
                    .show();
        }
        return isGranted;
    }
}
