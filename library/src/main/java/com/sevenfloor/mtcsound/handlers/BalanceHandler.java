package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class BalanceHandler extends ParameterHandler {
    public BalanceHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        // lr, fr - both biased as 0..28, but out setting needs -14..14
        String[] values = value.split(",");
        if (values.length != 2) return null;

        Integer v;

        v = Utils.stringToInt(values[0]);
        if (v != null)
            device.state.balance.setValue(v - 14);

        v = Utils.stringToInt(values[1]);
        if (v != null)
            device.state.fader.setValue(v - 14);

        device.applyState();

        return null;
    }

    @Override
    public String get() {
        return String.format("%d,%d", device.state.balance.getValue() + 14, device.state.fader.getValue() + 14);
    }
}
