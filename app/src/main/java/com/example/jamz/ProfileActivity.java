package com.example.jamz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TabLayout profileTabLayout;
    private ViewPager profileViewPager;
    private static final String TAG = "FragProfile";

    private String visit_username;

    private GoogleApiClient mGoogleApiClient;

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
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    // Pass DATA to FRAGMENT
    public String getVisitUsername(){
        return visit_username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        visit_username = bundle.getString("toName");

        profileTabLayout = (TabLayout) findViewById(R.id.profileTabLayout);
        profileViewPager = (ViewPager) findViewById(R.id.profileViewPager);

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

        adapter.AddFragment( new FragUserProfileFriend(), "Profile");
        adapter.AddFragment( new FragMusicListFriend(), "Music");
        adapter.AddFragment(new FragVideoListFriend(), "Videos");

        profileViewPager.setAdapter(adapter);
        profileTabLayout.setupWithViewPager(profileViewPager);
    }
}
