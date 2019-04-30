package com.example.jamz;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.internal.v;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Settings extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private EditText nameEdtText;
    private CheckBox bassCB;
    private CheckBox drumCB;
    private CheckBox fluteCB;
    private CheckBox guitaraCB;
    private CheckBox guitareCB;
    private CheckBox pianoCB;
    private CheckBox saxophoneCB;
    private CheckBox violinCB;
    private CheckBox voiceCB;
    private EditText userbioEdtTxt;
    private Button saveBtn;
    private DatabaseReference mFirebaseDatabaseReference;
    StringBuffer instruments;
    String userInstruments;
    private String mUsername;
    private String userBio;
    private String nameChange;


    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

//        signOutbtn = (Button) findViewById(R.id.signOutbtn);
//        signOutbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//            }
//        });


        mUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        nameEdtText = (EditText) findViewById(R.id.nameEdtTxt);
        bassCB = (CheckBox) findViewById(R.id.bassCB);
        drumCB = (CheckBox) findViewById(R.id.drumCB);
        fluteCB = (CheckBox) findViewById(R.id.fluteCB);
        guitaraCB = (CheckBox) findViewById(R.id.guitaraCB);
        guitareCB = (CheckBox) findViewById(R.id.guitareCB);
        pianoCB = (CheckBox) findViewById(R.id.pianoCB);
        saxophoneCB = (CheckBox) findViewById(R.id.saxophoneCB);
        violinCB = (CheckBox) findViewById(R.id.violinCB);
        voiceCB = (CheckBox) findViewById(R.id.voiceCB);
        userbioEdtTxt = (EditText) findViewById(R.id.userbioEdtTxt);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        instruments = new StringBuffer();


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bassCB.isChecked()==true){
                    instruments.append("Bass ");
                }
                if (drumCB.isChecked()==true){
                    instruments.append("Drums ");
                }
                if (fluteCB.isChecked()==true){
                    instruments.append("Flute ");
                }
                if (guitaraCB.isChecked()==true){
                    instruments.append("Guitar(Acoustic) ");
                }
                if (guitareCB.isChecked()==true){
                    instruments.append("Guitar(Electric) ");
                }
                if (pianoCB.isChecked()==true){
                    instruments.append("Piano ");
                }
                if (saxophoneCB.isChecked()==true){
                    instruments.append("Saxophone ");
                }
                if (violinCB.isChecked()==true){
                    instruments.append("Violin ");
                }
                if (voiceCB.isChecked()==true){
                    instruments.append("Singer ");
                }

                userInstruments = instruments.toString();
                userBio = userbioEdtTxt.getText().toString();
                nameChange = nameEdtText.getText().toString();

                Toast.makeText(Settings.this, "Saved Successfully", Toast.LENGTH_SHORT).show();

              //  Bundle bundle = new Bundle();
              //  Intent intent = new Intent(this, FragUserProfile.class);
               // intent.putExtra("instruments", instruments.toString());

             //   bundle.putString("instrumentsBundle", userInstruments);

//                Fragment fragment = new FragUserProfile();
//                fragment.setArguments(bundle);

                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mFirebaseDatabaseReference.child("UserInfo").child(mUsername).child("altdisplayname").setValue(nameChange);
                mFirebaseDatabaseReference.child("UserInfo").child(mUsername).child("userinstruments").setValue(userInstruments);
                mFirebaseDatabaseReference.child("UserInfo").child(mUsername).child("userbio").setValue(userBio);

            }

        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }



//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (this.getMenuInflater() != null) {
//            MenuInflater inflater = this.getMenuInflater();
//            inflater.inflate(R.menu.main_menu, menu);
//            return true;
//        }else{return false;}
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.settings_menu:
//                startActivity(new Intent(this, Settings.class));
//            case R.id.sign_out_menu:
//                FirebaseAuth.getInstance().signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                startActivity(new Intent(this, MainActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Settings", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
