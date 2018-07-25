package com.anders.spotifyalarm.MediaSearch.songSearch;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.MediaSearch.SongAdapter;
import com.anders.spotifyalarm.MediaSearch.songSearch.TouchHelper.TouchCallback;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.anders.spotifyalarm.R;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

import static android.content.ContentValues.TAG;

public class SearchFragment extends Fragment implements Search.View {

    Context mContext;
    DBhelper mDB;

    static String EXTRA_TOKEN;
    static FrameLayout mFrameLayout;
    static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    static int mAlarmId;
    static ArrayList<SongObject> mHistoricalPlaylist;
    String mPlaylistTitleString;

    SearchAdapter mSearchAdapter;
    SongAdapter mSongAdapter;

    Search.ActionListener mActionListener;
    ReturnPlaylistListener mReturnPlaylistListener;
    ReturnSongsListener mReturnSongsListener;

    ImageView mShowButton;
    EditText mPlaylistTitle;
    FrameLayout mOverlappingSearch;
    ImageView mBackButton, mSearchButton;
    MasterSingleton mSingleton;
    SearchView mSearchView;
    TextView mTitle;
    LinearLayout mRVContainer;




    public SearchFragment() {
    }

    public static SearchFragment getInstance(ArrayList<SongObject> arrayList) {
        SearchFragment mSearchFrag = new SearchFragment();
        mHistoricalPlaylist = arrayList;
        Log.i(TAG, "SearchFragment: playlistArray secondary input " + arrayList.size());
        return mSearchFrag;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, SearchFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_view, container, false);

        mDB = DBhelper.getmInstance(mContext);
        mSingleton = MasterSingleton.getmInstance();


        mRVContainer = (LinearLayout) view.findViewById(R.id.rv_container_layout);
        mBackButton = (ImageView) view.findViewById(R.id.back_button);
        mSearchButton = (ImageView) view.findViewById(R.id.search_button);
        mShowButton = (ImageView) view.findViewById(R.id.back_button_clear_results);
        mOverlappingSearch = (FrameLayout) view.findViewById(R.id.overlapping_search_view);

        mActionListener = new SearchPresenter(mContext, this);
        mActionListener.init(EXTRA_TOKEN);

        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOverlappingSearch.setVisibility(View.GONE);
            }
        });


// ---------------------------------------------------
//         set title to new playlist
// ---------------------------------------------------

        mPlaylistTitle = (EditText) view.findViewById(R.id.new_list_title_edt);
        mPlaylistTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int after) {
//                mPlaylistTitle.setText(charSequence.toString());
                mPlaylistTitleString = charSequence.toString();
                Log.i(TAG, "onTextChanged: " + start);
                Log.i(TAG, "onTextChanged: " + i1);
                Log.i(TAG, "onTextChanged: " + after);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

// ---------------------------------------------------
//         back button methods
// ---------------------------------------------------

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < mSingleton.getSongObjArray().size(); i++) {
                    mSingleton.getSongObjArray().get(i).setPlaylist(mPlaylistTitleString);
                }

                if (mReturnSongsListener != null) {
                    mReturnSongsListener.returnSongsMethod(mSingleton.getSongObjArray());
                } else {}

                for (int j = 0; j < mSingleton.getSongObjArray().size(); j++) {
                    mSingleton.getSongObjArray().get(j).setPlaylist(mPlaylistTitle.getText().toString());
                }

                if (mReturnPlaylistListener != null) {
                    mReturnPlaylistListener.returnPlaylistListener(mSingleton.getSongObjArray());
                    for (int i = 0; i < mSingleton.getSongObjArray().size(); i ++){}
                } else {}
                getActivity().getSupportFragmentManager().popBackStack();
//                InputMethodManager imm = InputMethodManager(getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(
//                        FOCUSABLE_VIEW.getWindowToken(), 0);
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPlaylistTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });


        mRVContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: close ");
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPlaylistTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });



// ---------------------------------------------------
//         display selected songs set up
// ---------------------------------------------------

        RecyclerView songsRecyclerView = (RecyclerView) view.findViewById(R.id.current_list);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        songsRecyclerView.setLayoutManager(linearLayout1);
        mSongAdapter = new SongAdapter(mContext, mSingleton.getSongObjArray());
        songsRecyclerView.setAdapter(mSongAdapter);

        ItemTouchHelper.Callback callback = new TouchCallback(mSongAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(songsRecyclerView);


// ---------------------------------------------------
//         song search set up
// ---------------------------------------------------

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOverlappingSearch.setVisibility(View.VISIBLE);
            }
        });

        // ---------------------------------------------------
        //         song search, search bar methods
        // ---------------------------------------------------
        mSearchView = (SearchView) view.findViewById(R.id.search_bar);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mActionListener.search(query);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // ---------------------------------------------------
        //         song search recycler view
        // ---------------------------------------------------
        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.search_results_songs);
        resultsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mSearchAdapter = new SearchAdapter(mContext, mSingleton, mDB, mAlarmId, new SearchAdapter.PlaySearchListener() {
            @Override
            public void onItemSelected(View itemView, Track item, SongObject object) {
                mActionListener.selectTrack(item);
                mSingleton.addSong(object);
                mSongAdapter.updateList(mSingleton.getSongObjArray());
                Log.i(TAG, "onItemSelected: " + object.getTitle());
                mSongAdapter.notifyDataSetChanged();
            }
        });
        resultsList.setAdapter(mSearchAdapter);

        // ---------------------------------------------------
        // If Activity was recreated with active search, restore it
        // ---------------------------------------------------
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }


        return view;
    }


// ---------------------------------------------------
//         other methods
// ---------------------------------------------------

    @Override
    public void reset() {
        mSearchAdapter.clearData();
    }

    @Override
    public void addData(List<Track> items) {
        mSearchAdapter.addData(items);
        Log.i(TAG, "search: addData " + items.size());
    }

    public interface ReturnPlaylistListener {
        void returnPlaylistListener(ArrayList<SongObject> arrayList);
    }

    public void setReturnPlaylistList(ReturnPlaylistListener listener) {
        mReturnPlaylistListener = listener;
    }


    public interface ReturnSongsListener {
        void returnSongsMethod(ArrayList<SongObject> arrayList);
    }

    public void setReturnSongsListener(ReturnSongsListener listener) {
        mReturnSongsListener = listener;
    }


// ---------------------------------------------------
//         life cycle
// ---------------------------------------------------

    @Override
    public void onAttach(Context context) {
        mContext = context;
        if (mSingleton == null) {
            mSingleton = MasterSingleton.getmInstance();
        } else {
        }
        super.onAttach(context);
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