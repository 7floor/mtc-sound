package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class VolumeHandler extends ParameterHandler {
    public VolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_volume"; // stock
    }

    @Override
    public String set(String value) {
        Integer v = Utils.stringToInt(value);
        if (v == null) return value;
        device.state.volume.setValue(v);
        device.state.mute = device.state.volume.getValue() == 0;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return String.valueOf(device.state.volume.getValue());
    }
}

