package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class Volume {
    private VolumeRange range;
    private int value = 0;

    public Volume(VolumeRange range) {
        this.range = range;
    }

    public void setValue(int value)
    {
        this.value = Utils.adjustInt(value, 0, 100);
    }

    public int getValue(){
        return this.value;
    }

    public int getValueInDb() {
        int min = range.getMin();
        int max = range.getMax();
        return min + (max - min) * value / 100;
    }
}
