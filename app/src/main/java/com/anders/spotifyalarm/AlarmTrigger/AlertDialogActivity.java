package com.anders.spotifyalarm.AlarmTrigger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anders.spotifyalarm.MainActivity;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.Receivers.SnoozerBroadcastReceiver;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static com.spotify.sdk.android.player.PlayerEvent.kSpPlaybackNotifyPlay;

/**
 * Created by anders on 1/31/2017.
 */

public class AlertDialogActivity extends Activity implements Player.NotificationCallback, ConnectionStateCallback {

    Context mContext;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    View mAlertDialogView;
    Button mOKBtn, mCancelBtn;
    int mSnoozetime;
    MediaPlayer mMediaPlayer;
    PowerManager.WakeLock mWakeLock;
    MasterSingleton mSingleton;
    private static final String TAG = "AlertDialogActivity";
    SpotifyPlayer mPlayer;
    DBhelper mDatabaseHelper;
    String mMessage;
    Integer mSnoozeTime, mRequestNum, mAlarmId, mIndex;
    TextView mTime, mDate, mMessageTV;
    Player.OperationCallback mOperationCallback;
    private PlaybackState mCurrentPlaybackState;
    private static final int REQUEST_CODE = 1337;
    public static final String REDIRECT_URL = "http://localhost:8888/callback";
    Boolean mTimeStop, mDone;
    ArrayList<SongObject> songList;
    Ringtone currentRingtone;
    Thread mThread;
    Integer mCurrentTrack;
    ImageView mBackground;


    Config playerConfig;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.alarm_dialog);
        mContext = AlertDialogActivity.this;
        mDatabaseHelper = DBhelper.getmInstance(mContext);

        mSingleton = MasterSingleton.getmInstance();
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP, "Hello this is an alarm");
        mWakeLock.acquire();
        Log.i(TAG, "AlertDialogActivity onCreate:  inside alert dialog");
        Bundle bundle = getIntent().getExtras();
        mRequestNum = bundle.getInt("unique_code");
        mSnoozetime = bundle.getInt("snooze");
        mMessage = bundle.getString("message");
        Log.i(TAG, "AlertDialogActivity, onCreate: message " + mMessage);
        mAlarmId = bundle.getInt("alarm_id");
        mTimeStop = false;
        mDone = false;


        mMessageTV = (TextView) findViewById(R.id.message_text);

        if (mMessage == null || mMessage.equals(" ")) {
            mMessage = "Time's Up";
        } else {
        }
        mMessageTV.setText(mMessage);

        mOKBtn = (Button) findViewById(R.id.snooze);
        mCancelBtn = (Button) findViewById(R.id.stop);
        mTime = (TextView) findViewById(R.id.time);
        mDate = (TextView) findViewById(R.id.date);


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthWeekDayFormat = new SimpleDateFormat("EE MMM dd");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        String day = dayFormat.format(cal.getTime());
        String monthWeekDay = monthWeekDayFormat.format(cal.getTime());
        String dateOrdinalVersion = returnOrdinal(Integer.parseInt(day));

        mBackground = (ImageView) findViewById(R.id.background);


        SimpleDateFormat time = new SimpleDateFormat("HH");
        String currentTime = time.format(cal.getTime());

        if (Integer.parseInt(currentTime) < 13){
            Picasso.with(mContext).load(R.drawable.morning).fit().into(mBackground);
        } else {
            Picasso.with(mContext).load(R.drawable.evening).fit().into(mBackground);
        }



        mDate.setText(monthWeekDay + dateOrdinalVersion);

        Thread clockThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!mTimeStop) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        clockThread.start();

        if (playerConfig == null) {
            setToken();
        }


        mOperationCallback = new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: OK!");
            }

            @Override
            public void onError(Error error) {
                Log.i(TAG, "onError: " + error);
            }
        };

        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDone = false;
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(mContext, SnoozerBroadcastReceiver.class);
                intent.putExtra("unique_code", mRequestNum);
                intent.putExtra("snooze", mSnoozetime);
                intent.putExtra("message", mMessage);
                intent.putExtra("alarm_id", mAlarmId);
                Log.i(TAG, "AlertDialogActivity onClick: snooze " + mSnoozetime);
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getDefault());
                long timeNow = cal.getTimeInMillis();
                long temp = timeNow + mSnoozetime;
                Log.i(TAG, "AlertDialogActivity: timeNow + mSnoozetime " + temp);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 100000, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, temp, pendingIntent);

                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }

                if (mPlayer != null) {
                    mPlayer.pause(mOperationCallback);
                }

                if (currentRingtone != null) {
                    currentRingtone.stop();
                }


//                mWakeLock.release();
                onDestroy();

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
//                Intent intent2 = new Intent(mContext, MainActivity.class);
//                startActivity(intent2);
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeStop = true;
                mDone = true;
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }

                if (mPlayer != null) {
                    mPlayer.pause(mOperationCallback);
                }

                if (currentRingtone != null) {
                    currentRingtone.stop();
                }

                onDestroy();

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
//                mWakeLock.release();
//                Intent intent = new Intent(mContext, MainActivity.class);
//                startActivity(intent);

            }
        });
    }

    public void setToken() {
        if (mSingleton.getmAuthToken() != null && !mSingleton.getmAuthToken().equals("")) {
            Log.i(TAG, "setToken: 1");
            playerConfig = new Config(this, mSingleton.getmAuthToken(), "8245c9c6491c426cbccf670997c14766");
            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                        @Override
                        public void onInitialized(SpotifyPlayer spotifyPlayer) {

                            Log.i(TAG, "onInitialized: 2");

                            Log.i(TAG, "onInitialized: -- Player initialized 1 --");
                            spotifyPlayer.addNotificationCallback(AlertDialogActivity.this);
                            spotifyPlayer.addConnectionStateCallback(AlertDialogActivity.this);
                            mCurrentPlaybackState = spotifyPlayer.getPlaybackState();

                            if (spotifyPlayer.isLoggedIn()) {
                                Log.i(TAG, "onInitialized: 3");

                                songList = mDatabaseHelper.getSongs(mAlarmId);
                                mPlayer = spotifyPlayer;

                                if (spotifyPlayer.isLoggedIn()) {
                                    Log.i(TAG, "onInitialized: songList size = " + songList.size());
                                    if (songList.size() > 0) {
                                        Log.i(TAG, "onInitialized: size of list = " + songList.size());
                                        Thread read = new Thread() {
                                            @Override
                                            public void run() {
                                                if (songList.size() == 1) {
                                                    if (mPlayer != null) {
                                                        mPlayer.playUri(null, songList.get(0).getUri(), 0, 0);
                                                    }
                                                } else {
                                                    for (int j = 0; j < songList.size(); j++) {
                                                        if (songList.get(j).getDuration() != 0) {
                                                            try {
                                                                if (j != 0) {
                                                                    Log.i(TAG, "run: songList.get(j-1).getDuration() " + songList.get(j - 1).getDuration() + songList.get(j - 1).getTitle());
                                                                    sleep(songList.get((j - 1)).getDuration() + 3000);
                                                                    Log.i(TAG, "run:                inside kSpPlaybackNotifyPlay audio flush index " + j + Calendar.getInstance().getTime());
                                                                    if (mPlayer != null) {
                                                                        mPlayer.onAudioFlush();
                                                                    } else {
                                                                    }
                                                                    sleep(3000);
                                                                }
                                                                Log.i(TAG, "run:                inside kSpPlaybackNotifyPlay before index " + j + Calendar.getInstance().getTime());
                                                                if (mPlayer != null) {
                                                                    mPlayer.playUri(mOperationCallback, songList.get(j).getUri(), 0, 0);
                                                                } else {
                                                                }
                                                                Log.i(TAG, "run:                title " + songList.get(j).getTitle());
                                                                Log.i(TAG, "run:                inside kSpPlaybackNotifyPlay after index " + j + Calendar.getInstance().getTime());
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                                Log.i(TAG, "run: ====================================================== error sleeping ============================================================");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        };
                                        read.start();
                                    } else {
//                                        mPlayer.playUri(null,"spotify:user:spotify:playlist:3InXOgDxJPeA05l6rQyDoe",0,0);
                                        Log.i(TAG, "onInitialized: else ----------------------------------------------------------------------------------");
                                        Ringtone defaultRingtone = RingtoneManager.getRingtone(mContext,
                                                Settings.System.DEFAULT_RINGTONE_URI);
                                        //fetch current Ringtone
                                        Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext
                                                .getApplicationContext(), RingtoneManager.TYPE_ALARM);
                                        if (currentRingtone ==  null) {
                                            currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                            //display Ringtone title
                                            Log.i(TAG, "onInitialized: " + defaultRingtone.getTitle(mContext) + " and " +
                                                    "Current Ringtone:" + currentRingtone.getTitle(mContext));
                                            //play current Ringtone
                                            currentRingtone.play();
                                        } else
                                        {
                                            currentRingtone.stop();
                                            currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                            currentRingtone.play();
                                        }
                                    }
                                } else {

                                }
                            } else {
//                                mPlayer.playUri(null,"spotify:user:spotify:playlist:3InXOgDxJPeA05l6rQyDoe",0,0);
                                Log.i(TAG, "onInitialized: else ----------------------------------------------------------------------------------");
                                Ringtone defaultRingtone = RingtoneManager.getRingtone(mContext,
                                        Settings.System.DEFAULT_RINGTONE_URI);
                                //fetch current Ringtone
                                Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext
                                        .getApplicationContext(), RingtoneManager.TYPE_ALARM);
                                if (currentRingtone ==  null) {
                                    currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                    //display Ringtone title
                                    Log.i(TAG, "onInitialized: " + defaultRingtone.getTitle(mContext) + " and " +
                                            "Current Ringtone:" + currentRingtone.getTitle(mContext));
                                    //play current Ringtone
                                    currentRingtone.play();
                                } else
                                {
                                    currentRingtone.stop();
                                    currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                    currentRingtone.play();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.i(TAG, "AlertDialogActivity: Could not initialize player: " + throwable.getMessage());
                        }
                    }

            );
        } else {

            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(
                            "8245c9c6491c426cbccf670997c14766",
                            AuthenticationResponse.Type.TOKEN,
                            REDIRECT_URL);


            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

            Log.i(TAG, "AlertDialogActivity onCreate: spotify was not set ");
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                mSingleton.setmAuthToken(response.getAccessToken());
                Log.i(TAG, "onActivityResult: " + response.getAccessToken());

                playerConfig = new Config(AlertDialogActivity.this, response.getAccessToken(), "8245c9c6491c426cbccf670997c14766");

                if (mPlayer.isShutdown()) {
                    mPlayer.initialize(playerConfig);
                } else {
                }
                mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {

                        Log.i(TAG, "onInitialized: 2");

                        Log.i(TAG, "onInitialized: -- Player initialized 1 --");
                        spotifyPlayer.addNotificationCallback(AlertDialogActivity.this);
                        spotifyPlayer.addConnectionStateCallback(AlertDialogActivity.this);
                        mCurrentPlaybackState = spotifyPlayer.getPlaybackState();

                        if (spotifyPlayer.isLoggedIn()) {
                            Log.i(TAG, "onInitialized: 3");

                            songList = mDatabaseHelper.getSongs(mAlarmId);
                            mPlayer = spotifyPlayer;

                            if (spotifyPlayer.isLoggedIn()) {
                                Log.i(TAG, "onInitialized: songList size = " + songList.size());
                                if (songList.size() > 0) {
                                    Log.i(TAG, "onInitialized: size of list = " + songList.size());
                                    Thread read = new Thread() {
                                        @Override
                                        public void run() {
                                            while (!interrupted()) {
                                                for (int j = 0; j < songList.size(); j++) {
                                                    try {
                                                        if (j != 0) {
                                                            Log.i(TAG, "run: songList.get(j-1).getDuration() " + songList.get(j - 1).getDuration() + songList.get(j - 1).getTitle());
                                                            sleep(songList.get((j - 1)).getDuration() + 3000);
                                                            Log.i(TAG, "run:                inside kSpPlaybackNotifyPlay audio flush index " + j + Calendar.getInstance().getTime());
                                                            if (mPlayer != null) {
                                                                mPlayer.onAudioFlush();
                                                            } else {
                                                            }
                                                            sleep(3000);
                                                        } else {
                                                        }
                                                        Log.i(TAG, "run:                inside kSpPlaybackNotifyPlay before index " + j + Calendar.getInstance().getTime());
                                                        if (mPlayer != null) {
                                                            Log.i(TAG, "run: player is alive, ie not shut down");
                                                            mPlayer.playUri(mOperationCallback, songList.get(j).getUri(), 0, 0);
                                                        } else {
                                                        }
                                                        Log.i(TAG, "run:                title " + songList.get(j).getTitle());
                                                        Log.i(TAG, "run:                inside kSpPlaybackNotifyPlay after index " + j + Calendar.getInstance().getTime());
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                        Log.i(TAG, "run: ====================================================== error sleeping ============================================================");
                                                    }
                                                }
                                            }
                                        }
                                    };
                                    read.start();
                                } else {
                                    Log.i(TAG, "onInitialized: else ----------------------------------------------------------------------------------");
                                    Ringtone defaultRingtone = RingtoneManager.getRingtone(mContext,
                                            Settings.System.DEFAULT_RINGTONE_URI);
                                    //fetch current Ringtone
                                    Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext
                                            .getApplicationContext(), RingtoneManager.TYPE_ALARM);
                                    if (!currentRingtone.isPlaying()) {
                                        currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                        //display Ringtone title
                                        Log.i(TAG, "onInitialized: " + defaultRingtone.getTitle(mContext) + " and " +
                                                "Current Ringtone:" + currentRingtone.getTitle(mContext));
                                        //play current Ringtone
                                        currentRingtone.play();
                                    }
                                }
                            } else {

                            }
                        } else {
//                            mPlayer.playUri(null,"spotify:user:spotify:playlist:3InXOgDxJPeA05l6rQyDoe",0,0);
//                            Log.i(TAG, "onInitialized: else ----------------------------------------------------------------------------------");
                            Ringtone defaultRingtone = RingtoneManager.getRingtone(mContext,
                                    Settings.System.DEFAULT_RINGTONE_URI);
                            //fetch current Ringtone
                            Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext
                                    .getApplicationContext(), RingtoneManager.TYPE_ALARM);
                            if (currentRingtone ==  null) {
                                currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                //display Ringtone title
                                Log.i(TAG, "onInitialized: " + defaultRingtone.getTitle(mContext) + " and " +
                                        "Current Ringtone:" + currentRingtone.getTitle(mContext));
                                //play current Ringtone
                                currentRingtone.play();
                            } else
                            {
                                currentRingtone.stop();
                                currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                currentRingtone.play();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.i(TAG, "AlertDialogActivity: Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.i(TAG, "AlertDialogActivity: Playback event received: " + playerEvent.name());
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.i(TAG, "AlertDialogActivity: Playback error received: " + error.name());

    }


    private int index;

    @Override
    public void onLoggedIn() {
        Log.i(TAG, "onLoggedIn: ");
        setToken();
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    private void updateTextView() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        String currentDate = date.format(cal.getTime());
        String currentTime = time.format(cal.getTime());
        mTime.setText(currentTime);
        Date simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat("MMM dd yyyy hh:mma")
                    .parse(currentDate.replaceAll("(?<=\\d)(?=\\D* \\d+ )\\p{L}+", ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            Log.i(TAG, "onStop: player != null");
            Spotify.destroyPlayer(mPlayer);
            if (mPlayer == null) {
                Log.i(TAG, "onStop: mPlayer is now = null");
            } else {
                Log.i(TAG, "onStop: failed to destroy the player");
                if (mPlayer.isShutdown()) {
                    Log.i(TAG, "onStop: player has shut down");
                }
            }
        }

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            Log.i(TAG, "onStop: player != null");
            Spotify.destroyPlayer(this);
            if (mPlayer == null) {
                Log.i(TAG, "onStop: mPlayer is now = null");
            } else {
                Log.i(TAG, "onStop: failed to destroy the player");
                if (mPlayer.isShutdown()) {
                    Log.i(TAG, "onStop: player has shut down");
                }
            }
        }

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }



    public String returnOrdinal(int i){
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 1:
                return "st";
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return sufixes[i % 10];

        }
    }
}
