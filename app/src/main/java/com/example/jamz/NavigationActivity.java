package com.example.jamz;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

// Created a Bottom Navigation layout with the help of this resource: https://www.simplifiedcoding.net/bottom-navigation-android-example/#Creating-Fragments-2

public class NavigationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //loading the default fragment which is the Discover page
        loadFragment(new FragCreateJam());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }


    // This method will load the Fragment selected by User
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;

    }


    // This method will navigate to different fragments as they are selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_create_jam:
                fragment = new FragCreateJam();
                break;

            case R.id.navigation_discover:
                fragment = new FragDiscover();
                break;

            case R.id.navigation_friends:
                fragment = new FragFriends();
                break;

            case R.id.navigation_profile:
                fragment = new FragProfile();
                break;

        }

        return loadFragment(fragment);
    }
}
