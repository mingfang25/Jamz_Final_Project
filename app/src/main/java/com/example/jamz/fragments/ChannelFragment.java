package com.example.jamz.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.example.jamz.ChannelIdActivity;
import com.example.jamz.DetailsActivity;
import com.example.jamz.FragProfile;
import com.example.jamz.MainActivity;
import com.example.jamz.ProfileActivity;
import com.example.jamz.R;
import com.example.jamz.YouTubeCID;
import com.example.jamz.YoutubeActivity;
import com.example.jamz.adapters.VideoPostAdapter;
import com.example.jamz.interfaces.OnItemClickListener;
import com.example.jamz.models.YoutubeDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class ChannelFragment extends Fragment {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private String get_info_username;


    //private static String CHANNEL_ID = getLastBitFromUrl("http://example.com/UCRo6jBRIAV28zFmpI8s3-7g");
    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyBEjPfwZEYB3XHyglA3fdRML_HbhPt-q3g";//here you should use your api key for testing purpose you can use this api also
    private static String CHANNEL_ID = ChannelIdActivity.aaa;//"UCRo6jBRIAV28zFmpI8s3-7g";//(String) ChannelIdActivity.MakeRequestTask.getCID();//getLastBitFromUrl(FragProfile.getMyField()); //"UCRo6jBRIAV28zFmpI8s3-7g"; //here you should use your channel id for testing purpose you can use this api also
    private static String CHANNLE_GET_URL; //=YoutubeActivity.get_youtube_url;// "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=3&key=" + GOOGLE_YOUTUBE_API_KEY + "";

    private static String Firebase_URL;

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();


    public static String getLastBitFromUrl(final String url){
        // return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }
    public ChannelFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        CHANNEL_ID = ((ChannelIdActivity) activity).MakeRequestTask.getCID();
//
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        if(Firebase_URL!=null)
//        {
//            CHANNLE_GET_URL = Firebase_URL;
//        }

        //Toast.makeText(getContext(), "your account", Toast.LENGTH_SHORT).show();
        CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=3&key=" + GOOGLE_YOUTUBE_API_KEY + "";


        if(YoutubeActivity.way.equals("otheruser"))
        {
//            Toast.makeText(getContext(), "his account", Toast.LENGTH_SHORT).show();
            get_info_username = YoutubeActivity.userinfo;
            Firebase_URL = YoutubeActivity.get_youtube_url;
            CHANNLE_GET_URL = YoutubeActivity.get_youtube_url;
        }
//        Toast.makeText(getContext(), get_info_username + "/n" + Firebase_URL, Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        mList_videos = (RecyclerView) view.findViewById(R.id.mList_videos);
        initList(mListData);
        new RequestYoutubeAPI().execute();
        new RequestChannelId().execute();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
//        Toast.makeText(getContext(), mUsername, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(), get_info_username, Toast.LENGTH_SHORT).show();

        YouTubeCID youTubeCID = new YouTubeCID(mUsername,CHANNLE_GET_URL);
        mDatabase.child("YouTubeInfo").child(mUsername).setValue(youTubeCID);



//        if(!mUsername.equals(get_info_username))
//        {
//            CHANNLE_GET_URL = Firebase_URL;
//        }


        return view;
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                YoutubeDataModel youtubeDataModel = item;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                startActivity(intent);
            }
        });
        mList_videos.setAdapter(adapter);

    }




    //create an asynctask to get all the data from youtube
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNLE_GET_URL);
            Log.e("URL", CHANNLE_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    mListData = parseVideoListFromResponse(jsonObject);
                    initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                YoutubeDataModel youtubeObject = new YoutubeDataModel();
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);
                                mList.add(youtubeObject);

                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }


    private class RequestChannelId extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String id_URL = "https://www.googleapis.com/youtube/v3/channels?part=id&mine=true&key="+GOOGLE_YOUTUBE_API_KEY+"";
            //String id_URL = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true";
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(id_URL);
            Log.e("url: ", id_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
//            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
        }
    }


}
