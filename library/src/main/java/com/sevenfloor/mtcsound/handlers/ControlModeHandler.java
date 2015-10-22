package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class ControlModeHandler extends ParameterHandler {
    public ControlModeHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        return null;
    }

    @Override
    public String get() {
        return device.state.HardwareStatus;
    }
}
