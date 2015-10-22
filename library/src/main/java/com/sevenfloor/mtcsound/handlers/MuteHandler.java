package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class MuteHandler extends ParameterHandler {
    public MuteHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return null;
        device.state.mute = x;
        device.applyState();
        return value; // let the MCU know, useful for backward compatibility
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.mute, "true", "false");
    }
}

