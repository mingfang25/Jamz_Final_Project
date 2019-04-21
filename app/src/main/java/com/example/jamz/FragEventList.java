package com.example.jamz;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.Query;

import java.util.ArrayList;

// Add import statements for the new library.

public class FragEventList extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private RecyclerView mEventRecyclerView;
    private RecyclerView.Adapter mvAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

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

    public FragEventList(){
        //Required empty public constructor
    }

    ArrayList<Event> events = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.eventsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();

        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Events");

        FirebaseRecyclerOptions<Event> options =
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(query, new SnapshotParser<Event>() {
                            @NonNull
                            @Override
                            public Event parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Event(snapshot.child("username").getValue().toString(),
                                        snapshot.child("eventname").getValue().toString(),
                                        snapshot.child("eventDescription").getValue().toString(),
                                        snapshot.child("eventFromStart").getValue().toString(),
                                        snapshot.child("eventFromEnd").getValue().toString(),
                                        Boolean.parseBoolean(snapshot.child("eventallday").getValue().toString()),
                                        snapshot.child("eventAddress").getValue().toString(),
                                        Double.parseDouble(snapshot.child("eventlatitude").getValue().toString()),
                                        Double.parseDouble(snapshot.child("eventlongitude").getValue().toString()),
                                        snapshot.child("UserPhotoURL").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Event, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_post, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Event event) {
                holder.EventTitle.setText(event.eventname);
                holder.EventAddress.setText(event.eventAddress);
                holder.EventTime.setText(event.eventFromStart);
                holder.postAuthor.setText(event.username);
                Glide.with(getActivity())
                        .load(event.UserPhotoURL)
                        .into(holder.AuthorPhoto);

                final String username = event.username;
                final String eventname = event.eventname;
                final String eventDescription = event.eventDescription;
                final String eventFromStart = event.eventFromStart;
                final String eventFromEnd = event.eventFromEnd;
                final boolean eventallday = event.eventallday;
                final String eventAddress = event.eventAddress;
                final double eventlatitude = event.eventlatitude;
                final double eventlongitude = event.eventlongitude;
                final String UserPhotoURL = event.UserPhotoURL;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),EventDetailActivity.class);
                        intent.putExtra("username",username);
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

        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView EventTitle;
        public TextView EventAddress;
        public TextView EventTime;
        public TextView postAuthor;
        public ImageView AuthorPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            EventTitle = itemView.findViewById(R.id.EventTitle);
            EventAddress = itemView.findViewById(R.id.EventAddress);
            EventTime = itemView.findViewById(R.id.EventTime);
            postAuthor = itemView.findViewById(R.id.usernameTxt);
            AuthorPhoto = itemView.findViewById(R.id.uProfileImgVw);
        }
    }
}

