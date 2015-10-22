package com.sevenfloor.mtcsound.ui.controls;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class AfcChart extends View {
    private final int startFreqFactor = 1, endFreqFactor = 5;
    private final int minGain = -30, maxGain = +30, gainStep = 5, gainPadding = 10;
    private final int chartSteps = 100;
    private int w, h;
    private boolean gridOn;
    private Paint gridLine, chartLine;
    private Path chartPath;
    private float[] frequencies;

    public final FilterData bass = new FilterData();
    public final FilterData middle = new FilterData();
    public final FilterData treble = new FilterData();

    public AfcChart(Context context) {
        super(context);
        initialize();
    }

    public AfcChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public AfcChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setGrid(boolean on){
        this.gridOn = on;
        invalidate();
    }

    private void initialize() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        gridLine = new Paint();
        gridLine.setStyle(Paint.Style.STROKE);
        gridLine.setStrokeWidth(1);
        gridLine.setARGB(0x80, 255, 255, 255);

        chartLine = new Paint();
        chartLine.setStyle(Paint.Style.STROKE);
        chartLine.setStrokeWidth(2);
        chartLine.setARGB(255, 0, 255, 0);
        chartLine.setPathEffect(new CornerPathEffect(10));
        chartLine.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.SOLID));
        chartLine.setAntiAlias(true);

        chartPath = new Path();

        initFrequencies();
        bass.setF(100); bass.setQ(1);
        bass.setG(0);
        middle.setF(1000); middle.setQ(1); middle.setG(0);
        treble.setF(10000); treble.setQ(1); treble.setG(0);

        gridOn = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w-1;
        this.h = h-1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (gridOn)
            drawGrid(canvas);
        drawChart(canvas);
        canvas.restore();
    }

    private void drawGrid(Canvas canvas) {
        float y1 = h - (gainPadding) * h / (maxGain - minGain);
        float y2 = h - (maxGain - gainPadding - minGain) * h / (maxGain - minGain);
        for (int i = startFreqFactor; i <= endFreqFactor; i++) {
            for (int j = 1; j < 10; j++) {
                float x = (float) (Math.log10(Math.pow(10, (i - startFreqFactor)) * j) * (w / (endFreqFactor - startFreqFactor)) + 0.5);
                canvas.drawLine(x, y1, x, y2, gridLine);
                if (i == endFreqFactor) break;
            }
        }

        for (int i = minGain+gainPadding; i <= maxGain-gainPadding; i+=gainStep) {
            float y = h - (i - minGain) * h / (maxGain - minGain);
            canvas.drawLine(0, y, w, y, gridLine);
        }
    }

    private void drawChart(Canvas canvas) {
        chartPath.reset();
        float[] g1 = bass.getPoints(), g2 = middle.getPoints(), g3 = treble.getPoints();
        for (int i = 0; i < chartSteps; i ++) {
            float x = i * w / (chartSteps - 1);
            float g = g1[i] + g2[i] + g3[i];
            float y = h - (g - minGain) * h / (maxGain - minGain);
            if (x == 0)
                chartPath.moveTo(x, y);
            else
                chartPath.lineTo(x, y);
        }
        canvas.drawPath(chartPath, chartLine);
    }

    private void initFrequencies() {
        frequencies = new float[chartSteps];
        for (int i = 0; i < chartSteps; i++) {
            frequencies[i] = posToFreq((float) i / (chartSteps - 1));
        }
    }

    float posToFreq(float x)
    {
        float r = x * (endFreqFactor - startFreqFactor) + startFreqFactor;
        return (float) Math.pow(10, r);
    }

    public class FilterData {
        private float[] points;
        private float g = 0, q = 1, f = 1000;

        public void setF(float value) {
            f = value;
            points = null;
            invalidate();
        }

        public void setQ(float value) {
            q = value;
            points = null;
            invalidate();
        }

        public void setG(float value) {
            g = value;
            points = null;
            invalidate();
        }

        public float[] getPoints() {
            if (points == null)
            {
                points = new float[chartSteps];
                for (int i = 0; i < chartSteps; i++)
                {
                    points[i] = g(frequencies[i], f, q, g);
                }
            }
            return points;
        }

        private float g(float f, float f0, float q, float g) {
            float w = f / f0;
            return (float) (g / q * w / Math.sqrt(1 + Math.pow(w, 2) * (1 / Math.pow(q, 2) - 2) + Math.pow(w, 4)));
        }
    }
}
