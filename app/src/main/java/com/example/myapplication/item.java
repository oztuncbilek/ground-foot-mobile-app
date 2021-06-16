package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link item#newInstance} factory method to
 * create an instance of this fragment.
 */
public class item extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public TextView nameV;
    public TextView particV;
    public TextView dateV;
    public TextView timeV;
    public Button cancel;
    public ImageButton map;

    public  String nameS;
    public  String particS;
    public  String dateS;
    public  String timeS;
    public String id;
    public String Username;

    //variable about database
    FirebaseFirestore fstore;

    public item(String name,String date,String partc,String time,String location,String username) {
        // get data from event fragment
        nameS=name;
        particS=partc;
        dateS=date;
        timeS=time;
        id=location;
        Username=username;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment item.
     */
    // TODO: Rename and change types and number of parameters
    public static item newInstance(String param1, String param2, String nameS,
                                   String particS, String dateS,
                                   String timeS, String location,String username) {
        item fragment = new item(nameS,particS,dateS,timeS,location, username);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View cFragment=inflater.inflate(R.layout.fragment_item, container, false);
        timeV=cFragment.findViewById(R.id.textView5);
        dateV=cFragment.findViewById(R.id.textView3);
        nameV=cFragment.findViewById(R.id.textView2);
        particV=cFragment.findViewById(R.id.textView4);
        cancel=cFragment.findViewById(R.id.button2);
        map=cFragment.findViewById(R.id.map);
        setData();
        return cFragment;
    }
    public void setData(){
        timeV.setText(timeS);
        dateV.setText(dateS);
        nameV.setText(nameS);
        particV.setText(particS+" player");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //connect to database and delete the user according to the location id
                deleteUser();
                //reload page
                getActivity().finish();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        //go to the map page to show the location of registered event
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("locationID", id);
                startActivity(intent);
            }
        });
    }

    public void deleteUser(){
        fstore=FirebaseFirestore.getInstance();
        fstore.collection("Locations").document(id).update("user", FieldValue.arrayRemove(Username));
    }
}