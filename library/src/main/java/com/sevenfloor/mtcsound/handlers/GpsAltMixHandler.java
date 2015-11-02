package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

// when this parameter set to true
// the other parameters, av_gps_package and av_gps_ontop
// will be both ignored (i.e. set as "" and false
// this will prevent the sound from background apps
// being muted/silenced for the GPS app
public class GpsAltMixHandler extends ParameterHandler {
    public GpsAltMixHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "cfg_gps_altmix"; // non-stock
    }

    @Override
    public String set(String value) {
        Boolean x = Utils.stringToBoolean(value, "true", "false");
        if (x == null) return null;
        device.state.settings.gpsAltMix = x;
        device.applyState();
        return null;
    }

    @Override
    public String get(String value) {
        return Utils.booleanToString(device.state.settings.gpsAltMix, "true", "false");
    }
}

