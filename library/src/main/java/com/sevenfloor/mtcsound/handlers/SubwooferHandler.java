package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class SubwooferHandler extends ParameterHandler {
    public SubwooferHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "cfg_subwoofer"; // non-stock
    }

    @Override
    public String set(String value) {
        device.state.settings.subwoofer.setString(value);
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return device.state.settings.subwoofer.getString();
    }
}

