package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class VolumeRange {
    private int min = -60;

    public void setMin(int value){
        min = Utils.adjustInt(value, -79, +15);
        if (max < min) max = min;
    }

    public int getMin() {
        return min;
    }

    private int max = 0;

    public void setMax(int value){
        max = Utils.adjustInt(value, -79, +15);
        if (max < min) min = max;
    }

    public int getMax() {
        return max;
    }

    public void setString(String value){
        String[] values = value.split(",");
        if (values.length != 2) return;

        Integer v;

        v = Utils.stringToInt(values[0]);
        if (v != null)
            setMin(v);

        v = Utils.stringToInt(values[1]);
        if (v != null)
            setMax(v);
    }

    public String getString(){
        return String.format("%d,%d", min, max);
    }
}
