
package com.example.jamz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
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


// Was able to change code around provided by a user on StackOverflow to fit out app.
// Code reference: https://stackoverflow.com/questions/49949527/tablayout-inside-fragment-app-stopped

public class FragProfile extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private TabLayout profileTabLayout;
    private ViewPager profileViewPager;
    private static final String TAG = "FragProfile";

    private GoogleApiClient mGoogleApiClient;

    public FragProfile(){
        //Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profileTabLayout = (TabLayout) v.findViewById(R.id.profileTabLayout);
        profileViewPager = (ViewPager) v.findViewById(R.id.profileViewPager);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        ViewPageAdapter adapter = new ViewPageAdapter(getChildFragmentManager());

        adapter.AddFragment( new FragUserProfile(), "Profile");
        adapter.AddFragment( new FragMusicList(), "Music List");
        adapter.AddFragment(new FragMusicList(), "Recent Listens");
        adapter.AddFragment(new FragMusicList(), "Videos");

        profileViewPager.setAdapter(adapter);
        profileTabLayout.setupWithViewPager(profileViewPager);

        return v;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if (getActivity().getMenuInflater() != null) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            return true;
        }else{return false;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                startActivity(new Intent(getActivity(), FragSettings.class));
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(getActivity(), MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

