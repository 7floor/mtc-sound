package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class PhoneOut {
    public boolean fl = true;
    public boolean fr = true;
    public boolean rl = false;
    public boolean rr = false;

    public void setString(String value) {
        String[] values = value.split(",");
        if (values.length != 4) return;

        Integer v;

        v = Utils.stringToInt(values[0]);
        if (v != null)
            fl = v != 0;

        v = Utils.stringToInt(values[1]);
        if (v != null)
            fr = v != 0;

        v = Utils.stringToInt(values[2]);
        if (v != null)
            rl = v != 0;

        v = Utils.stringToInt(values[3]);
        if (v != null)
            rr = v != 0;
    }

    public String getString()
    {
        return String.format("%d,%d,%d,%d",  fl ? 1 : 0, fr ? 1 : 0, rl ? 1 : 0, rr ? 1 : 0);
    }

}
