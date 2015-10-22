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
        return null;
    }

    @Override
    public String get() {
        return getBand().getString();
    }

    protected abstract EqualizerBand getBand();
}

