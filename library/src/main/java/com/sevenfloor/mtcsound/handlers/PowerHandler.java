package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

// listen to power, to reapply state
// on some devices (maybe some 3066) it is known to lose system input after return from sleep
public class PowerHandler extends ParameterHandler {
    public PowerHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "rpt_power"; // stock
    }

    @Override
    public String set(String value) {
        device.applyState(true);
        return value; // pass through to MCU
    }
}
