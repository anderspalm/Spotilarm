package com.anders.spotifyalarm.AlarmTrigger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anders.spotifyalarm.MainActivity;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.Receivers.SnoozerBroadcastReceiver;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

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
    Thread read;
    AudioManager mAudioManager;
    Vibrator mVibrator;
    PowerManager.WakeLock mWakeLock;
    PowerManager mPowerManager;


    Config playerConfig;

    public void turnOnScreen(){
        // turn on screen
        Log.v("ProximityActivity", "ON!");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
    }

    @TargetApi(21) //Suppress lint error for PROXIMITY_SCREEN_OFF_WAKE_LOCK
    public void turnOffScreen(){
        // turn off screen
        Log.v("ProximityActivity", "OFF!");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
        mWakeLock.acquire();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_dialog);
        mContext = AlertDialogActivity.this;
        mDatabaseHelper = DBhelper.getmInstance(mContext);

        mSingleton = MasterSingleton.getmInstance();
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        turnOnScreen();


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


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        if (mDatabaseHelper.getVibrate().equals("oui")) {
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            int dot = 200;      // Length of a Morse Code "dot" in milliseconds
            int dash = 500;     // Length of a Morse Code "dash" in milliseconds
            int short_gap = 200;    // Length of Gap Between dots/dashes
            int medium_gap = 500;   // Length of Gap Between Letters
            int long_gap = 1000;    // Length of Gap Between Words
            long[] pattern = {
                    0,  // Start immediately
                    dot, short_gap, dot, short_gap, dot,    // s
                    medium_gap,
                    dash, short_gap, dash, short_gap, dash, // o
                    medium_gap,
                    dot, short_gap, dot, short_gap, dot,    // s
                    long_gap
            };

            mVibrator.vibrate(pattern, 10);
        } else {
        }

        SimpleDateFormat time = new SimpleDateFormat("HH");
        String currentTime = time.format(cal.getTime());

        if (Integer.parseInt(currentTime) < 13) {
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
                long snoozeTime = timeNow + mSnoozetime;
                Log.i(TAG, "AlertDialogActivity: timeNow + mSnoozetime " + snoozeTime);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 100000, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);

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

                if (mAudioManager != null) {
//                set volume to original levels
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSingleton.getOriginalVolume(), 0);
                } else {
//                    gradual is turned off
                    Log.i(TAG, "onClick snooze: gradual is turned off");
                }

                if (mVibrator != null) {
                    mVibrator.cancel();
                } else {
                }

                mPlayer = null;
                turnOffScreen();
                onDestroy();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
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

                if (mVibrator != null) {
                    mVibrator.cancel();
                } else {
                }
                if (mAudioManager != null) {
//                set volume to original levels
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSingleton.getOriginalVolume(), 0);
                } else {
//                    gradual is turned off
                    Log.i(TAG, "onClick cancel: gradual is turned off");
                }


                mPlayer = null;
                turnOffScreen();
                Intent exitIntent = new Intent(AlertDialogActivity.this, MainActivity.class);
                startActivity(exitIntent);
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

                            Log.i(TAG, "onInitialized: -- Player initialized 1 -a-");
                            spotifyPlayer.addNotificationCallback(AlertDialogActivity.this);
                            spotifyPlayer.addConnectionStateCallback(AlertDialogActivity.this);
                            mCurrentPlaybackState = spotifyPlayer.getPlaybackState();

                            if (spotifyPlayer.isLoggedIn()) {
                                Log.i(TAG, "onInitialized: 3");

                                songList = mDatabaseHelper.getSongs(mAlarmId);

//                                shuffle songs if shuffle is on
                                if (mDatabaseHelper.isShuffle(mAlarmId) == 1) {
                                    int songIndex;
                                    for (int i = 0; i < songList.size(); i++) {
                                        Log.i(TAG, "onInitialized: song title shuffle " + songList.get(i).getTitle());
                                        songIndex = (int) Math.abs(Math.random() * songList.size());
                                        SongObject temp = songList.get(i);
                                        SongObject temp2 = songList.get(songIndex);
                                        songList.set(i, temp2);
                                        songList.set(songIndex, temp);
                                        Log.i(TAG, "onInitialized: song title shuffle " + songList.get(i).getTitle());
                                    }
                                } else {
                                }

                                mPlayer = spotifyPlayer;

                                if (spotifyPlayer.isLoggedIn() && read == null) {
                                    Log.i(TAG, "onInitialized: songList size a = " + songList.size());
                                    if (songList.size() > 0) {

//                                        set volume
//                                          this is not null if there a gradual wake up
                                        if (mDatabaseHelper.getGradualWakeup().substring(0, 3).equals("oui")) {
                                            final int time = (Integer.parseInt(mDatabaseHelper.getGradualWakeup().substring(3, 4))) * 60;
                                            mSingleton.setmTimeGrad(time);

                                            Thread gradual = new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
//                                                    save user original volume to allow other apps and functions to be undisturbed
                                                    mSingleton.setOriginalVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
//                                                    get max volume
                                                    double temp = Integer.parseInt(mDatabaseHelper.getGradualWakeup().substring(4, 6));
                                                    double maxVolume = ((temp / 100) * 15);

                                                    Log.i(TAG, "run: --------- maxVolume " + maxVolume);
//                                                    set initial volume to 0
                                                    int volume = 1;
//                                                    this next line is for persistence sake & getting time taken
                                                    int timeTaken = mSingleton.getmTimeTaken();
//                                                    start function --> increasing by triangular numbers for incremental change
                                                    while (volume < 15 && mPlayer != null) {
//
                                                        int triangle = ((timeTaken * (timeTaken + 1)) / 2);
                                                        double percent = 0f;
                                                        Log.i(TAG, "run: --------- time " + time);
                                                        Log.i(TAG, "run: --------- maxVolume " + maxVolume);

                                                        if (volume <= maxVolume) {
                                                            switch (mSingleton.getmTimeGrad()) {
                                                                case 60:
                                                                    percent = triangle / 3660;
                                                                    volume = (int) (percent * maxVolume);
                                                                    if (volume == 0) {
                                                                        volume = 1;
                                                                    } else {
                                                                    }
                                                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                                                                    break;

                                                                case 120:
                                                                    percent = triangle / 7260f;
                                                                    volume = (int) (percent * maxVolume);
                                                                    if (volume == 0) {
                                                                        volume = 1;
                                                                    } else {
                                                                    }
                                                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                                                                    break;

                                                                case 180:
                                                                    percent = triangle / 16290f;
                                                                    volume = (int) (percent * maxVolume);
                                                                    if (volume == 0) {
                                                                        volume = 1;
                                                                    } else {
                                                                    }
                                                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                                                                    break;

                                                                case 240:
                                                                    percent = triangle / 28920f;
                                                                    volume = (int) (percent * maxVolume);
                                                                    if (volume == 0) {
                                                                        volume = 1;
                                                                    } else {
                                                                    }
                                                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                                                                    break;

                                                                case 300:
                                                                    percent = triangle / 45150f;
                                                                    volume = (int) (percent * maxVolume);
                                                                    if (volume == 0) {
                                                                        volume = 1;
                                                                    } else {
                                                                    }
                                                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                                                                    break;
                                                            }
                                                        }

                                                        Log.i(TAG, "run: --------- percent * volume " + percent * maxVolume);
                                                        Log.i(TAG, "run: --------- percent " + percent);
                                                        Log.i(TAG, "run: --------- volume " + volume);

                                                        try {
//                                                        increase volume
                                                            timeTaken = (timeTaken + 1);
                                                            mSingleton.setmTimeTaken(timeTaken + 1);
                                                            Log.i(TAG, "run: --------- time taken " + timeTaken);
//                                                            sleep for a second
                                                            sleep(1000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            };
//                                            start the thread
                                            gradual.start();
//                                            float percent = 0.7f;
//                                            int seventyVolume = (int) (maxVolume * percent);
//                                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
                                        }

//                                        set music
                                        Log.i(TAG, "onInitialized: size of list a = " + songList.size());
                                        read = new Thread() {
                                            @Override
                                            public void run() {
                                                Log.i(TAG, "run: a");
                                                for (int k = 0; k < 20; k++) {
                                                    Log.i(TAG, "run: k = " + k);
                                                    if (songList.size() == 1) {
                                                        if (mPlayer != null) {
                                                            try {
                                                                if (songList.get(0).getDuration() > 0) {
                                                                    mPlayer.playUri(null, songList.get(0).getUri(), 0, 0);
                                                                    sleep(songList.get(0).getDuration() + 3000);
                                                                    Log.i(TAG, "run: song event duration index 1 ");
                                                                } else {
                                                                    mPlayer.playUri(null, songList.get(0).getUri(), 0, 0);
                                                                    Log.i(TAG, "run: song event break ");
                                                                    break;
                                                                }
                                                                if (mPlayer != null) {
                                                                    mPlayer.onAudioFlush();
                                                                } else {}
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    } else {
                                                        Log.i(TAG, "run: song event k != 0, k = " + k);
                                                        for (int j = 0; j < songList.size(); j++) {
                                                            if (j == 0) {
                                                                Log.i(TAG, "run: singing cylcle has started");
                                                            } else {
                                                            }
                                                            if (j != 0) {
                                                                if (mPlayer != null) {
                                                                    try {
                                                                        Log.i(TAG, "run: singing about to sleep");
                                                                        sleep(songList.get((j - 1)).getDuration() + 3000);
                                                                        if (mPlayer != null) {
                                                                            mPlayer.onAudioFlush();
                                                                        } else {}
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                }
                                                            }
                                                            Log.i(TAG, "run: k index " + k);
                                                            Log.i(TAG, "run: j index " + j);
                                                            Log.i(TAG, "run: song length " + songList.get(j).getDuration());
                                                            Log.i(TAG, "run: song title " + songList.get(j).getTitle());
                                                            Log.i(TAG, "run: singing about to start");
                                                            if (mPlayer != null) {
                                                                mPlayer.playUri(null, songList.get(j).getUri(), 0, 0);
                                                            }
//

                                                            if (j == (songList.size() - 1)) {
                                                                Log.i(TAG, "run: singing after");
                                                                try {
                                                                    Log.i(TAG, "run: singing about to sleep before cycle restarts");
                                                                    sleep(songList.get((j)).getDuration() + 3000);
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                    Log.i(TAG, "run: ====================================================== error sleeping ============================================================");
                                                                }
                                                            }
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
                                        if (currentRingtone == null) {
                                            currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                            //display Ringtone title
                                            Log.i(TAG, "onInitialized: " + defaultRingtone.getTitle(mContext) + " and " +
                                                    "Current Ringtone:" + currentRingtone.getTitle(mContext));
                                            currentRingtone.play();
                                        } else {
                                            currentRingtone.stop();
                                            currentRingtone = RingtoneManager.getRingtone(mContext, currentRintoneUri);
                                            currentRingtone.play();
                                        }
                                    }
                                } else {

                                }
                            } else {
//
                                Log.i(TAG, "onInitialized: else ");
                                Log.i(TAG, "onCreate:  mSingleton.getmAuthToken() " + mSingleton.getmAuthToken());
                                Log.i(TAG, "onCreate: mSingleton.getUserId() " + mSingleton.getUserId());

                                if (!mSingleton.getUserId().equals("0") && mSingleton.getmAuthToken() != null) {
                                    Log.i(TAG, "onCreate: AuthenticationRequest.Builder builder");
                                    AuthenticationRequest.Builder builder =
                                            new AuthenticationRequest.Builder(
                                                    "8245c9c6491c426cbccf670997c14766",
                                                    AuthenticationResponse.Type.TOKEN,
                                                    REDIRECT_URL);


                                    builder.setScopes(new String[]{"user-read-private", "streaming"});
                                    AuthenticationRequest request = builder.build();

                                    AuthenticationClient.openLoginActivity(AlertDialogActivity.this, REQUEST_CODE, request);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.i(TAG, "AlertDialogActivity: Could not initialize player: " + throwable.getMessage());
                        }
                    }
            );


            if (mPlayer != null) {
                mPlayer.initialize(playerConfig);
            } else {
            }
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

                mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {

                        Log.i(TAG, "onInitialized: -- Player initialized 1 -b-");
                        spotifyPlayer.addNotificationCallback(AlertDialogActivity.this);
                        spotifyPlayer.addConnectionStateCallback(AlertDialogActivity.this);
                        mCurrentPlaybackState = spotifyPlayer.getPlaybackState();

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
//        String currentDate = date.format(cal.getTime());
        String currentTime = time.format(cal.getTime());
        mTime.setText(currentTime);
//        Date localSDF = null;
//        try {
//            localSDF = new SimpleDateFormat("MMM dd yyyy hh:mma")
//                    .parse(currentDate.replaceAll("(?<=\\d)(?=\\D* \\d+ )\\p{L}+", ""));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
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
    }


    public String returnOrdinal(int i) {
        String th = getResources().getString(R.string.ordinal_abbreviation_th);
        String st = getResources().getString(R.string.ordinal_abbreviation_st);
        String nd = getResources().getString(R.string.ordinal_abbreviation_nd);
        String rd = getResources().getString(R.string.ordinal_abbreviation_rd);
        String[] sufixes = new String[]{th, st, nd, rd, th, th, th, th, th, th};
        int num = i;
        num = (num % 10);
        Log.i(TAG, "returnOrdinal: " + i + " " + sufixes[num]);
        return sufixes[num];
    }
}
