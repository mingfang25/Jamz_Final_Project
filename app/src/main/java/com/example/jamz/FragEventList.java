package com.example.jamz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Add import statements for the new library.

public class FragEventList extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

private TabLayout EventTabLayout;
private ViewPager EventViewPager;
private static final String TAG = "FragEventList";

public FragEventList(){
        //Required empty public constructor

        }



@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

    //don't show menu options in action bar
    setHasOptionsMenu(false);


        // Inflate layout
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);

        EventTabLayout = (TabLayout) v.findViewById(R.id.EventTabLayout);
        EventViewPager = (ViewPager) v.findViewById(R.id.EventViewPager);

        ViewPageAdapter adapter = new ViewPageAdapter(getChildFragmentManager());

        adapter.AddFragment( new FragRecommend(), "Recommend");
        adapter.AddFragment( new FragJAMZ(), "JAMZ");

        EventViewPager.setAdapter(adapter);
        EventTabLayout.setupWithViewPager(EventViewPager);

        return v;
        }


@Override
public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
        }

@Override
public void onPause() {
        super.onPause();

        }

@Override
public void onResume() {
        super.onResume();
        }

@Override
public void onDestroy() {
        super.onDestroy();

        }

@Override
public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }
}

