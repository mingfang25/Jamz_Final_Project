package com.example.jamz;

import android.app.Activity;
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
import android.util.Log;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Add import statements for the new library.

public class FragEventList extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //TICKETMASTER EVENTLIST
    private ArrayList<Event> mListData = new ArrayList<>();

    //RECYCLERVIEW
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
   // private FirebaseRecyclerAdapter adapter;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListData = ((NavigationActivity) activity).getTicketMasterEvents();
        int test =1;
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


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        public ArrayList<Event> mDataset;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView EventTitle;
            public TextView EventAddress;
            public TextView EventTime;
            public TextView postAuthor;
            public ImageView AuthorPhoto;

            public MyViewHolder(View itemView) {
                super(itemView);
                EventTitle = itemView.findViewById(R.id.EventTitle);
                EventAddress = itemView.findViewById(R.id.EventAddress);
                EventTime = itemView.findViewById(R.id.EventTime);
                postAuthor = itemView.findViewById(R.id.usernameTxt);
                AuthorPhoto = itemView.findViewById(R.id.uProfileImgVw);
            }
        }

//        // Provide a reference to the views for each data item
//        // Complex data items may need more than one view per item, and
//        // you provide access to all the views for a data item in a view holder
//        public static class MyViewHolder extends RecyclerView.ViewHolder {
//            // each data item is just a string in this case
//            public TextView textView;
//            public MyViewHolder(TextView v) {
//                super(v);
//                textView = v;
//            }
//        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<Event> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

            //                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
//                return new ViewHolder(view);
            // create a new view

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
//            TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_post, parent, false);

            MyViewHolder vh = new MyViewHolder(view);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.EventTitle.setText(mDataset.get(position).eventname);
            holder.EventAddress.setText(mDataset.get(position).eventAddress);
            holder.EventTime.setText(mDataset.get(position).eventFromStart);
            holder.postAuthor.setText(mDataset.get(position).username);
            Glide.with(getActivity())
                    .load(mDataset.get(position).UserPhotoURL)
                    .into(holder.AuthorPhoto);

            final String username = mDataset.get(position).username;
            final String eventname = mDataset.get(position).eventname;
            final String eventDescription = mDataset.get(position).eventDescription;
            final String eventFromStart = mDataset.get(position).eventFromStart;
            final String eventFromEnd = mDataset.get(position).eventFromEnd;
            final boolean eventallday = mDataset.get(position).eventallday;
            final String eventAddress = mDataset.get(position).eventAddress;
            final double eventlatitude = mDataset.get(position).eventlatitude;
            final double eventlongitude = mDataset.get(position).eventlongitude;
            final String UserPhotoURL = mDataset.get(position).UserPhotoURL;

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

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


    private void fetch() {

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

        events.addAll(mListData);

//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Events");



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


        adapter = new MyAdapter(events);

//
//        adapter = new FirebaseRecyclerAdapter<Event, ViewHolder>(options) {
//            @Override
//            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
//                return new ViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(ViewHolder holder, final int position, Event event) {
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
//
//        };
//

        int test = 1;
//        for(Event event: mListData) {
//            ViewHolder holder = new ViewHolder();
//            holder.EventTitle.setText(event.eventname);
//            holder.EventAddress.setText(event.eventAddress);
//            holder.EventTime.setText(event.eventFromStart);
//            holder.postAuthor.setText(event.username);
//        }
//        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //adapter.stopListening();
    }


}

