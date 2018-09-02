package com.dc.tax.data;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class RoundGraphPanel implements Callback {

    private static final String TAG = RoundGraphPanel.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;
    private int canvasWidth;
    private int canvasHeight;

    Paint strokePaint;
    Paint fillPaint;

    private int maxX = 20000;
    private int maxY = 16000;

    private float mCurrentX = 0.0f;
    private float mLastX = 0.0f;

    private float mCurrentMoney;
    private String mInfo = "";

    public RoundGraphPanel(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
    }

    public void init() {
        strokePaint = new Paint();
        strokePaint.setColor(Color.RED);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(3);
        strokePaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint();
        fillPaint.setColor(Color.WHITE);
        fillPaint.setAntiAlias(true);
        fillPaint.setStrokeWidth(3);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        draw(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void draw(int width, int height) {

        if (mSurfaceHolder == null) {
            return;
        }

        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }

        if (strokePaint == null || fillPaint == null) {
            return;
        }

        // draw background
        canvas.drawColor(Color.WHITE);
        strokePaint.setColor(Color.RED);
        strokePaint.setPathEffect(null);
        canvas.drawRect(0, 0, width, height, strokePaint);



        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
}
