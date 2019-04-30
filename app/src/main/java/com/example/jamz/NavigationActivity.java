package com.example.jamz;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

// Created a Bottom Navigation layout with the help of this resource: https://www.simplifiedcoding.net/bottom-navigation-android-example/#Creating-Fragments-2

public class NavigationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //TICKET MASTER
    private Button call_api;
    private TextView print_api;
    private String ROOT_URL = "https://app.ticketmaster.com/discovery/v2/events?";
    private String TM_KEY = "apikey=OdN6pW3p8rPEDMzrvmKhycIZEhx5HPyd";
    private String music_segment_id = "&segmentId=KZFzniwnSyZfZ7v7nJ";
    private String boston_latlong = "&latlong=42.3600825,-71.0588801";
    private String country_code = "&countryCode=US";
    private ArrayList<Event> mListData = new ArrayList<>();

    private class requestTicketmaster extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = ROOT_URL + TM_KEY + boston_latlong + country_code + music_segment_id;
            HttpClient client = new DefaultHttpClient();
            HttpGet getEvents = new HttpGet(url);
            try {

                HttpResponse res = client.execute(getEvents);
                HttpEntity http_entity = res.getEntity();
                //String json = EntityUtils.toString(http_entity);
                return EntityUtils.toString(http_entity);

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.e("s_json", jsonObject.toString());
                mListData = parseJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int test = 1;
            //print_api.setText(s);
        }

        public ArrayList<Event> parseJson(JSONObject jsonObject) {
            ArrayList<Event> mList = new ArrayList<>();

            if (jsonObject.has("_embedded")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject json = jsonArray.getJSONObject(i);

                        JSONObject jsonDate = json.getJSONObject("dates");

                        String event_name = json.getString("name");
                        String event_url = json.getString("url");

                        String event_description = "";
                        if(json.has("info")) {
                            event_description = json.getString("info");
                        }

                        String event_start_date = jsonDate.getJSONObject("start").getString("localDate");
                        String event_start_time = jsonDate.getJSONObject("start").getString("localTime");
                        String event_datetime = event_start_date + " " + event_start_time;
                        String event_address_name = json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                        String event_address_city = json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("city").getString("name");
                        String event_address_state = json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("state").getString("name");
                        String event_address = event_address_name + ", " + event_address_city + ", " + event_address_state;
                        String event_photo_url = json.getJSONArray("images").getJSONObject(0).getString("url");
                        double event_lat = Double.valueOf(json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude"));
                        double event_lont = Double.valueOf(json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude"));

                        Event youtubeObject = new Event("TicketMaster",event_name,event_description,event_start_date,event_start_time,event_address,event_lat,event_lont,event_photo_url);

                        mList.add(youtubeObject);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return mList;

        }
    }

    private int NavitationButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //HTTP TICKET MASTER
        requestTicketmaster call_tm = new requestTicketmaster();
        AsyncTask r = call_tm.execute();

        //loading the default fragment which is the Discover page
        loadFragment(new FragCreateJam());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    // This method will load the Fragment selected by User
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;

    }

    // Pass DATA to FRAGMENT
    public ArrayList<Event> getTicketMasterEvents(){
        return mListData;
    }

    // This method will navigate to different fragments as they are selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_create_jam:
                fragment = new FragCreateJam();
                break;

            case R.id.navigation_discover:
                fragment = new FragDiscover();
                break;

            case R.id.navigation_eventlist:
                fragment = new FragEventList();
                break;

            case R.id.navigation_friends:
                fragment = new FragFriends();
                break;

            case R.id.navigation_profile:
                fragment = new FragProfile();
                break;


        }

        return loadFragment(fragment);
    }
}
