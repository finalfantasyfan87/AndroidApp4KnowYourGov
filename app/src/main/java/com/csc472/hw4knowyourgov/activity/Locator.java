package com.csc472.hw4knowyourgov.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static com.csc472.hw4knowyourgov.activity.MainActivity.LOCATION_CODE;

public class Locator {

    private MainActivity mainActivity;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Locator(MainActivity activity) {
        mainActivity = activity;

        if (verifyAppPermission()) {
            setUpLocationManager();
            findLocation();
        }
    }

    private boolean verifyAppPermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
            return false;
        }
        return true;
    }
    public void setUpLocationManager() {
        if (!verifyAppPermission())
            return;

        locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mainActivity.doLocationWork(location.getLatitude(), location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(
                GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void findLocation() {
        if (!verifyAppPermission())
            return;

        if (locationManager == null)
            setUpLocationManager();

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                mainActivity.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(mainActivity, "Network Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                mainActivity.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(mainActivity, "Passive Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(GPS_PROVIDER);
            if (loc != null) {
                mainActivity.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(mainActivity, "GPS Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mainActivity.showNoLocationToast();
        return;
    }

    public void shutdown() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
}
