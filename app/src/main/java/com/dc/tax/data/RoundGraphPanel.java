package com.dc.tax.data;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.dc.tax.TaxCalculator;

/**
 * 饼状图
 */
public class RoundGraphPanel implements Callback {

    private static final String TAG = RoundGraphPanel.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;
    private int canvasWidth;
    private int canvasHeight;

    Paint strokePaint;
    Paint fillPaint;

    private String mInfo = "";

    float mCirclePadding = 200.0f;

    private TaxCalculator mTaxCalculator;

    public RoundGraphPanel(SurfaceHolder surfaceHolder, TaxCalculator taxCalculator) {
        this.mSurfaceHolder = surfaceHolder;
        this.mTaxCalculator = taxCalculator;
    }

    public void init() {
        strokePaint = new Paint();
        strokePaint.setColor(Color.BLUE);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(3);
        strokePaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint();
        fillPaint.setColor(Color.GREEN);
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
//        canvas.drawRect(0, 0, width, height, strokePaint);

        // draw arc
        int min = Math.min(width, height);
        float radius = (min - mCirclePadding) / 2;
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        // draw circle
        strokePaint.setColor(Color.BLACK);
        canvas.drawCircle(centerX, centerY, radius, strokePaint);

        RectF rectF = new RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
        fillPaint.setColor(Color.parseColor("#AAAAAA"));
        canvas.drawArc(rectF, 0, 360, true, fillPaint);
        canvas.drawLine(centerX, centerY, centerX, centerY - radius, strokePaint);

        if (mTaxCalculator.getMoneyBeforeTax() > 0) {
            // 个人所得税
            fillPaint.setColor(Color.BLUE);
            float sweepAngle = mTaxCalculator.getMoneyTax() / mTaxCalculator.getMoneyBeforeTax() * 360;
            float startAngle = -90;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint);
            // draw arc stroke
            strokePaint.setColor(Color.BLUE);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, strokePaint);

            // 住房公积金
            startAngle += sweepAngle;
            fillPaint.setColor(Color.RED);
            sweepAngle = mTaxCalculator.getGongjijin() / mTaxCalculator.getMoneyBeforeTax() * 360;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint);
            // draw arc stroke
            strokePaint.setColor(Color.BLUE);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, strokePaint);

            // 医疗
            startAngle += sweepAngle;
            fillPaint.setColor(Color.YELLOW);
            sweepAngle = mTaxCalculator.getYiliao() / mTaxCalculator.getMoneyBeforeTax() * 360;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint);
            // draw arc stroke
            strokePaint.setColor(Color.BLUE);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, strokePaint);

            // 失业
            startAngle += sweepAngle;
            fillPaint.setColor(Color.GREEN);
            sweepAngle = mTaxCalculator.getShiye() / mTaxCalculator.getMoneyBeforeTax() * 360;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint);
            // draw arc stroke
            strokePaint.setColor(Color.BLUE);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, strokePaint);

            // 养老
            startAngle += sweepAngle;
            fillPaint.setColor(Color.CYAN);
            sweepAngle = mTaxCalculator.getYanglao() / mTaxCalculator.getMoneyBeforeTax() * 360;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint);
            // draw arc stroke
            strokePaint.setColor(Color.BLUE);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, strokePaint);
        }

        // draw label
        /*
        float left = 40;
        float top = centerY - radius;
        float right = width / 3.0f;
        float bottom = centerY + radius;
        canvas.drawRect(40, centerY + radius, width / 3.0f, centerY - radius, strokePaint);
        float textMargin = 10;
        strokePaint.setTextSize(30);
        canvas.drawText("green: ", left + textMargin, top + textMargin, strokePaint);
        */

        strokePaint.setTextSize(40);
        Rect bounds = new Rect();
        String text = "税前工资去向";
        strokePaint.setColor(Color.parseColor("#AAAAAA"));
        strokePaint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, centerX - bounds.width() / 2, centerY + radius + 50, strokePaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void updateGraph() {
        draw(canvasWidth, canvasHeight);
    }
}
