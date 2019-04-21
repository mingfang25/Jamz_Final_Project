package com.example.jamz;


import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.AlphabeticIndex;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Users extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }



    public static class UserViewHolder extends RecyclerView.ViewHolder{

        TextView usernameTxt;
        ImageView uProfileImgVw;

        public UserViewHolder(View v) {
            super(v);

            usernameTxt = (TextView) itemView.findViewById(R.id.usernameTxt);
            uProfileImgVw = (ImageView) itemView.findViewById(R.id.uProfileImgVw);

        }

    }

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyUser, UserViewHolder>
            mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static final String TAG = "MainActivity";
    public static final String USERS_CHILD = "users";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private String mUsername;
    private String mPhotoUrl;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private TextView m_usernameTxt;
    private ImageView m_uProfileImgVw;
    private RecyclerView mUsersRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

// Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
// available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        // Initialize RecyclerView

        mUsersRecyclerView = (RecyclerView) findViewById(R.id.usersRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mUsersRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<FriendlyUser> parser = new SnapshotParser<FriendlyUser>() {
            @Override
            public FriendlyUser parseSnapshot(@NonNull DataSnapshot snapshot) {
                FriendlyUser friendlyUser = snapshot.getValue(FriendlyUser.class);
                if(friendlyUser != null) {
                    friendlyUser.setId(snapshot.getKey());
                }
                return friendlyUser;
            }
        };

        DatabaseReference usersRef = mFirebaseDatabaseReference.child(USERS_CHILD);

        FirebaseRecyclerOptions<FriendlyUser> options =
                new FirebaseRecyclerOptions.Builder<FriendlyUser>()
                        .setQuery(usersRef, parser)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyUser, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final UserViewHolder viewHolder, int position, FriendlyUser friendlyUser) {

                viewHolder.usernameTxt.setVisibility(TextView.VISIBLE);
                viewHolder.uProfileImgVw.setVisibility(ImageView.GONE);
                if(friendlyUser.getPhotoUrl() != null) {
                    String photoUrl = friendlyUser.getPhotoUrl();

                    if (photoUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(photoUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(viewHolder.uProfileImgVw.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.uProfileImgVw);
                                        } else {
                                            Log.w(TAG, "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.uProfileImgVw.getContext())
                                .load(friendlyUser.getPhotoUrl())
                                .into(viewHolder.uProfileImgVw);
                    }
                }
                if (friendlyUser.getDisplayName()=="" || friendlyUser.getDisplayName()==null) {
                    viewHolder.usernameTxt.setText("Anonymous User");
                } else{
                viewHolder.usernameTxt.setText(friendlyUser.getDisplayName());}

                if(friendlyUser.getPhotoUrl() == null){
                    viewHolder.uProfileImgVw.setImageDrawable(ContextCompat.getDrawable(Users.this, R.drawable.ic_account_circle_black_36dp));
                }
                else {
                    Glide.with(Users.this)
                            .load(friendlyUser.getPhotoUrl())
                            .into(viewHolder.uProfileImgVw);
                }
            }
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new UserViewHolder(inflater.inflate(R.layout.item_user,viewGroup,false));
            }
        };


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int friendlyUserCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyUserCount-1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    mUsersRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mUsersRecyclerView.setAdapter(mFirebaseAdapter);


    }

    private void fetchConfig() {

        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that
        // each fetch goes to the server. This should not be used in release
        // builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                }
            }
        }

    }


    @Override
    public void onResume() {
        mFirebaseAdapter.startListening();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fresh_config_menu:
                fetchConfig();
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


