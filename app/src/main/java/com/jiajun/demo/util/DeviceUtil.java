package com.jiajun.demo.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * 与设备有关信息的工具类
 * Created by cai.jia on 2016/7/22 0022.
 */

public class DeviceUtil {


    /**
     * dp转px
     * @param context
     * @param value
     * @return
     */
    public static int dp2px(Context context, float value) {
        return (int) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, context.getResources().getDisplayMetrics()));
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
