package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class PowerHandler extends ParameterHandler {
    public PowerHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        device.applyState(true);
        return value;
    }

    @Override
    public String get() {
        return null;
    }
}
