package com.dc.tax;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * 功能：
 * 1. 五险一金、个税计算
 * 2. 不同年份税收比较曲线
 * 3. 拖到查看当前收入
 * 4. 不同省份视图
 * 5. 不同类型视图：社保视图、公积金视图、税后收入视图
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "Tax";

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    Paint strokePaint;
    Paint fillPaint;

    private int maxX = 20000;
    private int maxY = 16000;

    private int canvasWidth;
    private int canvasHeight;

    private TextView mMoneyText;

    GestureDetector mGestureDetector;
    private float mCurrentMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        mMoneyText = findViewById(R.id.money);

        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mGestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {

                float x = motionEvent.getX();
                mMoneyText.setText(String.valueOf(x));

                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });

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

    private float mCurrentX = 0.0f;
    private float mLastX = 0.0f;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float delta = x - mLastX;

            if(mCurrentX == 0){
                mCurrentX = 0.1f * canvasWidth;
            }else{
                mCurrentX += delta;
                if(mCurrentX <= 0.1f * canvasWidth){
                    mCurrentX = 0.1f * canvasWidth;
                }
            }
            mLastX = x;
            mCurrentMoney = (mCurrentX - canvasWidth * 0.1f) / canvasWidth * maxX;
            mMoneyText.setText(String.format("%.2f", mCurrentMoney));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                || motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mLastX = x;
        }

        draw(canvasWidth, canvasHeight);

        mGestureDetector.onTouchEvent(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        // draw axes
        strokePaint.setColor(Color.BLUE);
        canvas.drawLine(0, 0.9f * height, width, 0.9f * height, strokePaint); // horizon
        canvas.drawLine(0.1f * width, 0, 0.1f * width, height, strokePaint);  // vertical

        // draw axes title
        float textSize = 30;
        strokePaint.setTextSize(textSize);
        canvas.drawText("0", 0.1f * width - textSize, 0.9f * height + textSize, strokePaint);
        canvas.drawText(String.format("x: %d", maxX), width - 4 * textSize, 0.9f * height + textSize, strokePaint);
        canvas.drawText(String.format("y: %d", maxY), 0.1f * width - 2 * textSize, textSize, strokePaint);

        // TODO draw grid

        float W = 0.9f * width;
        float H = 0.9f * height;

        float px_last = width - W;
        float py_last = H;
        // draw curve
        for (int i = 0; i < maxX; i += 100) {
            float money_x = i;
            float money_y = calcTax(money_x);
            Log.i(TAG, "draw: x=" + money_x + ", y=" + money_y);

            float px = 0.1f * width + money_x / maxX * W;
            float py = H - money_y / maxY * H;
            strokePaint.setColor(Color.BLACK);
            canvas.drawLine(px_last, py_last, px, py, strokePaint);
            px_last = px;
            py_last = py;
        }

        drawMark(canvas);

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawMark(Canvas canvas) {
        // draw mark
        float money_y = calcTax(mCurrentMoney);
        float px = 0.1f * canvasWidth + mCurrentMoney / maxX * (0.9f * canvasWidth);
        float py = 0.9f * canvasHeight - money_y / maxY * (0.9f * canvasHeight);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));
        canvas.drawLine(0.1f * canvasWidth, py, canvasWidth, py, strokePaint);
        canvas.drawLine(px, 0, px, 0.9f * canvasHeight, strokePaint);

        fillPaint.setColor(Color.RED);
        canvas.drawCircle(px, py, 10, fillPaint);
        strokePaint.setPathEffect(null);

        int textSize = 30;
        strokePaint.setTextSize(30);
        canvas.drawText(String.format("%.2f", mCurrentMoney), px - (float) textSize / 2, 0.9f * canvasHeight + textSize, strokePaint);
        canvas.drawText(String.format("%.2f", money_y), px + 10, py + textSize, strokePaint);
    }

    private float calcTax(float money_x) {
        float ratio_1 = 0.07f;
        float ratio_2 = 0.02f;
        float ratio_3 = 0.005f;
        float ratio_4 = 0.08f;
        float ratio_sum = (ratio_1 + ratio_2 + ratio_3 + ratio_4);
        float money_4jin = money_x * (1 - ratio_sum);
        float no_tax = 3500f;
        if (money_4jin < no_tax) {
            return money_x;
        }

        float money_tax_base = money_4jin - no_tax;

//        if (money_x > 000) {
//            Log.i(TAG, String.format("%.2f: ratio_4jin=%.2f, money_4jin_after=%.2f, ratio=%.2f base=%.2f, susuan=%.2f, tax=%.2f, after=%.2f",
//                    money_x,
//                    ratio_sum,
//                    money_4jin,
//                    ratio(money_tax_base),
//                    money_tax_base,
//                    susuan(money_tax_base),
//                    (money_tax_base * ratio(money_tax_base) - susuan(money_tax_base)),
//                    money_4jin - (money_tax_base * ratio(money_tax_base) - susuan(money_tax_base))));
//        }


        return money_4jin - (money_tax_base * ratio(money_tax_base) - susuan(money_tax_base));
    }

    private float susuan(float x) {
        for (TaxRato rato : TaxRato.values()) {
            if (x > rato.min && x < rato.max) {
                return rato.kouchu;
            }
        }

        return 0;
    }

    private float ratio(float x) {
        for (TaxRato rato : TaxRato.values()) {
            if (x > rato.min && x < rato.max) {
                return rato.ratio;
            }
        }

        return 0.0f;
    }

    private enum TaxRato {
        L1(0f, 1500f, 0.03f, 0f),
        L2(1500f, 4500f, 0.10f, 105f),
        L3(4500f, 9000f, 0.20f, 555f),
        L4(9000f, 35000f, 0.25f, 1005f),
        R5(35000f, 55000f, 0.30f, 2755f),
        R6(55000f, 80000f, 0.35f, 5505f),
        R7(80000f, Float.MAX_VALUE, 0.40f, 13505f);

        public float min;
        public float max;
        public float ratio;
        public float kouchu;

        TaxRato(float min, float max, float ratio, float kouchu) {
            this.min = min;
            this.max = max;
            this.ratio = ratio;
            this.kouchu = kouchu;
        }
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
}
