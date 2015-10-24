package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsOnTopHandler extends ParameterHandler {
    public GpsOnTopHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean b = device.state.settings.gpsOnTopEnable
                ? Utils.stringToBoolean(value, "true", "false")
                : (Boolean)false;

        if (b == null) b = false;

        device.state.gpsState.gpsOnTop = b;
        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.gpsState.gpsOnTop, "true", "false");
    }
}

