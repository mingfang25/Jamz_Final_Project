package com.example.jamz;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Set;


public class FragUserProfile extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    public FragUserProfile() {
        //Required empty public constructor
    }

    //Components from XML file
    private ImageView profImageView;
    private TextView txtUserProf;
    private TextView txtInstrument;
    private TextView txtUserBio;
    private ImageButton messageImgBtn;

    //Firebase references
    private DatabaseReference databaseReference;
    private DatabaseReference prefNameRef;
    private DatabaseReference bioRef;
    private DatabaseReference instrumentRef;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String preferredName;
    private String mUsername;
    private String mPhotoURL;
    private String instruments;
    private String userBio;

    //String to get the current User's information
    private String currentUserID;

    private Button youtube;

    private Button spotify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //show menu options in action bar
        setHasOptionsMenu(true);

        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_profile, container, false);

        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);
        messageImgBtn = (ImageButton) view.findViewById(R.id.messageImgBtn);
        txtUserBio = (TextView) view.findViewById(R.id.txtUserBio);
        txtInstrument = (TextView) view.findViewById(R.id.txtInstrument);

        messageImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserChatActivity.class);
                intent.putExtra("displayName",mUsername);
                intent.putExtra("toName", mUsername);
                startActivity(intent);
            }
        });


        mAuth = FirebaseAuth.getInstance();
//        currentUserID = mAuth.getCurrentUser().getDisplayName();
//        mUsername = mAuth.getCurrentUser().getDisplayName();
//        mPhotoURL = mAuth.getCurrentUser().getPhotoUrl().toString();

        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);
        txtInstrument = (TextView) view.findViewById(R.id.txtInstrument);


        mUser = mAuth.getCurrentUser();
        if (mUser == null){
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        else {
            currentUserID = mAuth.getCurrentUser().getDisplayName();
            mUsername = mUser.getDisplayName();
            if (mUser.getPhotoUrl() != null){
            mPhotoURL = mAuth.getCurrentUser().getPhotoUrl().toString();
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mUsername);
        prefNameRef = databaseReference.child("altdisplayname");
        bioRef = databaseReference.child("userbio");
        instrumentRef = databaseReference.child("userinstruments");



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    preferredName = dataSnapshot.child("altdisplayname").getValue().toString();
                    if (preferredName != null) {
                        txtUserProf.setText(preferredName);
                    } else {
                        txtUserProf.setText(mUsername);
                    }
                    userBio = dataSnapshot.child("userbio").getValue().toString();
                    if (userBio != null) {
                        txtUserBio.setText(userBio);
                    } else {
                        txtUserBio.setText("Nothing to say, yet.");
                    }
                    instruments = dataSnapshot.child("userinstruments").getValue().toString();
                    if (instruments != null) {
                        txtInstrument.setText(instruments);
                    } else {
                        txtInstrument.setText("No Instruments, yet.");
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database read failed", Toast.LENGTH_SHORT).show();

            }
        });

        if (mUsername != null) {
                           txtUserProf.setVisibility(TextView.VISIBLE);
                           if (mPhotoURL != null){
                           Glide.with(getActivity()).load(mPhotoURL).into(profImageView);
                           profImageView.setVisibility(ImageView.VISIBLE);
                           }
                           else{Picasso.with(getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(profImageView);
                           }
                    }
//        if (userBio != null) {
//            txtUserBio.setText(userBio);
//        } else{txtUserBio.setText("This User Doesn't Have A Bio, Yet");}
//        if (instruments != null) {
//            txtInstrument.setText(instruments);
//        } else {txtUserBio.setText("This User Doesn't Play An Instrument, Yet");}


        youtube = (Button) view.findViewById(R.id.youtube);
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ChannelIdActivity.class);
                i.putExtra("way", "myself");

                startActivity(i);
                // Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, 1, this::onConnectionFailed /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        spotify = (Button) view.findViewById(R.id.to_spotify_activity);
        spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SpotifyPersonalization.class);
                startActivity(i);
            }
        });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                startActivity(new Intent(getActivity(), Settings.class));
                return true;
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(getActivity(), MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Settings", "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
