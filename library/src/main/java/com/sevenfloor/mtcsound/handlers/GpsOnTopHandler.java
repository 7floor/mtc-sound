package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsOnTopHandler extends ParameterHandler {
    public GpsOnTopHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_gps_ontop"; // stock
    }

    @Override
    public String set(String value) {
        if (device.state.settings.gpsAltMix)
            value = "false";
        Boolean b = Utils.stringToBoolean(value, "true", "false");
        if (b == null) return value;
        device.state.gpsState.gpsOnTop = b;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.gpsState.gpsOnTop, "true", "false");
    }
}

