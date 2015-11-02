package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class VolumeRangeHandler extends ParameterHandler {
    public VolumeRangeHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "cfg_volumerange"; // non-stock
    }

    @Override
    public String set(String value) {
        device.state.settings.volumeRange.setString(value);
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return device.state.settings.volumeRange.getString();
    }
}
