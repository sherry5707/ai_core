package com.kinstalk.her.voip.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by siqing on 17/5/22.
 */

public class NetUtils {


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (cm.getActiveNetworkInfo() != null) {
                return cm.getActiveNetworkInfo().isAvailable();
            }
        }
        return false;
    }
}
