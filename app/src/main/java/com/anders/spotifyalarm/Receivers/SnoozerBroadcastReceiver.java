package com.anders.spotifyalarm.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.anders.spotifyalarm.AlarmTrigger.AlertDialogActivity;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 1/31/2017.
 */

public class SnoozerBroadcastReceiver extends BroadcastReceiver {

    PowerManager.WakeLock mWakeLock;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
//        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Hello this is an alarm");
//        mWakeLock.acquire();

        Calendar calendar = Calendar.getInstance();

        Log.i(TAG, "SnoozerBroadcastReceiver: current time = " + calendar.getTime());

        Bundle bundle = intent.getExtras();
        int code = bundle.getInt("unique_code");
        int snooze = bundle.getInt("snooze");
        int message = bundle.getInt("message");
        int alarm_id = bundle.getInt("alarm_id");

//        Intent musicServiceIntent = new Intent(context, MusicService.class);
//        context.startService(musicServiceIntent);
        Intent dialogIntent = new Intent(mContext,AlertDialogActivity.class);
        dialogIntent.putExtra("unique_code",code);
        dialogIntent.putExtra("snooze",snooze);
        dialogIntent.putExtra("message",message);
        dialogIntent.putExtra("alarm_id",alarm_id);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);


        Toast.makeText(context, "You have snoozed, the time now is " + calendar.getTime(), Toast.LENGTH_SHORT).show();
//        mWakeLock.release();
    }
}
