package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class Balance {
    private int value = 0;

    public void setValue(int value) {
        // -14..14 (minus for to the left, plus to the right)
        this.value = Utils.adjustInt(value, -14, 14);
    }

    public int getValue() {
        return value;
    }

    public int getAttenuationInDB() {
        int v = value > 0 ? value : -value;
        return v * 2;
    }
}
