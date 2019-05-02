package com.example.jamz;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadMusicActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mUID;
    private String mPhotoUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private EditText txtAudioName;
    private Uri audioUri;

    //important file tracking variables
    public static final String FB_STORAGE_PATH="audio/";
    public static final String FB_DATABASE_PATH="audio";
    public static final String FB_HEAD = "gs://chat-demo-101.appspot.com/";
    public static final int REQUEST_CODE = 1234;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    //using requirement in AndroidManifest does not work in this case, so I check manually and add manually.
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    //add permission manually or will fail, add permission in manifest does not work here
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);

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
            mUID = mFirebaseUser.getUid();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        txtAudioName = findViewById(R.id.txtAudioName);

//        Button btnShow = findViewById(R.id.btnShowVideo);
//        btnShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(UploadMusicActivity.this, ShowVideo.class);
//                startActivity(i);
//            }
//        });
    }

    public void btnBrowse_Click(View v){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select audio"),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData()!=null){
            audioUri=data.getData();
        }
    }

    public String getAudioExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    String url_out;
    String s;
    String file_name;

    //upload button here, and track process
    public void btnUpload_Click(View v){
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            if (audioUri != null) {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Uploading audio");
                dialog.show();

                //important address info, use to retrieve
                s = FB_STORAGE_PATH + System.currentTimeMillis() + "." + getAudioExt(audioUri);
                file_name = txtAudioName.getText().toString();

                StorageReference ref;

                if(!getAudioExt(audioUri).equals("mp3") && !getAudioExt(audioUri).equals("mp4")) {
                    ref = mStorageRef.child(FB_STORAGE_PATH + file_name + ".mp3");
                }
                else {
                    ref = mStorageRef.child(FB_STORAGE_PATH + file_name + "." + getAudioExt(audioUri));
                }

                if (getAudioExt(audioUri).equals("3gpp"))
                {
                    ref = mStorageRef.child(FB_STORAGE_PATH + file_name + ".mp4");
                }

                ref.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Audio uploaded", Toast.LENGTH_SHORT).show();

                        final FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference storageRef;

                        if(!getAudioExt(audioUri).equals("mp3") && !getAudioExt(audioUri).equals("mp4")) {
                            // Create a storage reference from our app
                            storageRef = storage.getReferenceFromUrl(FB_HEAD + FB_STORAGE_PATH + file_name + ".mp3");
                        }
                        else{
                            storageRef = storage.getReferenceFromUrl(FB_HEAD + FB_STORAGE_PATH + file_name + "." + getAudioExt(audioUri));
                        }

                        if (getAudioExt(audioUri).equals("3gpp"))
                        {
                            storageRef = storage.getReferenceFromUrl(FB_HEAD + FB_STORAGE_PATH + file_name + ".mp4");
                        }

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Download url of file
                                final String url = uri.toString();
                                int test = 1;
                                url_out = url;

                                ImageUpload imageUpload;
                                if(getAudioExt(audioUri).equals("mp3") && getAudioExt(audioUri).equals("mp4")) {
                                    // Create a storage reference from our app
                                    imageUpload = new ImageUpload(file_name, url, mUsername, "mp3");
                                }
                                else{
                                    imageUpload = new ImageUpload(file_name, url, mUsername, getAudioExt(audioUri));
                                }

                                if (getAudioExt(audioUri).equals("3gpp"))
                                {
                                    imageUpload = new ImageUpload(file_name, url, mUsername, "mp4");
                                }
                                //ImageUpload imageUpload = new ImageUpload(file_name, url, mUsername, getAudioExt(audioUri));

                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(file_name).setValue(imageUpload);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("TAG", e.getMessage());
                                    }
                                });

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Select", Toast.LENGTH_SHORT).show();
            }

            fetchAudioUrlFromFirebase();
        }
    }

    private void fetchAudioUrlFromFirebase() {

    }

    public void btnShowListAudio_Click(View v){
        Intent i = new Intent(UploadMusicActivity.this, NavigationActivity.class);
        startActivity(i);
    }
}
