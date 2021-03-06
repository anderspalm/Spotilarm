package com.anders.spotifyalarm.AlarmTrigger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anders on 1/29/2017.
 */

public class AlarmObject implements Serializable{

    Integer mHour, mMinute, mSnooze, mAlarmId, mDay, mIndex;
    String mMessage, mSetmChecked;

    public AlarmObject(Integer hour, int minute, int snooze, int alarmId, int dayId, String message) {
        mHour = hour; mMinute = minute; mSnooze = snooze;
        mAlarmId = alarmId; mDay = dayId; mMessage = message;
    }

    public AlarmObject(Integer hour, int minute, int snooze, int alarmId, int dayId, String message, int index) {
        mHour = hour; mMinute = minute; mSnooze = snooze;
        mAlarmId = alarmId; mDay = dayId; mMessage = message;
        mIndex = index;
    }

    public AlarmObject(Integer mHour, Integer mMinute, Integer mSnooze, Integer mAlarmId, Integer mDay, Integer mIndex, String mMessage, String mSetmChecked) {
        this.mHour = mHour;
        this.mMinute = mMinute;
        this.mSnooze = mSnooze;
        this.mAlarmId = mAlarmId;
        this.mDay = mDay;
        this.mIndex = mIndex;
        this.mMessage = mMessage;
        this.mSetmChecked = mSetmChecked;
    }

    public String getmChecked() {
        return mSetmChecked;
    }

    public void setmChecked(String mSetmChecked) {
        this.mSetmChecked = mSetmChecked;
    }

    public Integer getmIndex() {
        return mIndex;
    }

    public void setmIndex(Integer mIndex) {
        this.mIndex = mIndex;
    }

    public AlarmObject(){}

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

    public Integer getmSnooze() {
        return mSnooze;
    }

    public void setmSnooze(Integer mSnooze) {
        this.mSnooze = mSnooze;
    }

    public Integer getmAlarmId() {
        return mAlarmId;
    }

    public void setmAlarmId(Integer mAlarmId) {
        this.mAlarmId = mAlarmId;
    }

    public Integer getmDay() {
        return mDay;
    }

    public void setmDay(Integer mDayId) {
        this.mDay = mDayId;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
