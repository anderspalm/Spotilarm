package com.anders.spotifyalarm.MediaSearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.AlbumSearch.AlbumSearch;
import com.anders.spotifyalarm.MediaSearch.songSearch.AlbumFragment;
import com.anders.spotifyalarm.MediaSearch.songSearch.SearchFragment;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.PhotoTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 2/20/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ContainerViewHolder> {


    ArrayList<ArrayList<SongObject>> mPlaylists;
    Context mContext;
    SetCurrPlaylistListener mItemSelectList;
    ArrayList<SongObject> mSongArrayList;
    DBhelper mDBhelper;
    FragmentManager mFragManager;
    FrameLayout mPlayEditFrag;
    MasterSingleton mMasterSingleton;
    SearchFragment searchFragment;
    Animation mButtonAnimator;


    public PlaylistAdapter(FrameLayout playlist_edit_fragment, ArrayList<ArrayList<SongObject>> playlists,
                           Context context, FragmentManager fragmentManager,
                           SetCurrPlaylistListener listener) {
        mPlayEditFrag = playlist_edit_fragment;
        mPlaylists = playlists;
        mContext = context;
        mItemSelectList = listener;
        Log.i(TAG, "PlaylistAdapter: ");
        mFragManager = fragmentManager;
        mSongArrayList = new ArrayList<>();
        mMasterSingleton = MasterSingleton.getmInstance();
        mDBhelper = DBhelper.getmInstance(mContext);
        mButtonAnimator = AnimationUtils.loadAnimation(mContext, R.anim.button_anim);
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
    }

    public class ContainerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle, mSetAsCurrent, mRemovePlaylist, mEditAlbum;
        ImageView mPlaylistPhoto;

        public ContainerViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mPlaylistPhoto = (ImageView) itemView.findViewById(R.id.playlist_photo);
            mSetAsCurrent = (TextView) itemView.findViewById(R.id.set);
            mRemovePlaylist = (TextView) itemView.findViewById(R.id.cancel);
            mEditAlbum = (TextView) itemView.findViewById(R.id.edit_album);
            mSetAsCurrent.setOnClickListener(this);
            mRemovePlaylist.setOnClickListener(this);
            mEditAlbum.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mPlaylists.get(getAdapterPosition()).get(0).getDuration() > 0) {
                if (view.getId() == R.id.edit_album) {
                    mEditAlbum.setAnimation(mButtonAnimator);
                    mMasterSingleton.setSongObjectArray(mPlaylists.get(getAdapterPosition()));
                    mPlayEditFrag.setVisibility(View.VISIBLE);

                    searchFragment.setReturnPlaylistList(new SearchFragment.ReturnPlaylistListener() {
                        @Override
                        public void returnPlaylistListener(ArrayList<SongObject> arrayList) {
                            mPlayEditFrag.setVisibility(View.GONE);
                            if (arrayList.size() > 0) {
//                              change database
                                mDBhelper.deleteSpecificPlaylist(mPlaylists.get(getAdapterPosition()));
                                mDBhelper.insertHistPlaylists(mMasterSingleton.getSongObjArray());
                                for (int i = 0; i < mPlaylists.get(getAdapterPosition()).size(); i++) {
                                    Log.i(TAG, "PlaylistAdapter: " + mPlaylists.get(getAdapterPosition()).get(i).getTitle());
                                }
                                mPlaylists.set(getAdapterPosition(), arrayList);
                                notifyDataSetChanged();
                            } else {
                                Log.i(TAG, "returnPlaylistMethod: error no songs were returned");
                            }
                        }
                    });

                    FragmentTransaction fragmentTransaction = mFragManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                    fragmentTransaction.replace(R.id.change_album_fragment, searchFragment)
                            .addToBackStack("song_fragment").commit();
                }
            } else {
                mEditAlbum.setVisibility(View.GONE);
            }

            switch (view.getId()) {
                case R.id.set:
                    mSetAsCurrent.setAnimation(mButtonAnimator);
                    if (mPlaylists.get(getAdapterPosition()).size() != 0) {
                        mItemSelectList.onCurrentPlaylistSelected(mPlaylists.get(getAdapterPosition()));
                    } else {
                        mItemSelectList.onCurrentPlaylistSelected(mDBhelper.getAllHistPlaylists().get(getAdapterPosition()));
                    }
                    break;
                case R.id.edit_album:
                    break;
                case R.id.cancel:
                    mRemovePlaylist.setAnimation(mButtonAnimator);
                    mDBhelper.deleteSpecificPlaylist(mPlaylists.get(getAdapterPosition()));
                    mPlaylists.remove(mPlaylists.get(getAdapterPosition()));
                    notifyDataSetChanged();
                    break;

            }
        }
    }

    public interface SetCurrPlaylistListener {
        void onCurrentPlaylistSelected(ArrayList<SongObject> playlist);
    }

    @Override
    public ContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.playlist_item, parent, false);
        ContainerViewHolder containerViewHolder = new ContainerViewHolder(view);
        return containerViewHolder;
    }

    @Override
    public void onBindViewHolder(ContainerViewHolder holder, int position) {
        holder.mTitle.setText(mPlaylists.get(position).get(0).getPlaylist());

        if (mPlaylists.get(position).get(0).getDuration() > 0) {
            holder.mEditAlbum.setVisibility(View.VISIBLE);
        } else {
            holder.mEditAlbum.setVisibility(View.GONE);
        }

        Picasso.with(mContext)
                .load(mPlaylists.get(position).get(0).getPhotoUri())
                .fit()
                .into(holder.mPlaylistPhoto);

    }

    @Override
    public int getItemCount() {
        if (mPlaylists.size() > 0) {
            return mPlaylists.size();
        } else {
            return 0;
        }
    }
}
