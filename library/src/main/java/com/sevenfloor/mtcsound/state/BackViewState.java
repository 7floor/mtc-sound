package com.sevenfloor.mtcsound.state;

public class BackViewState {
    public boolean active = false;
    public int cut = 0;

    // 0 to 10 is the cut in dB, 11 is the total mute
    public int getActualCut() {
        return active ? (cut < 11 ? cut : 100) : 0;
    }
}
