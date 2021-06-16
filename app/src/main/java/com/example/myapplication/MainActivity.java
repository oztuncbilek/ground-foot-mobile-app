package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MainActvity";
    private MapView mapView;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userId;

    //variables for switching between different fragments
    private event eve_fragment;
    private more more_fragment;

    private TextView eventRegister;
    private TextView eventMore;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public String Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String SOURCE_ID = "SOURCE_ID";
        final String ICON_ID = "ICON_ID";
        final String LAYER_ID = "LAYER_ID";
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);

        //add toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //button to start the map page
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("locationID", "");
                startActivity(intent);
            }
        });

        //switch on and off the user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        Switch onOffSwitch = (Switch) findViewById(R.id.switch1);
//        //functions to detect the on and off status
//        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    Double lat = location.getLatitude();
//                                    Double longt = location.getLongitude();
//                                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                                    Log.d(TAG, " => " + lat + longt);
//                                }
//                            }
//                        });
//                    } else {
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                    }
//                }
//            }
//
//        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        updateNavhHeadrer();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_info, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //register for buttons
        eventRegister=(TextView) findViewById(R.id.eventRegister);
        eventMore=(TextView) findViewById(R.id.event_more);

        initFragment1();

        //get the username
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();

        //add click event listener to the textview
        eventRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment1();
            }
        });
        eventMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment2();
            }
        });
    }

    //    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    // Getting user name data on nav drawer(Profile name and email)
    public void updateNavhHeadrer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.login_name);
        TextView navUseremail = headerView.findViewById(R.id.login_email);
        userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("database").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                Username=documentSnapshot.getString("name");
                navUsername.setText(Username);
                navUseremail.setText(documentSnapshot.getString("Email"));
            }
        });
    }

    //show the upcoming event fragment
    private void initFragment1(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //hide other fragments
        if(eve_fragment == null){
            eve_fragment = new event();
            transaction.add(R.id.fl, eve_fragment);
        }
        eventRegister.setText(Html.fromHtml("<b>"+"Upcoming Events"+"</b>"));
        eventMore.setText(Html.fromHtml("<p>"+"Find More"+"</p>"));
        hideFragment(transaction);
        //show the needed fragment
        transaction.show(eve_fragment);
        transaction.commit();
    }

    //show the find more fragment
    private void initFragment2(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(more_fragment == null){
            more_fragment = new more();
            transaction.add(R.id.fl, more_fragment);
        }
        eventMore.setText(Html.fromHtml("<b>"+"Find More"+"</b>"));
        eventRegister.setText(Html.fromHtml("<p>"+"Upcoming Events"+"</p>"));
        hideFragment(transaction);
        transaction.show(more_fragment);
        transaction.commit();
    }

    //hide all fragments
    private void hideFragment(FragmentTransaction transaction) {
        if (eve_fragment != null) {
            transaction.hide(eve_fragment);
        }
        if (more_fragment != null) {
            transaction.hide(more_fragment);
        }
    }

}