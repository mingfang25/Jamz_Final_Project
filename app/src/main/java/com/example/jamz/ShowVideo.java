package com.example.jamz;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowVideo extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private VideoView videoView;
    private Button bplay, stop;
    private String file_url;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.showvideo);

        Bundle bundle = getIntent().getExtras();
        file_url = bundle.getString("url");

        Firebase.setAndroidContext(this);

        videoView = (VideoView) findViewById(R.id.video);
        bplay = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.pause);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(UploadMusicActivity.FB_DATABASE_PATH);

        String str = file_url;
        Uri uri = Uri.parse(str);

        videoView.setVideoURI(uri);
        videoView.requestFocus();

        bplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
            }
        });

    }
}
