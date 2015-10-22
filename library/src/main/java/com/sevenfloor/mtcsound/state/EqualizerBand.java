package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class EqualizerBand {
    private final int gainMin;
    private final int gainMax;
    private final int numFreq;
    private final int numQ;

    private int gain = 0;

    public EqualizerBand(int gainMin, int gainMax, int numFreq, int numQ) {
        this.gainMin = gainMin;
        this.gainMax = gainMax;
        this.numFreq = numFreq;
        this.numQ = numQ;
    }

    public void setGain(int value){
        gain = Utils.adjustInt(value, gainMin, gainMax);
    }

    public int getGain(){
        return gain;
    }

    private int frequency = 0;

    public void setFrequency(int value) {
        frequency = Utils.adjustInt(value, 0, numFreq - 1);
    }

    public int getFrequency() {
        return frequency;
    }

    private int quality = 0;

    public void setQuality(int value) {
        quality = Utils.adjustInt(value, 0, numQ - 1);
    }

    public int getQuality() {
        return quality;
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
            setQuality(v);
    }

    public String getString(){
        return String.format("%d,%d,%d", gain, frequency, quality);
    }
}
