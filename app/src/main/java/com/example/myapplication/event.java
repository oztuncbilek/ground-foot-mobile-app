package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link event#newInstance} factory method to
 * create an instance of this fragment.
 */
//this class is created for upcoming events fragment in home page
public class event extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    private String userId;
    public String Username;

    private static final String TAG = "EventFragment";
    public event() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment event.
     */
    // TODO: Rename and change types and number of parameters
    public static event newInstance(String param1, String param2) {
        event fragment = new event();
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
        View cFragment = inflater.inflate(R.layout.fragment_event, container, false);
        mAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        getName();
        getEvent();
        return cFragment;
    }

    public void getName(){
        userId=mAuth.getCurrentUser().getUid();
        //get username
        DocumentReference documentReference = fstore.collection("database").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                Username = documentSnapshot.getString("name");
            }
        });
    }

    public void getEvent(){
        //get all registered events from the database
        fstore.collection("Locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.get("user")!=null){
                                String players;
                                players = document.get("user").toString();
                                players=players.substring(1,players.length()-1);
                                String[] users=players.split(",");
                                //delete the space of each item in users
                                for(int i=0;i<users.length;i++){
                                    users[i]=users[i].trim();
                                }
                                //if the user in the user list
                                if(Arrays.asList(users).contains(Username)){
                                    String nameS=document.get("Name").toString();
                                    String particS=String.valueOf(users.length);
                                    String dateS=document.get("date").toString();
                                    String timeS=document.get("time").toString();
                                    String location=document.getId();
                                    FragmentTransaction transaction= getChildFragmentManager().beginTransaction();
                                    Fragment item = new item(nameS,particS,dateS,timeS,location,Username);
                                    transaction.add(R.id.linear, item).commit();
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}