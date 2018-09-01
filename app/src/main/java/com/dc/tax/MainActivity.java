package com.dc.tax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.TextView;

import com.dc.tax.data.GraphPanel;

/**
 * 功能：
 * 1. 五险一金、个税计算
 * 2. 不同年份税收比较曲线
 * 3. 拖到查看当前收入
 * 4. 不同省份视图
 * 5. 不同类型视图：社保视图、公积金视图、税后收入视图
 * 6. 饼图
 * 7. 整数自动吸附
 */
public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    GraphPanel mSurfaceCallback;

    private TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        mInfoText = findViewById(R.id.info);

        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceCallback = new GraphPanel(mSurfaceHolder);
        mSurfaceCallback.setOnInfoUpdateListener(new GraphPanel.InfoUpdateListener() {
            @Override
            public void onUpdate(String info) {
                mInfoText.setText(info);
            }
        });
        mSurfaceCallback.init();
        mSurfaceHolder.addCallback(mSurfaceCallback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mSurfaceCallback.onTouchEvent(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




}
