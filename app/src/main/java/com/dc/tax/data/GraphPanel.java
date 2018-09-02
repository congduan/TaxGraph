package com.dc.tax.data;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.dc.tax.TaxCalculator;

import java.util.ArrayList;
import java.util.List;

public class GraphPanel implements Callback {

    private static final String TAG = GraphPanel.class.getSimpleName();

    private static final int UNIT_XIFU = 100; // 吸附单位

    public interface InfoUpdateListener {
        void onUpdate(String x);

        void onUpdateCurrentValue();
    }

    private TaxCalculator mTaxCalculator;

    private SurfaceHolder mSurfaceHolder;
    private int canvasWidth;
    private int canvasHeight;

    Paint strokePaint;
    Paint fillPaint;

    private int minXmoney = 1000;
    private int maxXmoney = 20000;
    private int minYmoney = 0;
    private int maxYmoney = 16000;

    private float mCurrentX = 0.0f;
    private float mLastX = 0.0f;

    private float mCurrentMoney = 0.0f;

    // 所有的数据
    private List<PointF> pointFList = new ArrayList<PointF>();

    private InfoUpdateListener mInfoUpdateListener;

    public GraphPanel(SurfaceHolder surfaceHolder, TaxCalculator calculator) {
        this.mSurfaceHolder = surfaceHolder;
        this.mTaxCalculator = calculator;
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

    /**
     * 屏幕滑动
     *
     * @param motionEvent
     */
    public void onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float delta = x - mLastX;

            if (mCurrentX == 0) {
                mCurrentX = 0.1f * canvasWidth;
            } else {
                mCurrentX += delta;
                if (mCurrentX <= 0.1f * canvasWidth) {
                    mCurrentX = 0.1f * canvasWidth;
                }
            }
            mLastX = x;

            // update current value
            mCurrentMoney = (mCurrentX - canvasWidth * 0.1f) / canvasWidth * (maxXmoney - minXmoney) + minXmoney;
            // mCurrentMoney = Math.round(mCurrentMoney); // 吸附整数
            // 吸附100的倍数
            if (mCurrentMoney % UNIT_XIFU != 0) {
                mCurrentMoney = mCurrentMoney - mCurrentMoney % UNIT_XIFU;
            }

            // callback
            if (mInfoUpdateListener != null) {
                mInfoUpdateListener.onUpdate(mTaxCalculator.toString());
                mInfoUpdateListener.onUpdateCurrentValue();
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                || motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mLastX = x;
        }

        draw(canvasWidth, canvasHeight);
    }

    public void setOnInfoUpdateListener(InfoUpdateListener listener) {
        mInfoUpdateListener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;

        pointFList.clear();

        float W = 0.9f * width;
        float H = 0.9f * height;

        for (int i = minXmoney; i < maxXmoney; i += 100) {
            float money_x = i;
            float money_y = mTaxCalculator.calcTax(money_x);

            float px = 0.1f * width + (money_x - minXmoney) / (maxXmoney - minXmoney) * W;
            float py = H - money_y / maxYmoney * H;

            pointFList.add(new PointF(px, py));
        }

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

        // draw axes
        strokePaint.setColor(Color.BLACK);
        float axesPadding = 30;
        canvas.drawLine(axesPadding, 0.9f * height, width, 0.9f * height, strokePaint); // horizon
        canvas.drawLine(0.1f * width, 0, 0.1f * width, height - axesPadding, strokePaint);  // vertical

        // draw x axes
        for (int moneyx = minXmoney; moneyx < maxXmoney; moneyx += 1000) {
            float px = convert2CanvasX(moneyx);
            if (moneyx % 1000 == 0 && moneyx % 5000 != 0) {
                canvas.drawLine(
                        px, 0.9f * canvasHeight,
                        px, 0.9f * canvasHeight - 10,
                        strokePaint);
            } else if (moneyx % 5000 == 0) {
                canvas.drawLine(
                        px, 0.9f * canvasHeight,
                        px, 0.9f * canvasHeight - 30,
                        strokePaint);

                String text = String.format("%d", moneyx);
                Rect bound = new Rect();
                strokePaint.setTextSize(30);
                strokePaint.getTextBounds(text, 0, text.length(), bound);
                canvas.drawText(text,
                        px - bound.width() / 2, 0.9f * canvasHeight + bound.height() + 10,
                        strokePaint);
            }
        }

        // draw y axes
        for (int moneyy = minYmoney; moneyy < maxYmoney; moneyy += 1000) {
            float py = convert2CanvasY(moneyy);
            if (moneyy % 1000 == 0 && moneyy % 5000 != 0) {
                canvas.drawLine(
                        0.1f * canvasWidth, py,
                        0.1f * canvasWidth + 10, py,
                        strokePaint);
            } else if (moneyy % 5000 == 0) {
                canvas.drawLine(
                        0.1f * canvasWidth, py,
                        0.1f * canvasWidth + 30, py,
                        strokePaint);

                String text = String.format("%d", moneyy);
                Rect bound = new Rect();
                strokePaint.setTextSize(30);
                strokePaint.getTextBounds(text, 0, text.length(), bound);
                canvas.drawText(text,
                        0.1f * canvasWidth - bound.width() - 10, py + bound.height() / 2,
                        strokePaint);
            }
        }

        // draw axes title
        float textSize = 30;

        String text = String.format("%d", minXmoney);
        Rect bound = new Rect();
        strokePaint.setTextSize(textSize);
        strokePaint.getTextBounds(text, 0, text.length(), bound);
        canvas.drawText(text, 0.1f * width + 10, 0.9f * height + bound.height() + 10, strokePaint);
        canvas.drawText(String.format("x"), width - 4 * textSize, 0.9f * height + textSize, strokePaint);
        canvas.drawText(String.format("y"), 0.1f * width - 2 * textSize, textSize, strokePaint);

        // TODO draw grid

        float W = 0.9f * width;
        float H = 0.9f * height;

        float px_last = pointFList.get(0).x;
        float py_last = pointFList.get(0).y;
        // draw curve
        for (int i = 1; i < pointFList.size(); i++) {
            float px = pointFList.get(i).x;
            float py = pointFList.get(i).y;
            strokePaint.setColor(Color.BLACK);
            canvas.drawLine(px_last, py_last, px, py, strokePaint);
            px_last = px;
            py_last = py;
        }

        float money_y = mTaxCalculator.calcTax(mCurrentMoney);

        // draw current mark
        float px = 0.1f * canvasWidth + (mCurrentMoney - minXmoney) / (maxXmoney - minXmoney) * (0.9f * canvasWidth);
        float py = 0.9f * canvasHeight - money_y / maxYmoney * (0.9f * canvasHeight);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));
        canvas.drawLine(0.1f * canvasWidth, py, px, py, strokePaint);
        canvas.drawLine(px, py, px, 0.9f * canvasHeight, strokePaint);

        fillPaint.setColor(Color.RED);
        canvas.drawCircle(px, py, 10, fillPaint);
        strokePaint.setPathEffect(null);

        text = String.format("%.0f", mCurrentMoney);
        bound = new Rect();
        strokePaint.getTextBounds(text, 0, text.length(), bound);
        if (convert2CanvasX(mCurrentMoney) + bound.width() > canvasWidth) {
            canvas.drawText(text, convert2CanvasX(mCurrentMoney) - bound.width() - 10, 0.9f * height - bound.height() - 10, strokePaint);
        } else {
            canvas.drawText(text, convert2CanvasX(mCurrentMoney), 0.9f * height - bound.height() - 10, strokePaint);
        }
        canvas.drawText(String.format("%.2f", money_y), px - bound.width() - 10, py - bound.height(), strokePaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private PointF convert2CanvasPoint(PointF pointF) {
        return new PointF(convert2CanvasX(pointF.x), convert2CanvasY(pointF.y));
    }

    private float convert2CanvasX(float value) {
        return 0.1f * canvasWidth + (value - minXmoney) / (maxXmoney - minXmoney) * (0.9f * canvasWidth);
    }

    private float convert2CanvasY(float value) {
        return 0.9f * canvasHeight - (value - minYmoney) / (maxYmoney - minYmoney) * (0.9f * canvasHeight);
    }

}
