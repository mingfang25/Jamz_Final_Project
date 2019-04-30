package com.example.jamz;

import android.nfc.Tag;
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

    private String CLIENT_ID = "201d4e09e68d403384b8279d0f8e96ad"; // Your client id
    private String CLIENT_SECRET = "caf478dc33ee43389d465f917ed7cb95"; // Your secret
    private String REDIRECT_URI = "Jamzapp://Jamzcalback";
    private String scope = "user-top-read";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private String mRefreshToken;
    private Call mCall;

    private String TAG = "TEST";


    private Button reqSpot;
    private String top_json;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;

    private ArrayList<TopTrack> mListData = new ArrayList<>();
    private ArrayList<TopTrack> tracks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_personalization);


        reqSpot = (Button) findViewById(R.id.button3);
        reqSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetTopClicked();
            }
        });

        /*recyclerView = findViewById(R.id.top_track_list);

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SpotifyPersonalization.MyAdapter(tracks);
        recyclerView.setAdapter(adapter);*/

    }


    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();

    }


    public void onGetTopClicked() {


        if (mAccessToken == null) {

            Log.w(TAG, "Token = null getting token");
            onRequestTokenClicked();

        } else {

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



    public void onRequestTokenClicked() {

        Log.w(TAG, "Requesting Access Token");

        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        Log.i(TAG, request.toString());
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {

        Log.w(TAG, "CAlled getAuthenticationReuest");

        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-top-read"})
                .setCampaign("your-campaign-token")
                .build();
    }


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
                        tracks = parseJson(jsonObject);
                        setResponse(tracks);


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



    private void setResponse(ArrayList<TopTrack> l) {

        Log.w(TAG, "In SetResponse");


        //top_json = text;



        //recyclerView = findViewById(R.id.top_track_list);

        //Log.w(TAG, "init recycler view");

        //linearLayoutManager = new LinearLayoutManager(this);

        //Log.w(TAG, "init linear layout");

        //recyclerView.setLayoutManager(linearLayoutManager);

        //Log.w(TAG, ".setLayoutmanager");
        //recyclerView.setHasFixedSize(true);
        //fetch();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.w(TAG, "In UI Thread");

                //tracks.addAll(l);

                recyclerView = findViewById(R.id.top_track_list);

                linearLayoutManager = new LinearLayoutManager(getApplicationContext());

                recyclerView.setLayoutManager(linearLayoutManager);

                adapter = new SpotifyPersonalization.MyAdapter(l);
                recyclerView.setAdapter(adapter);

                //final TextView responseView = findViewById(R.id.response_text_view);
                //responseView.setText(text);
            }
        });
    }

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
            //holder.postAuthor.setText(mDataset.get(position).username);
            //Glide.with(getActivity())
            //        .load(mDataset.get(position).UserPhotoURL)
            //        .into(holder.AuthorPhoto);


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

    /*private void fetch() {

        Log.w(TAG, "In Fetch");

        tracks.addAll(mListData);

        Log.w(TAG, "Passed add all");

        adapter = new SpotifyPersonalization.MyAdapter(tracks);

        Log.w(TAG, "made Adapter");

        int test = 1;

        recyclerView.setAdapter(adapter);
    }*/

    /*private void updateTokenView() {
        final TextView tokenView = findViewById(R.id.token_text_view);
        tokenView.setText(getString(R.string.token, mAccessToken));
    }*/




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
                    mListData.add(track_object);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mListData;

    }



    /*public void onRequestCodeClicked(View view) {

        Log.w(TAG, "Requesting Access code");

        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.CODE);
        AuthenticationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
    }*/


    /*private void updateCodeView() {
        final TextView codeView = findViewById(R.id.code_text_view);
        codeView.setText(getString(R.string.code, mAccessCode));
    }*/

}
