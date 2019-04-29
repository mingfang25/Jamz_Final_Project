package com.example.jamz;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.jamz.adapters.PagerAdapter;

public class YoutubeActivity extends AppCompatActivity {

    public static String get_youtube_url;

    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        Bundle bundle = getIntent().getExtras();
        get_youtube_url = bundle.getString("youtube");

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //setting the tabs title
        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
        // tabLayout.addTab(tabLayout.newTab().setText("PlayList"));
        //tabLayout.addTab(tabLayout.newTab().setText("Live"));

        //setup the view pager
        final PagerAdapter adapter = new com.example.jamz.adapters.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }}
