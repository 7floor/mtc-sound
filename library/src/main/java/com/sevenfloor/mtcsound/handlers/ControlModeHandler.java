package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class ControlModeHandler extends ParameterHandler {
    public ControlModeHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_control_mode"; // non-stock
    }

    @Override
    public String set(String value) {
        return null; // prevent from sending to MCU
    }

    @Override
    public String get(String value) {
        return device.state.HardwareStatus;
    }
}
