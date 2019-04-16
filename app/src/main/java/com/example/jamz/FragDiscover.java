package com.example.jamz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
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

import java.util.ArrayList;
import java.util.Arrays;

// Add import statements for the new library.

public class FragDiscover extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {


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


//        /**
//         * Initialize Places. For simplicity, the API key is hard-coded. In a production
//         * environment we recommend using a secure mechanism to manage API keys.
//         */
//        if (!Places.isInitialized()) {
//            Places.initialize(getActivity().getApplicationContext(), "AIzaSyBEjPfwZEYB3XHyglA3fdRML_HbhPt-q3g");
//        }
//
//        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(AUTOTAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(AUTOTAG, "An error occurred: " + status);
//            }
//        });

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

//
//    private void setUpLocation() {
//        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            }, PERMISSION_REQUEST_CODE);
//        }
//        else
//        {
//            if(checkPlayServices())
//            {
//                buildGoogleApiClient();
//                createLocationRequest();
//                displayLocation();
//            }
//        }
//    }
//
//    private void displayLocation() {
//        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
//        {
//            return;
//        }
//        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if(mLocation!=null)
//        {
//            final double latitude = mLocation.getLatitude();
//            final double longitude = mLocation.getLongitude();
//
//            //show marker
//            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("your position"));
//            //Animate camera to your position
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
//        }
//    }
//
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//    }
//
//    private void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }
//
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if(resultCode != ConnectionResult.SUCCESS)
//        {
//            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_REQUEST).show();
//            else
//            {
//                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

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
        final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
        mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400"));

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
        }

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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btUsers = (Button) getView().findViewById(R.id.btnUsers);
        btUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Users.class);
                startActivity(i);
            }
        });
    }
}

