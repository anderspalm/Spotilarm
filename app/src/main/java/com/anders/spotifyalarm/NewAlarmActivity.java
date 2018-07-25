package com.anders.spotifyalarm;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MediaSearch.MediaFragment;
import com.anders.spotifyalarm.Receivers.AlarmBroadcastReceiver;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;

import java.util.ArrayList;

public class NewAlarmActivity extends AppCompatActivity {

    private static final String TAG = "NewAlarmActivity";
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    Context mContext;

    ArrayList<SongObject> songArray;
    ArrayList<AlarmObject> mBundleArray;
    ArrayList<Integer> mDaysForAlarmArray;

    Boolean mSaved;
    int mCurrent_Alarm_id;
    Integer mSnoozeTimeInt;
    Integer mHour, mMinute;
    Integer mBundleAlarmId, mBundleSnooze;
    Integer mMonBool, mTuesBool, mWedBool, mThurBool, mFriBool, mSatBool, mSunBool;
    String mBundleMessage;
    String mMessage, mToken, mPlaylistTitle;

    Button mSetAlarmButton1;
    Button mAcceptTheTime, mBackButtonNoSave;
    Button mMon, mTues, mWed, mThu, mFri, mSat, mSun;
    EditText mEditText;
    FrameLayout mFrameLayout, mSetTheTime;
    FrameLayout mSetTimeButton;
    FrameLayout mSong;
    SeekBar mSnoozeSeekBar;
    TextView mSetMessage;
    TextView mCurrentPlaylist, mCurrentTime;
    TextView mSeekbarTime, mTextview, mSeekMinutes;
    TimePicker mTimePicker;
    Animation mAnimationTimeEnter, mAnimationTimeExit;

    AlarmObject mAlarmObject;

    InputMethodManager mINMM;
    MasterSingleton mSingleton;
    DBhelper mDatabaseHelper;
    Fragment mFragment;
    AlarmBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        mCurrentTime = (TextView) findViewById(R.id.current_time);

        mINMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mContext = NewAlarmActivity.this;
        mSingleton = MasterSingleton.getmInstance();
        songArray = new ArrayList<>();
        mSaved = false;

        Intent intent = getIntent();
        mToken = intent.getStringExtra(EXTRA_TOKEN);

        mBackButtonNoSave = (Button) findViewById(R.id.back_button);
        mBackButtonNoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainActivity = new Intent(mContext, MainActivity.class);
                startActivity(toMainActivity);

                mSingleton.setPlaylistTitle("");
                mSingleton.setSongObjectArray(new ArrayList<SongObject>());
            }
        });


        mSetAlarmButton1 = (Button) findViewById(R.id.set_alarm_1);
        mSetAlarmButton1.setVisibility(View.VISIBLE);
        mCurrentPlaylist = (TextView) findViewById(R.id.current_playlist);

        mMon = (Button) findViewById(R.id.monday);
        mTues = (Button) findViewById(R.id.tuesday);
        mWed = (Button) findViewById(R.id.wednesday);
        mThu = (Button) findViewById(R.id.thursday);
        mFri = (Button) findViewById(R.id.friday);
        mSat = (Button) findViewById(R.id.saturday);
        mSun = (Button) findViewById(R.id.sunday);

//        mBackArrow = (Button) findViewById(R.id.back_arrow);
        mReceiver = new AlarmBroadcastReceiver();
        mDaysForAlarmArray = new ArrayList<>();
        mDatabaseHelper = DBhelper.getmInstance(mContext);
        mAlarmObject = new AlarmObject();

        mMonBool = 0;
        mTuesBool = 0;
        mWedBool = 0;
        mThurBool = 0;
        mFriBool = 0;
        mSatBool = 0;
        mSunBool = 0;

        setListeners();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBundleAlarmId = bundle.getInt("alarm_id");
            mBundleArray = (ArrayList<AlarmObject>) bundle.getSerializable("array");
            mBundleSnooze = bundle.getInt("snooze");
            mBundleMessage = bundle.getString("message");


// ---------------------------------------------------
//         Setting playlist name
// ---------------------------------------------------
            ArrayList<SongObject> playlstTemp = mDatabaseHelper.getSongs(mBundleAlarmId);
            if (playlstTemp.size() > 0) {
                String temp = playlstTemp.get(0).getPlaylist();
                mCurrentPlaylist.setText(temp);
                mPlaylistTitle = temp;
                mSingleton.setPlaylistTitle(temp);
            }


// ---------------------------------------------------
//         Set Colors for saved dates, or not
// ---------------------------------------------------
            for (int j = 0; j < mBundleArray.size(); j++) {

                switch (mBundleArray.get(j).getmDay()) {
                    case 2:
                        mMonBool = 1;
                        mDaysForAlarmArray.add(2);
                        mMon.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                    case 3:
                        mTuesBool = 1;
                        mDaysForAlarmArray.add(3);
                        mTues.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                    case 4:
                        mWedBool = 1;
                        mDaysForAlarmArray.add(4);
                        mWed.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                    case 5:
                        mThurBool = 1;
                        mDaysForAlarmArray.add(5);
                        mThu.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                    case 6:
                        mFriBool = 1;
                        mDaysForAlarmArray.add(6);
                        mFri.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                    case 7:
                        mSatBool = 1;
                        mDaysForAlarmArray.add(7);
                        mSat.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                    case 1:
                        mSunBool = 1;
                        mDaysForAlarmArray.add(1);
                        mSun.setTextColor(Color.parseColor("#A8DADC"));
                        break;
                }
            }
        }



// ---------------------------------------------------
//         setting time pop up
// ---------------------------------------------------

        mSetTheTime = (FrameLayout) findViewById(R.id.time_frame_layout);
        mSetTimeButton = (FrameLayout) findViewById(R.id.choose_time);
        mAnimationTimeEnter = AnimationUtils.loadAnimation(mContext,R.anim.enter_from_bottom);
        mAnimationTimeExit = AnimationUtils.loadAnimation(mContext,R.anim.exit_to_bottom);

        mSetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (mSetTheTime.getVisibility() == View.GONE) {
                    mSetTheTime.setVisibility(View.VISIBLE);
                    mSetTheTime.startAnimation(mAnimationTimeEnter);
                    mSetAlarmButton1.setVisibility(View.GONE);
                } else {
                    mSetTheTime.setVisibility(View.GONE);
                     mSetTheTime.startAnimation(mAnimationTimeExit);
                    mSetAlarmButton1.setVisibility(View.VISIBLE);
                }
            }
        });

        Button timeBack = (Button) findViewById(R.id.time_back_button);
        timeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSetTheTime.getVisibility() == View.GONE) {
                    mSetTheTime.setVisibility(View.VISIBLE);
                    mSetTheTime.startAnimation(mAnimationTimeEnter);
                    mSetAlarmButton1.setVisibility(View.GONE);
                } else {
                    mSetTheTime.setVisibility(View.GONE);
                    mSetTheTime.startAnimation(mAnimationTimeExit);
                    mSetAlarmButton1.setVisibility(View.VISIBLE);
                }
            }
        });


// ---------------------------------------------------
//         setting media fragment
// ---------------------------------------------------
        mSong = (FrameLayout) findViewById(R.id.chooseSong);
        mFrameLayout = (FrameLayout) findViewById(R.id.media_fragment);
        mFragment = MediaFragment.newInstance(bundle, mFrameLayout, mToken, mCurrent_Alarm_id);
        mSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBundleArray != null) {
                    mCurrent_Alarm_id = mBundleArray.get(0).getmAlarmId();
                } else {
                    mCurrent_Alarm_id = mDatabaseHelper.getAllAlarms().size() + 1;
                }

                ArrayList<SongObject> mSongArray = new ArrayList<>();
                mSongArray = mDatabaseHelper.getSongs(mCurrent_Alarm_id);
                mSingleton.setSongObjectArray(mSongArray);

                Bundle bundle = new Bundle();
                bundle.putInt("alarm_id", mCurrent_Alarm_id);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.media_fragment, mFragment)
                        .addToBackStack("media_fragment").commit();
            }
        });

        if (mBundleArray != null) {
            mCurrentTime.setText(mBundleArray.get(0).getmHour() + ":" + mBundleArray.get(0).getmMinute());
        } else {
            Log.i(TAG, "onCreate: this is a new alarm or the bundle is empty" );
        }

        mEditText = (EditText) findViewById(R.id.message_et);
        mTextview = (TextView) findViewById(R.id.message_tv);
        if (mBundleMessage != null && mMessage == null) {
            mTextview.setText(mBundleMessage);
            mEditText.setText(mBundleMessage);
            mMessage = mBundleMessage;
        } else {
        }



        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mMessage = editable.toString();
                mTextview.setText(mMessage);
            }
        });

        mSnoozeSeekBar = (SeekBar) findViewById(R.id.snooze_seek_bar);
        mSeekbarTime = (TextView) findViewById(R.id.seek_bar_time);
        if (mSnoozeTimeInt == null && mBundleSnooze != null) {
            int num = (mBundleSnooze / 60000);
            Log.i(TAG, "mBundleSnooze: " + num);
            mSnoozeSeekBar.setProgress(num);
            mSeekbarTime.setText(String.valueOf(num));
            mSnoozeTimeInt = mBundleSnooze;
        } else {
            mSnoozeTimeInt = 5;
            mSnoozeSeekBar.setProgress(mSnoozeTimeInt / 5);
        }

        mSeekMinutes = (TextView) findViewById(R.id.minutes);
        mSnoozeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSeekbarTime.setText(String.valueOf(i));
                mSnoozeTimeInt = i * 60000;
                if (i == 1) {
                    mSeekMinutes.setText(R.string.minute);
                } else {
                    mSeekMinutes.setText(R.string.minutes);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mTimePicker = (TimePicker) findViewById(R.id.time_picker);
        mTimePicker.setIs24HourView(true);
        if (mBundleArray != null && mBundleArray.get(0).getmHour() != null && mHour == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mTimePicker.setCurrentHour(mBundleArray.get(0).getmHour());
                mTimePicker.setCurrentMinute(mBundleArray.get(0).getmMinute());
                mHour = mBundleArray.get(0).getmHour();
                mMinute = mBundleArray.get(0).getmMinute();
                Log.i(TAG, "onTimeChanged: hour: " + mHour);
                Log.i(TAG, "onTimeChanged: minute: " + mMinute);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mTimePicker.setHour(mBundleArray.get(0).getmHour());
                    mTimePicker.setMinute(mBundleArray.get(0).getmMinute());
                } else {
                    mTimePicker.setCurrentHour(mBundleArray.get(0).getmHour());
                    mTimePicker.setCurrentMinute(mBundleArray.get(0).getmMinute());
                }
                mHour = mBundleArray.get(0).getmHour();
                mMinute = mBundleArray.get(0).getmMinute();
                Log.i(TAG, "onTimeChanged: hour: " + mHour);
                Log.i(TAG, "onTimeChanged: minute: " + mMinute);
            }
        } else {
            mHour = 0;
            mMinute = 0;
        }

        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mINMM.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
            }
        });
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                Log.i(TAG, "onTimeChanged: hour: " + hour);
                Log.i(TAG, "onTimeChanged: minute: " + minute);
                mHour = hour;
                mMinute = minute;
                if (hour<12) {
                    if (minute < 10) {
                        mCurrentTime.setText(hour + ":0" + minute + "am");
                    } else {
                        mCurrentTime.setText(hour + ":" + minute + "am");
                    }
                } else {
                    if (minute < 10) {
                        mCurrentTime.setText(hour + ":0" + minute + "pm");
                    } else {
                        mCurrentTime.setText(hour + ":" + minute + "pm");
                    }
                }
            }
        });


    }


    public void setListeners() {

        mSetAlarmButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "mSetAlarmButton: one ");
                Log.i(TAG, "onClick: ");
                Log.i(TAG, "onClick: " + mHour + "," + mMinute + "," + mDaysForAlarmArray.size() + "," + mSaved);
                if (mHour != null && mMinute != null && !mSaved && (mHour != 0 || mMinute != 0)) {
                    mSaved = true;


                    if (mMessage == null || mMessage.equals("null")) {
                        mMessage = "";
                    } else {
                    }
                    if (mSnoozeTimeInt == null) {
                        mSnoozeTimeInt = 60000;
                    } else {
                    }

//                    check for the gradual wakeup boolean
                    String grad = mDatabaseHelper.getGradualWakeup();
                    String num = grad.substring(3, 4);
                    int minPadding = Integer.parseInt(num);

                    if (mBundleArray != null && (mBundleArray.get(0).getmDay() > 0)) {

                        mCurrent_Alarm_id = mBundleArray.get(0).getmAlarmId();

//                    check for shuffle boolean
                        int shuffle = mDatabaseHelper.getShuffle(mCurrent_Alarm_id);

                        if (mSingleton.getSongObjArray().size() > 0) {
                            songArray = mSingleton.getSongObjArray();
                        } else {
                            songArray = mDatabaseHelper.getSongs(mCurrent_Alarm_id);
                        }

//                        remove and delete previous alarms
                        mDatabaseHelper.removeAndCancelAlarm(mCurrent_Alarm_id, mContext);


//                          delete old songs
                        mDatabaseHelper.deleteSongsById(mBundleAlarmId);

//                          insert new songs
                        mDatabaseHelper.insertSongs(mCurrent_Alarm_id,songArray);

                        for (int i = 0; i < mDaysForAlarmArray.size(); i++) {

//                              set object
                            mAlarmObject.setmAlarmId(mCurrent_Alarm_id);
                            mAlarmObject.setmSnooze(mSnoozeTimeInt);
                            mAlarmObject.setmMessage(mMessage);
                            mAlarmObject.setmDay(mDaysForAlarmArray.get(i));
                            mAlarmObject.setmHour(mHour);
                            mAlarmObject.setmMinute(mMinute);

//                              set new alarms,
//                              add new songs
                            mDatabaseHelper.insertAlarms(mAlarmObject, songArray, mCurrent_Alarm_id, shuffle);

//                              get unique id of alarm for use later (input: alarm_id && day_num)
                            int unique_code = mDatabaseHelper.getAlarmItemId(mCurrent_Alarm_id, mDaysForAlarmArray.get(i));
                            Log.i(TAG, "onClick: " + mHour + "," + mMinute + "," + mDaysForAlarmArray.size() + "," +
                                    mSaved + "," + unique_code + "," + mCurrent_Alarm_id);


                            Log.i(TAG, "-------------------------------------");
                            Log.i(TAG, "-----------------" + grad.substring(0,3));
                            Log.i(TAG, "-------------------------------------");

                            if (unique_code != 300000) {
                                if (mReceiver != null && mDaysForAlarmArray.get(i) != null) {

                                    if (grad.substring(0,3).equals("oui")) {

                                        int minOriginal = mMinute;
                                        int hour = mHour;
                                        int day = mDaysForAlarmArray.get(i);

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
//                                        mDatabaseHelper.resetAlarmsForGradualWake(mContext);
                                        mReceiver.setAlarm(mContext, day, hour, minOriginal, unique_code, mCurrent_Alarm_id, mSnoozeTimeInt, mMessage);
                                    } else {
                                        mReceiver.setAlarm(mContext, mDaysForAlarmArray.get(i), mHour, mMinute, unique_code, mCurrent_Alarm_id, mSnoozeTimeInt, mMessage);
                                    }
                                }
                            }
                        }
                        mSingleton.clearSongs();
                        mSingleton.setPlaylistTitle("");

                    } else {
                        mCurrent_Alarm_id = mDatabaseHelper.getAllAlarms().size() + 1;
                        songArray = mSingleton.getSongObjArray();


//                    check for shuffle boolean
                        int shuffle = mDatabaseHelper.getShuffle(mCurrent_Alarm_id);

//                          insert new songs
                        mDatabaseHelper.insertSongs(mCurrent_Alarm_id, songArray);

                        for (int i = 0; i < mDaysForAlarmArray.size(); i++) {

//                              set object
                            mAlarmObject.setmAlarmId(mCurrent_Alarm_id);
                            mAlarmObject.setmSnooze(mSnoozeTimeInt);
                            mAlarmObject.setmMessage(mMessage);
                            mAlarmObject.setmDay(mDaysForAlarmArray.get(i));
                            mAlarmObject.setmHour(mHour);
                            mAlarmObject.setmMinute(mMinute);

//                              insert new alarms
                            mDatabaseHelper.insertAlarms(mAlarmObject, songArray, mCurrent_Alarm_id, shuffle);

//                              get unique id of alarm for use later (input: alarm_id && day_num)
                            int unique_code = mDatabaseHelper.getAlarmItemId(mCurrent_Alarm_id, mDaysForAlarmArray.get(i));
                            Log.i(TAG, "onClick: " + mHour + "," + mMinute + "," + mDaysForAlarmArray.size() + "," +
                                    mSaved + "," + unique_code + "," + mCurrent_Alarm_id);

                            Log.i(TAG, "onReceive: grad.substring(0,3grad.substring(0,3grad.substring(0,3grad.substr" +
                                    "ing(0,3grad.substring(0,3grad.substring(0,3grad.substring(0,3" + grad.substring(0,3));

                            if (unique_code != 300000) {
                                if (mReceiver != null && mDaysForAlarmArray.get(i) != null) {
                                    if (grad.substring(0,3).equals("oui")) {

                                        int minOriginal = mMinute;
                                        int hour = mHour;
                                        int day = mDaysForAlarmArray.get(i);

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
                                        mReceiver.setAlarm(mContext, day, hour, minOriginal, unique_code, mCurrent_Alarm_id, mSnoozeTimeInt, mMessage);
                                    } else {
                                        mReceiver.setAlarm(mContext, mDaysForAlarmArray.get(i), mHour, mMinute, unique_code, mCurrent_Alarm_id, mSnoozeTimeInt, mMessage);
                                    }
                                }
                            }
                        }
                    }


                    if (mSingleton.getSongObjArray().size() > 0) {
                        Log.i(TAG, "onClick: HistPlaylistsID" + mSingleton.getSongObjArray().get(0).getId());
                        if (mSingleton.getSongObjArray().get(0).getId() == 0) {
                            mDatabaseHelper.insertHistPlaylists(mSingleton.getSongObjArray());
                        } else {
                            mDatabaseHelper.updateHistPlaylist(mSingleton.getSongObjArray());
                        }
                        mSingleton.clearSongs();
                        mSingleton.setPlaylistTitle("");
                    }


                    Intent intent = new Intent(mContext, MainActivity.class);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
                    finish();
                    startActivity(intent);
                }
            }
        });


//      Day of week listeners

        mMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Mon");
                if (mMonBool == 1) {
                    mMonBool = 0;
                    removeInt(2);
                    mMon.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mMonBool = 1;
                    mMon.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(2);
                }
            }
        });

        mTues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Tues");
                if (mTuesBool == 1) {
                    mTuesBool = 0;
                    removeInt(3);
                    mTues.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mTuesBool = 1;
                    mTues.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(3);
                }
            }
        });

        mWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Wed");
                if (mWedBool == 1) {
                    mWedBool = 0;
                    removeInt(4);
                    mWed.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mWedBool = 1;
                    mWed.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(4);
                }
            }
        });

        mThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Thu");
                if (mThurBool == 1) {
                    mThurBool = 0;
                    removeInt(5);
                    mThu.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mThurBool = 1;
                    mThu.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(5);
                }
            }
        });

        mFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Fri");
                if (mFriBool == 1) {
                    mFriBool = 0;
                    removeInt(6);
                    mFri.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mFriBool = 1;
                    mFri.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(6);
                }
            }
        });

        mSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Sat");
                if (mSatBool == 1) {
                    mSatBool = 0;
                    removeInt(7);
                    mSat.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mSatBool = 1;
                    mSat.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(7);
                }
            }
        });

        mSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: day chosen = Sun");
                if (mSunBool == 1) {
                    mSunBool = 0;
                    removeInt(1);
                    mSun.setTextColor(Color.parseColor("#CBCBC9"));
                } else {
                    mSunBool = 1;
                    mSun.setTextColor(Color.parseColor("#A8DADC"));
                    mDaysForAlarmArray.add(1);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        if (mFrameLayout == null) {
            mFrameLayout = (FrameLayout) findViewById(R.id.song_fragment);
            if (mFrameLayout != null) {
                mFrameLayout.setVisibility(View.GONE);
                mSetAlarmButton1.setVisibility(View.VISIBLE);
            }
        } else {
            mFrameLayout.setVisibility(View.GONE);
            mSetAlarmButton1.setVisibility(View.VISIBLE);
        }
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    public void removeInt(int num) {
        for (int l = 0; l < mDaysForAlarmArray.size(); l++) {
            if (mDaysForAlarmArray.get(l) == num) {
                mDaysForAlarmArray.remove(l);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSetAlarmButton1.setVisibility(View.VISIBLE);
    }
}