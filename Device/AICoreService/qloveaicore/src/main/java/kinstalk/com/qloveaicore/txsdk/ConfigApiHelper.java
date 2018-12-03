/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.txsdk;

import android.content.Context;
import android.text.TextUtils;

//import com.tencent.aiaudio.TXSdkWrapper;
//import com.tencent.aiaudio.utils.UIUtils;

import java.util.Timer;
import java.util.TimerTask;

//import kinstalk.com.api.Api;
//import kinstalk.com.api.ConfigApi;
//import kinstalk.com.common.AIConstants;
import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.qloveaicore.api.Api;
//import kinstalk.com.thread.QAIThreadManager;
//import kinstalk.com.utils.QAILog;

/**
 * Created by Knight.Xu on 2017/12/29.
 */

public class ConfigApiHelper {

    private static final String TAG = "AI-ConfigApiHelper";

    private final Object fetchConfigLock = new Object();
    private static volatile boolean isConfigFetched = false;

    private Timer mFetchConfigTimer;
    private static final int FETCH_CONFIG_PERIOD = 1000 * 20;

    private static ConfigApiHelper sInstance;
    public static ConfigApiHelper getInstance() {
        if (sInstance == null) {
            sInstance =  new ConfigApiHelper();
        }
        return sInstance;
    }

    private ConfigApiHelper() {
    }

    public void startReqConfigTimer(final Context context) {
        if (QAIConfig.ENABLE_CONFIG_API){
            if (mFetchConfigTimer == null) {
                mFetchConfigTimer = new Timer();
                mFetchConfigTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        QAILog.d(TAG, "mFetchConfigTimer: run");
                        fetchConfigList(context, QAIConstants.CONFIG_ACTION_ALL);
                    }
                }, 0, FETCH_CONFIG_PERIOD);
            }
        }
    }

    public void changeConfig(final Context context, final String action) {
        if (QAIConfig.ENABLE_CONFIG_API) {
            synchronized (fetchConfigLock) {
                QAILog.d(TAG, "newFetchConfig: enter");
                isConfigFetched = false;
                /*QAIThreadManager.getInstance().start(new Runnable() {
                    @Override
                    public void run() {
                        QAILog.d(TAG, "newFetchConfig: run");
                        fetchConfigList(context, action);
                    }
                });*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QAILog.d(TAG, "newFetchConfig: run");
                        fetchConfigList(context, action);
                    }
                }).start();
            }
        }
    }

    public void tryFetchConfig(final Context context, boolean isForceFetch) {
        if (QAIConfig.ENABLE_CONFIG_API){
            synchronized (fetchConfigLock) {
                QAILog.d(TAG, "tryFetchConfig: enter");
                if (isForceFetch) {
                    QAILog.d(TAG, "force Fetch Config: enter, reset config fetched status");
                    isConfigFetched = false;
                }

                if (!isConfigFetched) {
                    /*QAIThreadManager.getInstance().start(new Runnable() {
                        @Override
                        public void run() {
                            QAILog.d(TAG, "tryFetchConfig: run");
                            fetchConfigList(context, QAIConstants.CONFIG_ACTION_ALL);
                        }
                    });*/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            QAILog.d(TAG, "tryFetchConfig: run");
                            fetchConfigList(context, QAIConstants.CONFIG_ACTION_ALL);
                        }
                    }).start();
                }
            }
        } else {
//            ConfigApi.postToEngineListsApi();
        }

    }

    private synchronized void fetchConfigList(Context context, String action) {
        synchronized (fetchConfigLock) {
            if (!isConfigFetched) {
                QAILog.d(TAG, "fetchConfigList: Enter");
                try {
                    if (QAIUtils.isNetworkAvailable(context)) {
                        // 准备sn
                        TXOpenSdkWrapper.getInstance(context).getSn();
                        Api.reqConfigList(context, action);
                        if (!TextUtils.isEmpty(QAIConfig.txlicense_url) && !TextUtils.isEmpty(QAIConfig.accesskey_url)){
                            QAILog.d(TAG, "fetchConfigList: Fetched");
                            TxSdkHelper.getInstance(context.getApplicationContext()).tryInitTxSdk(context);
                            isConfigFetched = true;
                            if (mFetchConfigTimer != null) {
                                mFetchConfigTimer.cancel();
                                mFetchConfigTimer = null;
                            }
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    QAILog.e(TAG, "fetchConfigList: Exception: " + t.getMessage());
                }
            }
        }
    }

}
