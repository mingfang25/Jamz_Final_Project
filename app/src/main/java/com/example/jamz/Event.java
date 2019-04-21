package com.example.jamz;

import com.google.android.gms.maps.model.LatLng;

public class Event {

        public String username;
        public String eventname;
        public String eventDescription;
        public String eventFromStart;
        public String eventFromEnd;
        public boolean eventallday;
        public String eventAddress;
        public double eventlatitude;
        public double eventlongitude;
        public String UserPhotoURL;

        public Event() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

    public Event(String username, String eventname, String eventDescription, String eventFromStart, String eventFromEnd,
                 boolean eventallday, String eventAddress, double eventlatitude, double eventlongitude ) {
        this.username = username;
        this.eventname = eventname;
        this.eventDescription = eventDescription;
        this.eventFromStart = eventFromStart;
        this.eventFromEnd = eventFromEnd;
        this.eventallday = eventallday;
        this.eventAddress = eventAddress;
        this.eventlatitude = eventlatitude;
        this.eventlongitude = eventlongitude;
        //this.eventLat = eventLat;
    }

    public Event(String username, String eventname, String eventDescription, String eventFromStart, String eventFromEnd,
                 boolean eventallday, String eventAddress, double eventlatitude, double eventlongitude, String UserPhotoURL ) {
        this.username = username;
        this.eventname = eventname;
        this.eventDescription = eventDescription;
        this.eventFromStart = eventFromStart;
        this.eventFromEnd = eventFromEnd;
        this.eventallday = eventallday;
        this.eventAddress = eventAddress;
        this.eventlatitude = eventlatitude;
        this.eventlongitude = eventlongitude;
        this.UserPhotoURL = UserPhotoURL;
    }

        public Event(String username, String eventname, String eventDescription, String eventFromStart, String eventFromEnd, String eventToStart, String eventToEnd,
                     boolean eventallday, String eventAddress, LatLng eventLat) {
            this.username = username;
            this.eventname = eventname;
            this.eventDescription = eventDescription;
            this.eventFromStart = eventFromStart;
            this.eventFromEnd = eventFromEnd;
            this.eventallday = eventallday;
            this.eventAddress = eventAddress;
            //this.eventLat = eventLat;
        }


}
