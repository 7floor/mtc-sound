package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class LoudnessOnHandler extends ParameterHandler {
    public LoudnessOnHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "on", "off");
        if (x == null) return null;
        device.state.getCurrentProfile().loudnessOn = x;
        device.applyState();
        return value; // let the MCU know, useful for no HW patch
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.getCurrentProfile().loudnessOn, "on", "off");
    }
}
