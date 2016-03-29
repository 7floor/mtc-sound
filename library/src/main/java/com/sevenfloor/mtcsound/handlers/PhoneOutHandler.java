package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class PhoneOutHandler extends ParameterHandler {
    public PhoneOutHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "cfg_gps_phoneout"; // non-stock
    }

    @Override
    public String set(String value) {
        device.state.settings.phoneOut.setString(value);
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return device.state.settings.phoneOut.getString();
    }
}
