package com.example.jamz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragFriends extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("TAG", "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static final String TAG = "MainActivity";
    public static final String USERS_CHILD = "users";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private String mUsername;
    private String mPhotoUrl;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    public FragFriends(){
        //Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);


//        // Initialize Firebase Auth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            if (mFirebaseUser.getPhotoUrl() != null) {
//                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//            }
//        }
//
//
//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//
//        // Initialize Firebase Remote Config.
//        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//
//// Define Firebase Remote Config Settings.
//        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
//                new FirebaseRemoteConfigSettings.Builder()
//                        .setDeveloperModeEnabled(true)
//                        .build();
//
//        // Define default config values. Defaults are used when fetched config values are not
//// available. Eg: if an error occurred fetching values from the server.
//        Map<String, Object> defaultConfigMap = new HashMap<>();
//
//        // Apply config settings and default values.
//        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
//        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
//
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        SnapshotParser<FriendlyUser> parser = new SnapshotParser<FriendlyUser>() {
//            @Override
//            public FriendlyUser parseSnapshot(@NonNull DataSnapshot snapshot) {
//                FriendlyUser friendlyUser = snapshot.getValue(FriendlyUser.class);
//                if(friendlyUser != null) {
//                    friendlyUser.setId(snapshot.getKey());
//                }
//                return friendlyUser;
//            }
//        };
//
//        DatabaseReference usersRef = mFirebaseDatabaseReference.child(USERS_CHILD);
//
//        FirebaseRecyclerOptions<FriendlyUser> options =
//                new FirebaseRecyclerOptions.Builder<FriendlyUser>()
//                        .setQuery(usersRef, parser)
//                        .build();
//
//        // Fetch remote config.
//        fetchConfig();
//
//
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();
//
//
//        // initialize user
//        recyclerView = view.findViewById(R.id.eventsList);
//        linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);
//        fetch();

        return view;
    }

//    private void fetch() {
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Events");
//
//        FirebaseRecyclerOptions<Event> options =
//                new FirebaseRecyclerOptions.Builder<Event>()
//                        .setQuery(query, new SnapshotParser<Event>() {
//                            @NonNull
//                            @Override
//                            public Event parseSnapshot(@NonNull DataSnapshot snapshot) {
//                                return new Event(snapshot.child("username").getValue().toString(),
//                                        snapshot.child("eventname").getValue().toString(),
//                                        snapshot.child("eventDescription").getValue().toString(),
//                                        snapshot.child("eventFromStart").getValue().toString(),
//                                        snapshot.child("eventFromEnd").getValue().toString(),
//                                        Boolean.parseBoolean(snapshot.child("eventallday").getValue().toString()),
//                                        snapshot.child("eventAddress").getValue().toString(),
//                                        Double.parseDouble(snapshot.child("eventlatitude").getValue().toString()),
//                                        Double.parseDouble(snapshot.child("eventlongitude").getValue().toString()),
//                                        snapshot.child("UserPhotoURL").getValue().toString());
//                            }
//                        })
//                        .build();
//
//        adapter = new FirebaseRecyclerAdapter<Event, FragFriends.ViewHolder>(options) {
//            @Override
//            public FragFriends.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.item_post, parent, false);
//                return new FragFriends.ViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(FragFriends.ViewHolder holder, final int position, Event event) {
//                holder.EventTitle.setText(event.eventname);
//                holder.EventAddress.setText(event.eventAddress);
//                holder.EventTime.setText(event.eventFromStart);
//                holder.postAuthor.setText(event.username);
//                Glide.with(getActivity())
//                        .load(event.UserPhotoURL)
//                        .into(holder.AuthorPhoto);
//
//                final String username = event.username;
//                final String eventname = event.eventname;
//                final String eventDescription = event.eventDescription;
//                final String eventFromStart = event.eventFromStart;
//                final String eventFromEnd = event.eventFromEnd;
//                final boolean eventallday = event.eventallday;
//                final String eventAddress = event.eventAddress;
//                final double eventlatitude = event.eventlatitude;
//                final double eventlongitude = event.eventlongitude;
//                final String UserPhotoURL = event.UserPhotoURL;
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent=new Intent(getActivity(),EventDetailActivity.class);
//                        intent.putExtra("username",username);
//                        intent.putExtra("eventname",eventname);
//                        intent.putExtra("eventDescription",eventDescription);
//                        intent.putExtra("eventFromStart",eventFromStart);
//                        intent.putExtra("eventFromEnd",eventFromEnd);
//                        intent.putExtra("eventallday",eventallday);
//                        intent.putExtra("eventAddress",eventAddress);
//                        intent.putExtra("eventlatitude",eventlatitude);
//                        intent.putExtra("eventlongitude",eventlongitude);
//                        intent.putExtra("UserPhotoURL",UserPhotoURL);
//                        startActivity(intent);
//                    }
//                });
//            }
//        };
//        recyclerView.setAdapter(adapter);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView EventTitle;
//        public TextView EventAddress;
//        public TextView EventTime;
//        public TextView postAuthor;
//        public ImageView AuthorPhoto;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            EventTitle = itemView.findViewById(R.id.EventTitle);
//            EventAddress = itemView.findViewById(R.id.EventAddress);
//            EventTime = itemView.findViewById(R.id.EventTime);
//            postAuthor = itemView.findViewById(R.id.usernameTxt);
//            AuthorPhoto = itemView.findViewById(R.id.uProfileImgVw);
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btchat = (Button) getView().findViewById(R.id.btnChat);
        btchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ChatActivity.class);
                startActivity(i);
            }
        });

        Button btnUsers = (Button) getView().findViewById(R.id.btnUsers);
        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Users.class);
                startActivity(i);
            }
        });
    }




}
