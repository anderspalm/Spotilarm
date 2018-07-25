package com.anders.spotifyalarm.SingAndDB;

/**
 * Created by anders on 3/24/2017.
 */

public class SettingsObject {

    String mVibrate, mGradual;

    public SettingsObject(String mVibrate, String mGradual) {
        this.mVibrate = mVibrate;
        this.mGradual = mGradual;
    }

    public String getmVibrate() {
        return mVibrate;
    }

    public void setmVibrate(String mVibrate) {
        this.mVibrate = mVibrate;
    }

    public String getmGradual() {
        return mGradual;
    }

    public void setmGradual(String mGradual) {
        this.mGradual = mGradual;
    }
}
