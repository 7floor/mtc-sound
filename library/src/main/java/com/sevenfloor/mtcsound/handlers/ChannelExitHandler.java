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

        boolean wasMuted = device.state.mute;
        if (!wasMuted && device.state.inputMode.phoneState != PhoneState.answer) {
            device.state.mute = true;
            device.applyState();
            Utils.sleep(25);
        }

        device.state.inputMode.input = Input.sys;
        device.applyState();

        if (!wasMuted && device.state.mute) {
            Utils.sleep(25);
            device.state.mute = false;
            device.applyState();
        }

        device.notifyInputChange();

        return value;
    }
}
