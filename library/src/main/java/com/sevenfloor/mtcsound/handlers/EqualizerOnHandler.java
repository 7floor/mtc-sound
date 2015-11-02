package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class EqualizerOnHandler extends ParameterHandler {
    public EqualizerOnHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_eq_on"; // non-stock
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "on", "off");
        if (x == null) return null;
        device.state.getCurrentProfile().equalizerOn = x;
        device.applyState();
        return null; // prevent from sending to MCU
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.getCurrentProfile().equalizerOn, "on", "off");
    }
}

