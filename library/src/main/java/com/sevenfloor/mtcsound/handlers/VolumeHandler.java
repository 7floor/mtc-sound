package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class VolumeHandler extends ParameterHandler {
    public VolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Integer v = Utils.stringToInt(value);
        if (v == null) return null;
        device.state.volume.setValue(v);
        if (device.state.volume.getValue() != 0)
            device.state.mute = false;
        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return String.valueOf(device.state.volume.getValue());
    }
}

