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
        if (device.state.phoneVolume.getValue() != 0)
            device.state.mute = false;
        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return String.valueOf(device.state.phoneVolume.getValue());
    }
}
