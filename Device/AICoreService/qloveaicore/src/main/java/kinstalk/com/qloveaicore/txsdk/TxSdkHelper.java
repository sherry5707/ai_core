/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.txsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

//import com.tencent.aiaudio.TXSdkWrapper;
//import com.tencent.aiaudio.utils.UIUtils;
//import com.tencent.device.TXCommonDef;
//import com.tencent.device.TXDeviceBaseManager;

import java.util.Timer;
import java.util.TimerTask;

//import kinstalk.com.api.Api;
//import kinstalk.com.api.response.PidPubKeyLicence;
//import kinstalk.com.base.BaseApplication;
import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.qloveaicore.api.Api;
import kinstalk.com.qloveaicore.api.response.PidPubKeyLicence;

/**
 * Created by Knight.Xu on 2017/12/28.
 */

public class TxSdkHelper {
    private static final String TAG = "AI-TxSdkHelper";

    private final Object initTxSdkLock = new Object();
    public static volatile boolean isTxInited = false;

    private Timer mReqLicenceTimer;
    private static final int REQ_LICENCE_PERIOD = 1000 * 20;
    private TXOpenSdkWrapper.InitListener mInitListener;
    private Context mAppContext = null;

    private static TxSdkHelper sInstance;
    public static TxSdkHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance =  new TxSdkHelper(context);
        }
        return sInstance;
    }

    private TxSdkHelper(Context context) {
        if (QAIConfig.ENABLE_RECONNECT_ON_PRIVACY_KEY_PRESS) {
            registerPrivacyReceiver();
        }
        mAppContext = context;
    }

    public void setInitListener(TXOpenSdkWrapper.InitListener listener) {
        mInitListener = listener;
    }

    public void startInitTimer(final Context context) {
        mReqLicenceTimer = new Timer();
        mReqLicenceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                QAILog.d(TAG, "mReqLicenceTimer: run");
                initTxSdk(context);
            }
        }, 0 , REQ_LICENCE_PERIOD);
    }

    public void tryInitTxSdk(final Context context) {
        synchronized (initTxSdkLock) {
            QAILog.d(TAG, "tryInitTxSdk: enter");
            if (!isTxInited) {
                /*QAIThreadManager.getInstance().start(new Runnable() {
                    @Override
                    public void run() {
                        QAILog.d(TAG, "tryInitTxSdk: run");
                        initTxSdk(context);
                    }
                });*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QAILog.d(TAG, "tryInitTxSdk: run");
                        initTxSdk(context);
                    }
                }).start();
            }
        }
    }
    /**
     * TODO 目前的实现是每次app重启会从server端拉取pid，public key， license
     * 以后需要考虑在本地加密存储起来，加快tx sdk初始化的进程，等返回license相关的错误之后再申请新的license
     * {@link TXCommonDef.ErrorCode#ERROR_INVALID_LICENSE } {@link TXCommonDef.ErrorCode#ERROR_INVALID_SERVER_PUB_KEY }
     */
    private synchronized void initTxSdk(Context context) {
        synchronized (initTxSdkLock) {
            if (!isTxInited) {
                QAILog.d(TAG, "initTxSdk: Enter");
                try {
                    if (QAIUtils.isNetworkAvailable(context)) {
                        TXOpenSdkWrapper txSdk = TXOpenSdkWrapper.getInstance(context);
                        // 准备sn
                        txSdk.getSn();

                        if (QAIConfig.GET_LICENCE_FROM_SRV) {
                            //从服务器获取licence, 先拿到licence再调用txSdk.onCreate();
                            PidPubKeyLicence pidPubKeyLicence = Api.reqPidPubKeyLicence(TXOpenSdkWrapper.sn, QAIConfig.qLoveProductVersionNum);
                            if (pidPubKeyLicence != null) {
                                PidPubKeyLicence.DataBean dataBean = pidPubKeyLicence.getData();
                                if (dataBean != null) {
                                    TXOpenSdkWrapper.pid = Long.parseLong(dataBean.getPid());
                                    TXOpenSdkWrapper.srvPubKey = dataBean.getPub();
                                    TXOpenSdkWrapper.licence = dataBean.getLicense();
                                    QAILog.v(TAG, "get pid:" + TXOpenSdkWrapper.pid
                                            + " pub: " + TXOpenSdkWrapper.srvPubKey
                                            + " srv licence = " + TXOpenSdkWrapper.licence);
                                    if (TXOpenSdkWrapper.pid > 0 && !TextUtils.isEmpty(TXOpenSdkWrapper.srvPubKey) && !TextUtils.isEmpty(TXOpenSdkWrapper.licence)) {
                                        txSdk.onCreate();
                                        if (mInitListener != null) {
                                            mInitListener.initComplete();
                                        }

//                                        QAILog.v(TAG, "server getQRCodeUrl = " + TXDeviceBaseManager.getQRCodeUrl());
                                        isTxInited = true;
                                        if (mReqLicenceTimer != null) {
                                            mReqLicenceTimer.cancel();
                                        }
                                    }
                                }
                            }
                        } else {
                            //从本地获取licence, 先拿到licence再调用txSdk.onCreate();
                            // txSdk.getLicenseLocally();
                            txSdk.onCreate();
                            if (mInitListener != null) {
                                mInitListener.initComplete();
                            }
                            if (!TextUtils.isEmpty(TXOpenSdkWrapper.licence)) {
                                //QAILog.v(TAG, "local getQRCodeUrl = " + TXDeviceBaseManager.getQRCodeUrl());
                                isTxInited = true;
                                if (mReqLicenceTimer != null) {
                                    mReqLicenceTimer.cancel();
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    QAILog.e(TAG, "initTxSdk: Exception: " + t.getMessage());
                }
            }
        }
    }

    private String ACTION_PRIVACY_KEY_SHORT_PRESS = "kinstalk.com.aicore.action.privacy_short_press";

    private BroadcastReceiver mPrivacyKeyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            QAILog.d(TAG, "onReceive: ", intent);
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_PRIVACY_KEY_SHORT_PRESS)) {
                QAILog.d(TAG, "onReceive: privacy pressed");
                /*if (QAIConfig.isNetworkGood) {
                    // 曾经登录过并且不在线，尝试重连
                    TXDeviceBaseManager.deviceReconnect();// 这时候登录失败，因为底层连接还没建立好
                } else {
                    ToastUtils.showShort("网络暂不可用!");
                }*/
            }
        }
    };

    private void registerPrivacyReceiver() {
        QAILog.d(TAG, "registerPrivacyReceiver: enter");
        IntentFilter intentFilter = new IntentFilter(ACTION_PRIVACY_KEY_SHORT_PRESS);
        mAppContext.registerReceiver(mPrivacyKeyReceiver, intentFilter);
    }
}
