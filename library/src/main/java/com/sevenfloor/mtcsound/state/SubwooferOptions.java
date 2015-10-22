package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class SubwooferOptions {

    private int output = 0; // lpf

    public void setOutput(int value) {
        output = Utils.adjustInt(value, 0, 2); // lpf, front, rear
    }

    public int getOutput() {
        return output;
    }

    private int cutFrequency = 0; // off - full range

    public void setCutFrequency(int value) {
        cutFrequency = Utils.adjustInt(value, 0, 4); // off, 55, 85, 120, 160
    }

    public int getCutFrequency() {
        return cutFrequency;
    }

    private int phase = 0; // 0 degrees

    public void setPhase(int value) {
        phase = Utils.adjustInt(value, 0, 1); // 0, 180
    }

    public int getPhase() {
        return phase;
    }

    private int gain = 0; // 0dB

    public void setGain(int value) {
        gain = Utils.adjustInt(value, -79, 15); // -79dB .. +15dB
    }

    public int getGain() {
        return gain;
    }

    public void setString(String value) {
        String[] values = value.split(",");
        if (values.length != 4) return;

        Integer v;

        v = Utils.stringToInt(values[0]);
        if (v != null)
            setOutput(v);

        v = Utils.stringToInt(values[1]);
        if (v != null)
            setCutFrequency(v);

        v = Utils.stringToInt(values[2]);
        if (v != null)
            setPhase(v);

        v = Utils.stringToInt(values[3]);
        if (v != null)
            setGain(v);
    }

    public String getString()
    {
        return String.format("%d,%d,%d,%d", output, cutFrequency, phase, gain);
    }
}
