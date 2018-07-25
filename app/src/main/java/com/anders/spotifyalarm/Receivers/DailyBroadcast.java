package com.anders.spotifyalarm.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MainActivity;
import com.anders.spotifyalarm.SingAndDB.DBhelper;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 2/10/2017.
 */

public class DailyBroadcast extends BroadcastReceiver {

    DBhelper mDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        DBhelper dBhelper = DBhelper.getmInstance(context);
        AlarmBroadcastReceiver mReceiver = new AlarmBroadcastReceiver();
        int dayofWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Log.i(TAG, "onReceive: time = " +  Calendar.getInstance().getTime());
        ArrayList<AlarmObject> array = dBhelper.findDailyAlarms(dayofWeek);

        mDB = DBhelper.getmInstance(context);
        String grad = mDB.getGradualWakeup();
        String num = grad.substring(3,4);
        int minPadding = Integer.parseInt(num);

        for (int i = 0 ; i < array.size(); i ++) {
            Log.i(TAG, "onReceive: grad.substring(0,3grad.substring(0,3grad.substring(0,3grad.substr" +
                    "ing(0,3grad.substring(0,3grad.substring(0,3grad.substring(0,3" + grad.substring(0,3));
            if (grad.substring(0,3).equals("oui")) {
                mReceiver.setAlarm(context,
                        array.get(i).getmDay(), array.get(i).getmHour(),
                        array.get(i).getmMinute(), array.get(i).getmIndex(),
                        array.get(i).getmAlarmId(), array.get(i).getmSnooze(),
                        array.get(i).getmMessage());
            } else {

                int minOriginal = array.get(i).getmMinute();
                int hour = array.get(i).getmHour();
                int day = array.get(i).getmDay();

                if (minOriginal < minPadding){
                    minOriginal = (minOriginal - minPadding) + 60;
                    if (hour == 0){
                        hour = 23;
                        if (day == 1) {
                            day = 7;
                        } else {
                            day = day - 1;
                        }
                    } else {
                        hour = hour - 1;
                    }
                } else if(minOriginal > (60 - minPadding)){
                    minOriginal = (minOriginal + minPadding) - 60;
                    if (hour == 23 ){
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
                        day, hour, minOriginal, array.get(i).getmIndex(),
                        array.get(i).getmAlarmId(), array.get(i).getmSnooze(),
                        array.get(i).getmMessage());
            }
        }

    }

    public void setDailyAlarms(Context context){

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,00);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 100000, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , sender);

    }

}
