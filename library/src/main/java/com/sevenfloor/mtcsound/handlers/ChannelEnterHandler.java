package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;
import com.sevenfloor.mtcsound.state.Input;
import com.sevenfloor.mtcsound.state.PhoneState;

public class ChannelEnterHandler extends ParameterHandler {
    public ChannelEnterHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_channel_enter"; // stock
    }

    @Override
    public String set(String value) {
        Input input = Utils.stringToEnum(Input.class, value);
        if (input == null) return value;

        if (device.state.inputMode.input == input)
            return value;

        device.state.inputMode.input = input;
        device.applyState();

        device.notifyInputChange();

        return value;
    }
}


