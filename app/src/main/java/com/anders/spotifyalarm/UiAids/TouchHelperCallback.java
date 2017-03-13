package com.anders.spotifyalarm.UiAids;

import android.content.ClipData;
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
import android.widget.Toast;

import com.anders.spotifyalarm.MainActivity;
import com.anders.spotifyalarm.NewAlarmActivity;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.SwipeInterface;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 2/9/2017.
 */

public class TouchHelperCallback extends ItemTouchHelper.Callback {

    Paint mPaint = new Paint();
    MasterSingleton mSingleton = MasterSingleton.getmInstance();

    private final SwipeInterface mInterface;

    public TouchHelperCallback(SwipeInterface swipenterface) {
        mInterface = swipenterface;
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
//        if (direction == ItemTouchHelper.LEFT) {
            mInterface.onItemDismiss(viewHolder.getAdapterPosition());
//        } else {
//            if (mSingleton.getUserId() != 0) {
//                int hour = mNestedArrayList.get(getAdapterPosition()).get(0).getmHour();
//                int minute = mNestedArrayList.get(getAdapterPosition()).get(0).getmMinute();
//                Intent intent = new Intent(mContext, NewAlarmActivity.class);
//                intent.putExtra("hour", hour);
//                intent.putExtra("min", minute);
//                intent.putExtra("alarm_id", mNestedArrayList.get(getAdapterPosition()).get(0).getmAlarmId());
//                intent.putExtra("array", mNestedArrayList.get(getAdapterPosition()));
//                intent.putExtra("snooze", mNestedArrayList.get(getAdapterPosition()).get(0).getmSnooze());
//                intent.putExtra("message", mNestedArrayList.get(getAdapterPosition()).get(0).getmMessage());
//                mContext.startActivity(intent);
////                            mActivity.overridePendingTransition(0, 0);
////                            mActivity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
//            }
//            else {
//                Toast.makeText(mContext,"Please wait for it to load",Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "onCreate: AuthenticationRequest.Builder builder");
//                AuthenticationRequest.Builder builder =
//                        new AuthenticationRequest.Builder(
//                                "8245c9c6491c426cbccf670997c14766",
//                                AuthenticationResponse.Type.TOKEN,
//                                REDIRECT_URL);
//
//
//                builder.setScopes(new String[]{"user-read-private", "streaming"});
//                AuthenticationRequest request = builder.build();
//
//                AuthenticationClient.openLoginActivity(mActivity, REQUEST_CODE, request);
//            }
//        }
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Bitmap icon;
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//            View itemView = viewHolder.itemView;
//            float height = (float) itemView.getBottom() - (float) itemView.getTop();
//            float width = height / 3;
//
//            if(dX > 0){
//                mPaint.setColor(Color.parseColor("#388E3C"));
//                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                canvas.drawRect(background,mPaint);
//                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//            } else {
//                mPaint.setColor(Color.parseColor("#D32F2F"));
//                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
//                canvas.drawRect(background,mPaint);
//                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
//            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mInterface.onUpdateItems();
        // Action finished
    }
}
