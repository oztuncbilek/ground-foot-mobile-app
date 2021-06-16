package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;


import java.io.IOException;
import java.sql.Time;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    double lon;
    double lat;
    String title1;
    String id;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    FirebaseFirestore db;
    String userId, Username, Useremail;
    SearchView searchView;
    View mapView;
    private FusedLocationProviderClient fusedLocationClient;
    //event time and date
    private String date;
    private String time;
    private String max;

    double latitude;
    double longitude;

    //store the queryed location id
    public String locationid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //enable full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        //get the location id that is needed to be highlighted
        Intent intent = getIntent();
        locationid = intent.getStringExtra("locationID");

        //get username
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        getName();

        //add toolbar and back function
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Search
        searchView = findViewById(R.id.sv_location);
        // search View
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //get all data from database and add markers
        Data();

        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        //get current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                LatLng myLocation = new LatLng(latitude, longitude);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1340);
        }

    }

    public void getName() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //User Data fetch
        userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("database").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                Username = documentSnapshot.getString("name");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this,
                    "Location cannot be obtained due to missing permission.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void Data() {
        //get all sports locations from database
        db.collection("Locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // get document field
                            GeoPoint geoPoint = document.getGeoPoint("loc");
                            lat = geoPoint.getLatitude();
                            lon = geoPoint.getLongitude();
                            title1 = (String) document.get("long");
                            id = document.getId();
                            String Title2 = (String) document.get("Name");
                            // Print in Log
                            Log.d(TAG, document.getId() + " => " + document.getData() + lat);
                            LatLng sport = new LatLng(lat, lon);
                            Marker marker;
                            if(locationid!=""&&locationid.equals(id)){
                                marker = mMap.addMarker(new MarkerOptions().position(sport).title(title1).icon(BitmapDescriptorFactory.fromResource(R.drawable.highlight)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sport, 15));
                            }
                            else{
                                marker = mMap.addMarker(new MarkerOptions().position(sport).title(title1).icon(BitmapDescriptorFactory.fromResource(R.drawable.football)));
                            }
                            //used for identify which marker is clicked
                            marker.setTag(id);

                            //because the timestamp cannot be cast into string,the time field is string
                            String timeEl = (String) document.get("time");
                            String maxPlayer=String.valueOf(document.get("participants"));
                            //1 means there is an event, 0 means no event
                            if (timeEl != null) {
                                //get the number of already registered users
                                String players;
                                players = document.get("user").toString();
                                players=players.substring(1,players.length()-1);
                                int count=players.split(",").length;

                                marker.setTitle(" "+Title2 + "\n " + timeEl+"\n Maximum:"+maxPlayer+"\n Players:"+count);
                                marker.setSnippet("1,"+String.valueOf(count)+","+String.valueOf(maxPlayer));
                            } else {
                                marker.setTitle(Title2);
                                marker.setSnippet("0,");
                            }

                            //add inforwindow to markers
                            MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getApplicationContext());
                            mMap.setInfoWindowAdapter(markerInfoWindowAdapter);
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            // Current Location
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mMap.setMyLocationEnabled(true);

                            if (mapView != null &&
                                    mapView.findViewById(Integer.parseInt("1")) != null) {
                                // Get the button view
                                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                                // and next place it, on bottom right (as Google Maps app)
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                                        locationButton.getLayoutParams();
                                // position on right bottom
                                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                                layoutParams.setMargins(0, 0, 30, 350);
                            }
                            // Marker click event
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                public boolean onMarkerClick(Marker marker) {
                                    marker.showInfoWindow();
                                    //    Intent A= new Intent(MapsActivity.this,MainActivity.class);
                                    //  startActivity(A);
                                    return false;
                                }
                            });

                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }

                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.groundfoot));
        } catch (Resources.NotFoundException e) {

        }
        mMap = googleMap;
        //add the inforwindow click event listener
        mMap.setOnInfoWindowClickListener(this);

    }

    //inforwindow click function. if there is an event, register. if not, start a new one.
    @Override
    public void onInfoWindowClick(Marker marker) {
        String info = marker.getSnippet();
        String flag=info.substring(0,info.indexOf(","));
        String id1 = marker.getTag().toString();
        //if there is an event, add the username to the user array
        if (flag.equals("1")) {
            int max=Integer.parseInt(info.substring(info.lastIndexOf(",")+1,info.length()));
            int players=Integer.parseInt(info.substring(info.indexOf(",")+1,info.lastIndexOf(",")));
            if(players<max){
                register(id1);
            }
            //if there is no event, open a dialog to allow user enter time
        } else {
            startNewGame(id1);
        }
    }

    //update user array
    public void register(String id1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Locations").document(id1).update("user", FieldValue.arrayUnion(Username));
        mMap.clear();
        Data();
    }

    //add time and user array to document(start a game)
    public void startNewGame(String id1) {
        //open dialog to allow user input data
        startgame dia = new startgame(this);
        dia.show();
        dia.setLeftButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });
        dia.setRightButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = dia.getDate();
                time = dia.getTime();
                max=dia.getMax();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> event = new HashMap<>();
                event.put("date", date);
                event.put("time",time);
                event.put("participants",max);
                ArrayList<Object> users = new ArrayList<>();
                users.add(Username);
                event.put("user", users);
                db.collection("Locations").document(id1).set(event,SetOptions.merge());
                mMap.clear();
                Data();
                dia.dismiss();
            }
        });
    }

    public class MarkerInfoWindowAdapter implements
            GoogleMap.InfoWindowAdapter {
        private Context context;

        public MarkerInfoWindowAdapter(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(Marker arg0) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            String flag = arg0.getSnippet();
            //whether there is an event or not would open different popup
            if (flag.equals("1")) {
                v = inflater.inflate(R.layout.popupre, null);
            } else {
                v = inflater.inflate(R.layout.popup, null);
            }
            String txt = arg0.getTitle();
            TextView info = (TextView) v.findViewById(R.id.info);
            info.setText(txt);
            return v;
        }
    }

}
