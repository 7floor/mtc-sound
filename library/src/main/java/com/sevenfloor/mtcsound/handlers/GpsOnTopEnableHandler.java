package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsOnTopEnableHandler extends ParameterHandler {
    public GpsOnTopEnableHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return null;
        device.state.settings.gpsOnTopEnable = x;
        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.settings.gpsOnTopEnable, "true", "false");
    }
}

