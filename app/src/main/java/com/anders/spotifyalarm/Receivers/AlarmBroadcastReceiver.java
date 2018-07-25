package com.anders.spotifyalarm.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.anders.spotifyalarm.AlarmTrigger.AlertDialogActivity;
import com.anders.spotifyalarm.SingAndDB.DBhelper;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 1/26/2017.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    PowerManager.WakeLock mWakeLock;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
//
//        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|ACQUIRE_CAUSES_WAKEUP, "DoNotDimScreen");
//        mWakeLock.acquire();
//        mWakeLock.release();
        Bundle bundle = intent.getExtras();
        int day = bundle.getInt("day");
        int minute = bundle.getInt("minute");
        int hour = bundle.getInt("hour");
        int code = bundle.getInt("unique_code");
        int alarm_id = bundle.getInt("alarm_id_primary_table");
        int snooze = bundle.getInt("snooze");
        String message = bundle.getString("message");

        Log.i(TAG, "onReceive: message " + message);
        Calendar calendar = Calendar.getInstance();

        Log.i(TAG, "onReceive: current time = " + calendar.getTime());
        Log.i(TAG, "onReceive: supposed time = " + hour + ":" + minute + " on " + day);

        Intent dialogIntent = new Intent(context, AlertDialogActivity.class);
        dialogIntent.putExtra("unique_code", code);
        dialogIntent.putExtra("snooze", snooze);
        dialogIntent.putExtra("message", message);
        dialogIntent.putExtra("alarm_id", alarm_id);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);

//        Toast.makeText(context, "This is an alarm wake up!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
//        mWakeLock.release();
    }


    public void setAlarm(Context context, int day, int hour, int minute, int identifyingCode, Integer alarm_id, int snooze, String message) {

        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        Log.i(TAG, "setAlarmsetAlarm:  | time set:" + day + "/" + hour + ":" + minute + "|");
        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.DAY_OF_WEEK, day);
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        Log.i(TAG, "setAlarm:  | time set:" + mCalendar);
        Log.i(TAG, "setAlarm:  | time set:" + mCalendar.get(Calendar.DAY_OF_WEEK));
        if (mCalendar.get(Calendar.DAY_OF_MONTH) >= (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7)){
            mCalendar.set(Calendar.DAY_OF_MONTH,(mCalendar.get(Calendar.DAY_OF_MONTH) - 7));
        }else{}
        Log.i(TAG, "setAlarm:  | time set:" + mCalendar.get(Calendar.DAY_OF_MONTH));
        Log.i(TAG, "setAlarm:  | time set:" + mCalendar.getTime());
        Log.i(TAG, "setAlarm:  | time set:" + mCalendar.getTime());
        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
        if (!(mCalendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())) {

//        start new instance of alarm manager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        set intent for pending intent
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

            Log.i(TAG, "setAlarm: message " + message);

//        add data to intent
            intent.putExtra("hour", hour);
            intent.putExtra("minute", minute);
            intent.putExtra("day", day);
            intent.putExtra("unique_code", identifyingCode);
            intent.putExtra("alarm_id_primary_table", alarm_id);
            intent.putExtra("snooze", snooze);
            intent.putExtra("message", message);

//        set pending intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, identifyingCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        add everything to the alarm manager and start the receiver wait operation
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    mCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent);
        }
    }

}
