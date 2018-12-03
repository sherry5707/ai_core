package com.kinstalk.her.voip.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.TypedValue;

import com.kinstalk.her.voip.ui.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.List;

public class Utils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;


    public static final int DISABLE_EXPAND = 0x00010000;//4.2以上的整形标识
    public static final int DISABLE_EXPAND_LOW = 0x00000001;//4.2以下的整形标识
    public static final int DISABLE_NONE = 0x00000000;//取消StatusBar所有disable属性，即还原到最最原始状态

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    public static float dp2px(Context context, float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }



    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : lists){
            if(info.processName.equals(proessName)){
                LogUtils.i(info.processName);
                isRunning = true;
            }
        }

        return isRunning;
    }

    public static void unBanStatusBar(Context context) {//利用反射解除状态栏禁止下拉
        Object service = context.getSystemService("statusbar");
        try {
            LogUtils.e("unBanStatusBar");
            Class<?> statusBarManager = Class.forName
                    ("android.app.StatusBarManager");
            Method expand = statusBarManager.getDeclaredMethod("disable", int.class);
            expand.setAccessible(true);
            expand.invoke(service, DISABLE_NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setStatusBarDisable(Context context, int disable_status) {//调用statusBar的disable方法
        Object service = context.getSystemService("statusbar");
        try {
            LogUtils.e("setStatusBarDisable");
            Class<?> statusBarManager = Class.forName
                    ("android.app.StatusBarManager");
            Method expand = statusBarManager.getDeclaredMethod("disable", int.class);
            expand.setAccessible(true);
            expand.invoke(service, disable_status);
        } catch (Exception e) {
            LogUtils.e("setStatusBarDisable exception " + e.toString());
            unBanStatusBar(context);
            e.printStackTrace();
        }
    }

    public static void banStatusBar(Context context) {//禁止statusbar下拉，适配了高低版本
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion <= 16) {
            setStatusBarDisable(context, DISABLE_EXPAND_LOW);
        } else {
            setStatusBarDisable(context, DISABLE_EXPAND);
        }
    }
}