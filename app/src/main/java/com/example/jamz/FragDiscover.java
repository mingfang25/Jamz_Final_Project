package com.example.jamz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Arrays;

// Add import statements for the new library.

public class FragDiscover extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    //TICKET MASTER
    private Button call_api;
    private TextView print_api;
    private String ROOT_URL = "https://app.ticketmaster.com/discovery/v2/events?";
    private String TM_KEY = "apikey=OdN6pW3p8rPEDMzrvmKhycIZEhx5HPyd";
    private String music_segment_id = "&segmentId=KZFzniwnSyZfZ7v7nJ";
    private String boston_latlong = "&latlong=42.3600825,-71.0588801";
    private String country_code = "&countryCode=US";
    private ArrayList<Event> mListData = new ArrayList<>();

    // GOOGLE MAP
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private FragmentActivity myContext;

    //auto complete map
    private static final String AUTOTAG = "AUTOTAG";
    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;
    private static final int UPDATE_INTERVAL = 5000;//5 detik
    private static final int FASTEST_INTERVAL = 3000;//3detik
    private static final int DISPLACEMENT = 10;
    private AutocompleteSupportFragment AutocompleteSupportFragment;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    Marker marker;

    public FragDiscover(){
        //Required empty public constructor

    }

    ArrayList<Event> events = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);


        events.clear();


        int test =1;

        //FROM FIREBASE

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user_id_ref = database.getReference("Events");
        user_id_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("eventscount " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String eventname = (String) postSnapshot.child("eventname").getValue();
                    String username = (String) postSnapshot.child("username").getValue();
                    String eventDescription = (String) postSnapshot.child("eventDescription").getValue();
                    String eventFromStart = (String) postSnapshot.child("eventFromStart").getValue();
                    String eventFromEnd = (String) postSnapshot.child("eventFromEnd").getValue();
                    String eventAddress = (String) postSnapshot.child("eventAddress").getValue();
                    double latitude = (double) postSnapshot.child("eventlatitude").getValue();
                    double longitude = (double) postSnapshot.child("eventlongitude").getValue();
                    //LatLng eventLat = new LatLng(latitude,longitude);
                    boolean eventallday = (boolean) postSnapshot.child("eventallday").getValue();
                    Event post = new Event(username,eventname,eventDescription,eventFromStart,eventFromEnd,eventallday,eventAddress,latitude,longitude);
                    Log.e("READTAG", post.eventname);
                    events.add(post);
                }
                //Log.d(READTAG, "Value is: " + users.FirstName);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("READTAG", "Failed to read value.", error.toException());
            }
        });

        // Map part
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 2
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // 3
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListData = ((NavigationActivity) activity).getTicketMasterEvents();
        int test =1;
    }


    protected void placeMarkerOnMap(LatLng location) {
        // 1
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                (getResources(), R.mipmap.ic_user_location))).title("Your location");
        // 2
        mMap.addMarker(markerOptions);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
        //(getResources(), R.mipmap.ic_user_location)));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setBuildingsEnabled(true);
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                //add pin at user's location
                placeMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }

        // INFORMATION WINDOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
//        mMap.addMarker(new MarkerOptions()
//                .position(MELBOURNE)
//                .title("Melbourne")
//                .snippet("Population: 4,137,400"));

        events.addAll(mListData);

        ArrayList<LatLng> lat_set = new ArrayList<>();

        for(int i=0;i<events.size();i++){
            LatLng event_location = new LatLng(events.get(i).eventlatitude,events.get(i).eventlongitude);
            lat_set.add(event_location);
        }

        for(int i=0;i<events.size();i++){
            mMap.addMarker(new MarkerOptions()
                    .position(lat_set.get(i))
                    .title(events.get(i).eventname)
                    .snippet(events.get(i).eventAddress));

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int i = 0;
                    for(int n=0;n<events.size();n++) {
                        if(marker.getTitle().contains(events.get(n).eventname)){
                            i = n;
                            break;
                        }
                    }

                    final String username = events.get(i).username;
                    final String eventname = events.get(i).eventname;
                    final String eventDescription = events.get(i).eventDescription;
                    final String eventFromStart = events.get(i).eventFromStart;
                    final String eventFromEnd = events.get(i).eventFromEnd;
                    final boolean eventallday = events.get(i).eventallday;
                    final String eventAddress = events.get(i).eventAddress;
                    final double eventlatitude = events.get(i).eventlatitude;
                    final double eventlongitude = events.get(i).eventlongitude;
                    final String UserPhotoURL = events.get(i).UserPhotoURL;

                    Intent intent = new Intent(getActivity() ,EventDetailActivity.class);
                    intent.putExtra("username",eventname);
                    intent.putExtra("eventname",eventname);
                    intent.putExtra("eventDescription",eventDescription);
                    intent.putExtra("eventFromStart",eventFromStart);
                    intent.putExtra("eventFromEnd",eventFromEnd);
                    intent.putExtra("eventallday",eventallday);
                    intent.putExtra("eventAddress",eventAddress);
                    intent.putExtra("eventlatitude",eventlatitude);
                    intent.putExtra("eventlongitude",eventlongitude);
                    intent.putExtra("UserPhotoURL",UserPhotoURL);
                    startActivity(intent);
                }
            });
        }



//        final String username;
//        final String eventname;
//        final String eventDescription;
//        final String eventFromStart;
//        final String eventFromEnd;
//        final boolean eventallday;
//        final String eventAddress;
//        final double eventlatitude;
//        final double eventlongitude;
//        final String UserPhotoURL;
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}

