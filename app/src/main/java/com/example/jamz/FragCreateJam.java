package com.example.jamz;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragCreateJam extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private  String UserName;
    private String EventName;
    private String EventDescription;
    private String EventFromStart;
    private String EventFromEnd;
    private String EventToStart;
    private String EventToEnd;
    private boolean Eventallday;

    public FragCreateJam(){
        //Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create_jam, container, false);
        return inflater.inflate(R.layout.fragment_create_jam, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        UserName = mAuth.getCurrentUser().getDisplayName();


        final EditText edteventname = (EditText) getView().findViewById(R.id.editEventName);
        final EditText edteventdescription = (EditText) getView().findViewById(R.id.editDescription);
        final EditText edtFromStart = (EditText) getView().findViewById(R.id.editFromStart);
        final EditText edtFromEnd = (EditText) getView().findViewById(R.id.editFromEnd);
        final EditText edtToStart = (EditText) getView().findViewById(R.id.editToStart);
        final EditText edtToEnd = (EditText) getView().findViewById(R.id.editToEnd);
        final CheckBox cbAllday = (CheckBox) getView().findViewById(R.id.cballday);

        edteventname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edteventname.setText("");
            }
        });

        edteventdescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edteventdescription.setText("");
            }
        });
        edtFromStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edtFromStart.setText("");
            }
        });
        edtFromEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edtFromEnd.setText("");
            }
        });
        edtToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edtToStart.setText("");
            }
        });
        edtToEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                edtToEnd.setText("");
            }
        });

        Button btnCreateJam = (Button)getView().findViewById(R.id.btnAdd);
        btnCreateJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), UserName, Toast.LENGTH_SHORT);
                EventName = edteventname.getText().toString() ;
                EventDescription = edteventdescription.getText().toString();
                EventFromStart = edtFromStart.getText().toString();
                EventFromEnd = edtFromStart.getText().toString();
                EventToStart = edtToStart.getText().toString();
                EventToEnd = edtToEnd.getText().toString();
                Eventallday = cbAllday.isChecked();
                basicReadWrite();
            }
        });
    }

    private static final String TAG = "FragCreateJam";

    public void basicReadWrite() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Event event = new Event(UserName,EventName,EventDescription, EventFromStart, EventFromEnd, EventToStart, EventToEnd, Eventallday);
        mDatabase.child("Events").child(event.eventname).setValue(event);
//        DatabaseReference user_name_ref = database.getReference("User_Name");
//        DatabaseReference event_name_ref = database.getReference("Event_Name");
//        DatabaseReference event_description_ref = database.getReference("Event_Description");
//        user_name_ref.setValue(UserName);
//        event_name_ref.setValue(EventName);
//        event_description_ref.setValue(EventDescription);
        // [END write_message]



        // [START read_message]
        // Read from the database
//        event_name_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//        // [END read_message]
    }
}
