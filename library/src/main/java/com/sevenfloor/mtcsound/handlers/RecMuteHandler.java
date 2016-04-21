package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class RecMuteHandler extends ParameterHandler {
    public RecMuteHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "cfg_rec_mute"; // non-stock
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return null;
        device.state.settings.recMute = x;
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.settings.recMute, "true", "false");
    }
}
