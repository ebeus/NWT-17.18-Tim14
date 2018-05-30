package ba.tim14.nwt.nwt_android.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import android.os.AsyncTask;
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
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.classes.ManageLocation;
import ba.tim14.nwt.nwt_android.classes.Putovanje;
import ba.tim14.nwt.nwt_android.dialogs.TripNameDialog;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ba.tim14.nwt.nwt_android.utils.Utils.tripList;
import static ba.tim14.nwt.nwt_android.utils.Utils.usersLoc;
import static junit.framework.Assert.assertNotNull;

public class TripActivity extends FragmentActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnMapReadyCallback {//LocationListener {

    private static final String TAG = TripActivity.class.getSimpleName();

    private GoogleMap mMap;

    ArrayList<Korisnik> users = new ArrayList<>();

    FloatingActionButton fabStart;
    FloatingActionButton fabStop;
    FloatingActionButton fabUsers;
    FloatingActionButton fabFindMe;
    TextView textViewTitle;

    private ManageLocation manageLocation = null;
    private boolean firstTime = true;
    private boolean startedActivity = true;

    private Marker myLocationMarker;

    private Korisnik clickedUser;
    private boolean fabUsersClicked = false;
    private Dialog gpsDialog;

    private int userPosition = 0;
    private int step = 0;

    //private Trip trip;
    //private Putovanje putovanje;
    private double distance = 0;
    private boolean tripStarted = false;

    private ArrayList<LatLng> points;
    Polyline line;
    Handler handler;
    int delay = 1000; //milliseconds

    int positionOfTrip;

    String tripTitle;
    Putovanje currentTrip = new Putovanje();
    boolean oneClick = false;

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
        Utils.getKorisnike();
        users = Utils.getPopulatedListWithUsers();
        points = new ArrayList<>();

        textViewTitle = findViewById(R.id.toolbar_title);
        fabStart = findViewById(R.id.floatingActionButtonStart);
        fabStop = findViewById(R.id.floatingActionButtonStop);
        fabUsers = findViewById(R.id.floatingActionButtonUsers);
        fabFindMe = findViewById(R.id.floatingActionButtonLocateMe);
        if(SharedPreferencesManager.instance().getUserGroupId() == 0L){
            fabUsers.setVisibility(View.GONE);
        }
        else {
            fabUsers.setVisibility(View.VISIBLE);
        }
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
                    textViewTitle.setText(getString(R.string.title_activity_trip_history));
                    fabStart.setVisibility(View.GONE);
                    fabStop.setVisibility(View.GONE);
                    fabUsers.setVisibility(View.GONE);
                    fabFindMe.setVisibility(View.GONE);
                    break;
                case Constants.START_TRIP:
                    oneClick = true;
                    tripTitle = getIntent().getStringExtra(Constants.TRIP_NAME);
                    textViewTitle.setText(tripTitle);
                    fabStart.setVisibility(View.GONE);
                    fabStop.setVisibility(View.VISIBLE);
                    fabStart.setOnClickListener(this);
                    fabStop.setOnClickListener(this);
                    fabUsers.setOnClickListener(this);
                    fabFindMe.setOnClickListener(this);
                    break;
            }
        }
    }

    private void drawTripOnMap() {
        positionOfTrip = getIntent().getIntExtra(Constants.MY_TRIP_HISTORY_POSITION,0);
        if(!tripStarted){
            points = getLocationList();
           // points = tripList.get(positionOfTrip).getPath();
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

    private ArrayList<LatLng> getLocationList() {
        List<Lokacija> lokacije = tripList.get(positionOfTrip).getListaLokacija();
        ArrayList<LatLng> lista = new ArrayList<>();
        for(Lokacija lokacija : lokacije){
            lista.add(new LatLng(lokacija.getLatitude(),lokacija.getLongitude()));
        }
        return lista;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonStart:
                fabStart.setVisibility(View.GONE);
                fabStop.setVisibility(View.VISIBLE);
                buildStartTripDialog();
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
            if(step == Constants.USERS) textViewTitle.setText(String.format(getString(R.string.str_location_of), clickedUser.getUserName()));
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
        oneClick = false;

        startTripSaveInDB();
        getCurrentTrip();
        Log.i(TAG, "Putovanje "+ currentTrip);

        points = new ArrayList<>();
        Toast.makeText(this, "Trip started", Toast.LENGTH_SHORT).show();
        //Date currentDateTime =  Calendar.getInstance().getTime();
        //trip = new Trip(currentDateTime);
        //putovanje = new Putovanje(c)
        distance = 0;
        addMarkerOnMap(myLocationMarker.getPosition(),"Start" , 0,Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_start_trip));

        addLocationInTrip(myLocationMarker.getPosition());
        points.add(myLocationMarker.getPosition());

        handler = new Handler();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 5000);
    }


    private void addLocationInTrip(LatLng position) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> addedLocation = locatorService.addLocation(currentTrip.getId(), Calendar.getInstance().getTimeInMillis(),position.latitude,position.longitude);

        addedLocation.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        Log.i(TAG, "trip " + responseString);
                        assertNotNull(responseString);
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        Log.i(TAG, "trip " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void buildStartTripDialog() {
        TripNameDialog tripNameDialog=new TripNameDialog(this);
        tripNameDialog.show();
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
        //Date currentDateTime =  Calendar.getInstance().getTime();

        addMarkerOnMap(myLocationMarker.getPosition(),"Stop" , points.size()-1 ,Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_finish_trip));

        //trip.setStopDateTime(currentDateTime);
        //trip.setDistance(String.valueOf(distance));
        //trip.setDurationPeriod();
        //trip.setPath(points);
        //tripList.add(trip);

        stopTripSaveInDB();
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
            if(step == Constants.START_TRIP & oneClick){
            startTrip();
            }
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
                    Log.i(TAG, "trip DISTANCE " + SphericalUtil.computeDistanceBetween(points.get(points.size()-1), tmpLatLng));
                    distance = distance + SphericalUtil.computeDistanceBetween(points.get(points.size()-1), tmpLatLng);
                    points.add(tmpLatLng);
                    addLocationInTrip(tmpLatLng);

                    Log.i(TAG, "trip DISTANCE " + distance);
//                    measureDistance(points.get(points.size()-1), tmpLatLng);
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

    private void setOneUserOnMap() {
        clickedUser = users.get(userPosition);
        LatLng userLoc = new LatLng(43.856259, 18.413086);
        if(userPosition < 8) userLoc = usersLoc.get(userPosition);
        addMarkerOnMap(userLoc, clickedUser.getUserName(), userPosition, Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_user_pin));
        //Zoom to clicked user location
        animateCamera(userLoc,20);

        textViewTitle.setText(String.format(getString(R.string.str_location_of), clickedUser.getUserName()));
    }

    private void searchLocation() {
            runOnUiThread(() -> {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                while (manageLocation.getLocationValue() == null) {
                    if(startedActivity){
                        manageLocation.setLocationFirst();
                        startedActivity = false;
                    }
                    else{
                        manageLocation.setLocation();
                    }
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
            addMarkerOnMap(usersLoc.get(i), users.get(i).getUserName(), i, Utils.getBitmapDescriptor(getApplicationContext(), R.drawable.ic_user_pin));
        }
        if(step == Constants.USERS){ //Zoom to user
            int loc = Integer.valueOf(String.valueOf(clickedUser.getId()));
            animateCamera(usersLoc.get(loc), 13);
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
        gpsDialog = new Dialog(this);
        gpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gpsDialog.setContentView(R.layout.custom_location_dialog);
        gpsDialog.setCanceledOnTouchOutside(false);
        ((TextView) gpsDialog.findViewById(R.id.textView_title)).setText(getResources().getText(R.string.str_GPS));
        ((TextView) gpsDialog.findViewById(R.id.textView_text)).setText(getResources().getText(R.string.str_your_GPS_map));

        gpsDialog.findViewById(R.id.button_no).setOnClickListener(v ->  {
                gpsDialog.dismiss();
                gpsDialog.cancel();
        });
        gpsDialog.findViewById(R.id.button_yes).setOnClickListener(v -> {
            gpsDialog.dismiss();
            gpsDialog.cancel();
            this.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
        });
        gpsDialog.show();
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

    public void startTripSaveInDB() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> tripStarted = locatorService.start(tripTitle, Calendar.getInstance().getTimeInMillis(),SharedPreferencesManager.instance().getId());

        tripStarted.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        String responseObject= jsonObject.getString("responseObject");
                        Gson gson = new Gson();
                        currentTrip=gson.fromJson(responseObject,Putovanje.class);
                        Log.i(TAG, "startTripSaveInDB: " + currentTrip);
//                        getCurrentTrip();
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        Log.i(TAG,"startTripSaveInDB: " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG,"startTripSaveInDB - Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private boolean getCurrentTrip() {
        final boolean[] check = {false};
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.i(TAG, "Trip title: " + tripTitle);
                currentTrip = getTripByName(tripTitle);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (currentTrip != null) {

                    Log.i(TAG, "Trip title: " + tripTitle);
                    check[0] = true;
                }
                return null;
            }
        }.execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(check[0]){
            Log.i(TAG, "validUsername: " + check[0] + " " + currentTrip.getNaziv());
        }
        return check[0];
    }

    private Putovanje getTripByName(String tripName) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Putovanje> dobijenoPutovanje = locatorService.getTripByName(tripName);

        try {
            Response<Putovanje> response = dobijenoPutovanje.execute();
            currentTrip = response.body();
            Log.i( TAG, "doesUserWithUserNameExist - Provjera:  "+ currentTrip.getNaziv());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentTrip;
    }

    public void stopTripSaveInDB() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> tripStopped = locatorService.stop(currentTrip.getId(), Calendar.getInstance().getTimeInMillis(), distance);

        tripStopped.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        Log.i(TAG,"stopTripSaveInDB - trip " + responseString);
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        Log.i(TAG, "stopTripSaveInDB - trip " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG,"stopTripSaveInDB - Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
