package com.sherry.galleyindicator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

import com.sherry.galleyindicator.adapter.PicAdapter;
import com.sherry.galleyindicator.view.ifs.FlowIndicator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 重写gallery
 * 
 * @author sherry
 * 
 */
public class PicGallery extends Gallery {
	private static final String TAG = "PicGallery";

	private boolean mIsScroll; // 判断是否正在划动
	private Object mFlingObj;
	private Method mFlingMethod; // 反射调用划动方法
	private Adapter mAdapter; // 就Adapter
	private FlowIndicator mIndicator; // 指示器
	private int mCurrIndex; // 记录当前页
	private long mFingerUpTime; // 记录划动时间
	private ViewSwitchListener mViewSwitchListener;

	/**
	 * 构造函数
	 *
	 * @param context
	 *            上下文
	 */
	public PicGallery(Context context) {
		super(context);
		setFingerUpTime();
		this.setStaticTransformationsEnabled(true);
	}

	/**
	 * 构造函数
	 *
	 * @param context
	 *            上下文
	 * @param attrs
	 *            属性
	 */
	public PicGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFingerUpTime();
		this.setStaticTransformationsEnabled(true);

		try {
			Field fling = this.getClass().getSuperclass().getDeclaredField("mFlingRunnable");
			Class<?> cc = fling.getType();
			mFlingMethod = cc.getMethod("startUsingDistance", new Class[] { int.class });
			fling.setAccessible(true);
			mFlingObj = fling.get(this);
		} catch (Exception e) {
			Log.e(TAG, e + "");
		}
	}

	/**
	 * 构造函数
	 *
	 * @param context
	 *            上下文
	 * @param attrs
	 *            属性
	 * @param defStyle
	 *            类型
	 */
	public PicGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFingerUpTime();
		this.setStaticTransformationsEnabled(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			mIsScroll = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			setFingerUpTime();
			mIsScroll = false;
		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			setFingerUpTime();
			mIsScroll = false;
		}

		return super.onTouchEvent(event);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		super.onScroll(e1, e2, distanceX, distanceY);
		if (isScrollingLeft(e1, e2)) {
			mCurrIndex = getSelectedItemPosition();
		} else {
			mCurrIndex = getSelectedItemPosition();
		}
		callinterface(0, 0, 0, 0);
		return true;
	}

	@Override
	protected void onScrollChanged(int h, int v, int oldh, int oldv) {
		super.onScrollChanged(h, v, oldh, oldv);
		callinterface(h, v, oldh, oldv);
	}

	private void callinterface(int h, int v, int oh, int ov) {
		if (mIndicator != null) {
			/*
			 * The actual horizontal scroll origin does typically not match the
			 * perceived one. Therefore, we need to calculate the perceived
			 * horizontal scroll origin here, since we use a view buffer.
			 */
			int count = getViewsCount();
			if (count == 0) {
				return;
			}
			int hPerceived = h + (mCurrIndex % getViewsCount()) * getWidth();
			mIndicator.onScrolled(hPerceived, v, oh, ov);
		}
	}

	/**
	 * 是否正在手动滑动
	 *
	 * @return boolean
	 */
	public boolean issCrolling() {
		return mIsScroll;
	}

	/**
	 * 是否正在手动滑动
	 *
	 * @return boolean
	 */
	public long getFingerUpTime() {
		return mFingerUpTime;
	}

	/**
	 *
	 * 记录手指按下时间
	 */
	public void setFingerUpTime() {
		mFingerUpTime = System.currentTimeMillis();
	}

	/**
	 * 自动滑动
	 *
	 * @param distance
	 *            距离
	 */
	public void fling(int distance) {
		if (mCurrIndex == 0) {
			mCurrIndex = getFirstVisiblePosition();
		}

		setFingerUpTime();
		try {
			if (!mIsScroll) {
				if (distance > 0) {
					mCurrIndex--;
				} else {
					mCurrIndex++;
				}

				mFlingMethod.invoke(mFlingObj, new Object[] { distance });
				callinterface(0, 0, 0, 0);
			}
		} catch (Exception e) {
			Log.e(TAG, e + "");
		}
	}

	@Override
	public SpinnerAdapter getAdapter() {
		return super.getAdapter();
	}

	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		this.mAdapter = adapter;
		super.setAdapter(adapter);
	}

	/**
	 * 获取切换图片的大小
	 *
	 * @return 大小
	 */
	public int getViewsCount() {
		return ((PicAdapter) mAdapter).getSize();
	}

	@Override
	public void setSelection(int position) {
		Log.d(TAG, "setSelection " + position);
		if (position >= 0) {
			mCurrIndex = position;
			callinterface(0, 0, 0, 0);
			super.setSelection(position);
		}
	}

	/**
	 * 设置选中界面
	 */
	public void setSelectionOld() {
		super.setSelection(mCurrIndex);
	}

	/**
	 *
	 * 设置当前页的ID
	 *
	 * @param index
	 *            id
	 */
	public void setCurrIndex(int index) {
		mCurrIndex = index;
	}

	/**
	 *
	 * 重置当前页ID
	 */
	public void reSetIndex() {
		if (mIndicator != null) {
			// 重置指示器位置
			mIndicator.onScrolled(0, 0, 0, 0);
		}
		mCurrIndex = 0;
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		super.setOnItemSelectedListener(listener);
	}

	/**
	 * 设置接口
	 *
	 * @param flowIndicator
	 *            接口
	 */
	public void setFlowIndicator(FlowIndicator flowIndicator) {
		mIndicator = flowIndicator;
		mIndicator.setViewFlow(this);
	}

	/**
	 * 轮换图片与指示器的接口
	 *
	 * @author qiu.d
	 *
	 */
	public interface ViewSwitchListener {

		/**
		 * This method is called when a new View has been scrolled to.
		 *
		 * @param view
		 *            the {@link View} currently in focus.
		 * @param position
		 *            The position in the adapter of the {@link View} currently
		 *            in focus.
		 */
		void onSwitched(View view, int position);

	}

	/**
	 *
	 * @Method: setViewSwitchListener
	 * @Description: 判断是否为手动侦听的滑动接口
	 * @param switchListener
	 *            接口回调参数
	 * @return
	 * @throws
	 */
	public void setViewSwitchListener(ViewSwitchListener switchListener) {
		mViewSwitchListener = switchListener;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int keyCode;
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		if (isScrollingLeft(e1, e2)) {
			mCurrIndex = getFirstVisiblePosition();
		} else {
			mCurrIndex = getFirstVisiblePosition() + 1;
		}
		// 手动滑动切换图片接口
		if (mViewSwitchListener != null) {
			mViewSwitchListener.onSwitched(null, mCurrIndex);
		}
		callinterface(0, 0, 0, 0);
		onKeyDown(keyCode, null);
		return true;
	}

	/**
	 *
	 * 根据按下和松开的x坐标判断划动方向
	 *
	 * @param e1
	 * @param e2
	 */
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

}
