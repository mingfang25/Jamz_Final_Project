package com.example.jamz;

import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import android.widget.Toast;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.BuildConfig;

import org.apache.http.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyPersonalization extends AppCompatActivity {

    //Firebase references
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUsername;
    private String requestResponse;

    //Spotify Credentials
    private String CLIENT_ID = "201d4e09e68d403384b8279d0f8e96ad"; // Your client id
    private String CLIENT_SECRET = "caf478dc33ee43389d465f917ed7cb95"; // Your secret
    private String REDIRECT_URI = "Jamzapp://Jamzcalback";
    private String scope = "user-top-read";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    //Http tools
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private String mRefreshToken;
    private Call mCall;

    //for log and debug
    private String TAG = "TEST";

    //For Ui
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;

    private ArrayList<TopTrack> mListData = new ArrayList<>();
    private ArrayList<TopTrack> tracks = new ArrayList<>();
    private String res_to_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_personalization);

        //start DB reference
        mAuth = FirebaseAuth.getInstance();

        /**Check if activity was started from the current users profile (ie. Account owner)
         * or If it was started from a friends profile
         * If it is a friend Code will check DB for spotify data
         *
         * */

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b == null){ //owner
            Log.w(TAG, "No Intent received");
            mUser = mAuth.getCurrentUser();
            mUsername = mUser.getDisplayName();

        } else { //friend

            Log.w(TAG, "Intent received");

            mUsername = b.getString("userinfo");
        }

        //reference for appropriate table in DB
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("TopTracks");

        /**start listening for data changes
         *
         * first we will check if user has data in DB already
         *
         * if not - we will call the Spotify Api if the current user
         * doesnt already have spotify info in the Db
         *
         * */
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             //   Log.w(TAG, "onDataChnage Listener");

                //check for existance
                if (dataSnapshot.hasChild("TopTracks/"+mUsername)){

            //        Log.w(TAG, "USER HAS TOP TRACKS");

                    requestResponse = dataSnapshot.child("TopTracks").child(mUsername).getValue().toString();

                    try {

                        final JSONObject jsonObject = new JSONObject(requestResponse);

                        tracks = parseJson(jsonObject);
                        setResponse(tracks);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext() , "Failed to retrieve from Firebase.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //execute spotify Api call
                    if(mUsername == mAuth.getCurrentUser().getDisplayName()) {
                        onGetTopClicked();
                    } else { //No data in DB
                        Toast.makeText(getApplicationContext(), "This user has no spotify data", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database read failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    /**
     * This method initiates the call to the Spotify Api
     * by checking for access tokens if there is none it will make an authentication
     * request to exchange tokens and later make the api call
     *
     * Most of this method was created with the help of the Spotify Android Api Git Repo
     *
     *      https://github.com/spotify/android-auth
     *      https://github.com/spotify/android-sdk/blob/master/auth-sample/src/main/java/com/spotify/sdk/android/authentication/sample/MainActivity.java
     *
     * */
    public void onGetTopClicked() {

        //no token so make a request for one by calling onRequestTokenClicked
        if (mAccessToken == null) {

            Log.w(TAG, "Token = null getting token");
            onRequestTokenClicked();

        } else {
            //make the request to retrieve Spotify User data
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/tracks")
                    .addHeader("Authorization","Bearer " + mAccessToken)
                    .build();

            cancelCall();
            mCall = mOkHttpClient.newCall(request);

            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext() , "FILED to make Call ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e(TAG , jsonObject.toString());
                        mListData = parseJson(jsonObject);
                        setResponse(mListData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext() , "FILED to make Call ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    //This method makes an authentication request to get a token
    //by calling getAuthenticationRequest
    public void onRequestTokenClicked() {

        Log.w(TAG, "Requesting Access Token");

        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        Log.i(TAG, request.toString());
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    //Builds authentication requests with appropriate scopes
    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {

        Log.w(TAG, "CAlled getAuthenticationReuest");

        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-top-read"})
                .setCampaign("your-campaign-token")
                .build();
    }

    /** After the appropriate exchanges happen
     * we make the APi request and wait for response or failure
     *
     * onResponse - the code gets the JSON response and loads it to the DB
     *
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(TAG, "ON activity result");

        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {

            Log.w(TAG, "It is an Access CODE");

            mAccessToken = response.getAccessToken();
            //updateTokenView();

            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/tracks")
                    .addHeader("Authorization","Bearer " + mAccessToken)
                    .build();

            cancelCall();
            mCall = mOkHttpClient.newCall(request);

            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext() , "FILED to make Call ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e(TAG , jsonObject.toString());

                        //to store in firebase
                        res_to_db = jsonObject.toString();
                        loadResponseToDB(res_to_db);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext() , "FILED to make Call ", Toast.LENGTH_SHORT).show();

                    }
                }
            });


        } else {

            Toast.makeText(this, "OOps Error", Toast.LENGTH_SHORT).show();

        }

    }

    /** When the response is loaded to the DB
     *  the listener will be activated again and since now there is
     *  spotify information in the DB we can set the top tracks UI
     * */
    private void loadResponseToDB( String res ){

        Log.w(TAG, "loading to Firebase");

        DatabaseReference newRef = databaseReference.child("TopTracks");
        newRef.child(mUsername).setValue(res);

    }

    /** Updates UI with users top tracks by using a recycler list view
     * */
    private void setResponse(ArrayList<TopTrack> l) {

        Log.w(TAG, "In SetResponse");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.w(TAG, "In UI Thread");

                recyclerView = findViewById(R.id.top_track_list);

                linearLayoutManager = new LinearLayoutManager(getApplicationContext());

                recyclerView.setLayoutManager(linearLayoutManager);

                adapter = new SpotifyPersonalization.MyAdapter(l);
                recyclerView.setAdapter(adapter);

            }
        });
    }

    /** traditional adpater to create a recycler view
     * */
    public class MyAdapter extends RecyclerView.Adapter<SpotifyPersonalization.MyAdapter.MyViewHolder> {
        public ArrayList<TopTrack> trackDataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView track_name;
            public TextView album_name;
            public TextView artist_name;


            public MyViewHolder(View itemView) {
                super(itemView);
                track_name = itemView.findViewById(R.id.track_name);
                album_name = itemView.findViewById(R.id.album_name);
                artist_name = itemView.findViewById(R.id.artist_name);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<TopTrack> myDataset) {
            trackDataSet = myDataset;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(SpotifyPersonalization.MyAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.track_name.setText(trackDataSet.get(position).track_name);
            holder.album_name.setText(trackDataSet.get(position).album_name);
            holder.artist_name.setText(trackDataSet.get(position).artist_name);

        }


        // Create new views (invoked by the layout manager)
        @Override
        public SpotifyPersonalization.MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int i) {

            View view = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.top_track, parent, false);

            SpotifyPersonalization.MyAdapter.MyViewHolder vh = new SpotifyPersonalization.MyAdapter.MyViewHolder(view);
            return vh;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return trackDataSet.size();
        }
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private Uri getRedirectUri() {

        Log.w(TAG, "Redirecting");

        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }

    //Method used to Parse the Api responses
    public ArrayList<TopTrack> parseJson(JSONObject jsonObject) {
        //ArrayList<TopTrack> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    //getting track name
                    String track_name = json.getString("name");

                    JSONObject jsonAlbum = json.getJSONObject("album");
                    String album_name = jsonAlbum.getString("name");

                    JSONArray artist_list = jsonAlbum.getJSONArray("artists");
                    JSONObject artist = artist_list.getJSONObject(0);
                    String artist_name = artist.getString("name");

                    TopTrack track_object = new TopTrack(track_name, album_name, artist_name);
                    mListData.add(track_object); //to be used in Ui later

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mListData;

    }


}
