package com.anders.spotifyalarm.MediaSearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anders on 4/11/2017.
 */

public class HistPlaylistAdapter extends RecyclerView.Adapter<HistPlaylistAdapter.HistViewHolder> {

    Boolean mLocal;
    Context mContext;
    ArrayList<ArrayList<SongObject>> mArrayOfHist;

    public HistPlaylistAdapter(ArrayList<ArrayList<SongObject>> arrayOfHistPlaylists, Boolean local, Context context) {
        mLocal = local;
        mContext = context;
        mArrayOfHist = arrayOfHistPlaylists;
    }

    @Override
    public HistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_rec_view_layout, parent, false);
        HistViewHolder viewHolder = new HistViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistViewHolder holder, int position) {

        Picasso.with(mContext)
                .load(mArrayOfHist.get(position).get(0).getPhotoUri())
                .into(holder.mHistPhoto);
        holder.mHistTitle.setText(mArrayOfHist.get(position).get(0).getPlaylist());
        if (mLocal){
            holder.mHistSize.setText(mArrayOfHist.get(position).size());
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class HistViewHolder extends RecyclerView.ViewHolder {

        ImageView mHistPhoto;
        TextView mHistTitle, mHistSize;
        public HistViewHolder(View itemView) {
            super(itemView);
            mHistPhoto = (ImageView) itemView.findViewById(R.id.hist_photo);
            mHistTitle = (TextView) itemView.findViewById(R.id.hist_title);
            mHistSize = (TextView) itemView.findViewById(R.id.hist_size);
        }

    }
}
