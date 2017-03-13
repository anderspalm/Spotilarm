package com.anders.spotifyalarm.UiAids;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MainActivity;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.NewAlarmActivity;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.andexert.library.RippleView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import kaaes.spotify.webapi.android.SpotifyService;

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
        holder.mOuterContainer.setAnimation(mAnimator);

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

        //        Button mSwitch;
        TextView mTime, mAmPm, mMon, mTues, mWed, mThu, mFri, mSat, mSun;
        TextView mSongCount, mSongType;
        LinearLayout mContainer;
//        RippleView mRippleLayout;
        RippleView mOuterContainer;

        protected LandingRViewViewHolder(View itemView) {
            super(itemView);
//            mRippleLayout = (RippleView) itemView.findViewById(R.id.ripple_layout);
            mContainer = (LinearLayout) itemView.findViewById(R.id.rv_item_container);
            mOuterContainer = (RippleView) itemView.findViewById(R.id.container);
//            mContainer.setOnClickListener(this);
//            mRippleLayout.setOnClickListener(this);
            mOuterContainer.setOnClickListener(this);

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
                case R.id.container:
                    if (!mSingleton.getUserId().equals("0")) {
                        Log.i(TAG, "onClick: mSingleton.getUserId() " + mSingleton.getUserId());
                        mOuterContainer.startAnimation(mAnimator);
                        int hour = mNestedArrayList.get(getAdapterPosition()).get(0).getmHour();
                        int minute = mNestedArrayList.get(getAdapterPosition()).get(0).getmMinute();
                        Intent intent = new Intent(mContext, NewAlarmActivity.class);
                        intent.putExtra("hour", hour);
                        intent.putExtra("min", minute);
                        intent.putExtra("alarm_id", mNestedArrayList.get(getAdapterPosition()).get(0).getmAlarmId());
                        intent.putExtra("array", mNestedArrayList.get(getAdapterPosition()));
                        intent.putExtra("snooze", mNestedArrayList.get(getAdapterPosition()).get(0).getmSnooze());
                        intent.putExtra("message", mNestedArrayList.get(getAdapterPosition()).get(0).getmMessage());
                        mContext.startActivity(intent);
//                            mActivity.overridePendingTransition(0, 0);
//                            mActivity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
                    }
                    else {
                        Toast.makeText(mContext,"Please wait for it to load",Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onCreate: AuthenticationRequest.Builder builder");
                        AuthenticationRequest.Builder builder =
                                new AuthenticationRequest.Builder(
                                        "8245c9c6491c426cbccf670997c14766",
                                        AuthenticationResponse.Type.TOKEN,
                                        REDIRECT_URL);


                        builder.setScopes(new String[]{"user-read-private", "streaming"});
                        AuthenticationRequest request = builder.build();

                        AuthenticationClient.openLoginActivity(mActivity, REQUEST_CODE, request);
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
