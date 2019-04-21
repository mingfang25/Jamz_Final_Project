package com.example.jamz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventDetailActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private  String UserName;
    private String EventName;
    private String EventDescription;
    private String EventFromStart;
    private String EventFromEnd;
    private String EventToStart;
    private String EventToEnd;
    private LatLng EventLat;
    private String EventAddress;
    private boolean Eventallday;

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private FragmentActivity myContext;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mPhotoUrl;

    String username;
    String eventname;
    String eventDescription;
    String eventFromStart;
    String eventFromEnd;
    boolean eventallday;
    String eventAddress;
    double eventlatitude;
    double eventlongitude;
    String UserPhotoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("JAM DETAIL");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        eventname = bundle.getString("eventname");
        eventDescription = bundle.getString("eventDescription");
        eventFromStart = bundle.getString("eventFromStart");
        eventFromEnd = bundle.getString("eventFromEnd");
        eventallday = bundle.getBoolean("eventallday");
        eventAddress = bundle.getString("eventAddress");
        eventlatitude = bundle.getDouble("eventlatitude");
        eventlongitude = bundle.getDouble("eventlongitude");
        UserPhotoURL = bundle.getString("UserPhotoURL");

        Event event = new Event(username,eventname,eventDescription,eventFromStart,eventFromEnd,eventallday,eventAddress,eventlatitude,eventlongitude,UserPhotoURL);

        // Map part
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        final TextView edteventname = (TextView) findViewById(R.id.editEventName);
        final TextView edteventdescription = (TextView) findViewById(R.id.editDescription);
        final TextView edtFromStart = (TextView) findViewById(R.id.editStart);
        final TextView edtFromEnd = (TextView) findViewById(R.id.editEnd);
        final CheckBox cbAllday = (CheckBox) findViewById(R.id.cballday);

        edteventname.setText(eventname);
        edteventdescription.setText(eventDescription);
        edtFromStart.setText(eventFromStart);
        edtFromEnd.setText(eventFromEnd);
        cbAllday.setChecked(eventallday);

        int test = 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    protected void placeMarkerOnMap(LatLng location) {
        // 1
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                (getResources(), R.mipmap.ic_user_location))).title("Your location");
        // 2
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker for alice and move the camera
//        LatLng alice = new LatLng(42.361145, -71.057083);
//        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap
//                (BitmapFactory.decodeResource(getResources(), R.mipmap.ic_user_location))).position(alice).title("Alice"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alice, 12));
//
//        // Add a marker for alice and move the camera
//        LatLng ryan = new LatLng(42.37, -71.06);
//        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap
//                (BitmapFactory.decodeResource(getResources(), R.mipmap.ic_user_location))).position(ryan).title("Ryan"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ryan, 14));

        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
        //(getResources(), R.mipmap.ic_user_location)));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setBuildingsEnabled(true);
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
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
        final LatLng location = new LatLng(eventlatitude, eventlongitude);
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(eventname)
                .snippet(eventAddress));
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
