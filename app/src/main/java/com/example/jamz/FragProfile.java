
package com.example.jamz;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Was able to change code around provided by a user on StackOverflow to fit out app.
// Code reference: https://stackoverflow.com/questions/49949527/tablayout-inside-fragment-app-stopped

public class FragProfile extends Fragment {

    private TabLayout profileTabLayout;
    private ViewPager profileViewPager;

    public FragProfile(){
        //Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profileTabLayout = (TabLayout) v.findViewById(R.id.profileTabLayout);
        profileViewPager = (ViewPager) v.findViewById(R.id.profileViewPager);
        ViewPageAdapter adapter = new ViewPageAdapter(getChildFragmentManager());

        adapter.AddFragment( new FragUserProfile(), "Profile");
        adapter.AddFragment( new FragSettings(), "Settings");

        profileViewPager.setAdapter(adapter);
        profileTabLayout.setupWithViewPager(profileViewPager);

        return v;
    }



}

