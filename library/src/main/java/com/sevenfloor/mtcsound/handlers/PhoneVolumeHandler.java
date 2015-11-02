package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class PhoneVolumeHandler extends ParameterHandler {
    public PhoneVolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_phone_volume"; // stock
    }

    @Override
    public String set(String value) {
        Integer v = Utils.stringToInt(value);
        if (v == null) return value;
        device.state.phoneVolume.setValue(v);
        device.state.mute = device.state.phoneVolume.getValue() == 0;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return String.valueOf(device.state.phoneVolume.getValue());
    }
}
