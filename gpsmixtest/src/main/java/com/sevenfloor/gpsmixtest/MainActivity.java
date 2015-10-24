package com.sevenfloor.gpsmixtest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends Activity implements View.OnClickListener {

    private CheckBox cbPackageEnable, cbOnTopEnable, cbMonitor, cbSwitch, cbOnTop;
    private Button bnRefresh;
    private AudioManager am;
    private ContentResolver cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (cbPackageEnable = (CheckBox)findViewById(R.id.av_gps_package_enable)).setOnClickListener(this);
        (cbOnTopEnable = (CheckBox)findViewById(R.id.av_gps_ontop_enable)).setOnClickListener(this);
        (cbMonitor = (CheckBox)findViewById(R.id.av_gps_monitor)).setOnClickListener(this);
        (cbSwitch = (CheckBox)findViewById(R.id.av_gps_switch)).setOnClickListener(this);
        (cbOnTop = (CheckBox)findViewById(R.id.av_gps_ontop)).setOnClickListener(this);
        (bnRefresh = (Button)findViewById(R.id.bnRefresh)).setOnClickListener(this);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        cr = getContentResolver();

        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.av_gps_package_enable: setPackageEnable(((CheckBox) v).isChecked()); break;
            case R.id.av_gps_ontop_enable: setOntopEnable(((CheckBox) v).isChecked()); break;
            case R.id.av_gps_monitor: setMonitor(((CheckBox) v).isChecked()); break;
            case R.id.av_gps_switch: setSwitch(((CheckBox) v).isChecked()); break;
            case R.id.av_gps_ontop: setOntop(((CheckBox) v).isChecked()); break;
            case R.id.bnRefresh: refresh(); break;
        }
    }

    private void refresh() {
        cbPackageEnable.setChecked("false".equals(am.getParameters("cfg_gps_altmix=")));
        cbOnTopEnable.setChecked("true".equals(am.getParameters("cfg_gps_ontop=")));
        cbMonitor.setChecked("on".equals(am.getParameters("av_gps_monitor=")));
        cbSwitch.setChecked("on".equals(am.getParameters("av_gps_switch=")));
        cbOnTop.setChecked("true".equals(am.getParameters("av_gps_ontop=")));
    }

    private void setOntop(boolean value) {
        am.setParameters("av_gps_ontop=" + String.valueOf(value));
    }

    private void setSwitch(boolean value) {
        am.setParameters("av_gps_switch=" + (value ? "on" : "off"));
    }

    private void setMonitor(boolean value) {
        am.setParameters("av_gps_monitor=" + (value ? "on" : "off"));
    }

    private void setOntopEnable(boolean value) {
        am.setParameters("cfg_gps_ontop=" + String.valueOf(value));
        am.setParameters("av_gps_ontop=" + String.valueOf(cbOnTop.isChecked()));
    }

    public void setPackageEnable(boolean value) {
        am.setParameters("cfg_gps_altmix=" + String.valueOf(!value));
        am.setParameters("av_gps_package=" + android.provider.Settings.System.getString(cr, "gpspkname"));
    }
}
