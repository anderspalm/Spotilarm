package com.anders.spotifyalarm.SingAndDB;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anders.spotifyalarm.Receivers.AlarmBroadcastReceiver;
import com.anders.spotifyalarm.AlarmTrigger.AlarmObject;
import com.anders.spotifyalarm.MediaSearch.songSearch.SongObject;

import java.util.ArrayList;

/**
 * Created by anders on 1/27/2017.
 */

public class DBhelper extends SQLiteOpenHelper {


    public static DBhelper mInstance;
    ArrayList<AlarmObject> mCombinedAlarmArray;
    private int id;

    public static final String DATABASE_NAME = "SpotifyAlarm.db";
    public static final Integer DATABASE_VERSION = 36;
    private static final String TAG = "DBhelper";

    //    ALARM TABLE - MASTER
    public static final String ALARM_TABLE = "alarm_table";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String MESSAGE = "message";
    public static final String SNOOZETIME = "snooze_time";
    public static final String ALARM_ID = "alarm_id";
    public static final String ITEM_ID = "item_id";
    public static final String DAY = "day";
    public static final String ALARM_SONG = "song";

    //    DATE TABLE - NESTED
    public static final String SONG_TABLE = "song_table";
    public static final String SONGURI = "uri";
    public static final String SONGTITLE = "title";
    public static final String PLAYLIST = "playlist";
    public static final String POPULARITY = "popularity";
    public static final String PHOTOURL = "photo_url";
    public static final String ARTISTS = "artists";
    public static final String DURATION = "duration";


    public static final String HIST_PLAYLIST_TABLE = "hist_playlist_table";
    public static final String HIST_PLAYLIST = "hist_playlist";
    public static final String HIST_PLAYLIST_ID = "hist_playlist_id";

    public static final String HIST_SONG_TABLE = "hist_song_table";
    public static final String HIST_SONG_URI = "hist_uri";
    public static final String HIST_SONG_TITLE = "hist_title";
    public static final String HIST_POPULARITY = "hist_popularity";
    public static final String HIST_PHOTO_URL = "hist_photo_url";
    public static final String HIST_ARTISTS = "hist_artists";
    public static final String HIST_DURATION = "hist_duration";

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBhelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBhelper(context);
        }
        return mInstance;
    }


//    make join table for getting unique id for each request for the alarm

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        mCombinedAlarmArray = new ArrayList<>();

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + ALARM_TABLE + " ("
                        + ITEM_ID + " INTEGER PRIMARY KEY, "
                        + HOUR + " INTEGER, "
                        + MINUTE + " INTEGER, "
                        + DAY + " INTEGER, "
                        + MESSAGE + " TEXT, "
                        + SNOOZETIME + " INTEGER, "
                        + ALARM_SONG + " TEXT, "
                        + ALARM_ID + " INTEGER NOT NULL )");

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + SONG_TABLE + " ("
                        + SONGURI + " TEXT, "
                        + SONGTITLE + " TEXT, "
                        + POPULARITY + " TEXT, "
                        + PLAYLIST + " TEXT, "
                        + PHOTOURL + " TEXT, "
                        + ARTISTS + " TEXT, "
                        + DURATION + " INTEGER, "
                        + ALARM_ID + " INTEGER NOT NULL, "
                        + "FOREIGN KEY(" + ALARM_ID + ") REFERENCES " + ALARM_TABLE + "(" + ALARM_ID + "))");


        sqLiteDatabase.execSQL(
                "CREATE TABLE " + HIST_PLAYLIST_TABLE + " ("
                        + HIST_PLAYLIST_ID + " INTEGER PRIMARY KEY, "
                        + HIST_PLAYLIST + " TEXT )");

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + HIST_SONG_TABLE + " ("
                        + HIST_SONG_URI + " TEXT, "
                        + HIST_SONG_TITLE + " TEXT, "
                        + HIST_POPULARITY + " TEXT, "
                        + HIST_PLAYLIST + " TEXT, "
                        + HIST_PHOTO_URL + " TEXT, "
                        + HIST_ARTISTS + " TEXT, "
                        + HIST_DURATION + " INTEGER, "
                        + HIST_PLAYLIST_ID + " INTEGER NOT NULL, "
                        + "FOREIGN KEY(" + HIST_PLAYLIST_ID + ") REFERENCES " + HIST_PLAYLIST_TABLE + "(" + HIST_PLAYLIST_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE);
        onCreate(sqLiteDatabase);
    }

    public int getAlarmItemId(int alarmId, int day) {
        SQLiteDatabase db = getReadableDatabase();
        int num = 300000;
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + ALARM_ID + " = " + alarmId + " AND " + DAY + " = " + day, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
//                Log.i(TAG, "insertAlarms: num " + num);
                num = cursor.getInt(cursor.getColumnIndex(ITEM_ID));
                cursor.moveToNext();
            }
        } else {
//            Log.i(TAG, "getAlarmItemId: ");
        }
        db.close();
        cursor.close();
        return num;
    }

    public void removeAndCancelAlarm(int alarm_id, Context context) {
        SQLiteDatabase db1 = getReadableDatabase();

        ArrayList<Integer> arrayList = new ArrayList<>();

//        FIRST I MUST FIND ALL THE UNIQUE ALARM IDs
        Cursor cursor = db1.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + ALARM_ID + " = " + alarm_id, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int uniqueIdentifier = cursor.getInt(cursor.getColumnIndex(ITEM_ID));
                arrayList.add(uniqueIdentifier);
                cursor.moveToNext();
            }
        } else {
//            Log.i(TAG, "removeAndCancelAlarm: ");
        }

        db1.close();

//        REMOVE ALL ASSOCIATED ALARMS FROM TABLE
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + ALARM_TABLE + " WHERE " + ALARM_ID + " = " + alarm_id);
        db.close();

        for (int i = 0; i < arrayList.size(); i++) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
//            Log.i(TAG, "cancelAlarm: identifyingCode = " + arrayList.get(i));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, arrayList.get(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
//            Log.i(TAG, "cancelAlarm: cancelled");
        }

//        REMOVING THE SONGS AS WELL
        removeSongs(alarm_id);
    }

    public void removeAndCancelSpecificAlarm(Integer day_id, Context context) {
        SQLiteDatabase db1 = getReadableDatabase();
        Cursor cursor = db1.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + ITEM_ID + " = " + day_id, null);
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int uniqueIdentifier = cursor.getInt(cursor.getColumnIndex(ITEM_ID));
                arrayList.add(uniqueIdentifier);
                cursor.moveToNext();
            }
        } else {
//            Log.i(TAG, "removeAndCancelAlarm: ");
        }

        db1.close();

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + ALARM_TABLE + " WHERE " + ITEM_ID + " = " + day_id);
        db.close();

        for (int i = 0; i < arrayList.size(); i++) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
//            Log.i(TAG, "cancelAlarm: identifyingCode = " + arrayList.get(i));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, arrayList.get(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
//            Log.i(TAG, "cancelAlarm: cancelled");
        }

    }

    public int findSnooze(int alarm_id, int day) {
        SQLiteDatabase db = getReadableDatabase();
        int snooze = 30000;
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + ALARM_ID + " = " + alarm_id + " AND " + DAY + " = " + day, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                snooze = cursor.getInt(cursor.getColumnIndex(SNOOZETIME));
            }
        } else {
//            Log.i(TAG, "findSnooze: ");
        }
//        Log.i(TAG, "findSnooze: " + snooze);
        cursor.close();
        db.close();
        return snooze;
    }


    public ArrayList<AlarmObject> getAllAlarmsByDay(int day) {
        ArrayList<AlarmObject> arrayList = new ArrayList<>();
        AlarmObject alarmObject = new AlarmObject();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + DAY + " = " + day, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                alarmObject.setmHour(cursor.getInt(cursor.getColumnIndex(HOUR)));
                alarmObject.setmMinute(cursor.getInt(cursor.getColumnIndex(MINUTE)));
                alarmObject.setmSnooze(cursor.getInt(cursor.getColumnIndex(SNOOZETIME)));
                alarmObject.setmAlarmId(cursor.getInt(cursor.getColumnIndex(ALARM_ID)));
                alarmObject.setmDay(cursor.getInt(cursor.getColumnIndex(DAY)));
                alarmObject.setmMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                arrayList.add(alarmObject);
                cursor.moveToNext();
            }
        } else {
//            Log.i(TAG, "getAllAlarmsByDay: ");
        }
        db.close();
        cursor.close();
        return arrayList;
    }


    public ArrayList<ArrayList<AlarmObject>> getAllAlarms() {
        SQLiteDatabase dbR = getReadableDatabase();
//        initializers
        ArrayList<ArrayList<AlarmObject>> arrayListArrayList = new ArrayList<>();
        ArrayList<Integer> alarmIdArray = new ArrayList<>();
//        function

        ArrayList<Integer> intArray = new ArrayList<>();
        int now = 0;
        int last = 0;
        Cursor cursor = dbR.rawQuery("SELECT * FROM " + ALARM_TABLE, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                now = cursor.getInt(cursor.getColumnIndex(ALARM_ID));
                if (now != last) {
                    last = now;
                    intArray.add(now);
                }
                cursor.moveToNext();
            }
        } else {
//            Log.i(TAG, "getAllAlarms: ");
        }
        cursor.close();

        for (int i = 0; i < intArray.size(); i++) {

            ArrayList<AlarmObject> arrayList = new ArrayList<>();
            int id = intArray.get(i);
//            Log.i(TAG, "getAllAlarms: ALARM_ID outer: -------------------------------------- ");
//            Log.i(TAG, "getAllAlarms: ALARM_ID outer: -------------------" + id + "------------------- ");
            Cursor nested = dbR.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + ALARM_ID + " = " + id, null);
            if (nested.moveToFirst()) {
                while (!nested.isAfterLast()) {
                    AlarmObject alarmObject = new AlarmObject();
                    alarmObject.setmHour(nested.getInt(nested.getColumnIndex(HOUR)));
                    alarmObject.setmMinute(nested.getInt(nested.getColumnIndex(MINUTE)));
                    alarmObject.setmSnooze(nested.getInt(nested.getColumnIndex(SNOOZETIME)));
                    alarmObject.setmAlarmId(nested.getInt(nested.getColumnIndex(ALARM_ID)));
                    alarmObject.setmDay(nested.getInt(nested.getColumnIndex(DAY)));
                    alarmObject.setmMessage(nested.getString(nested.getColumnIndex(MESSAGE)));
                    alarmObject.setmIndex(nested.getInt(nested.getColumnIndex(ITEM_ID)));
                    arrayList.add(alarmObject);
//                    Log.i(TAG, "getAllAlarms nested.getInt(nested.getColumnIndex(ITEM_ID): " + nested.getInt(nested.getColumnIndex(ITEM_ID)));
//                    Log.i(TAG, "getAllAlarms nested.getInt(nested.getColumnIndex(ALARM_ID): " + nested.getInt(nested.getColumnIndex(ALARM_ID)));
                    nested.moveToNext();
                }
//            here we add the group of alarms to the master array
                arrayListArrayList.add(arrayList);
            } else {
//                Log.i(TAG, "getAllAlarms: ");
            }
            nested.close();
        }
        dbR.close();

        return arrayListArrayList;
    }





    public ArrayList<AlarmObject> findDailyAlarms(int day) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<AlarmObject> arraylist = new ArrayList<>();
        Cursor nested = db.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE " + DAY + " = " + day, null);
        if (nested.moveToFirst()) {
            while (!nested.isAfterLast()) {
                AlarmObject alarmObject = new AlarmObject();
                alarmObject.setmHour(nested.getInt(nested.getColumnIndex(HOUR)));
                alarmObject.setmMinute(nested.getInt(nested.getColumnIndex(MINUTE)));
                alarmObject.setmSnooze(nested.getInt(nested.getColumnIndex(SNOOZETIME)));
                alarmObject.setmAlarmId(nested.getInt(nested.getColumnIndex(ALARM_ID)));
                alarmObject.setmDay(nested.getInt(nested.getColumnIndex(DAY)));
                alarmObject.setmMessage(nested.getString(nested.getColumnIndex(MESSAGE)));
                alarmObject.setmIndex(nested.getInt(nested.getColumnIndex(ITEM_ID)));
                arraylist.add(alarmObject);
//                Log.i(TAG, "getAllAlarms nested.getInt(nested.getColumnIndex(ITEM_ID): " + nested.getInt(nested.getColumnIndex(ITEM_ID)));
//                Log.i(TAG, "getAllAlarms nested.getInt(nested.getColumnIndex(ALARM_ID): " + nested.getInt(nested.getColumnIndex(ALARM_ID)));
                nested.moveToNext();
            }
        } else {
//            Log.i(TAG, "findDailyAlarms: ");
        }
        nested.close();
        db.close();
        return arraylist;
    }

    public void deleteSongsById(int alarm_id){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(" DELETE FROM " + SONG_TABLE + " WHERE " + ALARM_ID + " = " + alarm_id);
        db.close();
    }


    public void insertAlarms(AlarmObject alarmObj, ArrayList<SongObject> songObj, int alarm_id) {
        SQLiteDatabase db = getWritableDatabase();
        String message = alarmObj.getmMessage().replace("'", "`");
        db.execSQL("INSERT INTO " + ALARM_TABLE
                + " (" + HOUR + ", " + MINUTE + ", " + DAY + ", " + MESSAGE + ", " + SNOOZETIME + ", " + ALARM_ID + ") "
                + " VALUES (" + alarmObj.getmHour() + ", " + alarmObj.getmMinute() + ", "
                + alarmObj.getmDay() + ", " + "'" + message + "'" + ", " + alarmObj.getmSnooze() + ", " + alarm_id + ")");

        String playlist = "";
//
//        db.execSQL(" DELETE FROM " + SONG_TABLE + " WHERE " + ALARM_ID + " = " + alarmObj.getmAlarmId());
//        db.close();
    }


    public void insertSongs(int alarmId, ArrayList<SongObject> songObj){

        String playlist = "";
        if (songObj.size() > 0) {
            playlist = songObj.get(0).getPlaylist();
            if (playlist == null ||
                    playlist.equals("null") || playlist.equals("")) {
                playlist = songObj.get(0).getTitle() + " Playlist";
            }

            for (int i = 0; i < songObj.size(); i++) {
//                Log.i(TAG, "insertAlarms: songArray");
                addSong(songObj.get(i).getUri(),
                        alarmId,
                        songObj.get(i).getDuration(),
                        songObj.get(i).getPopularity(),
                        songObj.get(i).getArtist(),
                        songObj.get(i).getPhotoUri(),
                        songObj.get(i).getTitle(),
                        playlist);
            }
        }
    }

    public void removeSongs(int alarm_id) {
        SQLiteDatabase dbSong = getWritableDatabase();
        dbSong.execSQL("DELETE FROM " + SONG_TABLE + " WHERE " + ALARM_ID + " = " + alarm_id);
        dbSong.close();
    }

    public void addSong(String uri, int alarm_id, long duration, String popularity, String artists, String photoUrl, String title, String playlist) {
        title = title.replace("'", "`");
        artists = artists.replace("'", "`");
        playlist = playlist.replace("'", "`");
        SQLiteDatabase db = getWritableDatabase();
//        Log.i(TAG, "addSong: songArray");
        db.execSQL("INSERT INTO " + SONG_TABLE + "(" + SONGURI + ", " + ALARM_ID + ", " + DURATION + ", " + PLAYLIST + ", " + POPULARITY + ", " + ARTISTS + ", " + PHOTOURL + ", " + SONGTITLE + ")" +
                " VALUES (" + "'" + uri + "'" + "," + alarm_id + "," + duration + "," + "'" + playlist + "'" + "," + "'" + popularity + "'" + "," + "'" + artists + "'" + "," + "'" + photoUrl + "'" + "," + "'" + title + "'" + ")");
        db.close();
    }

    public ArrayList<SongObject> getSongs(int alarm_id) {
        int duration;
        String uri, artist, songTitle, photoUrl, popularity, playlist;
        ArrayList<SongObject> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SONG_TABLE + " WHERE " + ALARM_ID + " = " + alarm_id, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                duration = cursor.getInt(cursor.getColumnIndex(DURATION));
                artist = cursor.getString(cursor.getColumnIndex(ARTISTS));
                photoUrl = cursor.getString(cursor.getColumnIndex(PHOTOURL));
                popularity = cursor.getString(cursor.getColumnIndex(POPULARITY));
                playlist = cursor.getString(cursor.getColumnIndex(PLAYLIST));
                songTitle = cursor.getString(cursor.getColumnIndex(SONGTITLE));
                uri = cursor.getString(cursor.getColumnIndex(SONGURI));
                SongObject songObject = new SongObject(duration, uri, songTitle, artist, popularity, photoUrl, playlist);
                arrayList.add(songObject);
                cursor.moveToNext();
            }
        } else {
        }
        db.close();
        cursor.close();
        return arrayList;
    }

    public void insertHistPlaylists(ArrayList<SongObject> arrayList) {
        if (arrayList.size() > 0) {

            ArrayList<SongObject> arrayListCheck = new ArrayList<>();


            SQLiteDatabase db3 = getReadableDatabase();
            Cursor cursor = null;
            for (int j = 0; j < arrayList.size(); j++) {
                arrayListCheck.add(checkIfExistsHistPlaylist(arrayList.get(0).getPlaylist(), db3, cursor));
            }
            db3.close();

            if (arrayList != arrayListCheck) {

                insertHist(arrayList);
            }
        }
    }

    public void insertHist(ArrayList<SongObject> arrayList) {
        int current_id = 0;

        SQLiteDatabase db = getWritableDatabase();
//        Log.i(TAG, "addSong: songArray");
        db.execSQL("INSERT INTO "
                + HIST_PLAYLIST_TABLE + "(" +
                HIST_PLAYLIST + ")" +
                " VALUES (" + "'" + arrayList.get(0).getPlaylist() + "'" + ")");
        db.close();


        SQLiteDatabase db2 = getReadableDatabase();
//        Log.i(TAG, "addSong: songArray");
        Cursor last_id = db2.rawQuery("SELECT * FROM " + HIST_PLAYLIST_TABLE + " ORDER BY " + HIST_PLAYLIST_ID + " DESC LIMIT 1", null);

        if (last_id.moveToFirst()) {
            while (!last_id.isAfterLast()) {
                current_id = last_id.getInt(last_id.getColumnIndex(HIST_PLAYLIST_ID));
                last_id.moveToNext();
            }
        } else {
//            Log.i(TAG, "insertHistPlaylists: error selecting HIST_PLAYLIST_ID from table");
        }

        last_id.close();
        db2.close();

//        Log.i(TAG, "insertHist: current id = " + id);

        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
//                Log.i(TAG, "insertAlarms: songArray");
                addHistSong(arrayList.get(i).getUri(),
                        arrayList.get(i).getDuration(),
                        arrayList.get(i).getPopularity(),
                        arrayList.get(i).getArtist(),
                        arrayList.get(i).getPhotoUri(),
                        arrayList.get(i).getTitle(),
                        arrayList.get(i).getPlaylist(),
                        current_id);
            }
        }
    }

    public void updateHistPlaylist(ArrayList<SongObject> arrayList) {

        SQLiteDatabase db = getWritableDatabase();
//        Log.i(TAG, "addSong: songArray");

//        UPDATE COMPANY SET ADDRESS = 'Texas' WHERE ID = 6;

//        db.execSQL("UPDATE " + HIST_PLAYLIST_TABLE + " SET " + HIST_PLAYLIST + " = " + arrayList.get(0).getPlaylist() );
//        db.execSQL("UPDATE " + HIST_SONG_TABLE + );

        db.execSQL(" DELETE FROM " + HIST_PLAYLIST_TABLE + " WHERE " + HIST_PLAYLIST_ID + " = " + arrayList.get(0).getId());
        db.execSQL(" DELETE FROM " + HIST_SONG_TABLE + " WHERE " + HIST_PLAYLIST_ID + " = " + arrayList.get(0).getId());
        db.close();

        insertHist(arrayList);
    }

    public void deleteSpecificId(String uri, String playlist) {
        SQLiteDatabase db = getWritableDatabase();
//        Log.i(TAG, "addSong: songArray");

        db.execSQL(" DELETE FROM " + HIST_SONG_TABLE + " WHERE " + HIST_SONG_URI + " = " + "'" +  uri + "'"
                + " AND " + HIST_PLAYLIST + " = " + "'" + playlist + "'" );
        db.close();
    }

    public void deleteSpecificPlaylist(ArrayList<SongObject> playlist) {
        if (playlist.size() > 0) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(" DELETE FROM " + HIST_PLAYLIST_TABLE + " WHERE " + HIST_PLAYLIST_ID + " = " + playlist.get(0).getId());
            db.execSQL(" DELETE FROM " + HIST_SONG_TABLE + " WHERE " + HIST_PLAYLIST_ID + " = " + playlist.get(0).getId());
            db.close();
        }
    }

    public void addHistSong(
            String uri, long duration,
            String popularity, String artists,
            String photoUrl, String title, String playlist, int id) {

        title = title.replace("'", "`");
        artists = artists.replace("'", "`");

        SQLiteDatabase db3 = getWritableDatabase();
//        Log.i(TAG, "addSong: songArray");
        db3.execSQL("INSERT INTO "
                + HIST_SONG_TABLE + "(" +
                HIST_SONG_URI + ", " +
                HIST_DURATION + ", " +
                HIST_PLAYLIST + ", " +
                HIST_POPULARITY + ", " +
                HIST_ARTISTS + ", " +
                HIST_PHOTO_URL + ", " +
                HIST_SONG_TITLE + ", " +
                HIST_PLAYLIST_ID + ")" +
                " VALUES ("
                + "'" + uri + "'" + ","
                + duration + "," +
                "'" + playlist + "'" + "," +
                "'" + popularity + "'" + "," +
                "'" + artists + "'" + "," +
                "'" + photoUrl + "'" + "," +
                "'" + title + "'" + "," +
                +id
                + ")");
        db3.close();

    }


    public SongObject
    checkIfExistsHistPlaylist(String playlist, SQLiteDatabase db3, Cursor cursor) {

        SongObject songObject = null;
//        Log.i(TAG, "addSong: songArray");
        cursor = db3.rawQuery("SELECT * FROM " + SONG_TABLE + " WHERE " + PLAYLIST + " = " + "'" + playlist + "'", null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                songObject = new SongObject(
                        cursor.getInt(cursor.getColumnIndex(DURATION)),
                        cursor.getString(cursor.getColumnIndex(SONGURI)),
                        cursor.getString(cursor.getColumnIndex(SONGTITLE)),
                        cursor.getString(cursor.getColumnIndex(ARTISTS)),
                        cursor.getString(cursor.getColumnIndex(POPULARITY)),
                        cursor.getString(cursor.getColumnIndex(PHOTOURL)),
                        cursor.getString(cursor.getColumnIndex(PLAYLIST)));
                cursor.moveToNext();
            }
        }

        cursor.close();

        if (songObject == null) {
            songObject = new SongObject();
        } else {
        }
        return songObject;
    }

    public ArrayList<ArrayList<SongObject>> getAllHistPlaylists() {
        ArrayList<Integer> intArray = new ArrayList<>();
        ArrayList<ArrayList<SongObject>> playListArray = new ArrayList<>();


        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + HIST_PLAYLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                intArray.add(cursor.getInt(cursor.getColumnIndex(HIST_PLAYLIST_ID)));
                cursor.moveToNext();
            }
        } else {
//            Log.i(TAG, "getAllHistPlaylists: error while selecting from hist table");
        }
        cursor.close();


//        GIVEN THE IDs WE CAN FIND AN ARRAY OF PLAYLISTS COMPLETE WITH THEIR SONGS
        SQLiteDatabase db2 = getReadableDatabase();
        Cursor cursor2;

        int duration, id;
        String uri, artist, songTitle, photoUrl, popularity, playlist;

        for (int i = 0; i < intArray.size(); i++) {
            ArrayList<SongObject> songArray = new ArrayList<>();
            cursor2 = db2.rawQuery("" +
                    "SELECT * FROM " + HIST_SONG_TABLE +
                    " WHERE " + HIST_PLAYLIST_ID + " = " + intArray.get(i), null);
            if (cursor2.moveToFirst()) {
                while (!cursor2.isAfterLast()) {
                    duration = cursor2.getInt(cursor2.getColumnIndex(HIST_DURATION));
                    artist = cursor2.getString(cursor2.getColumnIndex(HIST_ARTISTS));
                    photoUrl = cursor2.getString(cursor2.getColumnIndex(HIST_PHOTO_URL));
                    popularity = cursor2.getString(cursor2.getColumnIndex(HIST_POPULARITY));
                    playlist = cursor2.getString(cursor2.getColumnIndex(HIST_PLAYLIST));
                    songTitle = cursor2.getString(cursor2.getColumnIndex(HIST_SONG_TITLE));
                    uri = cursor2.getString(cursor2.getColumnIndex(HIST_SONG_URI));
                    id = cursor2.getInt(cursor2.getColumnIndex(HIST_PLAYLIST_ID));
                    SongObject songObject = new SongObject(duration, uri, songTitle, artist, popularity, photoUrl, playlist, id);
                    songArray.add(songObject);
                    cursor2.moveToNext();
                }
                playListArray.add(songArray);
            } else {
//                Log.i(TAG, "getAllHistPlaylists: HIST_SONG_TABLE is empty");
            }
            cursor2.close();
        }
        db2.close();
        return playListArray;
    }
}
