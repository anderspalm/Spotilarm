package com.anders.spotifyalarm;

import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class NewAlarmActivity extends AppCompatActivity implements MediaFragment.OnFragmentInteractionListener {

    private static final String TAG = "NewAlarmActivity";
    Context mContext;
    Button mMon, mTues, mWed, mThu, mFri, mSat, mSun;
    AlarmBroadcastReceiver mReceiver;
    TimePicker mTimePicker;
    Integer mHour, mMinute;
    ArrayList<Integer> mDaysForAlarmArray;
    DBhelper mDatabaseHelper;
    Integer mMonBool, mTuesBool, mWedBool, mThurBool, mFriBool, mSatBool, mSunBool;
    Button mSetAlarmButton1;
    Integer mSnoozeTimeInt;
    TextView mSeekbarTime, mTextview, mSeekMinutes;
    SeekBar mSnoozeSeekBar;
    String mMessage, mToken;
    EditText mEditText;
    AlarmObject mAlarmObject;
    Boolean mSaved;
    int mCurrent_Alarm_id;
    Fragment mFragment;
    FrameLayout mFrameLayout, mSetTheTime;
    MasterSingleton mSingleton;
    Button mAcceptTheTime, mBackButtonNoSave;
    LinearLayout mSetTimeButton, mSong;
    TextView mSetMessage;

    Integer mBundleAlarmId, mBundleSnooze;
    ArrayList<AlarmObject> mBundleArray;
    ArrayList<SongObject> songArray;
    String mBundleMessage;
    InputMethodManager mINMM;

    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        mINMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mContext = NewAlarmActivity.this;
        mSingleton = MasterSingleton.getmInstance();
        songArray = new ArrayList<>();
        mSaved = false;

        Intent intent = getIntent();
        mToken = intent.getStringExtra(EXTRA_TOKEN);

        mSetMessage = (TextView) findViewById(R.id.set_message);
        mSetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mINMM.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
                mMessage = mEditText.getText().toString();
            }
        });


        mBackButtonNoSave = (Button) findViewById(R.id.back_button);
        mBackButtonNoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainActivity = new Intent(mContext, MainActivity.class);
                startActivity(toMainActivity);
            }
        });


        mSetAlarmButton1 = (Button) findViewById(R.id.set_alarm_1);
        mSetAlarmButton1.setVisibility(View.VISIBLE);

        mSetTheTime = (FrameLayout) findViewById(R.id.time_frame_layout);
        mAcceptTheTime = (Button) findViewById(R.id.accept);
        mSetTimeButton = (LinearLayout) findViewById(R.id.set_time_button);

        mSetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSetTheTime.getVisibility() == View.GONE) {
                    mSetTheTime.setVisibility(View.VISIBLE);
                    mSetAlarmButton1.setVisibility(View.GONE);
                } else {
                    mSetTheTime.setVisibility(View.GONE);
                    mSetAlarmButton1.setVisibility(View.VISIBLE);
                }
            }
        });

        mAcceptTheTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSetTheTime.getVisibility() == View.GONE) {
                    mSetTheTime.setVisibility(View.VISIBLE);
                    mSetAlarmButton1.setVisibility(View.GONE);
                } else {
                    mSetTheTime.setVisibility(View.GONE);
                    mSetAlarmButton1.setVisibility(View.VISIBLE);
                }
            }
        });


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

            for (int j = 0; j < mBundleArray.size(); j++) {

                switch (mBundleArray.get(j).getmDay()) {
                    case 2:
                        mMonBool = 1;
                        mDaysForAlarmArray.add(2);
                        mMon.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                    case 3:
                        mTuesBool = 1;
                        mDaysForAlarmArray.add(3);
                        mTues.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                    case 4:
                        mWedBool = 1;
                        mDaysForAlarmArray.add(4);
                        mWed.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                    case 5:
                        mThurBool = 1;
                        mDaysForAlarmArray.add(5);
                        mThu.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                    case 6:
                        mFriBool = 1;
                        mDaysForAlarmArray.add(6);
                        mFri.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                    case 7:
                        mSatBool = 1;
                        mDaysForAlarmArray.add(7);
                        mSat.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                    case 1:
                        mSunBool = 1;
                        mDaysForAlarmArray.add(1);
                        mSun.setTextColor(Color.parseColor("#FF9EBF"));
                        break;
                }
            }
        }

        mSong = (LinearLayout) findViewById(R.id.chooseSong);


        mSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBundleArray != null) {
                    mCurrent_Alarm_id = mBundleArray.get(0).getmAlarmId();
                } else {
                    mCurrent_Alarm_id = mDatabaseHelper.getAllAlarms().size() + 1;
                }
                Log.i(TAG, "onClick: ");

                Bundle bundle = new Bundle();
                bundle.putInt("alarm_id", mCurrent_Alarm_id);
                mFrameLayout = (FrameLayout) findViewById(R.id.media_fragment);
                mFragment = MediaFragment.newInstance(bundle, mFrameLayout, mToken, mCurrent_Alarm_id);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.media_fragment, mFragment)
                        .addToBackStack("media_fragment").commit();


            }
        });

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
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                Log.i(TAG, "onTimeChanged: hour: " + hour);
                Log.i(TAG, "onTimeChanged: minute: " + minute);
                mHour = hour;
                mMinute = minute;
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
                        mMessage = "Your Alarm";
                    } else {
                    }
                    if (mSnoozeTimeInt == null) {
                        mSnoozeTimeInt = 60000;
                    } else {
                    }

                    if (mBundleArray != null && (mBundleArray.get(0).getmDay() > 0)) {
                        int id = mBundleArray.get(0).getmAlarmId();
                        if (mSingleton.getSongObjArray().size() > 0) {
                            songArray = mSingleton.getSongObjArray();
                        } else {
                            songArray = mDatabaseHelper.getSongs(id);
                        }

//                        remove and delete previous alarms
                        mDatabaseHelper.removeAndCancelAlarm(id, mContext);


                        mCurrent_Alarm_id = mBundleArray.get(0).getmAlarmId();

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
                            mDatabaseHelper.insertAlarms(mAlarmObject, songArray, mCurrent_Alarm_id);

//                              get unique id of alarm for use later (input: alarm_id && day_num)
                            int unique_code = mDatabaseHelper.getAlarmItemId(mCurrent_Alarm_id, mDaysForAlarmArray.get(i));
                            Log.i(TAG, "onClick: " + mHour + "," + mMinute + "," + mDaysForAlarmArray.size() + "," +
                                    mSaved + "," + unique_code + "," + mCurrent_Alarm_id);

                            if (unique_code != 300000) {
                                if (mReceiver != null && mDaysForAlarmArray.get(i) != null) {
                                    mReceiver.setAlarm(mContext, mDaysForAlarmArray.get(i), mHour, mMinute, unique_code, mCurrent_Alarm_id, mSnoozeTimeInt, mMessage);
                                }
                            }
                        }
                    } else {
                        mCurrent_Alarm_id = mDatabaseHelper.getAllAlarms().size() + 1;
                        songArray = mSingleton.getSongObjArray();

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
                            mDatabaseHelper.insertAlarms(mAlarmObject, songArray, mCurrent_Alarm_id);

//                              get unique id of alarm for use later (input: alarm_id && day_num)
                            int unique_code = mDatabaseHelper.getAlarmItemId(mCurrent_Alarm_id, mDaysForAlarmArray.get(i));
                            Log.i(TAG, "onClick: " + mHour + "," + mMinute + "," + mDaysForAlarmArray.size() + "," +
                                    mSaved + "," + unique_code + "," + mCurrent_Alarm_id);

                            if (unique_code != 300000) {
                                if (mReceiver != null && mDaysForAlarmArray.get(i) != null) {
                                    mReceiver.setAlarm(mContext, mDaysForAlarmArray.get(i), mHour, mMinute, unique_code, mCurrent_Alarm_id, mSnoozeTimeInt, mMessage);
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
                    }


                    Intent intent = new Intent(mContext, MainActivity.class);

                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.left_slide_in,R.anim.left_slide_out);
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
                    mMon.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mMonBool = 1;
                    mMon.setTextColor(Color.parseColor("#FF9EBF"));
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
                    mTues.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mTuesBool = 1;
                    mTues.setTextColor(Color.parseColor("#FF9EBF"));
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
                    mWed.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mWedBool = 1;
                    mWed.setTextColor(Color.parseColor("#FF9EBF"));
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
                    mThu.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mThurBool = 1;
                    mThu.setTextColor(Color.parseColor("#FF9EBF"));
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
                    mFri.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mFriBool = 1;
                    mFri.setTextColor(Color.parseColor("#FF9EBF"));
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
                    mSat.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mSatBool = 1;
                    mSat.setTextColor(Color.parseColor("#FF9EBF"));
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
                    mSun.setTextColor(Color.parseColor("#00B64F"));
                } else {
                    mSunBool = 1;
                    mSun.setTextColor(Color.parseColor("#FF9EBF"));
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSetAlarmButton1.setVisibility(View.VISIBLE);
    }
}