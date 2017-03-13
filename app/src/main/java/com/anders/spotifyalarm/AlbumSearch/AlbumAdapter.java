package com.anders.spotifyalarm.AlbumSearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anders on 2/14/2017.
 */

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    /**
     * Created by anders on 2/10/2017.
     */


    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    ArrayList<Integer> check;
    private int mAlarmId;
    DBhelper mDBhelper;
    ItemSelectedListener mListener;
    MasterSingleton mSingleton;
    Track mCurrentTrack;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView subtitle;
        public ImageView mAlbum_photo, mAddRemove;
        LinearLayout mLayout;
//        public final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mLayout = (LinearLayout) itemView.findViewById(R.id.layout);
            mAlbum_photo = (ImageView) itemView.findViewById(R.id.album_photo);
            mAddRemove = (ImageView) itemView.findViewById(R.id.addRemovebutton);
//            image = (ImageView) itemView.findViewById(R.id.entity_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, mItems.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item);
    }

    public AlbumAdapter(Context context, MasterSingleton singleton, DBhelper db, int alarm_id, ItemSelectedListener listener) {
        mContext = context;
        mSingleton = singleton;
        mListener = listener;
        mAlarmId = alarm_id;
        mDBhelper = db;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);

        if (check != null) {
            check.clear();
        } else {
        }

        for (int i = 0; i < mItems.size(); i++) {
            if (check != null) {
                check.add(0);
            } else {
                check = new ArrayList<>();
                check.add(0);
            }
        }
        notifyDataSetChanged();
    }


//    public interface ItemSelectedListener {
//        void onItemSelected(View itemView, Track item);
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCurrentTrack = mItems.get(position);

//        holder.title.setText(mCurrentTrack.name);

        List<String> names = new ArrayList<>();
        for (ArtistSimple i : mCurrentTrack.artists) {
            names.add(i.name);
        }

        String url = mCurrentTrack.album.images.get(0).url;
//        holder.subtitle.setText(mCurrentTrack.name  + " url: " + mCurrentTrack.uri +
//                mCurrentTrack.album.name  + " \nitem id: " + mCurrentTrack.id + "\nartist id" +
//                mCurrentTrack.artists.get(0).id  + "\n artist uri: " + mCurrentTrack.artists.get(0).uri + " \nduration " +
//                mCurrentTrack.duration_ms  + " \nalbum external url: " + mCurrentTrack.album.external_urls);
//        holder.subtitle.setText(names.get(0));
//        Log.i(TAG, "onBindViewHolder: url " + url);
//
//        Picasso.with(mContext).load(url)
//                .centerCrop()
//                .fit()
//                .transform(new PhotoTransformation(50, 4))
//                .into(holder.mAlbum_photo);
//
//        holder.mAddRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                mDBhelper.addSong(mAlarmId);
//                if (check.get(position) == 0) {
//                    mSingleton.addSong(new SongObject(mCurrentTrack.duration_ms, mCurrentTrack.uri));
//                    Picasso.with(mContext).load(R.drawable.cancel_white).into(holder.mAddRemove);
//                    check.set(position, 1);
//                } else {
//                    mSingleton.removeSong(new SongObject(mCurrentTrack.duration_ms, mCurrentTrack.uri));
//                    Picasso.with(mContext).load(R.drawable.pos_white).into(holder.mAddRemove);
//                    check.set(position, 0);
//                }
//                Log.i(TAG, "onClick: mSingleton.getSong_uri_string().size(): " + mSingleton.getSong_uri_string().size());
//            }
//        });
//
//        Image image = item.album.images.get(0);
//        if (image != null) {
//            Picasso.with(mContext).load(image.url).into(holder.image);
//        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null && mItems.size() > 0) {
            return mItems.size();
        } else {
            return 0;
        }
    }
}
