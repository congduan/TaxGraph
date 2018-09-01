package com.dc.tax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.dc.tax.data.SurfaceCallback;

/**
 * 功能：
 * 1. 五险一金、个税计算
 * 2. 不同年份税收比较曲线
 * 3. 拖到查看当前收入
 * 4. 不同省份视图
 * 5. 不同类型视图：社保视图、公积金视图、税后收入视图
 */
public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "Tax";

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    SurfaceCallback mSurfaceCallback;

    private TextView mMoneyText;

    GestureDetector mGestureDetector;

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
        mSurfaceCallback = new SurfaceCallback(mSurfaceHolder);
        mSurfaceCallback.setOnInfoUpdateListener(new SurfaceCallback.InfoUpdateListener() {
            @Override
            public void onUpdate(float currentMoney) {
                mMoneyText.setText(String.format("%.2f", currentMoney));
            }
        });
        mSurfaceCallback.init();
        mSurfaceHolder.addCallback(mSurfaceCallback);

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

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mSurfaceCallback.onTouchEvent(motionEvent);
        mGestureDetector.onTouchEvent(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




}
