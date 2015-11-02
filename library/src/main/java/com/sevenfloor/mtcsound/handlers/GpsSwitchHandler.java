package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsSwitchHandler extends ParameterHandler {
    public GpsSwitchHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_gps_switch"; // stock
    }

    @Override
    public String set(String value) {
        Boolean b = Utils.stringToBoolean(value, "on", "off");
        if (b == null) return value;
        device.state.gpsState.gpsSwitch = b;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.gpsState.gpsSwitch, "on", "off");
    }
}
