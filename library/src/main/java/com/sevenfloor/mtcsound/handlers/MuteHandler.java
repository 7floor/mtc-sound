package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class MuteHandler extends ParameterHandler {
    public MuteHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_mute"; // stock
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return value;
        device.state.mute = x;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.mute, "true", "false");
    }
}

