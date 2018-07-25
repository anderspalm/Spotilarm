package com.anders.spotifyalarm.UiAids;

/**
 * Created by anders on 3/26/2017.
 */

public class ChangeAlarmObj {

    Integer mDay, mHour, mMinute, mId, mSnooze;

    public ChangeAlarmObj(Integer mDay, Integer mHour, Integer mMinute, Integer mId, Integer mSnooze) {
        this.mDay = mDay;
        this.mHour = mHour;
        this.mMinute = mMinute;
        this.mId = mId;
        this.mSnooze = mSnooze;
    }

    public Integer getmDay() {
        return mDay;
    }

    public void setmDay(Integer mDay) {
        this.mDay = mDay;
    }

    public Integer getmHour() {
        return mHour;
    }

    public void setmHour(Integer mHour) {
        this.mHour = mHour;
    }

    public Integer getmMinute() {
        return mMinute;
    }

    public void setmMinute(Integer mMinute) {
        this.mMinute = mMinute;
    }

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public Integer getmSnooze() {
        return mSnooze;
    }

    public void setmSnooze(Integer mSnooze) {
        this.mSnooze = mSnooze;
    }
}
