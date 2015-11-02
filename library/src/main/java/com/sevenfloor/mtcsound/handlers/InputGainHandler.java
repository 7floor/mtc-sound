package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class InputGainHandler extends ParameterHandler {
    public InputGainHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_gain"; // non-stock
    }

    @Override
    public String set(String value) {
        Integer v = Utils.stringToInt(value);
        if (v == null) return null;
        device.state.getCurrentProfile().setInputGain(v);
        device.applyState();
        return null; // prevent from sending to MCU
    }

    @Override
    public String get(String value) {
        return String.valueOf(device.state.getCurrentProfile().getInputGain());
    }
}
