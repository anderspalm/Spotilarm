package com.anders.spotifyalarm.MediaSearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    static Context mContext;
    static DBhelper mDBhelper;
    ArrayList<ArrayList<SongObject>> mSongArray = new ArrayList<>();
    static FrameLayout mFragmentBackground;
    PlaylistAdapter mLocalAdapter, mForeignAdapter;
    MasterSingleton mMasterSingleton;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(Context context, FrameLayout frame) {
        HistoryFragment fragment = new HistoryFragment();
        mContext = context;
        mDBhelper = DBhelper.getmInstance(context);
        mFragmentBackground = frame;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMasterSingleton = MasterSingleton.getmInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// ---------------------------------------------------
//         inflate view
// ---------------------------------------------------
        View view = inflater.inflate(R.layout.fragment_history, container, false);

// ---------------------------------------------------
//         back button
// ---------------------------------------------------
        Button backButton = (Button) view.findViewById(R.id.hist_playlist_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentBackground.setVisibility(View.GONE);
            }
        });

// ---------------------------------------------------
//         separate the array
// ---------------------------------------------------
        mSongArray = mDBhelper.getAllHistPlaylists();
        ArrayList<ArrayList<SongObject>> localArray = new ArrayList<>();
        ArrayList<ArrayList<SongObject>> spotifyArray = new ArrayList<>();
        Toast.makeText(mContext, " " + mSongArray.size(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < mSongArray.size(); i++){
            if (mSongArray.get(i).get(0).getDuration() == 0){
                spotifyArray.add(mSongArray.get(i));
            } else {
                localArray.add(mSongArray.get(i));
            }
        }



// ---------------------------------------------------
//         RV for Local playlists
// ---------------------------------------------------
        RecyclerView mLocalPlaylistsRView = (RecyclerView) view.findViewById(R.id.local_playlists);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mLocalPlaylistsRView.setLayoutManager(linearLayout1);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FrameLayout editPlaylistFrame = (FrameLayout) view.findViewById(R.id.change_playlist_fragment);
        mLocalAdapter = new PlaylistAdapter(
                editPlaylistFrame,
                localArray,
                mContext,
                fragmentManager,
                new PlaylistAdapter.SetCurrPlaylistListener() {
                    @Override
                    public void onCurrentPlaylistSelected(ArrayList<SongObject> playlist) {
                        mMasterSingleton.setSongObjectArray(playlist);
                    }
                }
        );
        mLocalPlaylistsRView.setAdapter(mLocalAdapter);

// ---------------------------------------------------
//         RV for Foreign playlists
// ---------------------------------------------------
        RecyclerView mForeignPlaylistsRView = (RecyclerView) view.findViewById(R.id.foreign_playists);
        LinearLayoutManager linearLayout2 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mForeignPlaylistsRView .setLayoutManager(linearLayout2);
        FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
        mForeignAdapter = new PlaylistAdapter(
                editPlaylistFrame,
                spotifyArray,
                mContext,
                fragmentManager2,
                new PlaylistAdapter.SetCurrPlaylistListener() {
                    @Override
                    public void onCurrentPlaylistSelected(ArrayList<SongObject> playlist) {
                        mMasterSingleton.setSongObjectArray(playlist);
                    }
                }
        );
        mForeignPlaylistsRView .setAdapter(mLocalAdapter);


        return view;
    }















    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mSongArray = mDBhelper.getAllHistPlaylists();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
