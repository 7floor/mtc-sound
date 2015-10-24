package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class GpsPackageHandler extends ParameterHandler {
    public GpsPackageHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        device.state.gpsState.gpsPackage = device.state.settings.gpsAltMix
                ? ""
                : value;

        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return device.state.gpsState.gpsPackage;
    }
}
