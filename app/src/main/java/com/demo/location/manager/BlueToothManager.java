package com.demo.location.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.demo.location.model.BlueToothData;

import java.util.ArrayList;
import java.util.List;


public class BlueToothManager {

    private BluetoothAdapter mBlueToothAdapter;
    private ArrayList<BlueToothData> mDeviceList;
    private BlueToothDeviceCallback mCallback;
    private static final String LOG = "Device_Logs";

    private BroadcastReceiver bluetoothDiscoveryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.w(LOG, "bluetoothDiscoveryReceiver " + action);
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<>();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mCallback.getBlueToothDevice(mDeviceList);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (device != null) {
                    BlueToothData blueToothData = new BlueToothData();
                    blueToothData.setBlueToothDeviceName(device.getName());
                    blueToothData.setBlueToothDeviceRssi(rssi);
                    blueToothData.setBlueToothDeviceId(device.getAddress());
                    Log.w(LOG, "deviceData " + device.getName() + " " + rssi + " " + device.getAddress());
                }
            }
        }
    };

    public BlueToothManager(BlueToothDeviceCallback callback) {
        mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        mCallback = callback;
    }

    public void startDeviceDiscovery(Context context) {
        if (mBlueToothAdapter != null) {

            if (mBlueToothAdapter.isDiscovering()) {
                Log.w(LOG, "mBTA is Discovering");
                stopDeviceDiscovery();
            }

            mBlueToothAdapter.startDiscovery();
        }
    }

    public void stopDeviceDiscovery() {
        mBlueToothAdapter.cancelDiscovery();
    }

    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(bluetoothDiscoveryReceiver, filter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(bluetoothDiscoveryReceiver);
    }

    public interface BlueToothDeviceCallback {
        void getBlueToothDevice(List<BlueToothData> deviceList);
    }
}
