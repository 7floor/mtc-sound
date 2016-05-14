package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;
import com.sevenfloor.mtcsound.state.Input;
import com.sevenfloor.mtcsound.state.PhoneState;

public class ChannelExitHandler extends ParameterHandler {
    public ChannelExitHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "av_channel_exit"; // stock
    }

    @Override
    public String set(String value) {
        Input input = Utils.stringToEnum(Input.class, value);
        if (input == null) return value;

        if (device.state.inputMode.input == Input.sys)
            return value;

        device.state.inputMode.input = Input.sys;
        device.applyState();

        device.notifyInputChange();

        return value;
    }
}
