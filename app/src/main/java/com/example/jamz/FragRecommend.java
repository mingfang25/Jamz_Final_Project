package com.example.jamz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragRecommend extends Fragment implements
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
    private com.google.android.libraries.places.widget.AutocompleteSupportFragment AutocompleteSupportFragment;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    Marker marker;

    public FragRecommend(){
        //Required empty public constructor
    }

    ArrayList<Event> events = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_recommend, container, false);

        recyclerView = view.findViewById(R.id.eventsList_Recommend);
        linearLayoutManager = new LinearLayoutManager(getContext());
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

    public class MyAdapter extends RecyclerView.Adapter<FragRecommend.MyAdapter.MyViewHolder> {
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

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<Event> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FragRecommend.MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            FragRecommend.MyAdapter.MyViewHolder vh = new FragRecommend.MyAdapter.MyViewHolder(view);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(FragRecommend.MyAdapter.MyViewHolder holder, int position) {
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
        events.addAll(mListData);

        adapter = new FragRecommend.MyAdapter(events);

        int test = 1;

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
