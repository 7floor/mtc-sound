package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class GsmAltInputHandler extends ParameterHandler {
    public GsmAltInputHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "cfg_gsm_altinput"; // non-stock
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return null;
        device.state.settings.gsmAltInput = x;
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.settings.gsmAltInput, "true", "false");
    }
}
