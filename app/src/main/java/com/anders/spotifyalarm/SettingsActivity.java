package com.anders.spotifyalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anders.spotifyalarm.SingAndDB.DBhelper;

public class SettingsActivity extends AppCompatActivity {

    TextView mSeekBarTime, mSeekMinutes, mSeekPercent;
    DBhelper mDBHelper;
    Context mContext;
    private static final String TAG = "SettingsActivity";
    String mVibrateResultRaw, mGradualSwitch, mFinalVibrate, mFinalGradualYesNo,
            mFinalGradualTime, mPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = SettingsActivity.this;

        Button button = (Button) findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        mDBHelper = DBhelper.getmInstance(SettingsActivity.this);
        mVibrateResultRaw = mDBHelper.getVibrate();
        mGradualSwitch = mDBHelper.getGradualWakeup();
        if (mGradualSwitch.equals("oui") || mGradualSwitch.equals("non")){
            mGradualSwitch += "1";
        } else {}

        Log.i(TAG, "mGradualSwitch: " + mGradualSwitch);
        SwitchCompat vibrateSwitch = (SwitchCompat) findViewById(R.id.switch_vibrate);
        SwitchCompat gradualSwitch = (SwitchCompat) findViewById(R.id.switch_gradual_alarm);

        mFinalGradualYesNo = mGradualSwitch.substring(0,3);
        mFinalGradualTime = mGradualSwitch.substring(3,4);

        Log.i(TAG, "mGradualSwitch: " + mFinalGradualTime);
        mFinalVibrate = mVibrateResultRaw;


        if (mFinalVibrate.equals("oui")){
            vibrateSwitch.setChecked(true);
        } else { //non
            vibrateSwitch.setChecked(false);
        }

        if (mFinalGradualYesNo.equals("oui")){
            gradualSwitch.setChecked(true);
        } else if(mFinalGradualYesNo.equals("non")){ //non
            gradualSwitch.setChecked(false);
        }

        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mFinalVibrate = "oui";
                } else {
                    mFinalVibrate = "non";
                }
            }
        });

        gradualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mFinalGradualYesNo = "oui";
                } else {
                    mFinalGradualYesNo = "non";
                }
            }
        });



//        SeekBar
        mSeekMinutes = (TextView) findViewById(R.id.minutes);
        mSeekBarTime = (TextView) findViewById(R.id.seek_bar_time);
        mSeekBarTime.setText(mFinalGradualTime);
        SeekBar gradualAlarmSeekBar = (SeekBar) findViewById(R.id.gradual_alarm_time_seek_bar);
        gradualAlarmSeekBar.setProgress(Integer.parseInt(mFinalGradualTime));
        gradualAlarmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSeekBarTime.setText(String.valueOf(i));
                if (i == 1) {
                    mSeekMinutes.setText(R.string.minute);
                } else {
                    mSeekMinutes.setText(R.string.minutes);
                }
                mFinalGradualTime = String.valueOf(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        if(mGradualSwitch.length() > 5) {
            mPercent = mGradualSwitch.substring(4, 6);
        } else {
//        set default percent
            mPercent = "50";
        }
        mSeekPercent = (TextView) findViewById(R.id.seek_bar_percent);
        mSeekPercent.setText(mPercent);
        SeekBar gradualPercentSeekBar = (SeekBar) findViewById(R.id.gradual_alarm_percent_seek_bar);
        gradualPercentSeekBar.setProgress(Integer.parseInt(mPercent));
        gradualPercentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSeekPercent.setText(String.valueOf(i));
                mPercent = String.valueOf(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        String finalGradualWake = mFinalGradualYesNo + mFinalGradualTime + mPercent;
        if (mFinalGradualYesNo.equals("oui")){
            mDBHelper.resetAlarmsForGradualWake(mContext);
        } else {}
        mDBHelper.setGradualwakeBoolean(finalGradualWake);
        mDBHelper.setVibrate(mFinalVibrate);
    }
}
