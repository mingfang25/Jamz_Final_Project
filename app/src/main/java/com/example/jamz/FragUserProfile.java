package com.example.jamz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Set;


public class FragUserProfile extends Fragment {

    public FragUserProfile() {
        //Required empty public constructor
    }

    //Components from XML file
    private ImageView profImageView;
    private TextView txtUserProf;
    private TextView txtInstrument;
    private TextView txtUserBio;
    private ImageButton messageImgBtn;
    private ImageButton preferencesImgBtn;

    //Firebase references
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUsername;
    private String mPhotoURL;

    //String to get the current User's information
    private String currentUserID;


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
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });


        mAuth = FirebaseAuth.getInstance();
//        currentUserID = mAuth.getCurrentUser().getDisplayName();
//        mUsername = mAuth.getCurrentUser().getDisplayName();
//        mPhotoURL = mAuth.getCurrentUser().getPhotoUrl().toString();

        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);

        mUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        else {
            currentUserID = mAuth.getCurrentUser().getDisplayName();
            mUsername = mUser.getDisplayName();
            if (mUser.getPhotoUrl() != null){
            mPhotoURL = mAuth.getCurrentUser().getPhotoUrl().toString();
            }
        }

        //Preferences button should only be visible to current user on their profile
        if (currentUserID == mUsername) {
            preferencesImgBtn.setVisibility(ImageButton.VISIBLE);
        } else {preferencesImgBtn.setVisibility(ImageButton.GONE);}


        if (mUsername != null) {
                           txtUserProf.setText(mUsername);
                           txtUserProf.setVisibility(TextView.VISIBLE);
                           if (mPhotoURL != null){
                           Glide.with(getActivity()).load(mPhotoURL).into(profImageView);
                           profImageView.setVisibility(ImageView.VISIBLE);
                           }
                           else{Picasso.with(getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(profImageView);
                           }
                    }

//        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(mUsername);
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//
//                    String displayName = dataSnapshot.child(mUsername).getValue().toString();
//                    String photoURL = dataSnapshot.child(mPhotoURL).getValue().toString();
//                    Log.d("displayName", displayName);
//                    Log.d("photoURL", photoURL);
//
//                    if (mUsername != null) {
//                           txtUserProf.setText(mUsername);
//                           txtUserProf.setVisibility(TextView.VISIBLE);
//                           if (mPhotoURL != null){
//                           Glide.with(getActivity()).load(mPhotoURL).into(profImageView);
//                           profImageView.setVisibility(ImageView.VISIBLE);
//                           }
//                           else{Picasso.with(getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(profImageView);
//                           }
//                    }
//
//                    txtUserBio.setText("Here Is a new Bio");
//                    txtUserBio.setVisibility(TextView.VISIBLE);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }

}
