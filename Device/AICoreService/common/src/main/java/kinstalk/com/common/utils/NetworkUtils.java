/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by Knight.Xu on 2018/1/15.
 */

public class NetworkUtils {

    public static final int getNetworkState(Context c) {
        int result = QAIConstants.NETWORK_NONE;
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null) {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI) result = QAIConstants.NETWORK_WIFI;
            else if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
                result = QAIConstants.NETWORK_MOBILE;
        }
        return result;
    }
}
