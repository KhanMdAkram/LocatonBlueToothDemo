package com.demo.location.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.demo.location.R;
import com.demo.location.databinding.ActivityMainBinding;
import com.demo.location.service.LocationTrackingService;
import com.demo.location.util.Utils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding mBinding;
    LocationTrackingService mLocationTrackingService;
    boolean mIsServicesBounded;
    int MY_PERMISSIONS_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Utils.getDeviceIMEI(this);
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        boolean isPermissonNotGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;

        if (isPermissonNotGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST);
        } else if (!Utils.isLocationServiceEnabled(this)) {
            Utils.openLocationSourceSettings(this);
        }
    }

    private void initListeners() {
        mBinding.start.setOnClickListener(this);
        mBinding.stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (mIsServicesBounded) {
                    Toast.makeText(this, R.string.location_already_being_send, Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(mBinding.weburl.getText())) {
                    Toast.makeText(this, R.string.plese_enter_web_url, Toast.LENGTH_SHORT).show();
                } else {
                    startLocationTracking();
                }
                break;
            case R.id.stop:
                stopLocationTracking();
                break;
        }
    }

    private void startLocationTracking() {
        Intent locationTrackingServiceIntent = new Intent(this, LocationTrackingService.class);
        bindService(locationTrackingServiceIntent, mLocationServiceConnection, Context.BIND_AUTO_CREATE);
        updateStartButton(true);
    }

    private void updateStartButton(boolean start) {
        mBinding.start.setBackgroundColor(start ? getResources().getColor(android.R.color.holo_green_light) : getResources().getColor(android.R.color.black));
    }

    private void stopLocationTracking() {
        if (mIsServicesBounded) {
            unbindService(mLocationServiceConnection);
            mIsServicesBounded = false;
            updateStartButton(false);
        }
    }

    private ServiceConnection mLocationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationTrackingService.LocationBinder binder = (LocationTrackingService.LocationBinder) service;
            mLocationTrackingService = binder.getService();
            mIsServicesBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServicesBounded = false;
        }
    };

}
