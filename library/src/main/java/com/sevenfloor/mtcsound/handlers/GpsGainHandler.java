package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsGainHandler extends ParameterHandler {
    public GpsGainHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Integer i = Utils.stringToInt(value);
        if (i == null) i = 0;

        device.state.gpsState.gpsGain = i;
        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return String.valueOf(device.state.gpsState.gpsGain);
    }
}
