package com.anders.spotifyalarm.UiAids;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.Receivers.AlarmBroadcastReceiver;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 1/29/2017.
 */

public class LandingRViewAdapter extends RecyclerView.Adapter<LandingRViewAdapter.LandingRViewViewHolder> implements SwipeInterface {

    ArrayList<ArrayList<AlarmObject>> mNestedArrayList;
    Context mContext;
    DBhelper mDBHelper;
    Animation mAnimator;
    MasterSingleton mSingleton;
    Activity mActivity;
    Paint mPaint;

    private static final int REQUEST_CODE = 1337;
    public static final String REDIRECT_URL = "http://localhost:8888/callback";

    public LandingRViewAdapter(ArrayList<ArrayList<AlarmObject>> arrayList, Context context) {
        mNestedArrayList = arrayList;
        mContext = context;
        mActivity = (Activity) context;
        mDBHelper = DBhelper.getmInstance(mContext);
        mSingleton = MasterSingleton.getmInstance();
        mAnimator = AnimationUtils.loadAnimation(mContext, R.anim.button_anim);
        mPaint = new Paint();
    }

    @Override
    public LandingRViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.alarm_landing_rv_item, parent, false);
        LandingRViewViewHolder LRVH = new LandingRViewViewHolder(root);
        return LRVH;
    }

    @Override
    public void onBindViewHolder(LandingRViewViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder mArrayList: " + mNestedArrayList.size());
        holder.mMon.setText("");
        holder.mTues.setText("");
        holder.mWed.setText("");
        holder.mThu.setText("");
        holder.mFri.setText("");
        holder.mSat.setText("");
        holder.mSun.setText("");
//        holder.mOuterContainer.setAnimation(mAnimator);

        int shuffle = mDBHelper.isShuffle(mNestedArrayList.get(position).get(0).getmAlarmId());

        Log.i(TAG, "onBindViewHolder: shuffleshuffleshuffleshuffleshuffleshuffleshuffleshuffleshuffleshuffleshuffleshuffle" + shuffle);
        if (shuffle == 0){
            Drawable grey = mContext.getResources().getDrawable(R.drawable.no_shuffle);
            holder.mShuffle.setImageDrawable(grey);
        } else {
            Drawable green = mContext.getResources().getDrawable(R.drawable.yes_shuffle);
            holder.mShuffle.setImageDrawable(green);
        }


        String truth = mDBHelper.isAlive(mNestedArrayList.get(position).get(0).getmAlarmId());
//        Toast.makeText(mContext, truth, Toast.LENGTH_SHORT).show();

        if (truth.equals("true")){
            holder.mSwitch.setChecked(true);
        } else {
            holder.mSwitch.setChecked(false);
        }

        if (mNestedArrayList.get(position).size() > 0) {
            ArrayList<SongObject> count = mDBHelper.getSongs(mNestedArrayList.get(position).get(0).getmAlarmId());

            int temp = count.size();

            if (temp == 0) {
                holder.mSongType.setText(R.string.song_numbers);
            }

            holder.mSongCount.setText(String.valueOf(temp));
            if ((temp > 0) && (count.get(0).getDuration() > 0)) {
                if (temp == 1) {
                    holder.mSongType.setText(R.string.song_number);
                } else {
                    holder.mSongType.setText(R.string.song_numbers);
                }
            } else if ((temp > 0) && (count.get(0).getDuration() == 0)) {
                holder.mSongType.setText(R.string.playlist);
            }
        }

        for (int i = 0; i < mNestedArrayList.get(position).size(); i++) {
            Log.i(TAG, "onBindViewHolder: mNestedArrayList.get(position).get(i).getmDay() " + mNestedArrayList.get(position).get(i).getmDay());
            switch (mNestedArrayList.get(position).get(i).getmDay()) {
                case 2:
                    holder.mMon.setText(R.string.monday);
                    Log.i(TAG, "onBindViewHolder: mMon.setText(Mo);");
                    break;
                case 3:
                    holder.mTues.setText(R.string.tuesday);
                    Log.i(TAG, "onBindViewHolder: mTues.setText(Tu);");
                    break;
                case 4:
                    holder.mWed.setText(R.string.wednseday);
                    Log.i(TAG, "onBindViewHolder: mWed.setText(We);");
                    break;
                case 5:
                    holder.mThu.setText(R.string.thursday);
                    Log.i(TAG, "onBindViewHolder: mThu.setText(Th);");
                    break;
                case 6:
                    holder.mFri.setText(R.string.friday);
                    Log.i(TAG, "onBindViewHolder: mFri.setText(Fr);");
                    break;
                case 7:
                    holder.mSat.setText(R.string.saturday);
                    Log.i(TAG, "onBindViewHolder: mSat.setText(Sa);");
                    break;
                case 1:
                    holder.mSun.setText(R.string.sunday);
                    Log.i(TAG, "onBindViewHolder: mSun.setText(Su);");
                    break;
            }
        }


        int hour = mNestedArrayList.get(position).get(0).getmHour();
        final int minute = mNestedArrayList.get(position).get(0).getmMinute();

        Log.i(TAG, "onBindViewHolder: " + hour);


//      setting morning or afternoon
        final String amPm;
        if (hour >= 12) {
            amPm = "pm";
            Log.i(TAG, "onBindViewHolder: " + amPm);
        } else {
            amPm = "am";
            Log.i(TAG, "onBindViewHolder: " + amPm);
        }

        if (hour <= 12) {
            if (hour < 10) {
                if (minute < 10) {
                    holder.mTime.setText("0" + hour + ":0" + minute);
                } else {
                    holder.mTime.setText("0" + hour + ":" + minute);
                }
            } else {
                if (minute < 10) {
                    holder.mTime.setText(hour + ":0" + minute);
                } else {
                    holder.mTime.setText(hour + ":" + minute);
                }
            }
        } else {
            if (minute < 10) {
                holder.mTime.setText((hour - 12) + ":0" + minute);
            } else {
                holder.mTime.setText((hour - 12) + ":" + minute);
            }
        }
        if (holder.mTime.getText().toString().substring(0, 1).equals("-")) {
            Log.i(TAG, "onBindViewHolder: holder.mTime.getText() = " + holder.mTime.getText());
        } else {
        }
        holder.mAmPm.setText(amPm);
    }

    @Override
    public int getItemCount() {
        if (mNestedArrayList == null) {
            return 0;
        } else {
            return mNestedArrayList.size();
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mNestedArrayList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        int id = mNestedArrayList.get(position).get(0).getmAlarmId();
        Log.i(TAG, "onItemDismiss: " + id);
        mDBHelper.removeAndCancelAlarm(id, mContext);
        mNestedArrayList.remove(position);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    public void onUpdateItems() {
        Log.i(TAG, "onUpdateItems: ");
        update(mNestedArrayList);
    }


    protected class LandingRViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTime, mAmPm, mMon, mTues, mWed, mThu, mFri, mSat, mSun;
        TextView mSongCount, mSongType;
        LinearLayout mContainer;
//        RippleView mOuterContainer;
        SwitchCompat mSwitch;
        ImageView mShuffle;

        protected LandingRViewViewHolder(View itemView) {
            super(itemView);
            mContainer = (LinearLayout) itemView.findViewById(R.id.rv_item_container);
//            mOuterContainer = (RippleView) itemView.findViewById(R.id.container);
            mSwitch = (SwitchCompat) itemView.findViewById(R.id.switch2);
            mSwitch.setOnClickListener(this);
            mShuffle = (ImageView) itemView.findViewById(R.id.shuffle_image);
            mShuffle.setOnClickListener(this);
//            mOuterContainer.setOnClickListener(this);

            mSongCount = (TextView) itemView.findViewById(R.id.song_count);
            mSongType = (TextView) itemView.findViewById(R.id.song_type);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mAmPm = (TextView) itemView.findViewById(R.id.amVpm);
            mMon = (TextView) itemView.findViewById(R.id.monday);
            mTues = (TextView) itemView.findViewById(R.id.tuesday);
            mWed = (TextView) itemView.findViewById(R.id.wednesday);
            mThu = (TextView) itemView.findViewById(R.id.thursday);
            mFri = (TextView) itemView.findViewById(R.id.friday);
            mSat = (TextView) itemView.findViewById(R.id.saturday);
            mSun = (TextView) itemView.findViewById(R.id.sunday);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: view.getId() " + view.getId());
            switch (view.getId()) {
                case R.id.shuffle_image:
                    Drawable green = mContext.getResources().getDrawable(R.drawable.yes_shuffle);
                    Drawable grey = mContext.getResources().getDrawable(R.drawable.no_shuffle);
                    if (mShuffle.getDrawable().getConstantState() == green.getConstantState()){
                        mShuffle.setImageDrawable(grey);
                        mDBHelper.updateShuffle(mNestedArrayList.get(getAdapterPosition()).get(0).getmAlarmId(),"off");
                    } else {
                        mShuffle.setImageDrawable(green);
                        mDBHelper.updateShuffle(mNestedArrayList.get(getAdapterPosition()).get(0).getmAlarmId(),"on");
                    }
                    break;
                case R.id.switch2:
                    if (!mSwitch.isChecked()) {
                        for (int i = 0; i < mNestedArrayList.get(getAdapterPosition()).size(); i++) {
                            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(mContext, AlarmBroadcastReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                    mContext,
                                    mNestedArrayList.get(getAdapterPosition()).get(i).getmIndex(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pendingIntent);
                            pendingIntent.cancel();
                            mDBHelper.removeCheck("yes", mNestedArrayList.get(getAdapterPosition()).get(i).getmAlarmId());
                        }
                    } else {

                        Calendar mCalendar = Calendar.getInstance();
                        mCalendar.setTimeInMillis(System.currentTimeMillis());


                        for (int i = 0; i < mNestedArrayList.get(getAdapterPosition()).size(); i++) {
                            int minute = mNestedArrayList.get(getAdapterPosition()).get(i).getmMinute();
                            int hour = mNestedArrayList.get(getAdapterPosition()).get(i).getmHour();
                            int day = mNestedArrayList.get(getAdapterPosition()).get(i).getmDay();
                            String message = mNestedArrayList.get(getAdapterPosition()).get(i).getmMessage();
                            int snooze = mNestedArrayList.get(getAdapterPosition()).get(i).getmDay();
                            int identifyingCode = mNestedArrayList.get(getAdapterPosition()).get(i).getmIndex();
                            int alarm_id = mNestedArrayList.get(getAdapterPosition()).get(i).getmAlarmId();

                            mCalendar.set(Calendar.DAY_OF_WEEK, day);
                            mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                            mCalendar.set(Calendar.MINUTE, minute);
                            mCalendar.set(Calendar.SECOND, 0);
                            mCalendar.set(Calendar.MILLISECOND, 0);

                            Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
                            Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
                            Log.i(TAG, "setAlarm:  | time set:" + mCalendar);
                            Log.i(TAG, "setAlarm:  | time set:" + mCalendar.get(Calendar.DAY_OF_WEEK));
                            if (mCalendar.get(Calendar.DAY_OF_MONTH) >= (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7)) {
                                mCalendar.set(Calendar.DAY_OF_MONTH, (mCalendar.get(Calendar.DAY_OF_MONTH) - 7));
                            } else {
                            }
                            Log.i(TAG, "setAlarm:  | time set:" + mCalendar.get(Calendar.DAY_OF_MONTH));
                            Log.i(TAG, "setAlarm:  | time set:" + mCalendar.getTime());
                            Log.i(TAG, "setAlarm:  | time set:" + mCalendar.getTime());
                            Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");
                            Log.i(TAG, "setAlarm: --------------------------------------------------------------------------------");

                            String temp = String.valueOf(mCalendar.getTime());

//                            Toast.makeText(mContext, temp, Toast.LENGTH_SHORT).show();
                            if (!(mCalendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())) {

//        start new instance of alarm manager
                                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        set intent for pending intent
                                Intent intent = new Intent(mContext, AlarmBroadcastReceiver.class);

                                Log.i(TAG, "setAlarm: message " + message);

//        add data to intent
                                intent.putExtra("hour", hour);
                                intent.putExtra("minute", minute);
                                intent.putExtra("day", day);
                                intent.putExtra("unique_code", identifyingCode);
                                intent.putExtra("alarm_id_primary_table", alarm_id);
                                intent.putExtra("snooze", snooze);
                                intent.putExtra("message", message);

//                                Toast.makeText(mContext, "Not Checked", Toast.LENGTH_SHORT).show();
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, identifyingCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        add everything to the alarm manager and start the receiver wait operation
                                alarmManager.setRepeating(
                                        AlarmManager.RTC_WAKEUP,
                                        mCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7,
                                        pendingIntent);
                            } else {}
                            mDBHelper.removeCheck("reset", mNestedArrayList.get(getAdapterPosition()).get(i).getmAlarmId());
                        }
                    }
                    break;
                default:
            }
        }
    }

    public void update(ArrayList<ArrayList<AlarmObject>> array) {
        mNestedArrayList = array;

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        };

        handler.post(runnable);
    }
}
