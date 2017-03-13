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
    @Override
    public void onReceive(Context context, Intent intent) {
        DBhelper dBhelper = DBhelper.getmInstance(context);
        AlarmBroadcastReceiver mReceiver = new AlarmBroadcastReceiver();
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Log.i(TAG, "onReceive: time = " +  Calendar.getInstance().getTime());
        ArrayList<AlarmObject> array = dBhelper.findDailyAlarms(day);
        for (int i = 0 ; i < array.size(); i ++) {
            mReceiver.setAlarm(context,
                    array.get(i).getmDay(), array.get(i).getmHour(),
                    array.get(i).getmMinute(), array.get(i).getmIndex(),
                    array.get(i).getmAlarmId(), array.get(i).getmSnooze(),
                    array.get(i).getmMessage());
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
