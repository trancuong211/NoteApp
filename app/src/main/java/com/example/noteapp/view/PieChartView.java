package com.example.noteapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class PieChartView extends View {

    private Paint paint;
    private RectF rectF;
    private List<Float> values = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private float strokeWidth = 40f;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        rectF = new RectF();
    }

    public void setData(List<Float> values, List<Integer> colors) {
        this.values = values;
        this.colors = colors;
        invalidate();
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
        paint.setStrokeWidth(width);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || values.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float diameter = Math.min(width, height) - strokeWidth;
        float cx = width / 2f;
        float cy = height / 2f;

        rectF.set(cx - diameter / 2f, cy - diameter / 2f, cx + diameter / 2f, cy + diameter / 2f);

        paint.setStrokeWidth(strokeWidth);

        float total = 0;
        for (float value : values) {
            total += value;
        }

        if (total == 0) return;

        float startAngle = -90f;
        for (int i = 0; i < values.size(); i++) {
            float sweepAngle = (values.get(i) / total) * 360f;
            paint.setColor(colors.get(i));
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
            startAngle += sweepAngle;
        }
    }
}
