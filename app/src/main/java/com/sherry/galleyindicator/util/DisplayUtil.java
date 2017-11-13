package com.sherry.galleyindicator.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by shanxs on 2017/4/26.
 */

public class DisplayUtil {

    private static final String TAG = "DisplayUtil";

    /**
     * 获取分辨率
     *
     * @param context
     */
    public static int getScreenWidth(Context context) {
        int reWidth = 0;
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager window = (WindowManager) context.getSystemService("window");
            window.getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            int temp;
            if (width > height) {
                temp = width;
                width = height;
                height = temp;
            }
            reWidth = width;
        } catch (Exception e2) {
            Log.e(TAG, e2 + "");
        }
        return reWidth;
    }

    /**
     * 获取分辨率
     *
     * @param context
     */
    public static int getScreenHeight(Context context) {
        int reHeight = 0;
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager window = (WindowManager) context.getSystemService("window");
            window.getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            int temp;
            if (width > height) {
                temp = width;
                width = height;
                height = temp;
            }
            reHeight = height;
        } catch (Exception e2) {
            Log.e(TAG, e2 + "");
        }
        return reHeight;
    }

    /**
     * dip转换为px
     *
     * @param context
     *            上下文对象
     * @param dipValue
     *            dip值
     * @return px值
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转换为dip
     *
     * @param context
     *            上下文对象
     * @param pxValue
     *            px值
     * @return dip值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
