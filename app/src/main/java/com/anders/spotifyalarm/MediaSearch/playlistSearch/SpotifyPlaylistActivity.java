package com.anders.spotifyalarm.MediaSearch.playlistSearch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.anders.spotifyalarm.R;
import com.anders.spotifyalarm.SingAndDB.MasterSingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import static android.content.ContentValues.TAG;

public class SpotifyPlaylistActivity extends Fragment {

    MasterSingleton mMasterSingleton;
    String mAuthToken;
    RequestQueue queue;
    Button mBack;
    Context mContext;
    FrameLayout mFrame;
    SpotifyPlaylistAdapter mSpotifyPlaylistAdapter;
    ArrayList<PlaylistObject> mPlaylists;
    ReturnPlaylistToMedia mReturnPlayistToMedia;

    public SpotifyPlaylistActivity(){

    }


    private static final int REQUEST_CODE = 1337;
    SpotifyService mSpotify;
    public static final String REDIRECT_URL = "http://localhost:8888/callback";

    public SpotifyPlaylistActivity(FrameLayout frameLayout, ReturnPlaylistToMedia listener){
        mFrame = frameLayout;
        mReturnPlayistToMedia = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_spotify_playlist, container, false);

        Log.i(TAG, "SpotifyPlaylistActivity onCreateView: ");
        mPlaylists = new ArrayList<>();
        mContext = getContext();

        mBack = (Button) view.findViewById(R.id.back_button);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                mFrame.setVisibility(View.GONE);
            }
        });

        mMasterSingleton = MasterSingleton.getmInstance();

        String userId = mMasterSingleton.getUserId();
        mAuthToken = mMasterSingleton.getmAuthToken();
        Log.i(TAG, "SpotifyPlaylistActivity onCreateView: " + mAuthToken);
        Log.i(TAG, "SpotifyPlaylistActivity onCreateView: " + userId);
        if (mMasterSingleton.getUserId().equals("0") || mMasterSingleton.getmAuthToken() == null) {
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(
                            "8245c9c6491c426cbccf670997c14766",
                            AuthenticationResponse.Type.TOKEN,
                            REDIRECT_URL);


            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
        } else {
            sessionPolling(userId);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.spotify_playlist_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mSpotifyPlaylistAdapter = new SpotifyPlaylistAdapter(mContext, new SpotifyPlaylistAdapter.ReturnSpotifyPlaylist() {
            @Override
            public void returnPlaylistListener(PlaylistObject obj) {
                SongObject objectTemp = new SongObject();
                objectTemp.setPhotoUri(obj.getUrl());
                objectTemp.setUri(obj.getUri());
                objectTemp.setArtist("Spotify");
                objectTemp.setPlaylist(obj.getName() + " Playlist");
                objectTemp.setTitle(obj.getName());
                ArrayList<SongObject> arrayListTemp = new ArrayList<>();
                arrayListTemp.add(objectTemp);
                mReturnPlayistToMedia.returnPlaylist(arrayListTemp);
                Log.i(TAG, "returnPlaylistListener: " + arrayListTemp.size());
            }
        });
        recyclerView.setAdapter(mSpotifyPlaylistAdapter);

        return view;
    }

    public void sessionPolling(String user_id) {
//        Log.i(TAG, "onResponse: jnum2 ");
//        Log.i(TAG, "doInBackground: inside doInBackground with key: " + mSessionID + "?" + mApiKey);
//        Log.i(TAG, "doInBackground: check key above follows this key: http://partners.api.skyscanner.net/apiservices/pricing/v1.0/{sessionKey}?apiKey={apiKey}");

        queue = Volley.newRequestQueue(mContext);

        Log.i(TAG, "sessionPolling: user_id " + user_id);
        JsonObjectRequest playlists = new JsonObjectRequest(Request.Method.GET,
//                "https://api.spotify.com/v1/artists/1vCWHaC5f2uS3yhpwWbIA6/albums?album_type=SINGLE&offset=20&limit=10"
                "https://api.spotify.com/v1/users/" + user_id + "/playlists"
                , null
                , new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: sessionPolling " + response);
                Gson gsonStep2 = new Gson();
                PlaylistContainerGson itinerary = gsonStep2.fromJson(response.toString(), PlaylistContainerGson.class);
                for (int i = 0; i < itinerary.getItems().size(); i++) {
                    if (itinerary.getItems().get(i).getImages().size() > 0) {
                        mPlaylists.add(new PlaylistObject(
                                itinerary.getItems().get(i).getName(),
                                itinerary.getItems().get(i).getUri(),
                                itinerary.getItems().get(i).getImages().get(0).getUrl()));
                        Log.i(TAG, "onResponse: itinerary.getItems() uri = " + itinerary.getItems().get(i).getImages().get(0).getUrl());
                    }
                }
                mSpotifyPlaylistAdapter.updatePlaylist(mPlaylists);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: sessionPolling " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + mAuthToken);
                return params;
            }
        };

        queue.add(playlists);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(response.getAccessToken());

                Log.i(TAG, "onActivityResult: access token" + response.getAccessToken());
                mAuthToken = response.getAccessToken();
//
                mSpotify = api.getService();
                sessionPolling(mSpotify.getMe().id);
            }
        }
    }

    public interface ReturnPlaylistToMedia{
        public void returnPlaylist(ArrayList<SongObject> array);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (queue != null){
            queue.stop();
        }
    }
}
