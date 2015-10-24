package com.sevenfloor.mtcsound.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sevenfloor.mtcsound.R;
import com.sevenfloor.mtcsound.ui.controls.AfcChart;

public class EqualizerFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    private CheckBox muteOn, loudOn, equalizerOn;
    private SeekBar preampG, bassG, bassF, bassQ, middleG, middleF, middleQ, trebleG, trebleF, trebleQ, loudG, loudF, loudHC;
    private TextView preampV, bassV, middleV, trebleV, loudV, inputV;
    private AfcChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equalizer, container, false);

        muteOn = (CheckBox) view.findViewById(R.id.mute_on);
        loudOn = (CheckBox) view.findViewById(R.id.loud_on);
        equalizerOn = (CheckBox) view.findViewById(R.id.equalizer_on);

        muteOn.setOnClickListener(this);
        loudOn.setOnClickListener(this);
        equalizerOn.setOnClickListener(this);

        preampG = (SeekBar)view.findViewById(R.id.seekBarPreamp);
        bassG = (SeekBar)view.findViewById(R.id.seekBarBassG);
        bassF = (SeekBar)view.findViewById(R.id.seekBarBassF);
        bassQ = (SeekBar)view.findViewById(R.id.seekBarBassQ);
        middleG = (SeekBar)view.findViewById(R.id.seekBarMiddleG);
        middleF = (SeekBar)view.findViewById(R.id.seekBarMiddleF);
        middleQ = (SeekBar)view.findViewById(R.id.seekBarMiddleQ);
        trebleG = (SeekBar)view.findViewById(R.id.seekBarTrebleG);
        trebleF = (SeekBar)view.findViewById(R.id.seekBarTrebleF);
        trebleQ = (SeekBar)view.findViewById(R.id.seekBarTrebleQ);
        loudG = (SeekBar)view.findViewById(R.id.seekBarLoudG);
        loudF = (SeekBar)view.findViewById(R.id.seekBarLoudF);
        loudHC = (SeekBar)view.findViewById(R.id.seekBarLoudHC);

        preampG.setOnSeekBarChangeListener(this);
        bassG.setOnSeekBarChangeListener(this);
        bassF.setOnSeekBarChangeListener(this);
        bassQ.setOnSeekBarChangeListener(this);
        middleG.setOnSeekBarChangeListener(this);
        middleF.setOnSeekBarChangeListener(this);
        middleQ.setOnSeekBarChangeListener(this);
        trebleG.setOnSeekBarChangeListener(this);
        trebleF.setOnSeekBarChangeListener(this);
        trebleQ.setOnSeekBarChangeListener(this);
        loudG.setOnSeekBarChangeListener(this);
        loudF.setOnSeekBarChangeListener(this);
        loudHC.setOnSeekBarChangeListener(this);

        bassG.setOnTouchListener(this);
        bassF.setOnTouchListener(this);
        bassQ.setOnTouchListener(this);
        middleG.setOnTouchListener(this);
        middleF.setOnTouchListener(this);
        middleQ.setOnTouchListener(this);
        trebleG.setOnTouchListener(this);
        trebleF.setOnTouchListener(this);
        trebleQ.setOnTouchListener(this);

        preampV = (TextView)view.findViewById(R.id.preamp_v);
        bassV = (TextView)view.findViewById(R.id.bass_v);
        middleV = (TextView)view.findViewById(R.id.middle_v);
        trebleV = (TextView)view.findViewById(R.id.treble_v);
        loudV = (TextView)view.findViewById(R.id.loud_v);
        inputV = (TextView)view.findViewById(R.id.current_input);

        chart = (AfcChart)view.findViewById(R.id.afcChart);
        chart.setGrid(false);

        return view;
    }

    public void update()
    {
        updateInput();
        updateOnSwitches();
        updateBars();
    }

    private void updateInput() {
        Resources resources = getResources();

        String phone = audioManager.getParameters("av_phone=");
        String input = audioManager.getParameters("av_channel=");
        String result;

        if ("answer".equalsIgnoreCase(phone)) {
            result = resources.getString(R.string.input_phone);
        } else {
            if ("sys".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_sys);
            } else if ("fm".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_fm);
            } else if ("ipod".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_ipod);
            } else if ("line".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_line);
            } else if ("gsm_bt".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_gsm_bt);
            } else if ("dvd".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_dvd);
            } else if ("dtv".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_dtv);
            } else if ("dvr".equalsIgnoreCase(input)) {
                result = resources.getString(R.string.input_dvr);
            } else {
                result = "?";
            }
        }
        inputV.setText(result);
    }

    private void updateOnSwitches() {
        muteOn.setChecked("true".equalsIgnoreCase(audioManager.getParameters("av_mute=")));
        loudOn.setChecked("on".equalsIgnoreCase(audioManager.getParameters("av_lud=")));
        equalizerOn.setChecked("on".equalsIgnoreCase(audioManager.getParameters("av_eq_on=")));
    }

    private void updateBars() {
        int[] params = parseList(audioManager.getParameters("av_gain="));
        if (params.length == 1) {
            preampG.setProgress(params[0]);
        }

        params = parseList(audioManager.getParameters("av_eq_bass="));
        if (params.length == 3) {
            bassG.setProgress(params[0] + 20);
            bassF.setProgress(params[1]);
            bassQ.setProgress(params[2]);
        }

        params = parseList(audioManager.getParameters("av_eq_middle="));
        if (params.length == 3) {
            middleG.setProgress(params[0] + 20);
            middleF.setProgress(params[1]);
            middleQ.setProgress(params[2]);
        }

        params = parseList(audioManager.getParameters("av_eq_treble="));
        if (params.length == 3) {
            trebleG.setProgress(params[0] + 20);
            trebleF.setProgress(params[1]);
            trebleQ.setProgress(params[2]);
        }

        params = parseList(audioManager.getParameters("av_loudness="));
        if (params.length == 3) {
            loudG.setProgress(params[0]);
            loudF.setProgress(params[1]);
            loudHC.setProgress(params[2]);
        }

        updateValues();
        updateChart();
    }

    private void updateValues() {
        preampV.setText(formatGain(preampG.getProgress()));
        bassV.setText(formatGain(bassG.getProgress() - 20));
        middleV.setText(formatGain(middleG.getProgress() - 20));
        trebleV.setText(formatGain(trebleG.getProgress() - 20));
        loudV.setText(formatGain(loudG.getProgress()));
    }

    private void updateChart() {
        updateChartBass();
        updateChartMiddle();
        updateChartTreble();
    }

    private void updateChartBass() {
        chart.bass.setG(bassG.getProgress() - 20);
        chart.bass.setF(bassF(bassF.getProgress()));
        chart.bass.setQ(bassQ(bassQ.getProgress()));
    }

    private void updateChartMiddle() {
        chart.middle.setG(middleG.getProgress() - 20);
        chart.middle.setF(middleF(middleF.getProgress()));
        chart.middle.setQ(middleQ(middleQ.getProgress()));
    }

    private void updateChartTreble() {
        chart.treble.setG(trebleG.getProgress() - 20);
        chart.treble.setF(trebleF(trebleF.getProgress()));
        chart.treble.setQ(trebleQ(trebleQ.getProgress()));
    }

    private String formatGain(int gain) {
        if (gain == 0) return "0";
        return String.format("%+d", gain);
    }

    private float bassF(int i) {
        switch (i) {
            case 0:
                return 60;
            case 1:
                return 80;
            case 2:
                return 100;
            default:
                return 120;
        }
    }

    private float bassQ(int i) {
        switch (i) {
            case 0:
                return 0.5f;
            case 1:
                return 1.0f;
            case 2:
                return 1.5f;
            default:
                return 2.0f;
        }
    }

    private float middleF(int i) {
        switch (i) {
            case 0:
                return 500;
            case 1:
                return 1000;
            case 2:
                return 1500;
            default:
                return 2000;
        }
    }

    private float middleQ(int i) {
        switch (i) {
            case 0:
                return 0.75f;
            case 1:
                return 1.0f;
            case 2:
                return 1.25f;
            default:
                return 1.5f;
        }
    }

    private float trebleF(int i) {
        switch (i) {
            case 0:
                return 7500;
            case 1:
                return 10000;
            case 2:
                return 12500;
            default:
                return 15000;
        }
    }

    private float trebleQ(int i) {
        switch (i) {
            case 0:
                return 0.75f;
            default:
                return 1.25f;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.mute_on:
                audioManager.setParameters("av_mute=" + (muteOn.isChecked() ? "true" : "false"));
                break;
            case R.id.loud_on:
                audioManager.setParameters("av_lud=" + (loudOn.isChecked() ? "on" : "off"));
                break;
            case R.id.equalizer_on:
                audioManager.setParameters("av_eq_on=" + (equalizerOn.isChecked() ? "on" : "off"));
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) return;

        switch (seekBar.getId())
        {
            case R.id.seekBarPreamp:
                audioManager.setParameters(String.format("av_gain=%d", preampG.getProgress()));
                break;
            case R.id.seekBarBassG:
            case R.id.seekBarBassF:
            case R.id.seekBarBassQ:
                audioManager.setParameters(String.format("av_eq_bass=%d,%d,%d", bassG.getProgress()-20, bassF.getProgress(), bassQ.getProgress()));
                updateChartBass();
                break;
            case R.id.seekBarMiddleG:
            case R.id.seekBarMiddleF:
            case R.id.seekBarMiddleQ:
                audioManager.setParameters(String.format("av_eq_middle=%d,%d,%d", middleG.getProgress()-20, middleF.getProgress(), middleQ.getProgress()));
                updateChartMiddle();
                break;
            case R.id.seekBarTrebleG:
            case R.id.seekBarTrebleF:
            case R.id.seekBarTrebleQ:
                audioManager.setParameters(String.format("av_eq_treble=%d,%d,%d", trebleG.getProgress()-20, trebleF.getProgress(), trebleQ.getProgress()));
                updateChartTreble();
                break;
            case R.id.seekBarLoudG:
            case R.id.seekBarLoudF:
            case R.id.seekBarLoudHC:
                audioManager.setParameters(String.format("av_loudness=%d,%d,%d", loudG.getProgress(), loudF.getProgress(), loudHC.getProgress()));
                break;
        }
        updateValues();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (SeekBar.class.isInstance(v))
        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    chart.setGrid(true);
                    break;
                case MotionEvent.ACTION_UP:
                    chart.setGrid(false);
                    break;
            }
            return false;
        }
        return false;
    }
}
