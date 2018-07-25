package com.anders.spotifyalarm.AlbumSearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.MediaSearch.songSearch.Search;
import com.anders.spotifyalarm.MediaSearch.songSearch.SearchFragment;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anders on 2/14/2017.
 */

public class AlbumSearchFragment extends Fragment implements AlbumSearch.View{

    public static String EXTRA_TOKEN;
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    ImageView mBackButton;
    public static FrameLayout mFrameLayout;
    private Search.ActionListener mActionListener;
    private AlbumAdapter mAdapter;
    Context mContext;
    DBhelper mDB;
    static int mAlarmId;
    MasterSingleton mSingleton;

    public AlbumSearchFragment(){

    }

    public static AlbumSearchFragment getInstance(Bundle bundle, FrameLayout layout, String token, int alarm_id){
        AlbumSearchFragment fragment = new AlbumSearchFragment();
        EXTRA_TOKEN = token;
        mAlarmId = alarm_id;
        mFrameLayout = layout;
        return fragment;
    }


    public static Intent createIntent(Context context) {
        return new Intent(context, SearchFragment.class);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        mSingleton = MasterSingleton.getmInstance();
//        mSingleton.clearSongs();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_view, container, false);
        mSingleton.clearSongs();

        mBackButton = (ImageView) view.findViewById(R.id.back_button);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrameLayout.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mDB = DBhelper.getmInstance(mContext);
//        mActionListener = new AlbumSearchPresenter(mContext, this);
        mActionListener.init(EXTRA_TOKEN);

        // Setup search field
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


        // Setup search results list
        mAdapter = new AlbumAdapter(mContext, mSingleton, mDB, mAlarmId, new AlbumAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                mActionListener.selectTrack(item);
            }
        });

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.search_results);
        resultsList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext);
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
}
