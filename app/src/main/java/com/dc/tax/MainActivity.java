package com.dc.tax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.TextView;

import com.dc.tax.data.GraphPanel;
import com.dc.tax.data.RoundGraphPanel;

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
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    SurfaceView mSurfaceView;
    SurfaceView mSurfaceViewRound;
    SurfaceHolder mSurfaceHolder;
    SurfaceHolder mSurfaceHolderRound;
    GraphPanel mGraphPanel;
    RoundGraphPanel mRoundGraphPanel;

    private TextView mInfoText;
    private TextView mRoundInfoText;

    private TaxCalculator mTaxCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        mTaxCalculator = new TaxCalculator();
        initView();
    }

    private void initView() {

        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mGraphPanel = new GraphPanel(mSurfaceHolder, mTaxCalculator);
        mGraphPanel.setOnInfoUpdateListener(new GraphPanel.InfoUpdateListener() {
            @Override
            public void onUpdate(String info) {
                mInfoText.setText(info);
            }

            @Override
            public void onUpdateCurrentValue() {
                mRoundGraphPanel.updateGraph();
            }
        });
        mGraphPanel.init();
        mSurfaceHolder.addCallback(mGraphPanel);

        mSurfaceViewRound = findViewById(R.id.surfaceView_round);
        mSurfaceHolderRound = mSurfaceViewRound.getHolder();
        mRoundGraphPanel = new RoundGraphPanel(mSurfaceHolderRound, mTaxCalculator);
        mRoundGraphPanel.init();
        mSurfaceHolderRound.addCallback(mRoundGraphPanel);

        mInfoText = findViewById(R.id.info);
        mInfoText.setText(mTaxCalculator.toString());

        mRoundInfoText = findViewById(R.id.round_info);
        mRoundInfoText.setText("住房公积金:\n医疗保险:\n失业保险:\n养老保险:\n税后月薪:");
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mGraphPanel.onTouchEvent(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
