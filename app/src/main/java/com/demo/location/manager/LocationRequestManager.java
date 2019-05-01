package com.demo.location.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

public class LocationRequestManager {
    private long INTERVAL = 10000;
    private WeakReference<Context> mContext;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationUpdatesCallback mCallback;

    public LocationRequestManager(@NonNull Context context, @NonNull LocationUpdatesCallback callback) {
        mContext = new WeakReference<>(context);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext.get());
        mCallback = callback;
    }

    public void startLocationUpdateRequest() {
        if (!hasAllRequiredPermissions()) {
            return;
        }
        requestLocationUpdates();
    }

    public void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public boolean hasAllRequiredPermissions() {

        return ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext.get(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLocations().get(0);

            if (mCallback != null) {
                mCallback.onLocationChange(location);
            }
        }
    };

    public interface LocationUpdatesCallback {
        public void onLocationChange(Location location);
    }
}
