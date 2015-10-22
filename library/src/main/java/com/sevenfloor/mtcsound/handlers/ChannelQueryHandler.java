package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class ChannelQueryHandler extends ParameterHandler {
    public ChannelQueryHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        return value;
    }

    @Override
    public String get() {
        return device.state.inputMode.input.toString();
    }
}
