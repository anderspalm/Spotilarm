package com.anders.spotifyalarm.MediaSearch.playlistSearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.PhotoTransformation;
import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anders on 3/5/2017.
 */

public class SpotifyPlaylistAdapter extends RecyclerView.Adapter<SpotifyPlaylistAdapter.SpotifyAdapterViewholder> {

    Context mContext;
    ArrayList<PlaylistObject> mList;
    ReturnSpotifyPlaylist mReturnSpotifyPlaylist;
    Animation mButtonAnimation;
    MasterSingleton mSingleton;
    private static final String TAG = "SpotifyPlaylistAdapter";
    private static final int REQUEST_CODE = 1337;
    public static final String REDIRECT_URL = "http://localhost:8888/callback";

    public SpotifyPlaylistAdapter(Context context, ReturnSpotifyPlaylist listener) {
        mContext = context;
        mList = new ArrayList<>();
        mReturnSpotifyPlaylist = listener;
        mButtonAnimation = AnimationUtils.loadAnimation(mContext, R.anim.button_anim);
    }

    @Override
    public SpotifyAdapterViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.spotify_playlist_adapter_item, parent, false);

        SpotifyAdapterViewholder savh = new SpotifyAdapterViewholder(view);
        return savh;
    }

    @Override
    public void onBindViewHolder(SpotifyAdapterViewholder holder, int position) {

        Picasso.with(mContext).load(mList.get(position).getUrl())
                .fit()
                .transform(new PhotoTransformation(50, 4))
                .into(holder.mImage);

        holder.mRipple.setAnimation(mButtonAnimation);

        holder.mName.setText(mList.get(position).getName());
    }

    public class SpotifyAdapterViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mName;
        RippleView mRipple;
        LinearLayout mContainer;

        public SpotifyAdapterViewholder(View itemView) {
            super(itemView);
            mContainer = (LinearLayout) itemView.findViewById(R.id.container);
            mRipple = (RippleView) itemView.findViewById(R.id.layout);
            mName = (TextView) itemView.findViewById(R.id.playlist_title);
            mImage = (ImageView) itemView.findViewById(R.id.playlist_photo);
//            itemView.setOnClickListener(this);
            mRipple.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: ");
            if (v.getId() == mRipple.getId()) {
                mContainer.startAnimation(mButtonAnimation);
//                Toast.makeText(mContext, "Hello", Toast.LENGTH_LONG).show();
                mReturnSpotifyPlaylist.returnPlaylistListener(mList.get(getAdapterPosition()));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() > 0) {
            return mList.size();
        } else {
            return 0;
        }
    }

    public void updatePlaylist(ArrayList<PlaylistObject> arrayList) {
        mList = arrayList;
        notifyDataSetChanged();
    }

    public interface ReturnSpotifyPlaylist {
        void returnPlaylistListener(PlaylistObject obj);
    }
}
