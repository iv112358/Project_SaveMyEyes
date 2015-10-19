package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

public class SetPointsActivity extends Activity {

    public static SetPointsActivity Get() { return activity; }
    private static SetPointsActivity activity = null;
    private int[] m_newValues = {0, 0, 0};
    Dialog m_dialog = null;
    LinearLayout m_layout = null;
    private int m_previousBrightnessValue = 255;
    private boolean m_enableBrightnessPreview = false;
    private SharedPreferences m_preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.points_layout);
        m_layout = (LinearLayout)findViewById(R.id.pointsLayout);

        m_preferences = getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE);
        MainActivity.Get().setCurrentBrightnessPoint();
        updateSetPointScreen();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.w("info", "SetPointsActivity onResume");
    }

    public void onEditPointClick( View view )
    {
        showSetDialog(BrightnessPointManager.getPoint((int) view.getTag()));
    }

    public void onDeletePointClick( View view )
    {
        BrightnessPointManager.removePoint((int)view.getTag());
        BrightnessPointManager.saveToPreferences(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
        updateSetPointScreen();
    }

    public void onAddPointClick( View view )
    {
        showSetDialog(null);
    }

    private void showSetDialog( final BrightnessPoint point )
    {
        m_previousBrightnessValue = Utilites.getCurrentBrightness(getContentResolver());

        m_dialog = new Dialog(this);
        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dialog.setContentView(R.layout.setup_point);
        m_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                m_newValues[0] = m_newValues[1] = m_newValues[2] = 0;
                Log.i("info", "Dismiss is " + Utilites.convertTime(m_newValues[0], m_newValues[1]) + " Brightness is " + m_newValues[2]);
                updateSetPointScreen();
            }
        });

        final TextView brightness = (TextView)(m_dialog.findViewById(R.id.textSetPointBrightness));

        Button doneButton = (Button)(m_dialog.findViewById(R.id.buttonSetPointDone));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point != null) {
                    point.updatePoint(m_newValues[0], m_newValues[1], m_newValues[2]);
                } else {
                    BrightnessPointManager.addPoint(new BrightnessPoint(m_newValues[0], m_newValues[1], m_newValues[2]));
                }
                BrightnessPointManager.saveToPreferences(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
                Log.i("info", "Time is " + Utilites.convertTime(m_newValues[0], m_newValues[1]) + " Brightness is " + m_newValues[2]);
                m_dialog.dismiss();
            }
        });

        SeekBar seekBar = (SeekBar)(m_dialog.findViewById(R.id.seekBar));
        seekBar.setMax(255);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("info", "switcher onProgressChanged " + seekBar.getProgress());
                if (m_enableBrightnessPreview) {
                    Utilites.setBrightness(getContentResolver(), seekBar.getProgress());
                }
                m_newValues[2] = seekBar.getProgress();
                int percent = getPercentValue(seekBar.getProgress());
                brightness.setText("Brightness " + percent + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("info", "switcher onStartTrackingTouch " + seekBar.getProgress());
                m_enableBrightnessPreview = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("info", "switcher onStopTrackingTouch " + seekBar.getProgress());
                m_enableBrightnessPreview = false;
                Utilites.setBrightness(getContentResolver(), m_previousBrightnessValue);
            }
        });

        TimePicker timePicker = (TimePicker)(m_dialog.findViewById(R.id.timePicker));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                m_newValues[0] = hourOfDay;
                m_newValues[1] = minute;
            }
        });

        if ( point != null ) {
            seekBar.setProgress(point.getBrightness());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(point.getHour());
                timePicker.setMinute(point.getMinute());
            } else {
                timePicker.setCurrentHour(point.getHour());
                timePicker.setCurrentMinute(point.getMinute());
            }
        } else {
            seekBar.setProgress(seekBar.getMax());
            Calendar calendar = Calendar.getInstance();
            m_newValues[0] = calendar.get(Calendar.HOUR_OF_DAY);
            m_newValues[1] = calendar.get(Calendar.MINUTE);
        }

        brightness.setText("Brightness " + getPercentValue(seekBar.getProgress()) + "%");

        m_dialog.show();
    }

    private int getPercentValue( final int absoluteValue )
    {
        return (int)((float)absoluteValue / 255 * 100);
    }

    public void updateSetPointScreen()
    {
        for ( int i = 0; i < m_layout.getChildCount(); i++ ) {
            View point = m_layout.getChildAt(i);
            if ( point != null ) {
                if ( ("point").equals(point.getTag()) ) {
                    i--;
                    m_layout.removeView(point);
                }
            }
        }

        BrightnessPoint nextPoint = BrightnessPointManager.getClosestTimePoint(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
        BrightnessPointManager.sortPoints();

        int pointsCount = BrightnessPointManager.getPointsCount();
        for ( int i = 0; i < pointsCount; i++ ) {

            View point = getLayoutInflater().inflate(R.layout.point_element,null);

            Button button_delete = (Button)point.findViewById(R.id.button_delete_point);
            button_delete.setTag(i);

            Button button_edit = (Button)point.findViewById(R.id.button_edit_point);
            button_edit.setTag(i);

            TextView text = (TextView)point.findViewById(R.id.brightnessPointText);
            BrightnessPoint brPoint = BrightnessPointManager.getPoint(i);
            text.setText(getPercentValue(brPoint.getBrightness()) + "% on " + Utilites.convertTime(brPoint.getHour(), brPoint.getMinute()));

            point.setTag("point");
            m_layout.addView(point);

            if ( brPoint.equals(nextPoint) ) {
                point.setBackgroundColor(0xFF80888D);
            }
        }

        if ( MainActivity.Get().isChangeBrightnessEnable() && pointsCount > 0 ) {
            if ( !nextPoint.equals(MainActivity.Get().getCurrentBrightnessPoint()) ) {
                MainActivity.Get().setCurrentBrightnessPoint();
                MainActivity.Get().changeBrightnessState(true);
            }
        } else {
            MainActivity.Get().changeBrightnessState(false);
        }
    }

}
