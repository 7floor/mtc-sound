package com.sevenfloor.mtcsound.ui.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sevenfloor.mtcsound.R;

public class BalanceCross extends View
{
    public interface OnBalanceChangeListener {
        void onBalanceChange(int balanceX, int balanceY, int byUser);
    }

    private OnBalanceChangeListener onBalanceChangeListener;
    private int balanceX;
    private int balanceY;
    private int max;
    private int height;
    private int width;
    private int screenpx;
    private int screenpy;
    private int thmbw;

    public BalanceCross(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        thmbw = 50;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeset,
                R.styleable.BalanceCross,
                0, 0);

        try {
            max = a.getInt(R.styleable.BalanceCross_max, 28);
            balanceX = a.getInt(R.styleable.BalanceCross_balanceX, 14);
            balanceY = a.getInt(R.styleable.BalanceCross_balanceY, 14);
            if (max < 0) max = 0;
            if (balanceX < 0) balanceX = 0;
            if (balanceX > max) balanceX = max;
            if (balanceY < 0) balanceY = 0;
            if (balanceY > max) balanceY = max;
        } finally {
            a.recycle();
        }
    }

    public void setOnBalanceChangeListener(OnBalanceChangeListener balancechange)
    {
        onBalanceChangeListener = balancechange;
    }

    public void setBalance(int balanceX, int balanceY)
    {
        this.balanceX = Math.max(Math.min(balanceX, max), 0);
        this.balanceY = Math.max(Math.min(balanceY, max), 0);

        updateThumbView();
        invalidate();
    }

    public void balanceXup() {
        if (balanceX < max) {
            balanceX = balanceX + 1;
        } else {
            balanceX = max;
        }
        updateThumbView();
        invalidate();
    }

    public void balanceXdown() {
        if (balanceX > 0) {
            balanceX = balanceX - 1;
        } else {
            balanceX = 0;
        }
        updateThumbView();
        invalidate();
    }

    public void balanceYup() {
        if (balanceY < max) {
            balanceY = balanceY + 1;
        } else {
            balanceY = max;
        }
        updateThumbView();
        invalidate();
    }

    public void balanceYdown() {
        if (balanceY > 0) {
            balanceY = balanceY - 1;
        } else {
            balanceY = 0;
        }
        updateThumbView();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint foreground = new Paint(), background = new Paint();
        foreground.setAntiAlias(true);
        foreground.setStyle(Paint.Style.STROKE);
        foreground.setStrokeWidth(2);
        foreground.setColor(0xffffffff);
        background.setAntiAlias(true);
        background.setStyle(Paint.Style.FILL);
        background.setColor(0x80000000);
        canvas.save();
        canvas.drawLine(thmbw / 2, height / 2, width - thmbw / 2, height / 2, foreground);
        canvas.drawLine(width / 2, thmbw / 2, width / 2, height - thmbw / 2, foreground);
        canvas.drawCircle(screenpx, screenpy, 20, background);
        canvas.drawCircle(screenpx, screenpy, 20, foreground);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = 100;
        height = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(width, widthSize);
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(height, heightSize);
                break;
        }

        setMeasuredDimension(width, height);

        screenpx = ((this.width - thmbw) * balanceX) / max + thmbw / 2;
        screenpy = ((this.height - thmbw) * balanceY) / max + thmbw / 2;

        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionevent)
    {
        int x = (int)motionevent.getX();
        int y = (int)motionevent.getY();
        if (x < thmbw / 2)
        {
            x = thmbw / 2;
        }
        if (x > width - thmbw / 2)
        {
            x = width - thmbw / 2;
        }
        if (y < thmbw / 2)
        {
            y = thmbw / 2;
        }
        if (y > height - thmbw / 2)
        {
            y = height - thmbw / 2;
        }
        switch(motionevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                balanceX = Math.round((float)((x - thmbw / 2) * max) / (width - thmbw));
                balanceY = Math.round((float)((y - thmbw / 2) * max) / (height - thmbw));
                setBalance(balanceX,balanceY);
                //updateThumbView(x, y);
                //invalidate();
                break;
        }
        return true;
    }

    private void updateThumbView() {
        screenpx = ((width - thmbw) * balanceX) / max + thmbw / 2;
        screenpy = ((height - thmbw) * balanceY) / max + thmbw / 2;
        if (onBalanceChangeListener != null) {
            onBalanceChangeListener.onBalanceChange(balanceX, balanceY, 0);
        }
    }

    private void updateThumbView(int xPos, int yPos) {
        balanceX = ((xPos - thmbw / 2) * max) / (width - thmbw);
        balanceY = ((yPos - thmbw / 2) * max) / (height - thmbw);
        screenpx = xPos;
        screenpy = yPos;
        if (onBalanceChangeListener != null) {
            onBalanceChangeListener.onBalanceChange(balanceX, balanceY, 1);
        }
    }

}
