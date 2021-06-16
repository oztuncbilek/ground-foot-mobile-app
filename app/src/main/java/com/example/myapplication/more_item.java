package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link more_item#newInstance} factory method to
 * create an instance of this fragment.
 */
public class more_item extends Fragment {

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
    public Button add;

    public  String nameS;
    public  String particS;
    public  String dateS;
    public  String timeS;
    public String id;

    public more_item(String name,String date,String partc,String time,String location){
        // Required empty public constructor
        nameS=name;
        particS=partc;
        dateS=date;
        timeS=time;
        id=location;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment more_item.
     */
    // TODO: Rename and change types and number of parameters
    public static more_item newInstance(String param1, String param2,String nameS,
                                        String particS,String dateS,
                                        String timeS,String location) {
        more_item fragment = new more_item(nameS,particS,dateS,timeS,location);
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
        View cFragment=inflater.inflate(R.layout.fragment_more_item, container, false);
        timeV=cFragment.findViewById(R.id.textView5);
        dateV=cFragment.findViewById(R.id.textView3);
        nameV=cFragment.findViewById(R.id.textView2);
        particV=cFragment.findViewById(R.id.textView4);
        add=cFragment.findViewById(R.id.button2);
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
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start map acitivity
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("locationID", id);
                startActivity(intent);
            }
        });
    }
}