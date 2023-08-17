package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ZigzagSeekBar extends SeekBar {

    private Paint trackPaint;
    private Paint progressPaint;
    private Paint thumbPaint;

    public ZigzagSeekBar(Context context) {
        super(context);
        init();
    }

    public ZigzagSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZigzagSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        trackPaint = new Paint();
        trackPaint.setColor(Color.LTGRAY);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(8);

        progressPaint = new Paint();
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(8);

        thumbPaint = new Paint();
        thumbPaint.setColor(Color.GREEN);
        thumbPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int thumbPos = getThumb().getBounds().centerX();

        drawZigzag(canvas, 0, height / 2, width, height / 2, trackPaint);
        drawZigzag(canvas, 0, height / 2, thumbPos, height / 2, progressPaint);
        drawThumb(canvas, thumbPos, height / 2);
    }

    private void drawZigzag(Canvas canvas, int startX, int startY, int endX, int endY, Paint paint) {
        int zigzagHeight = 20;
        boolean goingUp = true;

        for (int x = startX; x <= endX; x += 20) {
            int nextX = x + 20;
            if (nextX > endX) nextX = endX;

            int nextY = goingUp ? startY - zigzagHeight : startY + zigzagHeight;
            if (nextX == endX) nextY = startY;  // make sure it ends in a straight line

            canvas.drawLine(x, startY, nextX, nextY, paint);
            startY = nextY;
            goingUp = !goingUp;
        }
    }

    private void drawThumb(Canvas canvas, int x, int y) {
        canvas.drawCircle(x, y, 20, thumbPaint);
    }
}
