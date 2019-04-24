package com.example.jamz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class FragUserProfile extends Fragment {

    public FragUserProfile() {
        //Required empty public constructor
    }

    //Components from XML file
    private ImageView profImageView;
    private TextView txtUserProf;


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

//        profImageView = (ImageView) getView().findViewById(R.id.profImageView);
//        txtUserProf = (TextView) getView().findViewById(R.id.txtUserProf);

        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mUsername = mAuth.getCurrentUser().getDisplayName();
       // mPhotoURL = mUser.getPhotoUrl().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);

        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String displayName = dataSnapshot.child(mUsername).getValue().toString();
                    String photoURL = dataSnapshot.child(mPhotoURL).getValue().toString();

                //    Picasso.with(getContext()).load(photoURL).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(profImageView);
                    //profImageView.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), Integer.parseInt(photoURL)));

                 //   txtUserProf.setText(displayName);

                }


             //   txtUserProf.setText(mUsername);
           //     Glide.with(getActivity()).load(mPhotoURL).into(profImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}
