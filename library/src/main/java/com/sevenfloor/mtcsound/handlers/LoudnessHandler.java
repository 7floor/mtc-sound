package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class LoudnessHandler extends ParameterHandler {
    public LoudnessHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_loudness"; // non-stock
    }

    @Override
    public String set(String value) {
        device.state.getCurrentProfile().loudness.setString(value);
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return device.state.getCurrentProfile().loudness.getString();
    }
}

