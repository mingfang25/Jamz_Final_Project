package com.example.jamz;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.jamz.adapters.PagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class YoutubeActivity extends AppCompatActivity {

    public static String get_youtube_url;
    public static String userinfo;
    public static String way="";


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;



    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        Bundle b = getIntent().getExtras();
        way = b.getString("way");

//        if(FragUserProfileFriend.flag ==1) {// if has url
        if(way.equals("myself"))
        {
//            Toast.makeText(this, "your", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Bundle bundle = getIntent().getExtras();
            get_youtube_url = bundle.getString("youtube");
            userinfo = bundle.getString("userinfo");
            way = bundle.getString("way");
            //Toast.makeText(this, mUsername, Toast.LENGTH_SHORT).show();
        }

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
