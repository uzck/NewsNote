package com.example.nozoeli.newsnote.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Field;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nozoeli on 16-4-21.
 */
public class UtilTools {

    // 获取状态栏高度
    public static int getBarHeight(Resources res){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = res.getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    // dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // px转dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String result = format.format(date);

        return result;
    }
}
