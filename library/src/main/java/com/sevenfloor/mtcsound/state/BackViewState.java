package com.sevenfloor.mtcsound.state;

public class BackViewState {
    public boolean active = false;
    public int cut = 0;

    public int getActualCut() {
        return active ? cut  : 0;
    }
}
