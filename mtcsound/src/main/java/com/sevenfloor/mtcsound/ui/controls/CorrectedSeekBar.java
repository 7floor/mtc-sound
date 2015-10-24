package com.sevenfloor.mtcsound.ui.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.lang.reflect.Method;

public class CorrectedSeekBar extends SeekBar {
    public CorrectedSeekBar(Context context) { super(context); }
    public CorrectedSeekBar(Context context, AttributeSet attrs) { super(context, attrs); }
    public CorrectedSeekBar(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    protected void setProgressPatch(int progress, boolean fromUser) {
        try {
            Method method = ProgressBar.class.getDeclaredMethod("setProgress", Integer.TYPE, Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(this, progress, fromUser);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
