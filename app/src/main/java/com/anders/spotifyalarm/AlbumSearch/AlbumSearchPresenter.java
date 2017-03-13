package com.anders.spotifyalarm.AlbumSearch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.anders.spotifyalarm.MediaSearch.songSearch.Search;
import com.anders.spotifyalarm.MediaSearch.songSearch.SearchPager;
import com.anders.spotifyalarm.MediaSearch.songSearch.SearchPresenter;
import com.spotify.sdk.android.player.Player;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anders on 2/14/2017.
 */

public class AlbumSearchPresenter implements AlbumSearch.ActionListener{


    private static final String TAG = SearchPresenter.class.getSimpleName();
    public static final int PAGE_SIZE = 20;

    private final Context mContext;
    private final Search.View mView;
    private String mCurrentQuery;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchListener;

    private Player mPlayer;

    public AlbumSearchPresenter(Context context, Search.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void init(String accessToken) {
        logMessage("Api Client created");
        SpotifyApi spotifyApi = new SpotifyApi();

        if (accessToken != null) {
            spotifyApi.setAccessToken(accessToken);
        } else {
            logError("No valid access token");
        }

        mSearchPager = new SearchPager(spotifyApi.getService());
    }


    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            logMessage("query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mView.reset();
            mSearchListener = new SearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    mView.addData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mSearchPager.getFirstPage(searchQuery, PAGE_SIZE, mSearchListener);
        }
    }


    @Override
    public void destroy() {
    }

    @Override
    @Nullable
    public String getCurrentQuery() {
        return mCurrentQuery;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mSearchPager.getNextPage(mSearchListener);
    }

    @Override
    public void selectTrack(Track item) {
        String previewUrl = item.preview_url;


    }

    private void logError(String msg) {
        Toast.makeText(mContext, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
}
