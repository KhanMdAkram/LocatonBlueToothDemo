package com.demo.location.service;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.location.manager.BlueToothManager;
import com.demo.location.manager.LocationRequestManager;
import com.demo.location.model.BlueToothData;
import com.demo.location.model.DeviceLocation;
import com.demo.location.model.ServerRequest;
import com.demo.location.network.NetWorkCall;
import com.demo.location.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class LocationTrackingService extends Service implements LocationRequestManager.LocationUpdatesCallback, BlueToothManager.BlueToothDeviceCallback {

    private IBinder mLocationBinder = new LocationBinder();
    private LocationRequestManager mLocationRequestManager;
    private BlueToothManager mBlueToothManager;
    private List<DeviceLocation> mLocationList;
    private Handler mHandler;
    private final int LOCATION_SEND_DELAY = 30000;

    private static final String LOG = "Device_Logs";

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationList = new ArrayList<>();
        mHandler = new Handler();
        mLocationRequestManager = new LocationRequestManager(this, this);
        mBlueToothManager = new BlueToothManager(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mLocationRequestManager.startLocationUpdateRequest();
        mBlueToothManager.registerReceiver(this);
        mHandler.post(mLocationRunnable);
        return mLocationBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(LOG, "onDestroy");
        mLocationRequestManager.stopLocationUpdates();
        mBlueToothManager.stopDeviceDiscovery();
        mBlueToothManager.unregisterReceiver(this);
        mHandler.removeCallbacks(mLocationRunnable);
    }

    @Override
    public void onLocationChange(Location location) {
        if (mLocationList != null) {
            DeviceLocation deviceLocation = new DeviceLocation();
            deviceLocation.setLatitude(location.getLatitude());
            deviceLocation.setLongitude(location.getLongitude());
            mLocationList.add(deviceLocation);
        }
    }

    private Runnable mLocationRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mLocationRunnable, LOCATION_SEND_DELAY);
            mBlueToothManager.startDeviceDiscovery(LocationTrackingService.this);
            Log.w(LOG, "runnable called");
        }
    };

    @Override
    public void getBlueToothDevice(List<BlueToothData> deviceList) {
        ServerRequest request = new ServerRequest();
        request.setBlueToothData(deviceList);
        request.setDeviceLocations(mLocationList);
        String IMEI = Utils.getIMEI(this);
        request.setDeviceImei(IMEI);
        mLocationList = new ArrayList<>();
        NetWorkCall.callNetworkApi(request);
    }


    public class LocationBinder extends Binder {
        public LocationTrackingService getService() {
            return LocationTrackingService.this;
        }
    }
}
