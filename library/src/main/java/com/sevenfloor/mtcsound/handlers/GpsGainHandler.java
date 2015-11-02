package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GpsGainHandler extends ParameterHandler {
    public GpsGainHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_gps_gain"; // stock
    }

    @Override
    public String set(String value) {
        Integer i = Utils.stringToInt(value);
        if (i == null) return value;
        device.state.gpsState.gpsGain = i;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return String.valueOf(device.state.gpsState.gpsGain);
    }
}
