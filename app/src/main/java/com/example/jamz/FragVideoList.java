package com.example.jamz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FragVideoList extends Fragment {

    private String get_info_username;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mUID;
    private String mPhotoUrl;

    private DatabaseReference mDatabaseRef;
    private List<ImageUpload> imgList;
    private ListView lv;
    private ImageListAdapter adapter;
    private ProgressDialog progressDialog;

    //Play music!!!!!!!!!!!
    //Variables
    private Uri filePath;
    private final int PICK_AUDIO_REQUEST = 71;
    private Button btnplay,btnstop;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private boolean initialStage = true;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;




    //upload audio function
    private void chooseAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_AUDIO_REQUEST);
    }

    public FragVideoList(){
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mUID = mFirebaseUser.getUid();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        if(get_info_username == null)
            get_info_username = mUsername;

        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Initialize Views


        //music play and pause settings
        //btnplay = (Button) getView().findViewById(R.id.btnPlay);

        progressDialog = new ProgressDialog(getActivity());
        //play/pause button settings

        // SHOW MUSIC LIST!!!
        imgList = new ArrayList<>();
        lv = (ListView) getView().findViewById(R.id.MediaListViewImage);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("wait loading");
        progressDialog.show();

//        LayoutInflater inflater = getLayoutInflater();
//        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_video_header,lv,false);
//        lv.addHeaderView(header);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(UploadMusicActivity.FB_DATABASE_PATH);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ImageUpload img= snapshot.getValue(ImageUpload.class);
                    if(img.username.equals(mUsername))
                    if(img.type.equals("mp4"))
                        imgList.add(img);
                }

                adapter = new ImageListAdapter(getActivity(), R.layout.image_item,imgList);

                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Intent i = new Intent(getActivity(), ShowVideo.class);
                    ImageUpload obj = (ImageUpload) lv.getAdapter().getItem(position);
                    String file_url = (String) obj.url;
                    i.putExtra("url", file_url);
                    startActivity(i);
                }
            }
        });

        Button btupload = (Button) getView().findViewById(R.id.btnUpload);
        btupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UploadMusicActivity.class);
                startActivity(i);
            }
        });

    }


}
