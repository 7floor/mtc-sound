package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class VolumeRangeHandler extends ParameterHandler {
    public VolumeRangeHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        device.state.settings.volumeRange.setString(value);
        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return device.state.settings.volumeRange.getString();
    }
}
