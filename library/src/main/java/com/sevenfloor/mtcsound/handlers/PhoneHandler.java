package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;
import com.sevenfloor.mtcsound.state.PhoneState;

public class PhoneHandler extends ParameterHandler {
    public PhoneHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        PhoneState state = Utils.stringToEnum(PhoneState.class, value);
        if (state == null) return null;

        if (device.state.inputMode.phoneState == state) {
            return value;
        }

        device.state.inputMode.phoneState = state;
        device.applyState();
        device.notifyInputChange();

        return value;
    }

    @Override
    public String get() {
        return device.state.inputMode.phoneState.name();
    }
}
