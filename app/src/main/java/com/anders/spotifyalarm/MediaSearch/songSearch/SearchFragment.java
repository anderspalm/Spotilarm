package com.anders.spotifyalarm.MediaSearch.songSearch;

import android.animation.Animator;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.anders.spotifyalarm.MediaSearch.SongAdapter;
import com.anders.spotifyalarm.MediaSearch.songSearch.TouchHelper.TouchCallback;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.UiAids.TouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

import static android.content.ContentValues.TAG;

public class SearchFragment extends Fragment implements Search.View {

    public static String EXTRA_TOKEN;
    public static FrameLayout mFrameLayout;
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    private Search.ActionListener mActionListener;
    private SearchAdapter mAdapter;
    private ReturnSongsListener mReturnSongsListener;
    private ReturnPlaylistListener mReturnPlaylistListener;
    static int mAlarmId;
    SongAdapter mSongAdapter;
    MasterSingleton mSingleton;
    ImageView mBackButton, mSearchButton;
    FrameLayout mTopRV;
    Button mShowButton;
    Context mContext;
    static ArrayList<SongObject> mHistoricalPlaylist;
    DBhelper mDB;
    TextView mTitle;
    static SearchFragment mSearchFrag;


    public SearchFragment() {
        mSingleton = MasterSingleton.getmInstance();
        mSingleton.clearSongs();
    }

    public static SearchFragment getInstance(ArrayList<SongObject> arrayList) {
        if (mSearchFrag == null) {
            mSearchFrag = new SearchFragment();
        } else {}
        mHistoricalPlaylist = arrayList;
        Log.i(TAG, "SearchFragment: playlistArray secondary input " + arrayList.size());
        return mSearchFrag;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, SearchFragment.class);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        if (mSingleton == null) {
            mSingleton = MasterSingleton.getmInstance();
        } else {
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_view, container, false);

        mSingleton = MasterSingleton.getmInstance();
        for (int i = 0; i < mSingleton.getSongObjArray().size(); i++) {
            Log.i(TAG, "mSingleton.getSongObjArray(): title " + i + " = " + mSingleton.getSongObjArray().get(i).getTitle());
        }

        mTitle = (TextView) view.findViewById(R.id.create_playlist_title);
        mBackButton = (ImageView) view.findViewById(R.id.back_button);
        mSearchButton = (ImageView) view.findViewById(R.id.search_button);
        mShowButton = (Button) view.findViewById(R.id.show);
        mTopRV = (FrameLayout) view.findViewById(R.id.topRecyclerView);

        if (mSingleton.getSongObjArray().size() > 0){
            mTitle.setText(mSingleton.getSongObjArray().get(0).getPlaylist());
        }

        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTopRV.setVisibility(View.GONE);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReturnSongsListener != null) {
                    mReturnSongsListener.returnSongsMethod(mSingleton.getSongObjArray());
                } else {}
                if (mReturnPlaylistListener != null) {
                    mReturnPlaylistListener.returnPlaylistListener(mSingleton.getSongObjArray());
                    for (int i = 0; i < mSingleton.getSongObjArray().size(); i ++){}
                } else {}
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        // Android native animator


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTopRV.setVisibility(View.VISIBLE);
                // get the center for the clipping circle
            }
        });

        mDB = DBhelper.getmInstance(mContext);
        mActionListener = new SearchPresenter(mContext, this);
        mActionListener.init(EXTRA_TOKEN);

        // Setup search field`
        final SearchView searchView = (SearchView) view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mActionListener.search(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_button", null, null);
        if (searchIconId != 0) {
            ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
            if (searchIcon != null) {
                searchIcon.setImageResource(R.drawable.alarm_clock);
            }
        } else {}


        // Setup search results list
        mAdapter = new SearchAdapter(mContext, mSingleton, mDB, mAlarmId, new SearchAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item, SongObject object) {
                mActionListener.selectTrack(item);
                mSingleton.addSong(object);
                mSongAdapter.updateList(mSingleton.getSongObjArray());
                mSongAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView songsRecyclerView = (RecyclerView) view.findViewById(R.id.currentItems);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        songsRecyclerView.setLayoutManager(linearLayout1);
        Log.i(TAG, "SearchFragment: playlistArray sing.size " + mSingleton.getSongObjArray().size());
        mSongAdapter = new SongAdapter(mContext, mSingleton.getSongObjArray());
        songsRecyclerView.setAdapter(mSongAdapter);

        ItemTouchHelper.Callback callback = new TouchCallback(mSongAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(songsRecyclerView);

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.search_results);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        resultsList.setLayoutManager(linearLayout);
        resultsList.setAdapter(mAdapter);

        // If Activity was recreated with active search, restore it
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }

        return view;
    }

    @Override
    public void reset() {
        mAdapter.clearData();
    }

    @Override
    public void addData(List<Track> items) {
        mAdapter.addData(items);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionListener.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionListener.resume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActionListener.getCurrentQuery() != null) {
            outState.putString(KEY_CURRENT_QUERY, mActionListener.getCurrentQuery());
        }
    }

    @Override
    public void onDestroy() {
        mActionListener.destroy();
        super.onDestroy();
    }

    public interface ReturnPlaylistListener {
        void returnPlaylistListener(ArrayList<SongObject> arrayList);
    }

    public void setReturnPlaylistList(ReturnPlaylistListener listener) {
        mReturnPlaylistListener = listener;
    }


    public interface ReturnSongsListener {
        void returnSongsMethod(ArrayList<SongObject> arrayList);
    }

    public void setReturnSongsListener(ReturnSongsListener listener) {
        mReturnSongsListener = listener;
        Log.i(TAG, "setmListener: ");
    }

}