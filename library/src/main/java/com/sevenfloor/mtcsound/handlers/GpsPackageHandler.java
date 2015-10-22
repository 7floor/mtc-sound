package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class GpsPackageHandler extends ParameterHandler {
    public GpsPackageHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        return device.state.settings.gpsAltMix ? "" : value;
    }

    @Override
    public String get() {
        return null;
    }
}
