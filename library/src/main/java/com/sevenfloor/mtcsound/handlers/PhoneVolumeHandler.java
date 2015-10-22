package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class PhoneVolumeHandler extends ParameterHandler {
    public PhoneVolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Integer v = Utils.stringToInt(value);
        if (v == null) return null;
        device.state.phoneVolume.setValue(v);
        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return String.valueOf(device.state.phoneVolume.getValue());
    }
}
