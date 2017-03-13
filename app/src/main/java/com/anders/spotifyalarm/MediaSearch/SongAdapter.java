package com.anders.spotifyalarm.MediaSearch;

import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.UiAids.PhotoTransformation;
import com.anders.spotifyalarm.UiAids.SwipeInterface;
import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

/**
 * Created by anders on 2/17/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements SwipeInterface{

    private static final String TAG = "SongAdapter";
    ArrayList<SongObject> mItems;
    Context mContext;
    Integer mPosition;
    DBhelper mDBHelper;
    MasterSingleton mMasterSingleton;
//    String[] sufixes = new String[] {"st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

    public SongAdapter(Context context, ArrayList<SongObject> arrayList) {
        mContext = context;
        mItems = arrayList;
        mDBHelper = DBhelper.getmInstance(mContext);
        mMasterSingleton = MasterSingleton.getmInstance();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle, mListNumber;
        public ImageView mAlbum_photo;
        RippleView mLayout;

        public SongViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.song_title);
            mListNumber = (TextView) itemView.findViewById(R.id.list_number);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mLayout = (RippleView) itemView.findViewById(R.id.layout);
            mAlbum_photo = (ImageView) itemView.findViewById(R.id.album_photo);
        }
    }


    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_rec_view_layout, parent, false);
        SongViewHolder songViewHolder = new SongViewHolder(view);
        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.title.setText(mItems.get(position).getTitle());
        holder.subtitle.setText(mItems.get(position).getArtist());
        String ordinal = returnOrdinal((position + 1));
        String temp = (position + 1) + "" + ordinal;
        holder.mListNumber.setText(temp);
//        Picasso.with(mContext).load(mItems.get(mPosition).getPhotoUri());
        Picasso.with(mContext).load(mItems.get(position).getPhotoUri())
                .centerCrop()
                .fit()
                .transform(new PhotoTransformation(50, 4))
                .into(holder.mAlbum_photo);
    }

    @Override
    public int getItemCount() {
        if (mItems != null && mItems.size() > 0) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems,fromPosition,toPosition);
        for (int i = 0; i < mItems.size(); i ++){
            Log.i(TAG, "onItemMove: " + mItems.get(i).getTitle());
        }
        notifyItemMoved(fromPosition,toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        String uri = mItems.get(position).getUri();
        String title = mItems.get(position).getPlaylist();
        Log.i(TAG, "onItemDismiss: " + uri);
        mDBHelper.deleteSpecificId(uri, title);
        mItems.remove(position);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    public void onUpdateItems() {
        Log.i(TAG, "onUpdateItems: ");
        updateList(mItems);
    }

    public void updateList(ArrayList<SongObject> arrayList) {
        Log.i(TAG, "updateList: ");
        mMasterSingleton.setSongObjectArray(arrayList);

        Handler handler = new Handler();
        mItems = arrayList;
        Log.i(TAG, "updateList: " + arrayList.size());
        Runnable r = new Runnable() {
            public void run() {
                notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    public String returnOrdinal(int i){
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 1:
                return "st";
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return sufixes[i % 10];

        }
    }
}
