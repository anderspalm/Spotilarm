package com.anders.spotifyalarm.AlbumSearch;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anders on 2/14/2017.
 */

public class AlbumSearch {

    public interface View {
        void reset();

        void addData(List<Track> items);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void search(String searchQuery);

        void loadMoreResults();

        void selectTrack(Track item);

        void resume();

        void pause();

        void destroy();

    }

}
