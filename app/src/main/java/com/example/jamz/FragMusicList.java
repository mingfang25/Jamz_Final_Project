package com.example.jamz;

import android.support.v4.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragMusicList extends Fragment {

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

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //upload audio function
    private void chooseAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_AUDIO_REQUEST);
    }


    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        initialStage = true;
                        playPause = false;
                        btnplay.setText("Play");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepare();
                prepared = true;

            } catch (Exception e) {
                Log.e("playApp", e.getMessage());
                prepared = false;
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            mediaPlayer.start();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }

    public FragMusicList(){
        //Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Initialize Views


        //music play and pause settings
        //btnplay = (Button) getView().findViewById(R.id.btnPlay);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(getActivity());
        //play/pause button settings
//        btnplay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!playPause) {
//                    btnplay.setText("Pause");
//
//                    if (initialStage) {
//                        try {
//                            new Player().execute("https://firebasestorage.googleapis.com/v0/b/video-16d9e.appspot.com/o/audios%2Fe5a86aa3-288a-4f48-9f2e-7acaff8d6270?alt=media&token=e6875e1c-3ada-447d-90ea-f54d9c1617df");
//                        }catch(Exception e){
//                            Log.e("error_play", e .getMessage());
//                        }
//                    } else {
//                        if (!mediaPlayer.isPlaying())
//                            mediaPlayer.start();
//                    }
//
//                    playPause = true;
//
//                } else {
//                    btnplay.setText("Play");
//
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                    }
//
//                    playPause = false;
//                }
//            }
//        });


        // SHOW MUSIC LIST!!!
        imgList = new ArrayList<>();
        lv = (ListView) getView().findViewById(R.id.ListViewImage);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("wait loading");
        progressDialog.show();

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header,lv,false);
        lv.addHeaderView(header);
//
//        lv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int test = 1;
//
////                if (!playPause) {
////                    btnplay.setText("Pause");
////
////                    if (initialStage) {
////                        try {
////                            new Player().execute("https://firebasestorage.googleapis.com/v0/b/video-16d9e.appspot.com/o/audios%2Fe5a86aa3-288a-4f48-9f2e-7acaff8d6270?alt=media&token=e6875e1c-3ada-447d-90ea-f54d9c1617df");
////                        }catch(Exception e){
////                            Log.e("error_play", e .getMessage());
////                        }
////                    } else {
////                        if (!mediaPlayer.isPlaying())
////                            mediaPlayer.start();
////                    }
////
////                    playPause = true;
////
////                } else {
////                    btnplay.setText("Play");
////
////                    if (mediaPlayer.isPlaying()) {
////                        mediaPlayer.pause();
////                    }
////
////                    playPause = false;
////                }
//            }
//        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(UploadMusicActivity.FB_DATABASE_PATH);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ImageUpload img= snapshot.getValue(ImageUpload.class);
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

                if (!playPause) {
                    //btnplay.setText("Pause");

                    ImageUpload obj = (ImageUpload)lv.getAdapter().getItem(position);
                    String file_url = (String) obj.url;
                    Log.d("Yourtag", file_url);

                    //Log.d("MyLog", "Value is: "+value);
                    int i = 0;

                    if (initialStage) {
                        try {
                            new Player().execute(file_url);
                        }catch(Exception e){
                            Log.e("error_play", e .getMessage());
                        }
                    } else {
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }

                    playPause = true;

                } else {
                    //btnplay.setText("Play");

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }

                    playPause = false;
                }
                Toast.makeText(getActivity(),"click", Toast.LENGTH_SHORT).show();
            }
        });



        Button btupload = (Button) getView().findViewById(R.id.btnUpload);
        btupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UploadMusicActivity.class);
                mediaPlayer.stop();
                startActivity(i);
            }
        });

    }


}