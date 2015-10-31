package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class BackViewHandler extends ParameterHandler {
    public BackViewHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return null;
        device.state.backViewState.active = x;
        device.applyState();
        return null;
    }

    @Override
    public String get() {
        return Utils.booleanToString(device.state.backViewState.active, "true", "false");
    }
}
