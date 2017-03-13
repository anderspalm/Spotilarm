package com.anders.spotifyalarm.MediaSearch.playlistSearch;

/**
 * Created by anders on 3/5/2017.
 */

public class PlaylistObject {

    String mName, mUri, mUrl;

    public PlaylistObject(String name, String uri, String url) {
        mName = name;
        mUri = uri;
        mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
