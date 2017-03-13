package com.anders.spotifyalarm.UiAids;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;

import java.util.ArrayList;

/**
 * Created by anders on 2/9/2017.
 */

public interface SwipeInterface  {
    boolean onItemMove (int fromPosition, int toPosition);
    void onItemDismiss (int position);
    void onUpdateItems ();
}
