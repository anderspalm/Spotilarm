package com.anders.spotifyalarm.Receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.SingAndDB.DBhelper;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RestartAlarmsReceiver extends BroadcastReceiver {

    DBhelper mDBHelper;
    AlarmBroadcastReceiver mReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive: RestartAlarmsReceiver ");

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Log.i(TAG, "onReceive: RestartAlarmsReceiver = " + intent.getAction());

            // instantiating the singleton so that the service does not need a valid context
            mDBHelper = DBhelper.getmInstance(context);
            // It is better to reset alarms using Background IntentService
//            Intent i = new Intent(context, BootService.class);
//            context.startService(i);
            mDBHelper = DBhelper.getmInstance(context);
            mDBHelper.getAllAlarms();
            Log.i(TAG, "onHandleIntent: ");

            mReceiver = new AlarmBroadcastReceiver();
            ArrayList<ArrayList<AlarmObject>> arrayListArrayList = mDBHelper.getAllAlarms();
            for (int i = 0; i < arrayListArrayList.size(); i ++) {
                for (int j = 0; j < arrayListArrayList.get(i).size(); j ++) {
                    mReceiver.setAlarm(context, arrayListArrayList.get(i).get(j).getmDay(),
                            arrayListArrayList.get(i).get(j).getmHour(),
                            arrayListArrayList.get(i).get(j).getmMinute(),
                            arrayListArrayList.get(i).get(j).getmIndex(),
                            arrayListArrayList.get(i).get(j).getmAlarmId(),
                            arrayListArrayList.get(i).get(j).getmSnooze(),
                            arrayListArrayList.get(i).get(j).getmMessage());
                    Log.i(TAG, "onReceive: day " + arrayListArrayList.get(i).get(j).getmDay() + " " +
                            "hour " + arrayListArrayList.get(i).get(j).getmHour() + " " +
                            "minute " + arrayListArrayList.get(i).get(j).getmMinute());
                }
            }
        }
    }
}