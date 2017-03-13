package com.anders.spotifyalarm.MediaSearch.songSearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anders.spotifyalarm.MediaSearch.SongAdapter;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anders on 3/3/2017.
 */

public class AlbumFragment extends Fragment {

    private static Context mContext;
    AlbumFragment mAlbFrag;
    MasterSingleton mSing;
    DBhelper mDBHelper;
    SongAdapter mSongAdapter;

    public AlbumFragment(){}

    public static AlbumFragment getInstance(ArrayList<SongObject> arrayList, Context context, PlaylistListener listener) {
        mContext = context;
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_edit_layout,container,false);


        RecyclerView songsRecyclerView = (RecyclerView) view.findViewById(R.id.currentItems);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        songsRecyclerView.setLayoutManager(linearLayout1);
        mSongAdapter = new SongAdapter(mContext, mSing.getSongObjArray());
        songsRecyclerView.setAdapter(mSongAdapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.album_rv_layout);
        SearchAdapter adapter = new SearchAdapter(mContext, mSing, mDBHelper, 0, new SearchAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item, SongObject object) {
//                mActionListener.selectTrack(item);
                mSing.addSong(object);
                mSongAdapter.updateList(mSing.getSongObjArray());
                mSongAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        return view;
    }

    public interface PlaylistListener  {
        void onUpdateAlbum(ArrayList<SongObject> playlist);
    }


}
