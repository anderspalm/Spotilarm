package com.anders.spotifyalarm.MediaSearch.songSearch;

/**
 * Created by anders on 2/13/2017.
 */

public class SongObject {

    long duration;
    int id;
    String uri, title, artist, popularity, photoUri, playlist;

    public SongObject(){}

    public SongObject(long duration, String uri, String title, String artist, String popularity, String photoUri) {
        this.duration = duration;
        this.uri = uri;
        this.title = title;
        this.artist = artist;
        this.popularity = popularity;
        this.photoUri = photoUri;
    }

    public SongObject(long duration, String uri, String title, String artist, String popularity, String photoUri, String playlist) {
        this.duration = duration;
        this.uri = uri;
        this.title = title;
        this.artist = artist;
        this.popularity = popularity;
        this.photoUri = photoUri;
        this.playlist = playlist;
    }

    public SongObject(long duration, String uri, String title, String artist, String popularity, String photoUri, String playlist, int id ) {
        this.duration = duration;
        this.id = id;
        this.uri = uri;
        this.title = title;
        this.artist = artist;
        this.popularity = popularity;
        this.photoUri = photoUri;
        this.playlist = playlist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SongObject(long duration, String uri) {
        this.duration = duration;
        this.uri = uri;
    }
}
