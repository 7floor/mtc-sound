package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class LoudnessOnHandler extends ParameterHandler {
    public LoudnessOnHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_lud"; // stock
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "on", "off");
        if (x == null) return value;
        device.state.getCurrentProfile().loudnessOn = x;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.getCurrentProfile().loudnessOn, "on", "off");
    }
}
