package com.example.jamz;

public class TopTrack {

    public String track_name;
    public String album_name;
    public String artist_name;

    public TopTrack() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public TopTrack(String track, String album, String artist) {

        this.track_name = track;
        this.album_name = album;
        this.artist_name = artist;

    }


}
