package com.sherry.galleyindicator;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.sherry.galleyindicator.adapter.PicAdapter;
import com.sherry.galleyindicator.util.DisplayUtil;
import com.sherry.galleyindicator.view.CircleFlowIndicator;
import com.sherry.galleyindicator.view.PicGallery;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private PicGallery vPicGallery;
    private CircleFlowIndicator vIndicator;


    private final int SLEEP_TIME_CHECK = 1000;
    private final int SLEEP_TIME_CHANGE = 4000;
    private final int INTERVAL = 500;
    private final int AUTO_FRONT = 1;
    private final int AUTO_BACK = 2;
    private int[] mGalleys = new int[]{
            R.drawable.gallery1, R.drawable.galley2, R.drawable.galley3
    };

    private int mWidth;
    private int mDistance;
    private PicAdapter mPicAdapter;
    
    private Handler mGalleryHandler;
    private MarqueeThread mScrollControlThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGallery();
        mWidth = DisplayUtil.getScreenWidth(this);

        RelativeLayout rlImage = (RelativeLayout) findViewById(R.id.rl_image);
        RelativeLayout.LayoutParams rlps = (RelativeLayout.LayoutParams) rlImage.getLayoutParams();
        // 轮换图片的高度
        rlps.width = mWidth;
        rlps.height = mWidth / 2;
        rlImage.setLayoutParams(rlps);

        vPicGallery = (PicGallery) findViewById(R.id.picgallery_main);
        mPicAdapter = new PicAdapter(this, mGalleys);
        vPicGallery.setAdapter(mPicAdapter);
        vIndicator = (CircleFlowIndicator) findViewById(R.id.indicator_main);
        vPicGallery.setFlowIndicator(vIndicator);
        if (mScrollControlThread == null || !mScrollControlThread.THREADAUTOPIC) {
            Log.d(TAG, "setGalleryAutoPlay");
            setGalleryAutoPlay();
        }
    }

    /**
     * 初始化轮换
     */
    private void initGallery() {
        mDistance = AUTO_FRONT;
        mGalleryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    int flingDistance = mWidth;
                    if (msg.arg1 == AUTO_FRONT) {
                        flingDistance = -mWidth;
                    }
                    if (msg.arg1 == AUTO_BACK) {
                        flingDistance = mWidth;
                    }
                    vPicGallery.fling(flingDistance);// 滚动距离
                }
            }
        };
    }

    /**
     * 开始自动滚动
     */
    private void setGalleryAutoPlay() {
        if (mGalleys.length <= 1) {
            return;
        }
        Log.d(TAG, "setGalleryAutoPlay  start");
        vPicGallery.setAnimationDuration(INTERVAL);
        mScrollControlThread = new MarqueeThread();
        mScrollControlThread.start();
    }

    /**
     * 停止轮换
     */
    private void stopAutoThread() {
        if (mScrollControlThread != null) {
            Log.d(TAG, "stopAutoThread");
            mScrollControlThread.THREADAUTOPIC = false;
            mScrollControlThread = null;
        }
    }

    /**
     * 轮播的线程
     *
     * @author sherry
     */
    class MarqueeThread extends Thread {
        public boolean THREADAUTOPIC = true;

        public MarqueeThread() {
            vPicGallery.setFingerUpTime();
        }

        @Override
        public void run() {
            while (THREADAUTOPIC) {
                if (!vPicGallery.issCrolling()
                        && Math.abs(System.currentTimeMillis() - vPicGallery.getFingerUpTime()) >= SLEEP_TIME_CHANGE) {
                    if (vPicGallery.issCrolling() && !THREADAUTOPIC) {
                        continue;
                    }
                    int count = vPicGallery.getCount();
                    if (vPicGallery.getSelectedItemPosition() == 0) {
                        mDistance = AUTO_FRONT;
                    }
                    if (vPicGallery.getSelectedItemPosition() == (count - 1)) {
                        mDistance = AUTO_BACK;
                    }
                    Message msg = mGalleryHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = mDistance;
                    mGalleryHandler.sendMessage(msg);
                    try {
                        Thread.sleep(SLEEP_TIME_CHECK);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e + "");
                    }
                } else {
                    try {
                        Thread.sleep(SLEEP_TIME_CHECK);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e + "");
                    }
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mScrollControlThread == null || !mScrollControlThread.THREADAUTOPIC) {
            setGalleryAutoPlay();
        }
    }


}
