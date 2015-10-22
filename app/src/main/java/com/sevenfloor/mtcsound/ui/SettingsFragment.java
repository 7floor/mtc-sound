package com.sevenfloor.mtcsound.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sevenfloor.mtcsound.R;

public class SettingsFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private SeekBar subOutput, subCutOff, subPhase, subGain, volMin, volMax;
    private TextView subGainV, volMinV, volMaxV, status;
    private CheckBox altNavi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        subOutput = (SeekBar)view.findViewById(R.id.seekBarSubOut);
        subCutOff = (SeekBar)view.findViewById(R.id.seekBarSubCutOff);
        subPhase = (SeekBar)view.findViewById(R.id.seekBarSubPhase);
        subGain = (SeekBar)view.findViewById(R.id.seekBarSubGain);
        volMin = (SeekBar)view.findViewById(R.id.seekBarVolGainMin);
        volMax = (SeekBar)view.findViewById(R.id.seekBarVolGainMax);

        subOutput.setOnSeekBarChangeListener(this);
        subCutOff.setOnSeekBarChangeListener(this);
        subPhase.setOnSeekBarChangeListener(this);
        subGain.setOnSeekBarChangeListener(this);
        volMin.setOnSeekBarChangeListener(this);
        volMax.setOnSeekBarChangeListener(this);

        subGainV = (TextView)view.findViewById(R.id.subGain);
        volMinV = (TextView)view.findViewById(R.id.volMinGain);
        volMaxV = (TextView)view.findViewById(R.id.volMaxGain);
        status = (TextView)view.findViewById(R.id.control_status);

        (altNavi = (CheckBox)view.findViewById(R.id.setup_altnavi)).setOnClickListener(this);

        return view;
    }

    @Override
    protected void update() {
        updateBars();
        updateOther();
    }

    private void updateOther() {
        altNavi.setChecked("true".equals(audioManager.getParameters("cfg_gps_altmix=")));
    }

    private void updateBars() {
        int[] params = parseList(audioManager.getParameters("cfg_subwoofer="));
        if (params.length == 4) {
            subOutput.setProgress(params[0]);
            subCutOff.setProgress(params[1]);
            subPhase.setProgress(params[2]);
            subGain.setProgress(params[3] + 79);
        }

        params = parseList(audioManager.getParameters("cfg_volumerange="));
        if (params.length == 2) {
            volMin.setProgress(params[0] + 79);
            volMax.setProgress(params[1] + 79);
        }
        updateValues();
    }

    private void updateValues() {
        subGainV.setText(formatGain(subGain.getProgress() - 79));
        volMinV.setText(formatGain(volMin.getProgress() - 79));
        volMaxV.setText(formatGain(volMax.getProgress() - 79));
        status.setText(audioManager.getParameters("av_control_mode="));
    }

    private String formatGain(int gain) {
        if (gain == 0) return "0 dB";
        return String.format("%+d dB", gain);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) return;

        switch (seekBar.getId()) {
            case R.id.seekBarSubOut:
            case R.id.seekBarSubCutOff:
            case R.id.seekBarSubPhase:
            case R.id.seekBarSubGain:
                audioManager.setParameters(String.format("cfg_subwoofer=%d,%d,%d,%d", subOutput.getProgress(), subCutOff.getProgress(), subPhase.getProgress(), subGain.getProgress() - 79));
                break;
            case R.id.seekBarVolGainMin:
            case R.id.seekBarVolGainMax:
                int min = volMin.getProgress();
                int max = volMax.getProgress();
                if (min > max) {
                    if (seekBar.getId() == R.id.seekBarVolGainMin){
                        min = max;
                        volMin.setProgress(min);
                    } else {
                        max = min;
                        volMax.setProgress(max);
                    }
                }
                audioManager.setParameters(String.format("cfg_volumerange=%d,%d", min - 79, max - 79));
                break;
        }
        updateValues();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.setup_altnavi:
                audioManager.setParameters("cfg_gps_altmix=" + (altNavi.isChecked() ? "true" : "false"));
                break;
        }
    }
}
