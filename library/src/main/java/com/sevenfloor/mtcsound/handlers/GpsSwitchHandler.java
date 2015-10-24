package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsSwitchHandler extends ParameterHandler {
    public GpsSwitchHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean b = Utils.stringToBoolean(value, "on", "off");
        if (b == null) b = false;

        device.state.gpsState.gpsSwitch = b;
        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.gpsState.gpsSwitch, "on", "off");
    }
}
