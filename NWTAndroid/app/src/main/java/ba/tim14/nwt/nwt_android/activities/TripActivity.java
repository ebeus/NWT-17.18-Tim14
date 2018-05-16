package ba.tim14.nwt.nwt_android.activities;

import android.Manifest;
import android.app.Activity;
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

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;

public class TripActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;
    ArrayList<User> users = new ArrayList<>();
    int userPosition = 0;

    FloatingActionButton fabStart;
    FloatingActionButton fabStop;
    FloatingActionButton fabUsers;

    TextView textViewTitle;

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
                showAllUsersOnMap();
                break;
        }
    }

    private void startTrip() {
        Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
    }

    private void endTrip() {
        Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sarajevo = new LatLng(43.856259, 18.413076);
        mMap.addMarker(new MarkerOptions().position(sarajevo).title(SharedPreferencesManager.instance().getUsername()));
        if(step == Constants.USERS){
            User clickedUser = users.get(userPosition);
            addMarkerOnMap(clickedUser.getLocation(), clickedUser.getUsername(), userPosition);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(clickedUser.getLocation()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            textViewTitle.setText("Location of " + clickedUser.getUsername());
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sarajevo));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}
