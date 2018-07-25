package com.anders.spotifyalarm.UiAids;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.NewAlarmActivity;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 2/9/2017.
 */

public class TouchHelperCallback extends ItemTouchHelper.Callback {

    Context mContext;
    Paint mPaint = new Paint();
    MasterSingleton mSingleton = MasterSingleton.getmInstance();
    ArrayList<ArrayList<AlarmObject>> mArrayListArrayList;

    private final SwipeInterface mInterface;

    public TouchHelperCallback(ArrayList<ArrayList<AlarmObject>> arrayListArrayList, SwipeInterface swipenterface, Context context) {
        mInterface = swipenterface;
        mContext = context;
        mArrayListArrayList = arrayListArrayList;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mInterface.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.START) {
            mInterface.onItemDismiss(viewHolder.getAdapterPosition());
        } else if (direction == ItemTouchHelper.END){
            int hour = mArrayListArrayList.get(viewHolder.getAdapterPosition()).get(0).getmHour();
            int minute = mArrayListArrayList.get(viewHolder.getAdapterPosition()).get(0).getmMinute();
            Intent intent = new Intent(mContext, NewAlarmActivity.class);
            intent.putExtra("hour", hour);
            intent.putExtra("min", minute);
            intent.putExtra("alarm_id", mArrayListArrayList.get(viewHolder.getAdapterPosition()).get(0).getmAlarmId());
            intent.putExtra("array", mArrayListArrayList.get(viewHolder.getAdapterPosition()));
            intent.putExtra("snooze", mArrayListArrayList.get(viewHolder.getAdapterPosition()).get(0).getmSnooze());
            intent.putExtra("message", mArrayListArrayList.get(viewHolder.getAdapterPosition()).get(0).getmMessage());
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Bitmap icon;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                mPaint.setColor(Color.parseColor("#36d98b"));
                RectF background = new RectF(
                        (float) itemView.getLeft(),
                        (float) itemView.getTop(),
                        dX,
                        (float) itemView.getBottom());
                canvas.drawRect(background, mPaint);
                    icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.edit_icon);
                if (dX > ((itemView.getLeft() + width) + icon.getWidth())) {
                    RectF icon_dest = new RectF(
                            (float) itemView.getLeft() + width,
                            itemView.getTop() + width,
                            (float) itemView.getLeft() + 2 * width,
                            (float) itemView.getBottom() - width);
                    canvas.drawBitmap(icon, null, icon_dest, mPaint);
                }
            } else {
                mPaint.setColor(Color.parseColor("#e63a4a"));
                RectF background = new RectF(
                        (float) itemView.getRight() + dX,
                        (float) itemView.getTop(),
                        (float) itemView.getRight(),
                        (float) itemView.getBottom());
                canvas.drawRect(background, mPaint);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cancel_white);
                Log.i(TAG, "onChildDraw: itemView.getRight() " + itemView.getRight());
                Log.i(TAG, "onChildDraw: width " + width);
                if (dX < - (((-1 * itemView.getRight()) + width))) {
                    RectF icon_dest = new RectF(
                            (float) itemView.getRight() - 2 * width,
                            (float) itemView.getTop() + width,
                            (float) itemView.getRight() - width,
                            (float) itemView.getBottom() - width);
                    canvas.drawBitmap(icon, null, icon_dest, mPaint);
                }
            }

        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public void update(ArrayList<ArrayList<AlarmObject>> arrayListArrayList){
        mArrayListArrayList = arrayListArrayList;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mInterface.onUpdateItems();
        // Action finished
    }
}
