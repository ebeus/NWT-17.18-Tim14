package ba.tim14.nwt.nwt_android.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.classes.ManageLocation;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;

public class TripActivity extends FragmentActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnMapReadyCallback {
        //OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;

    ArrayList<User> users = new ArrayList<>();

    FloatingActionButton fabStart;
    FloatingActionButton fabStop;
    FloatingActionButton fabUsers;
    TextView textViewTitle;

    private ManageLocation manageLocation = null;
    private boolean firstTime = true;
    private Marker myLocationMarker;

    private boolean fabUsersClicked = false;
    private Dialog openDialog;

    int userPosition = 0;
    int step = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initViews();

        users = Utils.getPopulatedListWithUsers();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setMainViews();
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
                    break;
                case Constants.USERS:
                    fabStart.setVisibility(View.GONE);
                    fabUsers.setOnClickListener(this);
                    userPosition = getIntent().getIntExtra("user", 0);
                    break;
            }
        }
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.toolbar_title);

        fabStart = findViewById(R.id.floatingActionButtonStart);
        fabStop = findViewById(R.id.floatingActionButtonStop);
        fabUsers = findViewById(R.id.floatingActionButtonUsers);

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
                textViewTitle.setText(getString(R.string.title_activity_group));
                if (fabUsersClicked){
                    fabUsersClicked = false;
                    deleteMarkers();
                } else {
                    fabUsersClicked = true;
                    showAllUsersOnMap();
                }
                break;
        }
    }

    private void startTrip() {
        Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
    }

    private void endTrip() {
        Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
    }

    private void setUpMap() {
        if (manageLocation == null)
            manageLocation = new ManageLocation(getApplicationContext());

        manageUserLocation();
    }

    private void setOneUserOnMap() {
        User clickedUser = users.get(userPosition);
        addMarkerOnMap(clickedUser.getLocation(), clickedUser.getUsername(), userPosition);
        //Zoom to clicked user location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(clickedUser.getLocation()));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(15));
        textViewTitle.setText("Location of " + clickedUser.getUsername());
    }

    public void manageUserLocation() {
        if (manageLocation.getLocationValue() != null) {
            setMarkerAndZoom();
            if(step == Constants.USERS){
                setOneUserOnMap();
            }
            return;
        }
        if (manageLocation.locationIsEnabled()) {
            searchLocation();
        } else buildAlertMessageNoGps();
    }


    private void setMarkerAndZoom() {
        if (firstTime) {
            myLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(manageLocation.getLocationValue().getLatitude(), manageLocation.getLocationValue().getLongitude()))
                    .title(SharedPreferencesManager.instance().getUsername()));
            myLocationMarker.setTag(SharedPreferencesManager.instance().getUsername());
            firstTime = false;
        } else {
            myLocationMarker.setPosition(new LatLng(manageLocation.getLocationValue().getLatitude(), manageLocation.getLocationValue().getLongitude()));
            myLocationMarker.setTag(SharedPreferencesManager.instance().getUsername());
        }
        if (fabUsersClicked) {
            showAllUsersOnMap();
        }
    }

    private void searchLocation() {
            runOnUiThread(() -> {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                while (manageLocation.getLocationValue() == null) {
                    manageLocation.setLocation();
                    Log.i("LOCATION", getString(R.string.str_searching_location));
                }
                if (manageLocation.getLocationValue() != null) {
                    Log.i("LOCATION", " Location Lattitude = " + String.valueOf(manageLocation.getLatitude()) + " Longitude = " + String.valueOf(manageLocation.getLongitude()));
                    manageUserLocation();
                    setUpMap();
                }
            });
        }

    private void showAllUsersOnMap() {
        //Show all users
        for (int i = 0; i < users.size(); i++) {
            addMarkerOnMap(users.get(i).getLocation(), users.get(i).getUsername(), i);
        }
    }

    private void addMarkerOnMap(LatLng location, String title, int tag) {
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_user_pin))).setTag(tag);
    }

    private void deleteMarkers() {
        mMap.clear();
        firstTime = true;
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
            setUpMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null){
            deleteMarkers();
            setUpMap();
        }
    }
}
