package com.anders.spotifyalarm.MediaSearch.playlistSearch;

import java.util.ArrayList;

/**
 * Created by anders on 3/5/2017.
 */

public class PlaylistContainerGson {

    ArrayList<PlaylistItem> items;

    public ArrayList<PlaylistItem> getItems() {
        return items;
    }

    public class PlaylistItem {

        ArrayList<PlaylistImage> images;

        public ArrayList<PlaylistImage> getImages() {
            return images;
        }

        public String uri;
        public String name;

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }

        public class PlaylistImage {
            String url;
            public String getUrl() {
                return url;
            }
        }
    }
}
