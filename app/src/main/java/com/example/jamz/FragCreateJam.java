package com.example.jamz;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class FragCreateJam extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private FragmentActivity myContext;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mPhotoUrl;

    //auto complete map
    private static final String AUTOTAG = "AUTOTAG";
    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;
    private static final int UPDATE_INTERVAL = 5000;//5 detik
    private static final int FASTEST_INTERVAL = 3000;//3detik
    private static final int DISPLACEMENT = 10;
    private com.google.android.libraries.places.widget.AutocompleteSupportFragment AutocompleteSupportFragment;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    Marker marker;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

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

    public FragCreateJam(){
        //Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create_jam, null, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyBEjPfwZEYB3XHyglA3fdRML_HbhPt-q3g");
        }

        // Create a new Places client instance.
        final PlacesClient placesClient = Places.createClient(getActivity());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(AUTOTAG, "Place: " + place.getName() + ", " + place.getId());

                EventLat = place.getLatLng();
                EventAddress = place.getAddress();
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap
                        (BitmapFactory.decodeResource(getResources(), R.mipmap.ic_user_location))).position(EventLat).title("Position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EventLat, 14));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(AUTOTAG, "An error occurred: " + status);
            }
        });

        // Map part
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        UserName = mAuth.getCurrentUser().getDisplayName();


        final EditText edteventname = (EditText) getView().findViewById(R.id.editEventName);
        final EditText edteventdescription = (EditText) getView().findViewById(R.id.editDescription);
        final EditText edtFromStart = (EditText) getView().findViewById(R.id.editStart);
        final EditText edtFromEnd = (EditText) getView().findViewById(R.id.editEnd);
        //final EditText edtToStart = (EditText) getView().findViewById(R.id.editToStart);
        //final EditText edtToEnd = (EditText) getView().findViewById(R.id.editToEnd);

        edteventname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edteventname.setText("");
            }
        });

        edteventdescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edteventdescription.setText("");
            }
        });
        edtFromStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edtFromStart.setText("");
            }
        });
        edtFromEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edtFromEnd.setText("");
            }
        });

        Button btnCreateJam = (Button)getView().findViewById(R.id.btnAdd);
        btnCreateJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), UserName, Toast.LENGTH_SHORT);
                EventName = edteventname.getText().toString() ;
                EventDescription = edteventdescription.getText().toString();
                EventFromStart = edtFromStart.getText().toString();
                EventFromEnd = edtFromEnd.getText().toString();
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                //EventToStart = edtToStart.getText().toString();
                //EventToEnd = edtToEnd.getText().toString();
                basicReadWrite();
                Toast.makeText(getActivity(), "Event Created", Toast.LENGTH_SHORT);
            }
        });
    }

    private static final String TAG = "FragCreateJam";

    public void basicReadWrite() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Event event = new Event(UserName,EventName,EventDescription, EventFromStart, EventFromEnd, Eventallday,EventAddress,EventLat.latitude,EventLat.longitude,mPhotoUrl);
        mDatabase.child("Events").child(event.eventname).setValue(event);
        //mDatabase.child("GroupChat").child(event.eventname).setValue(0);
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
