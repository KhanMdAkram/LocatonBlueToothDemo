package com.demo.location.model;

import java.util.List;

/**
 * Created by khanak7 on 02/05/19.
 */

public class ServerRequest {

    List<DeviceLocation> deviceLocations;
    List<BlueToothData> blueToothData;
    String deviceImei;

    public List<DeviceLocation> getDeviceLocations() {
        return deviceLocations;
    }

    public void setDeviceLocations(List<DeviceLocation> deviceLocations) {
        this.deviceLocations = deviceLocations;
    }

    public List<BlueToothData> getBlueToothData() {
        return blueToothData;
    }

    public void setBlueToothData(List<BlueToothData> blueToothData) {
        this.blueToothData = blueToothData;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }
}
