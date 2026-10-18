package com.focustimer.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class TimerRingView extends View {

    private Paint bgPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float progress = 1.0f; // 1.0 to 0.0

    public TimerRingView(Context context) {
        super(context);
        init();
    }

    public TimerRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimerRingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        float strokeWidth = 8f * density;

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);
        bgPaint.setColor(Color.parseColor("#252528"));

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(Color.parseColor("#F2F2F2"));

        rectF = new RectF();
    }

    public void setColors(int bgColor, int progressColor) {
        bgPaint.setColor(bgColor);
        progressPaint.setColor(progressColor);
        invalidate();
    }

    public void setProgress(float p) {
        this.progress = Math.max(0.0f, Math.min(1.0f, p));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        // Use the smaller dimension so the view is always a square that fits the parent
        int size = (w > 0 && h > 0) ? Math.min(w, h) : Math.max(w, h);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float pad = (bgPaint.getStrokeWidth() / 2f) + 4f;
        float size = Math.min(w, h) - (pad * 2f);
        float left = (w - size) / 2f;
        float top = (h - size) / 2f;
        rectF.set(left, top, left + size, top + size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rectF.width() <= 0 || rectF.height() <= 0) return;

        // Draw background full ring
        canvas.drawOval(rectF, bgPaint);

        // Draw progress arc starting from top (-90 degrees)
        float sweepAngle = 360f * progress;
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint);
    }
}
