package com.sevenfloor.mtcsound.ui.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalSeekBar extends CorrectedSeekBar {

    public VerticalSeekBar(Context context) { super(context); }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0); // a bug workaround for thumb visually resetting to 0
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(),0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int realHeight = getHeight() - getPaddingLeft() - getPaddingRight();
                int realStart = getPaddingLeft();
                if (isPaddingOffsetRequired()){
                    realHeight -= (getLeftPaddingOffset() + getRightPaddingOffset());
                    realStart += (getLeftPaddingOffset());
                }
                int thumbOffsetCorrection = getThumb().getBounds().width() / 2 - getThumbOffset();
                realHeight -= thumbOffsetCorrection * 2;
                realStart += thumbOffsetCorrection;

                float touchPos = (event.getY() - realStart) / (realHeight);
                int progress = getMax() - Math.round((float)getMax() * touchPos);
                setProgressPatch(progress, true);

                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
        }
        return true;
    }

}

