package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.state.EqualizerBand;

public class MiddleHandler extends EqualizerBandHandler {
    public MiddleHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_eq_middle"; // non-stock
    }

    @Override
    protected EqualizerBand getBand() {
        return device.state.getCurrentProfile().middleBand;
    }
}

