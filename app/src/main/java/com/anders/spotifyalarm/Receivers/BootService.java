package com.anders.spotifyalarm.Receivers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.SingAndDB.DBhelper;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    DBhelper mDBHelper;
    AlarmBroadcastReceiver mReceiver;

    @Override
    protected void onHandleIntent(Intent intent) {

        mDBHelper = DBhelper.getmInstance(this);
        mDBHelper.getAllAlarms();
        Log.i(TAG, "onHandleIntent: ");

        ArrayList<ArrayList<AlarmObject>> arrayListArrayList = mDBHelper.getAllAlarms();
        for (int i = 0; i < arrayListArrayList.size(); i ++) {
            for (int j = 0; j < arrayListArrayList.get(i).size(); j ++) {
                mReceiver.setAlarm(this, arrayListArrayList.get(i).get(j).getmDay(),
                        arrayListArrayList.get(i).get(j).getmHour(),
                        arrayListArrayList.get(i).get(j).getmMinute(),
                        arrayListArrayList.get(i).get(j).getmIndex(),
                        arrayListArrayList.get(i).get(j).getmAlarmId(),
                        arrayListArrayList.get(i).get(j).getmSnooze(),
                        arrayListArrayList.get(i).get(j).getmMessage());
            }
        }
        // Your code to reset alarms.
        // All of these will be done in Background and will not affect
        // on phone's performance


    }
}