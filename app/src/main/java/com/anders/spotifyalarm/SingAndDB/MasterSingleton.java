package com.anders.spotifyalarm.SingAndDB;

import android.util.Log;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MasterSingleton {

    ArrayList<SongObject> songObjectArray;
    String mUserId = "0";
    int mTimeGrad = 0;
    int mTimeTaken = 0;
    int originalVolume = 0;
    String playlistTitle = "";
    SpotifyPlayer mCurrentPlaylistPlayer;

    public SpotifyPlayer getmCurrentPlaylistPlayer() {
        return mCurrentPlaylistPlayer;
    }

    public void setmCurrentPlaylistPlayer(SpotifyPlayer mCurrentPlaylistPlayer) {
        this.mCurrentPlaylistPlayer = mCurrentPlaylistPlayer;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public int getOriginalVolume() {
        return originalVolume;
    }

    public void setOriginalVolume(int originalVolume) {
        this.originalVolume = originalVolume;
    }

    public int getmTimeGrad() {
        return mTimeGrad;
    }

    public void setmTimeGrad(int mTimeGrad) {
        this.mTimeGrad = mTimeGrad;
    }

    public int getmTimeTaken() {
        return mTimeTaken;
    }

    public void setmTimeTaken(int mTimeTaken) {
        this.mTimeTaken = mTimeTaken;
    }

    public static MasterSingleton getmInstance() {
        if (mInstance == null) {
            mInstance = new MasterSingleton();
        }
        return mInstance;
    }



    public void setUserId(String userId){
        mUserId = userId;
    }

    public String getUserId(){
        return mUserId;
    }



    String mAuthToken;

    public String getmAuthToken() {
        return mAuthToken;
    }

    public void setmAuthToken(String mAuthToken) {
        this.mAuthToken = mAuthToken;
    }

    public void clearSongs() {
        songObjectArray.clear();
    }

    public ArrayList<SongObject> getSongObjArray() {
        if (songObjectArray == null){
            songObjectArray = new ArrayList<>();
//            Log.i(TAG, "getSongObjArray: there were no songs in the singeton array");
            return songObjectArray;
        } else {
//            Log.i(TAG, "getSongObjArray: " + songObjectArray.size());
            return songObjectArray;
        }
    }

    public void setSongObjectArray(ArrayList<SongObject> arrayList) {
        if (songObjectArray == null){
            songObjectArray = new ArrayList<>(arrayList);
        } else {
            songObjectArray = arrayList;
        }
        Log.i(TAG, "setSongObjectArray: " + songObjectArray.size());
    }

    public void addSong(SongObject object) {
        if (songObjectArray != null) {
            songObjectArray.add(object);
        } else {
            songObjectArray = new ArrayList<>();
            songObjectArray.add(object);
        }
    }

    public void removeSong(SongObject songObject) {
        if (songObjectArray != null){
            songObjectArray.remove(songObject);
        }
    }

    public static MasterSingleton mInstance;
    ArrayList<AlarmObject> mAlarmObjectsArray = new ArrayList<>();

    Integer mSnoozeTime;

    public Integer getmSnoozeTime() {
        return mSnoozeTime;
    }

    public void setmSnoozeTime(Integer mSnoozeTime) {
        this.mSnoozeTime = mSnoozeTime;
    }


    public void addAlarmObjects(AlarmObject obj) {
        if (mAlarmObjectsArray == null) {
            mAlarmObjectsArray = new ArrayList<>();
            mAlarmObjectsArray.add(obj);
        } else {
            mAlarmObjectsArray.add(obj);
        }
    }

    public void removeAlarmObj(AlarmObject obj, Integer id) {
        if (mAlarmObjectsArray != null) {
            mAlarmObjectsArray.remove(id);
        }
    }
}
