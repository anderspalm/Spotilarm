package com.anders.spotifyalarm.UiAids;

/**
 * Created by anders on 2/9/2017.
 */

public interface SwipeInterface  {
    boolean onItemMove (int fromPosition, int toPosition);
    void onItemDismiss (int position);
    void onUpdateItems ();
}
