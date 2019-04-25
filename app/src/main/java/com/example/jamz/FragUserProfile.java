package com.example.jamz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        currentUserID = mAuth.getCurrentUser().getUid();
        mUsername = mAuth.getCurrentUser().getDisplayName();
       // mPhotoURL = mUser.getPhotoUrl().toString();

        profImageView = (ImageView) view.findViewById(R.id.profImageView);
        txtUserProf = (TextView) view.findViewById(R.id.txtUserProf);

        mUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        else {
            currentUserID = mAuth.getCurrentUser().getUid();
            mUsername = mUser.getDisplayName();
            if (mUser.getPhotoUrl() != null){
            mPhotoURL = mUser.getPhotoUrl().toString();
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (mUsername != null && mPhotoURL !=null) {
                        String displayName = dataSnapshot.child(mUsername).getValue().toString();
                        String photoURL = dataSnapshot.child(mPhotoURL).getValue().toString();
                        Log.d("displayName", displayName);
                        txtUserProf.setText(displayName);
                        txtUserProf.setVisibility(TextView.VISIBLE);
                        Glide.with(getActivity()).load(photoURL).into(profImageView);
                        profImageView.setVisibility(ImageView.VISIBLE);
                    }

                    if (mUsername != null && mPhotoURL != null) {
                           txtUserProf.setText(mUsername);
                           txtUserProf.setVisibility(TextView.VISIBLE);
                           Glide.with(getActivity()).load(mPhotoURL).into(profImageView);
                           profImageView.setVisibility(ImageView.VISIBLE);
                    }

                    txtUserBio.setText("Here Is a new Bio");
                    txtUserBio.setVisibility(TextView.VISIBLE);

                //    Picasso.with(getContext()).load(photoURL).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(profImageView);
                    //profImageView.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), Integer.parseInt(photoURL)));

                }

//                else {
//                    profImageView.setVisibility(ImageView.GONE);
//                    txtUserProf.setVisibility(TextView.GONE);
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
