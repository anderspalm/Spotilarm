package com.anders.spotifyalarm.AlbumSearch;

import com.anders.spotifyalarm.MediaSearch.songSearch.SearchPager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anders on 2/14/2017.
 */

public class AlbumSearchPager {
    private final SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPageSize;
    private String mCurrentQuery;

    public interface CompleteListener {
        void onComplete(List<Track> items);
        void onError(Throwable error);
    }

    public AlbumSearchPager(SpotifyService spotifyApi) {
        mSpotifyApi = spotifyApi;
    }

    public void getFirstPage(String query, int pageSize, SearchPager.CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        mCurrentQuery = query;
        getData(query, 0, pageSize, listener);
    }

    public void getNextPage(SearchPager.CompleteListener listener) {
        mCurrentOffset += mPageSize;
        getData(mCurrentQuery, mCurrentOffset, mPageSize, listener);
    }

    private void getData(String query, int offset, final int limit, final SearchPager.CompleteListener listener) {

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

//        mSpotifyApi.searchTracks(query, options, new SpotifyCallback<TracksPager>() {
//            @Override
//            public void success(TracksPager tracksPager, Response response) {
//                listener.onComplete(tracksPager.tracks.items);
//            }
//
//            @Override
//            public void failure(SpotifyError error) {
//                listener.onError(error);
//            }
//        });
    }
}
