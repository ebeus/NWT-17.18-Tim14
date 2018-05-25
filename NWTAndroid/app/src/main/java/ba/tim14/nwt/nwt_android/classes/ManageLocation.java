package ba.tim14.nwt.nwt_android.classes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

/**
 *
 * Created by ena on 5/16/18.
 */

public class ManageLocation implements LocationListener {

    private Context context;
    private Location location;
    private LocationManager locationManager;
    private double latitude;
    private double longitude;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;

    public ManageLocation(Context context) {

        this.context = context;
        this.latitude = getLongitude();
        this.longitude = getLatitude();
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    private boolean chooseProvider() {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return !(!gpsEnabled && !networkEnabled);
    }

    public boolean locationIsEnabled() {
        boolean locationEnabled = chooseProvider();
        if (!locationEnabled) {
            locationEnabled = chooseProvider();
        } else if (location != null)
            locationEnabled = true;
        return locationEnabled;
    }

    public void setLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (gpsEnabled) {
                if (location == null) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            setLatitudeAndLongitude();
                        }
                    }
                }
            }
            if (networkEnabled) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return;}
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 100, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        setLatitudeAndLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLocationFirst() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (networkEnabled) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return;}
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 100, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        setLatitudeAndLongitude();
                    }
                }
            }
            if (gpsEnabled) {
                if (location == null) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            setLatitudeAndLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLatitudeAndLongitude() {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
    }

    public double getLatitude() {
        return latitude;
    }

    private void setLatitude(double latitude) {
        if (latitude == 0) {
            this.latitude = 0;
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void setLongitude(double longitude) {
        if (longitude == 0) {
            this.longitude = 0;
        }
        this.longitude = longitude;
    }

    public Location getLocationValue() {
        return location;
    }

    @Override
    public void onLocationChanged(Location loc) {
        this.location = loc;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

}