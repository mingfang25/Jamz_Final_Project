package com.example.jamz.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.jamz.fragments.ChannelFragment;



public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0) {

            ChannelFragment tab1 = new ChannelFragment();
            return tab1;

        }
        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
