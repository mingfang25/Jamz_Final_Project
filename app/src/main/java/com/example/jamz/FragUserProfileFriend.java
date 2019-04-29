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

    private String get_info_username;
    private String user_youtube_url;

    //Components from XML file
    private ImageView profImageView;
    private TextView txtUserProf;
    private TextView txtInstrument;
    private TextView txtUserBio;
    private ImageButton messageImgBtn;
    private ImageButton preferencesImgBtn;
    private Button youtube;

    //Firebase references
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUsername;
    private String mPhotoURL;

    //String to get the current User's information
    private String currentUserID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        get_info_username = ((ProfileActivity) activity).getVisitUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_profile, container, false);

        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);
        messageImgBtn = (ImageButton) view.findViewById(R.id.messageImgBtn);
        preferencesImgBtn = (ImageButton) view.findViewById(R.id.preferencesImgBtn);

        messageImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserChatActivity.class);
                intent.putExtra("displayName",mUsername);
                intent.putExtra("toName", mUsername);
                startActivity(intent);
            }
        });

        //Preferences page for User
        preferencesImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FragSettings.class);
                startActivity(intent);
            }
        });

        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);

        //Preferences button should only be visible to current user on their profile
        if (currentUserID == mUsername) {
            preferencesImgBtn.setVisibility(ImageButton.VISIBLE);
        } else {preferencesImgBtn.setVisibility(ImageButton.GONE);}

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

//                    txtUserBio.setText("Here Is a new Bio");
//                    txtUserBio.setVisibility(TextView.VISIBLE);
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
                startActivity(i);
                // Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

}
