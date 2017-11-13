package com.sherry.galleyindicator.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.sherry.galleyindicator.R;
import com.sherry.galleyindicator.util.DisplayUtil;
import com.sherry.galleyindicator.view.ifs.FlowIndicator;

/**
 * @author sherry
 */
public class CircleFlowIndicator extends View implements FlowIndicator, AnimationListener {
    private static final int STYLE_STROKE = 0;
    private static final int STYLE_FILL = 1;
    private static final float ONE = 1.0f;

    public AnimationListener ANIMATIONLISTENER = this;
    private int mFadeOutTime;
    private float mRadius = ONE;
    private FadeTimer mTimer;
    private Animation mAnimation;
    private PicGallery mPicGallery;
    private float mRadiusInactive = 4;
    private float mRadiusActive = 4;
    private float spacing = 4;
    private final Paint mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintBgActive = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentScroll = 0;
    private int currentPosition = 0;
    private int galleryWidth = 0;
    public AnimationListener animationListener = this;
    private boolean mSnap = false;

    /**
     * Default constructor
     *
     * @param context 上下文
     */
    public CircleFlowIndicator(Context context) {
        super(context);
        initColors(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, STYLE_FILL, STYLE_STROKE);
    }

    public CircleFlowIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Retrieve styles attributs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleFlowIndicator);

        // Gets the active circle type, defaulting to "fill"
        int activeType = a.getInt(R.styleable.CircleFlowIndicator_activeType, STYLE_FILL);

        int activeDefaultColor = 0xFFFFFFFF;

        // Get a custom active color if there is one
        int activeColor = a.getColor(R.styleable.CircleFlowIndicator_activeColor, activeDefaultColor);

        int activeBgColor = a.getColor(R.styleable.CircleFlowIndicator_activeBackColor, activeDefaultColor);

        // Gets the inactive circle type, defaulting to "stroke"
        int inactiveType = a.getInt(R.styleable.CircleFlowIndicator_inactiveType, STYLE_FILL);

        int inactiveDefaultColor = 0x44FFFFFF;
        // Get a custom inactive color if there is one
        int inactiveColor = a.getColor(R.styleable.CircleFlowIndicator_inactiveColor, inactiveDefaultColor);

        // Retrieve the radius
        mRadius = a.getDimension(R.styleable.CircleFlowIndicator_radius2, 4.0f);
        mRadiusActive = a.getDimension(R.styleable.CircleFlowIndicator_radiusActive, 4.0f);
        mRadiusInactive = a.getDimension(R.styleable.CircleFlowIndicator_radiusActive, 4.0f);

        // Retrieve the spacing
        spacing = a.getDimension(R.styleable.CircleFlowIndicator_spacing, 0.0f);
        // We want the spacing to be center-to-center
        spacing += 3 * mRadiusActive;

        mSnap = a.getBoolean(R.styleable.CircleFlowIndicator_snap2, false);

        initColors(activeColor, activeBgColor, inactiveColor, activeType, inactiveType);
    }

    private void initColors(int activeColor, int activeBgColor, int inactiveColor, int activeType, int inactiveType) {
        // Select the paint type given the type attr
        switch (inactiveType) {
            case STYLE_FILL:
                mPaintInactive.setStyle(Style.FILL);
                break;
            default:
                mPaintInactive.setStyle(Style.STROKE);
                float strokeWidth = mPaintInactive.getStrokeWidth();
                if (strokeWidth == 0.0f) {
                    // It draws in "hairline mode", which is 1 px wide.
                    strokeWidth = 1.0f / getResources().getDisplayMetrics().density;
                }
                mRadiusInactive -= strokeWidth / 2.0f;
        }
        mPaintInactive.setColor(inactiveColor);

        // Select the paint type given the type attr
        switch (activeType) {
            case STYLE_STROKE:
                mPaintActive.setStyle(Style.STROKE);

                float strokeWidth = mPaintInactive.getStrokeWidth();
                if (strokeWidth == 0.0f) {
                    // It draws in "hairline mode", which is 1 px wide.
                    strokeWidth = 1.0f / getResources().getDisplayMetrics().density;
                }
                mRadiusActive -= strokeWidth / 2.0f;
                break;
            default:
                mPaintActive.setStyle(Style.FILL);
                mPaintBgActive.setStyle(Style.FILL);
        }
        mPaintActive.setColor(activeColor);
        mPaintBgActive.setColor(activeBgColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = 0;
        if (mPicGallery != null) {
            count = mPicGallery.getViewsCount();
        }

        // this is the amount the first circle should be offset to make the
        // entire thing centered
        // float centeringOffset = 0;

        int leftPadding = getPaddingLeft();
        Resources res = getResources();
        Bitmap mBitmapoint = BitmapFactory.decodeResource(res, R.drawable.unselect_point);
        Bitmap mBitmapRect = BitmapFactory.decodeResource(res, R.drawable.select_point);
        // Draw stroked circles
        for (int iLoop = 0; iLoop < count; iLoop++) {
//            canvas.drawCircle(leftPadding + mRadius + (iLoop * spacing) + centeringOffset, getPaddingTop() + mRadius,
//                    mRadiusInactive, mPaintInactive);
            canvas.drawBitmap(mBitmapoint,leftPadding + mRadius + (iLoop * spacing) , mRadius,null);
        }
        float cx = 0;
        if (mSnap) {
            cx = currentPosition * spacing;
        } else {
            if (galleryWidth != 0) {
                // Draw the filled circle according to the current scroll
                cx = (currentScroll * spacing) / galleryWidth;
            }
        }
        int startRadius= DisplayUtil.dip2px(getContext(), 3);
        canvas.drawBitmap(mBitmapRect, leftPadding + startRadius + cx , startRadius, null);
    }

    @Override
    public void onSwitched(View view, int position) {
    }

    @Override
    public void setViewFlow(PicGallery view) {
        resetTimer();
        mPicGallery = view;
        galleryWidth = mPicGallery.getWidth();
        invalidate();
    }

    @Override
    public void onScrolled(int h, int v, int oldh, int oldv) {
        resetTimer();
        currentScroll = h;
        galleryWidth = mPicGallery.getWidth();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Calculate the width according the views count
        else {
            int count = 3;
            if (mPicGallery != null) {
                count = mPicGallery.getViewsCount();
            }
            // Remember that spacing is centre-to-centre
            result = (int) (getPaddingLeft() + getPaddingRight() + (2 * mRadius) + (count - 1) * spacing);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Measure the height
        else {
            result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Sets the fill color
     *
     * @param color ARGB value for the text
     */
    public void setFillColor(int color) {
        mPaintActive.setColor(color);
        invalidate();
    }

    /**
     * Sets the stroke color
     *
     * @param color ARGB value for the text
     */
    public void setStrokeColor(int color) {
        mPaintInactive.setColor(color);
        invalidate();
    }

    /**
     * Resets the fade out timer to 0. Creating a new one if needed
     */
    private void resetTimer() {
        // Only set the timer if we have a timeout of at least 1 millisecond
        if (mFadeOutTime > 0) {
            // Check if we need to create a new timer
            if (mTimer == null || !mTimer.mRun) {
                // Create and start a new timer
                mTimer = new FadeTimer();
                mTimer.execute();
            } else {
                // Reset the current tiemr to 0
                mTimer.resetTimer();
            }
        }
    }

    /**
     * Counts from 0 to the fade out time and animates the view away when
     * reached
     */
    private class FadeTimer extends AsyncTask<Void, Void, Void> {
        // The current count
        private int mTimerInt;
        // If we are inside the timing loop
        private boolean mRun = true;

        public void resetTimer() {
            mTimerInt = 0;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            while (mRun) {
                try {
                    // Wait for a millisecond
                    Thread.sleep(1);
                    // Increment the timer
                    mTimerInt++;

                    // Check if we've reached the fade out time
                    if (mTimerInt == mFadeOutTime) {
                        // Stop running
                        mRun = false;
                    }
                } catch (InterruptedException e) {
                    Log.e("CircleFlowIndicator", e + "");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
            mAnimation.setAnimationListener(ANIMATIONLISTENER);
            startAnimation(mAnimation);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }
}