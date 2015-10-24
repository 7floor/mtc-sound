package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsMonitorHandler extends ParameterHandler {
    public GpsMonitorHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean b = Utils.stringToBoolean(value, "on", "off");
        if (b == null) b = false;

        device.state.gpsState.gpsMonitor = b;
        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.gpsState.gpsMonitor, "on", "off");
    }
}

