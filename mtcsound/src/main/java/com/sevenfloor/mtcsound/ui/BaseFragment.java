package com.sevenfloor.mtcsound.ui;

import android.app.Fragment;
import android.media.AudioManager;
import android.os.Bundle;

public abstract class BaseFragment extends Fragment {
    protected MainActivity activity;
    protected AudioManager audioManager;

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        audioManager = activity.getAudioManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    protected abstract void update();

    protected int[] parseList(String list)
    {
        if(list == null) return new int[0];

        String[] parts = list.split(",");
        int[] ints = new int[parts.length];

        try {
            for (int i = 0; i < parts.length; i++) {
                ints[i] = Integer.parseInt(parts[i]);
            }
        }
        catch (NumberFormatException e) {
            return new int[0];
        }
        return ints;
    }
}
