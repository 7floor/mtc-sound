package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class ChannelQueryHandler extends ParameterHandler {
    public ChannelQueryHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_channel"; // stock
    }

    @Override
    public String get(String value) {
        return device.state.inputMode.input.toString();
    }
}
