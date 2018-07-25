package com.anders.spotifyalarm.MediaSearch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anders.spotifyalarm.MediaSearch.playlistSearch.SpotifyPlaylistActivity;
import com.anders.spotifyalarm.MediaSearch.songSearch.SearchFragment;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.DBhelper;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class MediaFragment extends Fragment implements
        SearchFragment.ReturnSongsListener, Player.NotificationCallback, ConnectionStateCallback {


    Context mContext;

    public static String EXTRA_TOKEN;
    public SongAdapter mSongAdapter;
    public PlaylistAdapter mplaylistAdapter;

    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    private static FrameLayout mFrameLayout;
    private PlaybackState mCurrentPlaybackState;

    static int mAlarmId;
    static FrameLayout mThisPageLayoutContainer;

    Boolean mPaused;

    Animation mButtonAnimator;
    Button mPlaylists, mBackButton, mSetTitle, mCollapse;
    FrameLayout mCreationFragment;
    FrameLayout mAlbumEditFragment, mUserSpotifyPlaylistFrame, mHistorcalFrag;
    FrameLayout mPlaylistHist;
    FrameLayout mUserSpotifyPlaylists;
    FrameLayout mCreateANewPlaylist;
    ImageView mPlaylistImage;
    ImageView mSongState;
    ImageView mScrollBufferImage;
    LinearLayout mRVContainer;
    TextView mPlaylistTitleTxtv;
    TextView mEditPlaylist;

    HistoryFragment mHistoricalPlaylistFrag;
    SearchFragment searchFragment;

    ArrayList<SongObject> mSongArray;
    DBhelper mDBHelper;
    Handler handler;
    MasterSingleton mSingleton;


// ---------------------------------------------------
//         Playback Class Variables
// ---------------------------------------------------

    SpotifyPlayer mCurrentPlaylistPlayer;
    DBhelper mDatabaseHelper;
    private static final int REQUEST_CODE = 1337;
    public static final String REDIRECT_URL = "http://localhost:8888/callback";
    ArrayList<SongObject> songList;
    Ringtone currentRingtone;
    Thread read;
    AudioManager mAudioManager;
    Config mPlayerConfig;


// ---------------------------------------------------
//         Start of Fragment
// ---------------------------------------------------

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
//        show view

        mPaused = false;
        mEditPlaylist = (TextView) view.findViewById(R.id.edit_curr_playlist);
        mScrollBufferImage = (ImageView) view.findViewById(R.id.image_current_playlist_dummy);
        mPlaylistTitleTxtv = (TextView) view.findViewById(R.id.playlist_title);
        mPlaylistImage = (ImageView) view.findViewById(R.id.image_current_playlist);
        mButtonAnimator = AnimationUtils.loadAnimation(mContext, R.anim.button_anim);
        mUserSpotifyPlaylistFrame = (FrameLayout) view.findViewById(R.id.spotify_list_frag);

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
                        if (mFrameLayout != null) {
                            mFrameLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        runnable.start();

        int height = mPlaylistImage.getDrawable().getIntrinsicHeight();
        int width = mPlaylistImage.getDrawable().getIntrinsicWidth();
        if (height == 0){
            height = 1500;
        } else {}
        Log.i(TAG, "onCreateView: height singleton " + height);
        mScrollBufferImage.setMinimumHeight(height);
        mScrollBufferImage.setMinimumWidth(width);
        /* ---------------------------------------------------
         ON FRAGMENT VIEW CREATION THE IMAGE AND TITLE ARE SET
           check if Singleton has playlists,
            if not then find songs from the database,
            if not there are none
                do not set text,
                do not set image
        --------------------------------------------------- */
        Log.i(TAG, "mSingleton.getPlaylistTitle() " + mSingleton.getPlaylistTitle());
        Log.i(TAG, "mSingleton.getSongObjArray().size() " + mSingleton.getSongObjArray().size());
        Log.i(TAG, "mDBHelper.getSongs(mAlarmId).size() " + mDBHelper.getSongs(mAlarmId).size() + " " + mAlarmId);
        if (!mSingleton.getPlaylistTitle().equals("") && mSingleton.getSongObjArray().size() > 0) {
            mSongArray = mSingleton.getSongObjArray();
            String tempTitle = mSingleton.getPlaylistTitle();
            mPlaylistTitleTxtv.setText(tempTitle);
            Picasso.with(mContext)
                    .load(mSingleton.getSongObjArray().get(0).getPhotoUri())
                    .into(mPlaylistImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            int height = mPlaylistImage.getDrawable().getIntrinsicHeight();
                            int width = mPlaylistImage.getDrawable().getIntrinsicWidth();
                            Log.i(TAG, "onCreateView: height singleton " + height);
//                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
//                            mScrollBuff.setLayoutParams(parms);
                            mScrollBufferImage.setMinimumHeight(height);
                            mScrollBufferImage.setMinimumWidth(width);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            mSongArray = new ArrayList<>();
            mSongArray = mDBHelper.getSongs(mAlarmId);
            if (mSongArray.size() > 0) {
                mPlaylistTitleTxtv.setText(mSongArray.get(0).getPlaylist());
                Picasso.with(mContext)
                        .load(mSongArray.get(0).getPhotoUri())
                        .into(mPlaylistImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                int height = mPlaylistImage.getDrawable().getIntrinsicHeight();
                                int width = mPlaylistImage.getDrawable().getIntrinsicWidth();;
                                Log.i(TAG, "onCreateView: height db " + height);
//                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
//                                mScrollBuff.setLayoutParams(parms);
                                mScrollBufferImage.setMinimumHeight(height);
                                mScrollBufferImage.setMinimumWidth(width);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        }

// ---------------------------------------------------
//         Look at Spotify Playlist
// ---------------------------------------------------

        mUserSpotifyPlaylists = (FrameLayout) view.findViewById(R.id.spotify_playlists);
        mUserSpotifyPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserSpotifyPlaylistFrame.setVisibility(View.VISIBLE);
                SpotifyPlaylistActivity spPlFrag = new SpotifyPlaylistActivity(mUserSpotifyPlaylistFrame, new SpotifyPlaylistActivity.ReturnPlaylistToMedia() {
                    @Override
                    public void returnPlaylist(ArrayList<SongObject> array) {
                        // ---------------------------------------------------
                        //         set array, title & image
                        // ---------------------------------------------------
                        mSingleton.setPlaylistTitle(array.get(0).getPlaylist());
                        mSingleton.setSongObjectArray(array);
                        mPlaylistTitleTxtv.setText(array.get(0).getPlaylist());
                        Picasso.with(mContext)
                                .load(array.get(0).getPhotoUri())
                                .into(mPlaylistImage);
                    }
                });
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction
                        .replace(R.id.spotify_list_frag, spPlFrag)
                        .addToBackStack("spotifyPlaylistFrag").commit();
            }
        });

        mSongState = (ImageView) view.findViewById(R.id.song_state);
        LinearLayout playSongsButton = (LinearLayout) view.findViewById(R.id.play_current_playlist_button);
        playSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable pause = ResourcesCompat.getDrawable(getResources(), R.drawable.stop, null);
                Drawable play = ResourcesCompat.getDrawable(getResources(), R.drawable.play, null);
                mSongState.setImageResource(0);
                if (mSongState != null && pause != null && play != null) {
                    if (mSongState.getTag().toString().equals("play")) {
                        playMusic();
                        Picasso.with(mContext)
                                .load(R.drawable.stop)
                                .into(mSongState);
                        mSongState.setTag("pause");
                    } else {
                        if (mCurrentPlaylistPlayer != null) {
                            mCurrentPlaylistPlayer.pause(null);
                        } else {}
                        mPaused = true;
                        Picasso.with(mContext)
                                .load(R.drawable.play)
                                .into(mSongState);
                        mCurrentPlaylistPlayer = null;
                        mSongState.setTag("play");
                    }
                }
            }
        });


// ---------------------------------------------------
//         Playlist History on click
// ---------------------------------------------------

        mPlaylistHist = (FrameLayout) view.findViewById(R.id.open_historical_song_fragment);
        mHistorcalFrag = (FrameLayout) view.findViewById(R.id.to_hist_playlist_frag);
        mHistoricalPlaylistFrag = HistoryFragment.newInstance(mContext, mHistorcalFrag);
//        with only one instance ever created
        mPlaylistHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistorcalFrag.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                ft.replace(R.id.to_hist_playlist_frag, mHistoricalPlaylistFrag)
                        .addToBackStack(null).commit();
            }
        });

// ---------------------------------------------------
//         back button to newAlarm activity
// ---------------------------------------------------

        mBackButton = (Button) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackButton.setAnimation(mButtonAnimator);
                mThisPageLayoutContainer.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager()
                        .popBackStack("media_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });


// ---------------------------------------------------
//         set up playlist create fragment
// ---------------------------------------------------

        mCreationFragment = (FrameLayout) view.findViewById(R.id.creation_fragment);
        ArrayList<SongObject> emptyArray = new ArrayList<>();
        searchFragment = SearchFragment.getInstance(emptyArray);
        searchFragment.setReturnSongsListener(new SearchFragment.ReturnSongsListener() {
            @Override
            public void returnSongsMethod(ArrayList<SongObject> arrayList) {
                mCreationFragment.setVisibility(View.GONE);
                if (arrayList.size() > 0) {
                    mPlaylistTitleTxtv.setText(arrayList.get(0).getPlaylist());
                } else {
                }
//                mSongAdapter.updateList(arrayList);
                if (arrayList.size() > 0) {
                    mPlaylistTitleTxtv.setText(arrayList.get(0).getPlaylist());
                } else {}
//                mDBHelper.insertHistPlaylists(arrayList);
                Log.i(TAG, "returnSongsMethod: listener");
            }
        });

// --------------------------------------------------------------
//         set up playlist create fragment on click listener
// --------------------------------------------------------------

        mCreateANewPlaylist = (FrameLayout) view.findViewById(R.id.to_creation_fragment);
        mCreateANewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCreateANewPlaylist.setAnimation(mButtonAnimator);
                mCreationFragment.setVisibility(View.VISIBLE);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.creation_fragment, searchFragment)
                        .addToBackStack("song_fragment").commit();
            }
        });

        mEditPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPlaylist.setAnimation(mButtonAnimator);
                mCreationFragment.setVisibility(View.VISIBLE);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.creation_fragment, searchFragment)
                        .addToBackStack("song_fragment").commit();
            }
        });
        return view;
    }


// ---------------------------------------------------
//         general methods
// ---------------------------------------------------

    @Override
    public void returnSongsMethod(ArrayList<SongObject> arrayList) {
        mSongAdapter.updateList(arrayList);
    }


// ---------------------------------------------------
//         spotify play states methods
// ---------------------------------------------------

    public void playMusic() {
        songList = mSingleton.getSongObjArray();
        mCurrentPlaylistPlayer = mSingleton.getmCurrentPlaylistPlayer();
        Log.i(TAG, "onInitialized: songList size a = " + songList.size());
        if (songList.size() > 0) {
            read = new Thread() {
                @Override
                public void run() {
                    Log.i(TAG, "run: a");
                    for (int k = 0; k < 20; k++) {
                        Log.i(TAG, "run: k = " + k);
                        if (songList.size() == 1) {
                            if (mCurrentPlaylistPlayer != null) {
                                try {
                                    if (songList.get(0).getDuration() > 0) {
                                        mCurrentPlaylistPlayer.playUri(null, songList.get(0).getUri(), 0, 0);
                                        sleep(songList.get(0).getDuration() + 3000);
                                        Log.i(TAG, "run: song event duration index 1 ");
                                    } else {
                                        mCurrentPlaylistPlayer.playUri(null, songList.get(0).getUri(), 0, 0);
                                        Log.i(TAG, "run: song event break ");
                                        break;
                                    }
                                    if (mCurrentPlaylistPlayer != null) {
                                        mCurrentPlaylistPlayer.onAudioFlush();
                                    } else {
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.i(TAG, "run: song event k != 0, k = " + k);
                            for (int j = 0; j < songList.size(); j++) {
                                if (j != 0) {
                                    if (mCurrentPlaylistPlayer != null) {
                                        try {
                                            sleep(songList.get((j - 1)).getDuration() + 3000);
                                            if (mCurrentPlaylistPlayer != null) {
                                                mCurrentPlaylistPlayer.onAudioFlush();
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                if (mCurrentPlaylistPlayer != null) {
                                    mCurrentPlaylistPlayer.playUri(null, songList.get(j).getUri(), 0, 0);
                                }
//
                                if (j == (songList.size() - 1)) {
                                    Log.i(TAG, "run: singing after");
                                    try {
                                        Log.i(TAG, "run: singing about to sleep before cycle restarts");
                                        sleep(songList.get((j)).getDuration() + 3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        Log.i(TAG, "run: ====================================================== error sleeping ============================================================");
                                    }
                                }
                            }
                        }
                    }
                }
            };
            read.start();
        } else {
            if (mSingleton.getmAuthToken() != null && !mSingleton.getmAuthToken().equals("")) {
                Log.i(TAG, "setToken: 1");
                mPlayerConfig = new Config(mContext, mSingleton.getmAuthToken(), "8245c9c6491c426cbccf670997c14766");
                mCurrentPlaylistPlayer = Spotify.getPlayer(mPlayerConfig, this, new SpotifyPlayer.InitializationObserver() {
                            @Override
                            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                                mSingleton.setmCurrentPlaylistPlayer(spotifyPlayer);
                                playMusic();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.i(TAG, "AlertDialogActivity: Could not initialize player: " + throwable.getMessage());
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {

    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }


// ---------------------------------------------------
//         lifecycle methods
// ---------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mSongAdapter.updateList(mSingleton.getSongObjArray());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        /* ---------------------------------------------------
           check if Singleton has playlists,
            if not then find songs from the database,
            if not there are none
                set text
                set image
        --------------------------------------------------- */

        Log.i(TAG, "onResume: ");
        if (!mSingleton.getPlaylistTitle().equals("") && mSingleton.getSongObjArray().size() > 0) {
            mSongArray = mSingleton.getSongObjArray();
            String tempTitle = mSingleton.getPlaylistTitle();

            View view = getView();
            if (view != null) {
                if (mPlaylistImage == null) {
                    mPlaylistImage = (ImageView) view.findViewById(R.id.image_current_playlist);
                    int height = mPlaylistImage.getDrawable().getIntrinsicHeight();
                    int width = mPlaylistImage.getDrawable().getIntrinsicWidth();
                    if (height == 0){
                        height = 1500;
                    } else {}

                    mScrollBufferImage.setMinimumHeight(height);
                    mScrollBufferImage.setMinimumWidth(width);

                    mPlaylistTitleTxtv.setText(tempTitle);

                    Picasso.with(mContext)
                            .load(mSingleton.getSongObjArray().get(0).getPhotoUri())
                            .into(mPlaylistImage);

                    Log.i(TAG, "onResume: !mSingleton.getPlaylistTitle().equals('') height " + height);
                }
            }

        } else {
            mSongArray = new ArrayList<>();
            mSongArray = mDBHelper.getSongs(mAlarmId);
            mSingleton.setSongObjectArray(mSongArray);
            if (mSongArray.size() > 0) {
                if (mSongArray.get(0).getPlaylist().length() > 0) {
                    mPlaylistTitleTxtv.setText(mSongArray.get(0).getPlaylist());
                    Picasso.with(mContext)
                            .load(mSongArray.get(0).getPhotoUri())
                            .into(mPlaylistImage);

                    int height = mPlaylistImage.getDrawable().getIntrinsicHeight();
                    int width = mPlaylistImage.getDrawable().getIntrinsicWidth();
                    if (height == 0){
                        height = 1500;
                    } else {}

                    mScrollBufferImage.setMinimumHeight(height);
                    mScrollBufferImage.setMinimumWidth(width);
                    Log.i(TAG, "onResume:  mSongArray.size() > 0 height " + height);
                }
            }
        }
    }

}
