package com.anders.spotifyalarm.MediaSearch.songSearch.TouchHelper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.anders.spotifyalarm.UiAids.SwipeInterface;

/**
 * Created by anders on 2/21/2017.
 */

public class TouchCallback extends ItemTouchHelper.Callback {

    private final SwipeInterface mInterface;

    public TouchCallback(SwipeInterface swipeInterface) {
        mInterface = swipeInterface;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipe = ItemTouchHelper.START | ItemTouchHelper.END;
        int drag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(drag,swipe);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mInterface.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mInterface.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mInterface.onUpdateItems();
        // Action finished
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
