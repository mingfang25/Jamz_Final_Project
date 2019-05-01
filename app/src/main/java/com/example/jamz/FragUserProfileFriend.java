package com.example.jamz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.api.services.youtube.YouTube;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FragUserProfileFriend extends Fragment {

    public FragUserProfileFriend() {
        // Required empty public constructor
    }

    private String preferredName;
    private String instruments;
    private String userBio;

    private String get_info_username;

    public static String youtubeuser;
    private String user_youtube_url;

    //Components from XML file
    private ImageView profImageView;
    private TextView txtUserProf;
    private TextView txtInstrument;
    private TextView txtUserBio;
    private ImageButton messageImgBtn;
    private Button youtube;

    //Firebase references
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUsername;
    private String mPhotoURL;

    //String to get the current User's information
    private String currentUserID;

    public static int flag = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        get_info_username = ((ProfileActivity) activity).getVisitUsername();
        youtubeuser = get_info_username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_profile_friends, container, false);

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
                intent.putExtra("toName", get_info_username);
                startActivity(intent);
            }
        });


        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);
        txtInstrument = (TextView) view.findViewById(R.id.txtInstrument);


        // Load user's information onto their profile page
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        FriendlyUser user = snapshot.getValue(FriendlyUser.class);
                        String displayName = user.getDisplayName();
                        String photoURL = user.getPhotoUrl();

                        if(displayName.equals(get_info_username)){
                                txtUserProf.setText(displayName);
                                txtUserProf.setVisibility(TextView.VISIBLE);
                                if (photoURL != null){
                                    Glide.with(getActivity()).load(photoURL).into(profImageView);
                                    profImageView.setVisibility(ImageView.VISIBLE);
                                }
                                else{Picasso.with(getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(profImageView);
                                }
                                break;
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("UserInfo").child(get_info_username);

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
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

            }
        });





        databaseReference = FirebaseDatabase.getInstance().getReference("YouTubeInfo");

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        //YouTubeCID user = snapshot.getValue(YouTubeCID.class);
                        String username = (String) snapshot.child("username").getValue();
                        String YouTubeURL = (String) snapshot.child("YouTubeUrl").getValue();

                        //String YouTubeURL = user.YouTubeUrl;

                        if(username.equals(get_info_username)){
                            user_youtube_url = YouTubeURL;
                           // flag = 1;//find url
                        }
                    }

//                    txtUserBio.setText("Here Is a new Bio");
//                    txtUserBio.setVisibility(TextView.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        youtube = (Button) view.findViewById(R.id.youtube);
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), YoutubeActivity.class);
                i.putExtra("youtube",user_youtube_url);
                i.putExtra("userinfo",get_info_username);
                i.putExtra("way", "otheruser");
                startActivity(i);
            }
        });



        return view;
    }

}
