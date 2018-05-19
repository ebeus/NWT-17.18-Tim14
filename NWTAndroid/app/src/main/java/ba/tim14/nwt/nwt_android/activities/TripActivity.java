package ba.tim14.nwt.nwt_android.activities;

import android.app.Dialog;
import android.content.Intent;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.classes.ManageLocation;
import ba.tim14.nwt.nwt_android.classes.Trip;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;

import static ba.tim14.nwt.nwt_android.utils.Utils.tripList;

public class TripActivity extends FragmentActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnMapReadyCallback {//LocationListener {

    private static final String TAG = TripActivity.class.getSimpleName();

    private GoogleMap mMap;

    ArrayList<User> users = new ArrayList<>();

    FloatingActionButton fabStart;
    FloatingActionButton fabStop;
    FloatingActionButton fabUsers;
    FloatingActionButton fabFindMe;
    TextView textViewTitle;

    private ManageLocation manageLocation = null;
    private boolean firstTime = true;
    private Marker myLocationMarker;

    private User clickedUser;
    private boolean fabUsersClicked = false;
    private Dialog openDialog;
    private int userPosition = 0;
    private int step = 0;

    private Trip trip;
    private double distance = 0;
    private boolean tripStarted = false;

    private ArrayList<LatLng> points;
    Polyline line;
    Handler handler;
    int delay = 1000; //milliseconds

    int positionOfTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initViews();
        setMainViews();
    }

    private void initViews() {
        users = Utils.getPopulatedListWithUsers();
        points = new ArrayList<>();

        textViewTitle = findViewById(R.id.toolbar_title);
        fabStart = findViewById(R.id.floatingActionButtonStart);
        fabStop = findViewById(R.id.floatingActionButtonStop);
        fabUsers = findViewById(R.id.floatingActionButtonUsers);
        fabFindMe = findViewById(R.id.floatingActionButtonLocateMe);
    }

    private void setMainViews() {
        if (getIntent() != null) {
            step = getIntent().getIntExtra(Constants.STEP, 0);
            switch (step) {
                case Constants.MY_TRIP:
                    textViewTitle.setText(getString(R.string.title_activity_trip));
                    fabStart.setOnClickListener(this);
                    fabStop.setOnClickListener(this);
                    fabUsers.setOnClickListener(this);
                    fabFindMe.setOnClickListener(this);
                    break;
                case Constants.USERS:
                    fabStart.setVisibility(View.GONE);
                    fabUsers.setOnClickListener(this);
                    fabFindMe.setVisibility(View.GONE);
                    userPosition = getIntent().getIntExtra("user", 0);
                    break;
                case Constants.MY_TRIP_HISTORY:
                    Log.i(TAG,"trip 0: " + tripList.get(0).getPath().toString() + " trip 1: " + tripList.get(1).getPath().toString());
                    textViewTitle.setText(getString(R.string.title_activity_trip_history));
                    fabStart.setVisibility(View.GONE);
                    fabStop.setVisibility(View.GONE);
                    fabUsers.setVisibility(View.GONE);
                    fabFindMe.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void drawTripOnMap() {
        positionOfTrip = getIntent().getIntExtra(Constants.MY_TRIP_HISTORY_POSITION,0);
        if(!tripStarted){
            points = tripList.get(positionOfTrip).getPath();
        }
        addMarkerOnMap(points.get(0),"Start" , 0,Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_start_trip));
        redrawLine();
        int end = 0;
        if(points.size() > 1){
            end = points.size()-1;
        }
        if (!tripStarted){
            addMarkerOnMap(points.get(end),"Stop" , end ,Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_finish_trip));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonStart:
                fabStart.setVisibility(View.GONE);
                fabStop.setVisibility(View.VISIBLE);
                startTrip();
                break;
            case R.id.floatingActionButtonStop:
                fabStart.setVisibility(View.VISIBLE);
                fabStop.setVisibility(View.GONE);
                endTrip();
                break;
            case R.id.floatingActionButtonUsers:
                fabUsersClick();
                break;
            case R.id.floatingActionButtonLocateMe:
                deleteMarkers();
                animateCamera(myLocationMarker.getPosition(),20);
                break;
        }
    }

    private void fabUsersClick() {
        if (fabUsersClicked){
            if(step == Constants.MY_TRIP) textViewTitle.setText(getString(R.string.title_activity_trip));
            if(step == Constants.USERS) textViewTitle.setText(String.format(getString(R.string.str_location_of), clickedUser.getUsername()));
            fabUsersClicked = false;
            deleteMarkers();
            setUpMap();
        } else {
            textViewTitle.setText(getString(R.string.title_activity_group));
            fabUsersClicked = true;
            showAllUsersOnMapAndZoom();
        }
    }

    private void startTrip() {
        tripStarted = true;
        points = new ArrayList<>();
        Toast.makeText(this, "Trip started", Toast.LENGTH_SHORT).show();
        Date currentDateTime =  Calendar.getInstance().getTime();
        trip = new Trip(currentDateTime);
        distance = 0;
        addMarkerOnMap(myLocationMarker.getPosition(),"Start" , 0,Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_start_trip));
        points.add(myLocationMarker.getPosition());

        handler = new Handler();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 5000);
    }

    final Runnable runnable = new Runnable() {
        public void run() {
            Log.d("Runnable", "Handler is working");
            if (manageLocation.getLocationValue() != null) {
                Log.i(TAG, " location  != null");
                setMyLocationMarker();
            }
            if (manageLocation.locationIsEnabled()) {
                Log.i(TAG, " location  search");
                searchLocation();
            }
            handler.postDelayed(this, delay);
        }
    };

    private void redrawLine() {
        PolylineOptions options = new PolylineOptions().width(5)
                .color(getResources().getColor(R.color.colorAccent))
                .geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        line = mMap.addPolyline(options);
    }

    private void endTrip() {
        tripStarted = false;
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Trip stopped", Toast.LENGTH_SHORT).show();
        Date currentDateTime =  Calendar.getInstance().getTime();

        addMarkerOnMap(myLocationMarker.getPosition(),"Stop" , points.size()-1 ,Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_finish_trip));

        trip.setStopDateTime(currentDateTime);
        // TODO: 5/17/18 Calculate and save distance in kilometers from start to end
        trip.setDistance(String.valueOf(distance));
        trip.setDurationPeriod();
        trip.setPath(points);
        tripList.add(trip);
        points = new ArrayList<>();
        deleteMarkers();
    }

    private void setUpMap() {
        if (manageLocation == null)
            manageLocation = new ManageLocation(getApplicationContext());
        manageUserLocation();
    }

    public void manageUserLocation() {
        if (manageLocation.getLocationValue() != null) {
            setMyLocationMarker();
            if(step == Constants.USERS){
                setOneUserOnMap();
            }
            else if(firstTime){
                animateCamera(myLocationMarker.getPosition(),20);
            }
            return;
        }
        if (manageLocation.locationIsEnabled()) {
            searchLocation();
        } else buildAlertMessageNoGps();
    }

    private void setMyLocationMarker() {
        if (firstTime) {
            myLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(manageLocation.getLocationValue().getLatitude(), manageLocation.getLocationValue().getLongitude()))
                    .title(SharedPreferencesManager.instance().getUsername())
                    .icon(Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_my_location)));
            myLocationMarker.setTag(SharedPreferencesManager.instance().getUsername());
            animateCamera(myLocationMarker.getPosition(),20);

            firstTime = false;
        } else {
            LatLng tmpLatLng =new LatLng(manageLocation.getLocationValue().getLatitude(), manageLocation.getLocationValue().getLongitude());
            if(tripStarted){
                Log.i(TAG, "trip Started location changed");
                if(!points.get(points.size()-1).toString().equals(tmpLatLng.toString())){
                    Log.i(TAG, "trip diferent " + points.get(points.size()-1).toString().equals(tmpLatLng));
                    points.add(tmpLatLng);
                    measureDistance(points.get(points.size()-1), tmpLatLng);
                    redrawLine();
                    animateCamera(myLocationMarker.getPosition(),20);
                }
            }
            myLocationMarker.setPosition(new LatLng(manageLocation.getLocationValue().getLatitude(), manageLocation.getLocationValue().getLongitude()));
            myLocationMarker.setTag(SharedPreferencesManager.instance().getUsername());
        }
        if (fabUsersClicked) {
            showAllUsersOnMapAndZoom();
        }
    }

    private void measureDistance(LatLng latLng, LatLng tmpLatLng) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(tmpLatLng.latitude - latLng.latitude);
        double lonDistance = Math.toRadians(tmpLatLng.longitude - latLng.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latLng.latitude)) * Math.cos(Math.toRadians(tmpLatLng.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //double distanceFirst = R * c * 1000 * 1000; // convert to meters

        //double height = el1 - el2;

        double distanceNew = Math.pow(R * c * 1000, 2);// + Math.pow(height, 2);

        distance += Math.sqrt(distanceNew);
        Log.i(TAG,"DISTANCE " + distance);
    }

    private void setOneUserOnMap() {
        clickedUser = users.get(userPosition);
        addMarkerOnMap(clickedUser.getLocation(), clickedUser.getUsername(), userPosition, Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_user_pin));
        //Zoom to clicked user location
        animateCamera(clickedUser.getLocation(),20);

        textViewTitle.setText(String.format(getString(R.string.str_location_of), clickedUser.getUsername()));
    }

    private void searchLocation() {
            runOnUiThread(() -> {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                while (manageLocation.getLocationValue() == null) {
                    manageLocation.setLocation();
                    Log.i(TAG,"LOCATION " + getString(R.string.str_searching_location));
                }
                if (manageLocation.getLocationValue() != null) {
                    Log.i(TAG, "LOCATION Lattitude = " + String.valueOf(manageLocation.getLatitude()) + " Longitude = " + String.valueOf(manageLocation.getLongitude()));
                    manageUserLocation();
                    setUpMap();
                }
            });
        }

    private void showAllUsersOnMapAndZoom() {
        //Show all users
        for (int i = 0; i < users.size(); i++) {
            addMarkerOnMap(users.get(i).getLocation(), users.get(i).getUsername(), i, Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_user_pin));
        }
        if(step == Constants.USERS){ //Zoom to user
            animateCamera(clickedUser.getLocation(), 13);
        }
        else { //Zoom to my location
            if(!tripStarted) animateCamera(myLocationMarker.getPosition(),13);
        }
    }

    private void animateCamera(LatLng position, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addMarkerOnMap(LatLng location, String title, int tag, BitmapDescriptor iconImage) {
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(iconImage)).setTag(tag);
    }

    private void deleteMarkers() {
        mMap.clear();
        firstTime = true;
        if (tripStarted){
            drawTripOnMap();
        }
        setUpMap();
    }

    private void buildAlertMessageNoGps() {
        openDialog = new Dialog(this);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.custom_location_dialog);
        openDialog.setCancelable(false);
        openDialog.setCanceledOnTouchOutside(false);
        ((TextView) openDialog.findViewById(R.id.textView_title)).setText(getResources().getText(R.string.str_GPS));
        ((TextView) openDialog.findViewById(R.id.textView_text)).setText(getResources().getText(R.string.str_your_GPS_map));
        openDialog.findViewById(R.id.button_no).setOnClickListener(v -> openDialog.dismiss());
        openDialog.findViewById(R.id.button_yes).setOnClickListener(v -> {
            openDialog.dismiss();
            this.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
        });
        openDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            switch (requestCode) {
                case 1:
                    setUpMap();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (mMap != null){
            deleteMarkers();
            if(step == Constants.MY_TRIP_HISTORY){
                drawTripOnMap();
            }
            else setUpMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null){
            deleteMarkers();
            if(step == Constants.MY_TRIP_HISTORY){
                drawTripOnMap();
            }
            else setUpMap();
        }
    }

    /**
     * onBackPressed ends trip
     */
    @Override
    public void onBackPressed() {
        if (tripStarted){
            endTrip();
            tripStarted = false;
        }
        if(step == Constants.MY_TRIP_HISTORY){
            startActivity(new Intent(this, TripHistoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else if(step == Constants.USERS)
            startActivity(new Intent(this, GroupActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        else
            startActivity(new Intent(this, MenuActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

}
