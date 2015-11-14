package com.i112358.savemyeyes.Shake;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.i112358.savemyeyes.R;
import com.i112358.savemyeyes.Utilites;

public class ShakeServiceSettingsActivity extends Activity {

    private enum EditField {
        e_none,
        e_timeStart,
        e_timeEnd
    }

    private EditField m_currentEditField = EditField.e_none;
    private int[] m_newValue = {0,0};
    private int[] m_startValues = ShakeServiceSettings.getStartTime();
    private int[] m_endValues = ShakeServiceSettings.getEndTime();
    private int m_triggerStrength = ShakeServiceSettings.getTriggerStrength();
    private int m_averageStrength = ShakeServiceSettings.getAverageStrength();
    private boolean m_enableDebugMessages = ShakeServiceSettings.getIsDebugMessages();

    private TextView m_startTime = null;
    private TextView m_endTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("info", "ShakeServiceSettingsActivity onCreate");

        setContentView(R.layout.shake_settings);

        m_startTime = (TextView)findViewById(R.id.text_sssa_start_from);
        m_startTime.setText("Start from: " + Utilites.convertTime(ShakeServiceSettings.getStartTime()));

        m_endTime = (TextView)findViewById(R.id.text_sssa_end_on);
        m_endTime.setText("End on: " + Utilites.convertTime(ShakeServiceSettings.getEndTime()));

        NumberPicker np = (NumberPicker)findViewById(R.id.sssaTriggerPicker);
        np.setMaxValue(27);
        np.setMinValue(10);
        np.setValue(m_triggerStrength);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                m_triggerStrength = newVal;
            }
        });

        NumberPicker np1 = (NumberPicker)findViewById(R.id.sssaAveragePicker);
        np1.setMaxValue(27);
        np1.setMinValue(10);
        np1.setValue(m_averageStrength);
        np1.setWrapSelectorWheel(false);
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                m_averageStrength = newVal;
            }
        });

        CheckBox debug = (CheckBox)findViewById(R.id.sssaDebugCheckBox);
        debug.setChecked(m_enableDebugMessages);
        debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_enableDebugMessages = isChecked;
            }
        });

        findViewById(R.id.sssa_option_start_from).setVisibility(View.GONE);
        findViewById(R.id.sssa_option_end_on).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w("info", "ShakeServiceSettingsActivity onResume");
    }

    public void onSSSASetTime( View view )
    {
        switch ( view.getId() ) {
            case R.id.button_shake_start_from:
                m_currentEditField = EditField.e_timeStart;
                break;
            case R.id.button_shake_end_on:
                m_currentEditField = EditField.e_timeEnd;
                break;
            default:
                m_currentEditField = EditField.e_none;
                return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.setup_point);

        final Button doneButton = (Button)(dialog.findViewById(R.id.buttonSetPointDone));
        doneButton.setClickable(false);
        doneButton.setEnabled(false);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_currentEditField == EditField.e_timeStart) {
                    m_startValues = m_newValue;
                } else if (m_currentEditField == EditField.e_timeEnd) {
                    m_endValues = m_newValue;
                }
                dialog.dismiss();
            }
        });

        TimePicker timePicker = (TimePicker)(dialog.findViewById(R.id.timePicker));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                m_newValue[0] = hourOfDay;
                m_newValue[1] = minute;
                doneButton.setClickable(true);
                doneButton.setEnabled(true);
            }
        });

        (dialog.findViewById(R.id.textSetPointBrightness)).setVisibility(View.GONE);
        (dialog.findViewById(R.id.seekBar)).setVisibility(View.GONE);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if ( doneButton.isEnabled() )
                    updateScreen();
                m_currentEditField = EditField.e_none;
            }
        });

        dialog.show();
    }

    private void updateScreen()
    {
        m_startTime.setText("Start from: " + Utilites.convertTime(m_startValues));
        m_endTime.setText("End on: " + Utilites.convertTime(m_endValues));
        onSSSASaveValuesClick(null);
    }

    public void onSSSASaveValuesClick( View view )
    {
        ShakeServiceSettings.setDebugMessages(m_enableDebugMessages);
        ShakeServiceSettings.setTriggerStrength(m_triggerStrength);
        ShakeServiceSettings.setAverageStrength(m_averageStrength);
        ShakeServiceSettings.setStartTime(m_startValues);
        ShakeServiceSettings.setEndTime(m_endValues);
        ShakeServiceSettings.saveSettings(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }
}
