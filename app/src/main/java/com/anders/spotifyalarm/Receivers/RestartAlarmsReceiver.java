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


            String grad = mDBHelper.getGradualWakeup();
            String num = grad.substring(3, 4);
            int minPadding = Integer.parseInt(num);


            ArrayList<ArrayList<AlarmObject>> arrayListArrayList = mDBHelper.getAllAlarms();
            if (grad.substring(0, 3).equals("non")) {

                for (int i = 0; i < arrayListArrayList.size(); i++) {
                    for (int j = 0; j < arrayListArrayList.get(i).size(); j++) {
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

            } else {


                for (int i = 0; i < arrayListArrayList.size(); i++) {
                    for (int j = 0; j < arrayListArrayList.get(i).size(); j++) {

                        int minOriginal = arrayListArrayList.get(i).get(j).getmMinute();
                        int hour = arrayListArrayList.get(i).get(j).getmHour();
                        int day = arrayListArrayList.get(i).get(j).getmDay();

                        if (minOriginal < minPadding) {
                            minOriginal = (minOriginal - minPadding) + 60;
                            if (hour == 0) {
                                hour = 23;
                                if (day == 1) {
                                    day = 7;
                                } else {
                                    day = day - 1;
                                }
                            } else {
                                hour = hour - 1;
                            }
                        } else if (minOriginal > (60 - minPadding)) {
                            minOriginal = (minOriginal + minPadding) - 60;
                            if (hour == 23) {
                                hour = 1;
                                if (day == 7) {
                                    day = 1;
                                } else {
                                    day = day + 1;
                                }
                            } else {
                                hour = hour + 1;
                            }
                        } else {
                            minOriginal = minOriginal - minPadding;
                        }

                        mReceiver.setAlarm(context,
                                day,
                                hour,
                                minOriginal,
                                arrayListArrayList.get(i).get(j).getmIndex(),
                                arrayListArrayList.get(i).get(j).getmAlarmId(),
                                arrayListArrayList.get(i).get(j).getmSnooze(),
                                arrayListArrayList.get(i).get(j).getmMessage());
                        Log.i(TAG, "onReceive: day " + day+ " " +
                                "hour " + arrayListArrayList.get(i).get(j).getmHour() + " " +
                                "minute " + arrayListArrayList.get(i).get(j).getmMinute());
                    }
                }
            }
        }
    }
}