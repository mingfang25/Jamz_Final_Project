package com.example.jamz;

public class Event {

        public String username;
        public String eventname;
        public String eventDescription;
        public String eventFromStart;
        public String eventFromEnd;
        public String eventToStart;
        public String eventToEnd;
        public boolean eventallday;

        public Event() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Event(String username, String eventname, String eventDescription, String eventFromStart,String eventFromEnd, String eventToStart,String eventToEnd, boolean eventallday) {
            this.username = username;
            this.eventname = eventname;
            this.eventDescription = eventDescription;
            this.eventFromStart = eventFromStart;
            this.eventFromEnd = eventFromEnd;
            this.eventToStart = eventToStart;
            this.eventToEnd = eventToEnd;
            this.eventallday = eventallday;
        }


}
