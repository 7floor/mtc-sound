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
    private TextView subGainV, volMinV, volMaxV;
    private CheckBox phoneFL, phoneFR, phoneRL, phoneRR, altNavi, altGsm, recMute;

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

        (phoneFL = (CheckBox)view.findViewById(R.id.setup_phone_fl)).setOnClickListener(this);
        (phoneFR = (CheckBox)view.findViewById(R.id.setup_phone_fr)).setOnClickListener(this);
        (phoneRL = (CheckBox)view.findViewById(R.id.setup_phone_rl)).setOnClickListener(this);
        (phoneRR = (CheckBox)view.findViewById(R.id.setup_phone_rr)).setOnClickListener(this);

        (altNavi = (CheckBox)view.findViewById(R.id.setup_altnavi)).setOnClickListener(this);
        (altGsm = (CheckBox)view.findViewById(R.id.setup_altgsm)).setOnClickListener(this);
        (recMute = (CheckBox)view.findViewById(R.id.setup_recmute)).setOnClickListener(this);

        return view;
    }

    @Override
    protected void update() {
        updateBars();
        updatePhone();
        updateOther();
    }

    private void updatePhone() {
        int[] params = parseList(audioManager.getParameters("cfg_gps_phoneout="));
        if (params.length == 4){
            phoneFL.setChecked(params[0] != 0);
            phoneFR.setChecked(params[1] != 0);
            phoneRL.setChecked(params[2] != 0);
            phoneRR.setChecked(params[3] != 0);
        }
    }

    private void updateOther() {
        altNavi.setChecked("true".equals(audioManager.getParameters("cfg_gps_altmix=")));
        altGsm.setChecked("true".equals(audioManager.getParameters("cfg_gsm_altinput=")));
        recMute.setChecked("true".equals(audioManager.getParameters("cfg_rec_mute=")));
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
        switch (v.getId()) {
            case R.id.setup_altnavi:
                audioManager.setParameters("cfg_gps_altmix=" + (altNavi.isChecked() ? "true" : "false"));
                break;
            case R.id.setup_altgsm:
                audioManager.setParameters("cfg_gsm_altinput=" + (altGsm.isChecked() ? "true" : "false"));
                break;
            case R.id.setup_recmute:
                audioManager.setParameters("cfg_rec_mute=" + (recMute.isChecked() ? "true" : "false"));
                break;
            case R.id.setup_phone_fl:
            case R.id.setup_phone_fr:
            case R.id.setup_phone_rl:
            case R.id.setup_phone_rr:
                audioManager.setParameters(String.format("cfg_gps_phoneout=%d,%d,%d,%d",
                        phoneFL.isChecked() ? 1 : 0,
                        phoneFR.isChecked() ? 1 : 0,
                        phoneRL.isChecked() ? 1 : 0,
                        phoneRR.isChecked() ? 1 : 0));
                break;
        }
    }
}
