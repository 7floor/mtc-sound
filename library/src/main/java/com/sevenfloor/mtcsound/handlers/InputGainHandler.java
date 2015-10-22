package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class InputGainHandler extends ParameterHandler {
    public InputGainHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Integer v = Utils.stringToInt(value);

        if (v != null)
            device.state.getCurrentProfile().setInputGain(v);

        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return String.valueOf(device.state.getCurrentProfile().getInputGain());
    }
}
