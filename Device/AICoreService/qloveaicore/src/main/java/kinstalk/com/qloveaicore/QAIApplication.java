package kinstalk.com.qloveaicore;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.kinstalk.her.voip.manager.AVChatManager;

import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.NetworkUtils;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.common.utils.SharePreUtils;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.countly.CountlyUtils;
import kinstalk.com.qloveaicore.api.Api;
import kinstalk.com.qloveaicore.txsdk.ConfigApiHelper;
import kinstalk.com.qloveaicore.txsdk.TXOpenSdkWrapper;
import kinstalk.com.qloveaicore.txsdk.TxSdkHelper;

/**
 * Created by majorxia on 2018/3/30.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class QAIApplication extends Application {
    private static final String TAG = "AI-QAIApplication";
    private TXOpenSdkWrapper mTXOpenSdkWrapper;
    private int mNetworkType = QAIConstants.NETWORK_NONE;
    private static QAIApplication sInstance;

    public QAIApplication() {
        super();
    }

    @Override
    public void onCreate() {
        QAILog.d(TAG, "onCreate: Enter");
        super.onCreate();
        Api.applicationContext = getApplicationContext();
        sInstance = this;

        mTXOpenSdkWrapper = mTXOpenSdkWrapper.getInstance(this.getApplicationContext());
//        mTXOpenSdkWrapper.onCreate();
        SystemTool.getQLoveProductVersion(getApplicationContext());
        init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    private void init() {
        CountlyUtils.initCountly(getApplicationContext(), QAILog.isDebug(), BuildConfig.IS_RELEASE);
        SharePreUtils.init(getApplicationContext());
        ConfigApiHelper.getInstance().startReqConfigTimer(getApplicationContext());
        registerConnectivityChangeReceiver();
        initSpeechSdks();
//        QAILocationClient.getInstance().startGetLocation();

//        DbCore.init(getApplicationContext());
//        TimerManager.getInstance().createScanTimer();
    }

    private void registerConnectivityChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(mConnChangeReceiver, filter);
    }

    private void unConnectivityChangeRegisterReceivers() {
        unregisterReceiver(mConnChangeReceiver);
    }

    private final BroadcastReceiver mConnChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
                QAILog.d(TAG, "onReceive: Connectivity changed.");

                int connectionStatus = intent.getIntExtra("inetCondition", 0);
                QAILog.d(TAG, "connectionStatus =  "+ connectionStatus);
                QAIConfig.isNetworkGood  = connectionStatus > 50;

                mNetworkType = NetworkUtils.getNetworkState(QAIApplication.this);

                if (mNetworkType == QAIConstants.NETWORK_NONE) {
                    QAILog.d(TAG, "onReceive: no network.");
                    QAILog.d(TAG, "Network disconnected.");
//                    PcmBytesPlayer.getInstance().prepareAsync();
//                    PcmBytesPlayer.getInstance().play(AssetsUtil.getRing("network_disconnected.pcm"), new PcmBytesPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion() {
//                        }
//                    });
                    //no network
                } else {
                    QAILog.d(TAG, "onReceive: has network:" + mNetworkType);
                    ConfigApiHelper.getInstance().tryFetchConfig(getApplicationContext(), false);

                    if (/*QLoveSpeechStateMachine.useTXRecognizer*/true) {
                        //may need to init sdk on startup.

                        TxSdkHelper.getInstance(getApplicationContext()).tryInitTxSdk(getApplicationContext());

                        /*QAIPrompter.getInstance().tryDeviceReconnectOnNetworkConnected();*/
                    }
                    QAILog.d(TAG, "Network connected.");

//                    QAILocationClient.getInstance().startGetLocation();
//                    CookerTaskManager.instance().startCookerScan(getApplicationContext());

//                    TTSAnimController.getsInst().init(getApplicationContext());
//                    TTSAnimController.getsInst().onNetworkConnect();
                }
            } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_TIME_CHANGED)){
                QAILog.d(TAG, "onReceive: TIME_SET");
                if (QAIUtils.isNetworkAvailable(context)) {
                    QAILog.d(TAG, "onReceive: TIME_SET Network Available");
                    ConfigApiHelper.getInstance().tryFetchConfig(getApplicationContext(), false);

                    if (/*QLoveSpeechStateMachine.useTXRecognizer*/true) {
                        TxSdkHelper.getInstance(getApplicationContext()).tryInitTxSdk(getApplicationContext());
                    }

                }
            }

        }
    };

    private void initSpeechSdks() {
        QAILog.d(TAG, "initSpeechSdks: Enter");
//        QAILog.d(TAG, "initTxSdk: need? " + QLoveSpeechStateMachine.useTXRecognizer);
        if (/*QLoveSpeechStateMachine.useTXRecognizer*/true) {
            TxSdkHelper.getInstance(getApplicationContext()).setInitListener(new TXOpenSdkWrapper.InitListener() {
                @Override
                public void initComplete() {
                    QAILog.d(TAG, "initTxSdk: initComplete");
                    /*TXOpenSdkWrapper.getInstance(getApplicationContext()).initVoipParam(
                            VoipManager.getVoipParam(getApplicationContext()));
                    ConvertorHelper.getInstance().init();*/
                }
            });
            TxSdkHelper.getInstance(getApplicationContext()).startInitTimer(getApplicationContext());
        }
    }

    public static QAIApplication getInstance() {
        return sInstance;
    }

}
