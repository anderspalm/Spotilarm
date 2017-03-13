package com.anders.spotifyalarm.MediaSearch.songSearch;

/**
 * Created by anders on 2/10/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.PhotoTransformation;
import com.anders.spotifyalarm.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

import static android.content.ContentValues.TAG;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    ArrayList<Integer> check;
    private int mAlarmId;
    DBhelper mDBhelper;
    MasterSingleton mSingleton;
    Track mCurrentTrack;


    public SearchAdapter(Context context, MasterSingleton singleton, DBhelper db, int alarm_id, ItemSelectedListener listener) {
        mContext = context;
        mSingleton = singleton;
        mListener = listener;
        mAlarmId = alarm_id;
        mDBhelper = db;
        check = new ArrayList<>();
        for (int i = 0; i < mItems.size(); i++) {
            check.add(0);
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        mCurrentTrack = mItems.get(position);

        holder.title.setText(mCurrentTrack.name);
        holder.subtitle.setText(mCurrentTrack.artists.get(0).name);

        String url = mCurrentTrack.album.images.get(0).url;
        Picasso.with(mContext).load(url)
                .centerCrop()
                .fit()
                .transform(new PhotoTransformation(50, 4))
                .into(holder.mAlbum_photo);
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

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
            mLayout.setClickable(true);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            notifyItemChanged(getLayoutPosition());
            Log.i(TAG, "onClick: ViewHolder " + getAdapterPosition());
            Track currentTrack = mItems.get(getAdapterPosition());
            SongObject songObj = new SongObject(
                    currentTrack.duration_ms,
                    currentTrack.uri,
                    currentTrack.name,
                    currentTrack.artists.get(0).name,
                    currentTrack.popularity.toString(),
                    currentTrack.album.images.get(0).url);

            Log.i(TAG, "onLongClick:  mSingleton.getSong_uri_string().size() " + mSingleton.getSongObjArray().size());
            mListener.onItemSelected(view, mItems.get(getAdapterPosition()), songObj);
            return false;
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item, SongObject object);
    }

    @Override
    public int getItemCount() {
        check = new ArrayList<>();
        for (int j = 0; j < mItems.size(); j++) {
            check.add(0);
        }

        if (mItems != null && mItems.size() > 0) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
        check = new ArrayList<>();
        for (int i = 0; i < mItems.size(); i++) {
            check.add(0);
        }
    }
}
