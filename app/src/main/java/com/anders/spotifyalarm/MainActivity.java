package com.anders.spotifyalarm;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.Receivers.DailyBroadcast;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.LandingRViewAdapter;
import com.anders.spotifyalarm.UiAids.TouchHelperCallback;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    DailyBroadcast mReceiver;
    public Player mPlayer;
    Context mContext;
    ArrayList<ArrayList<AlarmObject>> mGroupedAlarms;
    DBhelper mDBHelper;
    LinearLayout mAddAlarm;
    MasterSingleton mSingleton;
    LandingRViewAdapter mAdapter;
    Animation mAnimator;
    RequestQueue queue;
    private static final int REQUEST_CODE = 1337;
    SpotifyService mSpotify;
    public static final String REDIRECT_URL = "http://localhost:8888/callback";
    String mAccessToken;
    Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSingleton = MasterSingleton.getmInstance();
        mReceiver = new DailyBroadcast();
        mContext = MainActivity.this;

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        Log.i(TAG, "onCreate:  mSingleton.getmAuthToken() " + mSingleton.getmAuthToken());
        Log.i(TAG, "onCreate: mSingleton.getUserId() " + mSingleton.getUserId());

        if (!mSingleton.getUserId().equals("0") && mSingleton.getmAuthToken() == null) {
            Log.i(TAG, "onCreate: AuthenticationRequest.Builder builder");
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(
                            "8245c9c6491c426cbccf670997c14766",
                            AuthenticationResponse.Type.TOKEN,
                            REDIRECT_URL);


            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }

        if (mGroupedAlarms == null) {
            mGroupedAlarms = new ArrayList<>();
        } else {
        }

        mDBHelper = DBhelper.getmInstance(mContext);
        mGroupedAlarms = mDBHelper.getAllAlarms();

        setInitializers();
        setDailyAlarms();

//        set recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.current_alarms_recycler);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayout);
        mAdapter = new LandingRViewAdapter(mGroupedAlarms, mContext);
        recyclerView.setAdapter(mAdapter);

//        set touch helper
        ItemTouchHelper.Callback callback = new TouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

/*        set animation and click listener
              for new alarm  */

        if (mHandler == null) {
            mHandler = new Handler();
        } else {
        }
        mAnimator = AnimationUtils.loadAnimation(mContext, R.anim.button_anim);
        mAddAlarm.setAnimation(mAnimator);
        mAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!mSingleton.getUserId().equals("0")) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAddAlarm.startAnimation(mAnimator);
                                }
                            });

                            try {
                                sleep(350);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(mContext, NewAlarmActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
                        }
                    };
                    thread.run();
//                } else {
//                    Toast.makeText(mContext, "Please wait for it to load", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "onCreate: AuthenticationRequest.Builder builder");
//                    AuthenticationRequest.Builder builder =
//                            new AuthenticationRequest.Builder(
//                                    "8245c9c6491c426cbccf670997c14766",
//                                    AuthenticationResponse.Type.TOKEN,
//                                    REDIRECT_URL);
//
//
//                    builder.setScopes(new String[]{"user-read-private", "streaming"});
//                    AuthenticationRequest request = builder.build();
//
//                    AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
//                }
            }
        });
    }


    //    set initializers
    public void setInitializers() {
        mAddAlarm = (LinearLayout) findViewById(R.id.alarm_add);
    }


    public void setDailyAlarms() {
        mReceiver.setDailyAlarms(mContext);
    }


    // set lifecycle methods
    @Override
    protected void onResume() {
        super.onResume();

        if (mDBHelper == null) {
            mDBHelper = DBhelper.getmInstance(mContext);
        }

        mGroupedAlarms = mDBHelper.getAllAlarms();
        if (mAdapter == null) {
            mAdapter = new LandingRViewAdapter(mGroupedAlarms, MainActivity.this);
        } else {
            mAdapter.update(mGroupedAlarms);
        }

    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                mSingleton.setmAuthToken(response.getAccessToken());
                Log.i(TAG, "onActivityResult: " + response.getAccessToken());

                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(response.getAccessToken());

                Log.i(TAG, "onActivityResult: access token" + response.getAccessToken());
                mAccessToken = response.getAccessToken();
                mSingleton.setmAuthToken(mAccessToken);
                mSpotify = api.getService();
                queue = Volley.newRequestQueue(mContext);
                MyTask findPlaylists = new MyTask();
                findPlaylists.execute();
            }
        }
    }


    private class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String user = mSpotify.getMe().id;
            return user;
        }

        @Override
        protected void onPostExecute(String user_id) {
            super.onPostExecute(user_id);
            Log.i(TAG, "onPostExecute: user_id substring(0,1) " + user_id.substring(0, 1));
            Log.i(TAG, "onPostExecute: user_id = " + user_id);
            mSingleton.setUserId(user_id);
        }
    }


    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.i(TAG, "MainActivity: Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.i(TAG, "MainActivity: Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        Log.i(TAG, "onLoggedIn: Logged in");
//
//        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {

        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (queue != null) {
            queue.stop();
        }
    }
}

