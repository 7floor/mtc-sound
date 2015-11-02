package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.state.EqualizerBand;

public abstract class EqualizerBandHandler extends ParameterHandler {
    public EqualizerBandHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        getBand().setString(value);
        device.applyState();
        return null; // prevent from sending to MCU
    }

    @Override
    public String get(String value) {
        return getBand().getString();
    }

    protected abstract EqualizerBand getBand();
}

