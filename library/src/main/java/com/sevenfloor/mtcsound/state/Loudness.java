package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class Loudness {
    private int gain = 0;

    public void setGain(int value) {
        gain = Utils.adjustInt(value, 0, 20);
    }

    public int getGain() {
        return gain;
    }

    private int frequency = 0;

    public void setFrequency(int value) {
        frequency = Utils.adjustInt(value, 0, 2);
    }

    public int getFrequency() {
        return frequency;
    }

    private int hicut = 0;

    public void setHicut(int value) {
        hicut = Utils.adjustInt(value, 0, 3);
    }

    public int getHicut() {
        return hicut;
    }

    public void setString(String value){
        String[] values = value.split(",");
        if (values.length != 3) return;

        Integer v;

        v = Utils.stringToInt(values[0]);
        if (v != null)
            setGain(v);

        v = Utils.stringToInt(values[1]);
        if (v != null)
            setFrequency(v);

        v = Utils.stringToInt(values[2]);
        if (v != null)
            setHicut(v);
    }

    public String getString(){
        return String.format("%d,%d,%d", gain, frequency, hicut);
    }

}
