package com.anders.spotifyalarm.SingAndDB;

import android.util.Log;

import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MasterSingleton {

    ArrayList<SongObject> songObjectArray;
    String mUserId = "0";

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
