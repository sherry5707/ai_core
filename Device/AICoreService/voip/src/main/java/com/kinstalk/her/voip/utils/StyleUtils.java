package com.kinstalk.her.voip.utils;

import android.content.Context;

//import kinstalk.com.utils.SystemPropertiesProxy;

/**
 * Created by siqing on 17/10/16.
 * 设备判断工具类
 */

public class StyleUtils {

    /**
     * 是否是hi哥伦比亚版本
     * @return
     */
    public static boolean isColumnbiaDevice(Context context){
        String device = "";//SystemPropertiesProxy.getString(context, "ro.boot.hwid");
        if (device.equals("3")) {
            return false;
        } else if (device.equals("5")) {
            return false;
        }
        return true;
    }
}
