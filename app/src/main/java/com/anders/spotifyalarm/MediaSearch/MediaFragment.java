package com.anders.spotifyalarm.MediaSearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anders.spotifyalarm.MediaSearch.playlistSearch.SpotifyPlaylistActivity;
import com.anders.spotifyalarm.MediaSearch.songSearch.SearchFragment;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class MediaFragment extends Fragment implements
        SearchFragment.ReturnSongsListener {

    public static String EXTRA_TOKEN;
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    public static FrameLayout mThisPageLayoutContainer;
    FrameLayout mSongFragment;
    EditText mPlaylistTitle;
    Context mContext;
    static int mAlarmId;
    MasterSingleton mSingleton;
    DBhelper mDBHelper;
    Button mPlaylists, mBackButton, mSetTitle, mCollapse;
    LinearLayout mSpotifyPlaylist, mFindMoreSongs;
    public SongAdapter mSongAdapter;
    public PlaylistAdapter mplaylistAdapter;
    ArrayList<SongObject> mSongArray;
    SearchFragment searchFragment;
    FrameLayout mAlbumEditFragment, mSpotifyPlaylistFrame;
    Animation mButtonAnimator;
    private static FrameLayout mFrameLayout;
    Handler handler;


    private OnFragmentInteractionListener mListener;

    public MediaFragment() {
        // Required empty public constructor
    }

    public static MediaFragment newInstance(Bundle bundle, FrameLayout layout, String token, int alarm_id) {
        MediaFragment fragment = new MediaFragment();
        EXTRA_TOKEN = token;
        fragment.setArguments(bundle);
        mAlarmId = alarm_id;
        mThisPageLayoutContainer = layout;
        mFrameLayout = layout;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleton = MasterSingleton.getmInstance();
        mDBHelper = DBhelper.getmInstance(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        mSpotifyPlaylistFrame = (FrameLayout) view.findViewById(R.id.spotify_list_frag);
        handler = new Handler();
        Thread runnable = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    public void run() {
                        mFrameLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        runnable.start();

        mButtonAnimator = AnimationUtils.loadAnimation(mContext, R.anim.button_anim);

        mFindMoreSongs = (LinearLayout) view.findViewById(R.id.toSongFragment);

        mPlaylistTitle = (EditText) view.findViewById(R.id.playlist_title_edit_text);
        mPlaylistTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int after) {
                for (int i = 0; i < mSingleton.getSongObjArray().size(); i++) {
                    mSingleton.getSongObjArray().get(i).setPlaylist(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSongArray = new ArrayList<>();
        if (mSingleton.getSongObjArray().size() > 0) {
            mSongArray = mSingleton.getSongObjArray();
            if (mSingleton.getSongObjArray().get(0).getPlaylist() != null) {
                if (mSingleton.getSongObjArray().get(0).getPlaylist().length() > 0) {
                    mPlaylistTitle.setText(mSingleton.getSongObjArray().get(0).getPlaylist());
                } else {
//                    Log.i(TAG, "onCreateView: mSingleton.getSongObjArray().get(0).getPlaylist() = " + mSingleton.getSongObjArray().get(0).getPlaylist());
                }

            } else {
//                Log.i(TAG, "onCreateView: mSingleton.getSongObjArray().get(0).getPlaylist() = " + mSingleton.getSongObjArray().get(0).getPlaylist());
            }
//            Log.i(TAG, "onCreateView: singleton = " + mSongArray.size());
        } else {
            mSongArray = mDBHelper.getSongs(mAlarmId);
            mSingleton.setSongObjectArray(mSongArray);
//            Log.i(TAG, "onCreateView: database = " + mSongArray.size());
            if (mSongArray.size() > 0) {
                if (mSongArray.get(0).getPlaylist().length() > 0) {
                    mPlaylistTitle.setText(mSongArray.get(0).getPlaylist());
                }
            }
        }


        mSetTitle = (Button) view.findViewById(R.id.set_title);
        mSetTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < mSingleton.getSongObjArray().size(); i++) {
                    mSingleton.getSongObjArray().get(i).setPlaylist(mPlaylistTitle.getText().toString());
                }
                mPlaylistTitle.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
                view.clearFocus();
            }
        });

        mSongArray = mDBHelper.getSongs(mAlarmId);


//        RECYCLER VIEW  FOR SONGS
        RecyclerView songsRecyclerView = (RecyclerView) view.findViewById(R.id.songs);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        songsRecyclerView.setLayoutManager(linearLayout1);
        mSongAdapter = new SongAdapter(mContext, mSongArray);
        songsRecyclerView.setAdapter(mSongAdapter);


        mSpotifyPlaylist = (LinearLayout) view.findViewById(R.id.spotify_playlists);
        mSpotifyPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyPlaylistFrame.setVisibility(View.VISIBLE);
                SpotifyPlaylistActivity spPlFrag = new SpotifyPlaylistActivity(mSpotifyPlaylistFrame, new SpotifyPlaylistActivity.ReturnPlaylistToMedia() {
                    @Override
                    public void returnPlaylist(ArrayList<SongObject> array) {
//                        Log.i(TAG, "returnPlaylist: " + array.size());
                        mSongAdapter.updateList(array);
                    }
                });
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);

//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_left);
                fragmentTransaction
                        .replace(R.id.spotify_list_frag, spPlFrag)
                        .addToBackStack("spotifyPlaylistFrag").commit();
            }
        });


//        RECYCLER VIEW PLAYLISTS
        ArrayList<ArrayList<SongObject>> arrayListArrayList = mDBHelper.getAllHistPlaylists();

        mAlbumEditFragment = (FrameLayout) view.findViewById(R.id.change_album_fragment);
        RecyclerView playlistsRecyclerView = (RecyclerView) view.findViewById(R.id.playlists);
        LinearLayoutManager linearLayout2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        playlistsRecyclerView.setLayoutManager(linearLayout2);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mplaylistAdapter = new PlaylistAdapter(mAlbumEditFragment, arrayListArrayList, mContext, fragmentManager,
                new PlaylistAdapter.SetCurrPlaylistListener() {
                    @Override
                    public void onCurrentPlaylistSelected(ArrayList<SongObject> playlist) {
//                         this is only to set a saved playlist to current alarm list
                        mSongAdapter.updateList(playlist);
                        if (playlist.size() > 0) {
                            mPlaylistTitle.setText(playlist.get(0).getPlaylist());
                        }
                    }
                });

        playlistsRecyclerView.setAdapter(mplaylistAdapter);


//      to go back to activity
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackButton.setAnimation(mButtonAnimator);
                if (mSongAdapter.getItemCount() > 0) {
                    if (!mPlaylistTitle.getText().toString().equals("null")
                            && !mPlaylistTitle.getText().toString().equals("")
                            && !mPlaylistTitle.getText().toString().equals(" ")) {

                        for (int i = 0; i < mSingleton.getSongObjArray().size(); i++) {
                            mSingleton.getSongObjArray().get(i).setPlaylist(mPlaylistTitle.getText().toString());
                        }

//                    Log.i(TAG, "onClick: mPlaylistTitle " + mPlaylistTitle.getText().toString());
                        mThisPageLayoutContainer.setVisibility(View.GONE);
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(mContext, "Please Add unique Title To Your Playlist", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mThisPageLayoutContainer.setVisibility(View.GONE);
                    getActivity().getSupportFragmentManager()
                            .popBackStack();
                }
            }
        });


//      to start song search fragment
        mSongFragment = (FrameLayout) view.findViewById(R.id.song_fragment);
        ArrayList<SongObject> array = new ArrayList<>();
        searchFragment = SearchFragment.getInstance(array);
        searchFragment.setReturnSongsListener(new SearchFragment.ReturnSongsListener() {
            @Override
            public void returnSongsMethod(ArrayList<SongObject> arrayList) {
                mSongFragment.setVisibility(View.GONE);
                mSongAdapter.updateList(arrayList);
                Log.i(TAG, "returnSongsMethod: listener");
            }
        });

        mFindMoreSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFindMoreSongs.setAnimation(mButtonAnimator);
                Log.i(TAG, "onClick: mFindMoreSongs");
                mSongFragment.setVisibility(View.VISIBLE);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.song_fragment, searchFragment)
                        .addToBackStack("song_fragment").commit();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updateSongList(ArrayList<SongObject> arrayList) {
        mSongAdapter.updateList(arrayList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateSongArray(ArrayList<SongObject> arrayList) {
        mSongAdapter.updateList(arrayList);
    }

    @Override
    public void returnSongsMethod(ArrayList<SongObject> arrayList) {
        mSongAdapter.updateList(arrayList);
        Log.i(TAG, "returnSongsMethod: ");
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isVisible()) {
            Log.i(TAG, "onHiddenChanged: mSingleton.getSong_uri_string() " + mSingleton.getSongObjArray());
            mSongAdapter.updateList(mSingleton.getSongObjArray());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        ArrayList<ArrayList<SongObject>> arrayListArrayList = mDBHelper.getAllHistPlaylists();
//        mplaylistAdapter.updateHistPlaylists(arrayListArrayList);
        if (mSingleton.getSongObjArray().size() > 0) {
            if (mSingleton.getSongObjArray().get(0).getPlaylist() != null) {
                if (mSingleton.getSongObjArray().get(0).getPlaylist().length() > 0) {
                    mPlaylistTitle.setText(mSingleton.getSongObjArray().get(0).getPlaylist());
                }
            }

            Log.i(TAG, "onResume: mSingleton.getSong_uri_string() " + mSingleton.getSongObjArray());
            mSongAdapter.updateList(mSingleton.getSongObjArray());
        }
//        else if (arrayListArrayList.size() > 0) {
////            mplaylistAdapter.updateHistPlaylists(arrayListArrayList);
//        }

    }

}
