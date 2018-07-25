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
import android.widget.TextView;

import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.PhotoTransformation;
import com.anders.spotifyalarm.R;
import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

import static android.content.ContentValues.TAG;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Track> mItems = new ArrayList<>();
    private Context mContext;
    private PlaySearchListener mListener;
    DBhelper mDBhelper;
    MasterSingleton mSingleton;
    Track mCurrentTrack;


    public SearchAdapter(Context context, MasterSingleton singleton, DBhelper db, int alarm_id, PlaySearchListener listener) {
        mContext = context;
        mSingleton = singleton;
        mListener = listener;
        mItems = new ArrayList<>();
        mDBhelper = db;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        SearchViewHolder svh = new SearchViewHolder(view);
        Log.i(TAG, "search: onCreateViewHolder: ");
        return svh;
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {

        Log.i(TAG, "search: onBindViewHolder " + mItems.size());
        mCurrentTrack = mItems.get(position);

        holder.title.setText(mCurrentTrack.name);
        holder.subtitle.setText(mCurrentTrack.artists.get(0).name);

        Log.i(TAG, "onBindViewHolder: album name " + mCurrentTrack.album.name);
        String url = mCurrentTrack.album.images.get(0).url;
        Picasso.with(mContext).load(url)
                .centerCrop()
                .fit()
                .transform(new PhotoTransformation(50, 4))
                .into(holder.mAlbum_photo);
    }


    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        clearData();
        mItems.addAll(items);
        notifyDataSetChanged();
        Log.i(TAG, "addData: in search adapter " + mItems.size() );
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView subtitle;
        public ImageView mAlbum_photo, mAddRemove;
        RippleView mLayout;

        //        public final ImageView image;
        public SearchViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mLayout = (RippleView) itemView.findViewById(R.id.layout);
            mAlbum_photo = (ImageView) itemView.findViewById(R.id.album_photo);
            mAddRemove = (ImageView) itemView.findViewById(R.id.addRemovebutton);
            mLayout.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
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
        }
    }

    public interface PlaySearchListener {
        void onItemSelected(View itemView, Track item, SongObject object);
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
