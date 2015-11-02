package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class GpsPackageHandler extends ParameterHandler {
    public GpsPackageHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_gps_package"; // stock
    }

    @Override
    public String set(String value) {
        // alternative mix makes MCU and us to forget about what is the GPS application
        // so there wouldn't be mute/volume lowering for it
        if (device.state.settings.gpsAltMix)
            value = "";
        device.state.gpsState.gpsPackage = value;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return device.state.gpsState.gpsPackage;
    }
}
