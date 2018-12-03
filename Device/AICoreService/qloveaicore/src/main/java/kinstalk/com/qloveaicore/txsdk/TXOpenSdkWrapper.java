package kinstalk.com.qloveaicore.txsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kinstalk.her.voip.manager.AVChatManager;
import com.tencent.xiaowei.control.XWeiAudioFocusManager;
import com.tencent.xiaowei.control.XWeiControl;
import com.tencent.xiaowei.def.XWCommonDef;
import com.tencent.xiaowei.info.XWAccountInfo;
import com.tencent.xiaowei.info.XWBinderInfo;
import com.tencent.xiaowei.info.XWLoginInfo;
import com.tencent.xiaowei.info.XWTTSDataInfo;
import com.tencent.xiaowei.sdk.XWCoreService;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
import com.tencent.xiaowei.sdk.XWSDK;
import com.tencent.xiaowei.util.QLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import kinstalk.com.common.BusEventCommon;
import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.AssetsUtil;
import kinstalk.com.common.utils.CountlyEvents;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.common.utils.SystemPropertiesProxy;
import kinstalk.com.qloveaicore.DeviceBindStateController;
import kinstalk.com.qloveaicore.QAIConfigController;
import kinstalk.com.qloveaicore.player.XWeiPlayerMgr;
import kinstalk.com.qloveaicore.statemachine.QAISpeechStatesMachine;
import kinstalk.com.qloveaicore.tts.TTSManager;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;

/**
 * Created by majorxia on 2018/3/30.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class TXOpenSdkWrapper {
    private static final String TAG = "AI-TXOpenSdkWrapper";

    public static final String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
    public static final String ACTION_LOGIN_FAILED = "ACTION_LOGIN_FAILED";

    public static final String ACTION_ON_BINDER_LIST_CHANGE = "BinderListChange";   //绑定列表变化

    public static final String AIC2CBusiness_GetVolume = "ai.internal.xiaowei.GetVolumeMsg";    //获取当前音量
    public static final String AIC2CBusiness_SetVolume = "ai.internal.xiaowei.SetVolumeMsg";    //设置音量
    public static final String AIC2CBusiness_ReturnVolume = "ai.internal.xiaowei.Cur100VolMsg"; //cc消息返回音量，返回GetVolumeMsg

    ////////////////////////////////////////////
    protected final static String URI_DEVICE_ICON_FORMAT = "http://i.gtimg.cn/open/device_icon/%s/%s/%s/%s/%s.png";
    ////////////////////////////////////////////

    public static Handler mHandler = new Handler();

    public static boolean isLogined;// 用来标记成功登录过
    public static boolean isOnline;// 用来标记当前是否在线

    public static Context mApplication;

    public static String mStoragePath;
    public static String mReceiveFileMenuPath;
    private static Toast mToast;

    //szjy begin
    private static Context mContext;
    private static String qloveSn = "";
    public static long pid;
    public static String sn;// = "0B1814D50A7049f1";//Qlove
    public static String licence;// = "3046022100BF704492F1846D0F707F0F0F1F685191A4360AB0023FF17BA4AC7199BA16A77E022100D4A44DFEDA700EDDEBE6717EFFCEBE7A8BD9A995D36C02C839A095A94C082162";//Qlove
    public static String srvPubKey;
    public static String ec_key;// From ec_key.pem
    private static TXOpenSdkWrapper sTXOpenSdkWrapper = null;
    public static boolean isOnBinderListChanged;// 用来标记获取绑定者列表是否成功
    public static boolean isUploadRegInfoSuccess;// 用来标记上传注册信息是否成功
    //szjy end


    private TXOpenSdkWrapper(Context mContext) {
        this.mContext = mContext;
        mApplication = mContext;
    }

    public synchronized static TXOpenSdkWrapper getInstance(Context context) {
        if (sTXOpenSdkWrapper == null) {
            sTXOpenSdkWrapper = new TXOpenSdkWrapper(context);
        }
        return sTXOpenSdkWrapper;
    }

    public void onCreate() {
        QAILog.d(TAG, "onCreate: Enter");
        if (QAIConfig.MODEL_MAGELLAN_M10 == QAIConfig.qLoveProductVersionNum)
            removePidCache(mContext);

        mStoragePath = Environment.getExternalStorageDirectory().toString();
        mReceiveFileMenuPath = Environment.getExternalStoragePublicDirectory("tencent") + "/device/file";

//      TODO  AVChatManager.setBroadcastPermissionDeviceSdkEvent("com.tencent.xiaowei.demo.chat");
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());// 保存java层的日志
        if (!mContext.getPackageName().equals(getProcessName(mContext, android.os.Process.myPid()))) {
            return;
        }

        AssetsUtil.init(mContext);

        initListener();// 初始化小微sdk的事件监听器

//        if (!PidInfoConfig.init()) {
//            Toast.makeText(mContext, "请先运行sn生成工具，再重新打开Demo", Toast.LENGTH_LONG).show();
//            return;
//        }
        if (pid < 0 || (sn != null && sn.length() < 10)) {
            Toast.makeText(mContext, "SN 或者 PID 不对", Toast.LENGTH_LONG).show();
            return;
        }

//        if (!UIUtils.isNetworkAvailable(this)) {
//            PcmBytesPlayer.getInstance().play(AssetsUtil.getRing("network_disconnected.pcm"), null);
//        }

        // 构造登录信息
        XWLoginInfo login = new XWLoginInfo();
//        login.deviceName = getString(R.string.app_name);
        login.deviceName = "QLoveAICore";
        login.license = licence;
        login.serialNumber = sn;
        login.srvPubKey = srvPubKey;
        login.productId = pid;
        Log.d(TAG, "onCreate: pid " + pid + " sn:" + sn + " pubkey:" + srvPubKey + " license:" + licence);
//        login.productVersion = UIUtils.getVersionCode(this);// build.gradle中的versionCode，用来检测更新
        login.productVersion = 15;
        login.networkType = XWLoginInfo.TYPE_NETWORK_WIFI;
        login.runMode = XWLoginInfo.SDK_RUN_MODE_DEFAULT;
        login.sysPath = mContext.getCacheDir().getAbsolutePath();
        login.sysCapacity = 1024000L;
        login.appPath = mContext.getCacheDir().getAbsolutePath();
        login.appCapacity = 1024000L;
        login.tmpPath = Environment.getExternalStoragePublicDirectory("tencent") + "/device/file/";
        login.tmpCapacity = 1024000L;

        int ret = XWCoreService.init(mContext, login);// 初始化小微sdk
        if (ret != 0) {
            showToast("初始化失败");
            return;
        }
//        if (!BuildConfig.IS_NEED_VOICE_LINK) {
//            startService(new Intent(this, WakeupAnimatorService.class));
//        }

        XWAccountInfo accountInfo = new XWAccountInfo();
        XWSDK.getInstance().init(mContext, accountInfo);
        QLog.e(TAG, "onCreate");

        XWeiControl.getInstance().init();
        XWeiControl.getInstance().setXWeiPlayerMgr(new XWeiPlayerMgr(mContext.getApplicationContext()));

        XWSDK.getInstance().setOnReceiveTTSDataListener(new XWSDK.OnReceiveTTSDataListener() {
            @Override
            public boolean onReceive(String voiceId, XWTTSDataInfo ttsData) {
                TTSManager.getInstance().write(ttsData);
                return true;
            }
        });

        XWSDK.getInstance().setAutoDownloadFileCallback(new XWSDK.OnAutoDownloadCallback() {
            @Override
            public int onDownloadFile(long size, int channel) {
                QLog.e(TAG, "onDownloadFile size: " + size + " channel: " + channel);
                //返回0表示继续下载，返回非0表示停止下载
                return 0;
            }
        });

        //初始化cc消息
/*
        XWCCMsgManager.initC2CMsgModule();
        XWCCMsgManager.setOnReceiveC2CMsgListener(new XWCCMsgManager.OnReceiveC2CMsgListener() {
            @Override
            public void onReceiveC2CMsg(long from, XWCCMsgInfo msg) {
                if (msg.businessName.equals("蓝牙")) {
                    BLEManager.onCCMsg(from, new String(msg.msgBuf));
                } else if (msg.businessName.equals(AIC2CBusiness_GetVolume)) {
                    AIAudioService service = AIAudioService.getInstance();
                    if (service != null) {
                        int volume = service.getVolume();
                        QLog.d(TAG, "GetVolume " + volume);

                        XWCCMsgInfo ccMsgInfo = new XWCCMsgInfo();
                        ccMsgInfo.businessName = AIC2CBusiness_ReturnVolume;
                        ccMsgInfo.msgBuf = Integer.toString(volume).getBytes();
                        XWCCMsgManager.sendCCMsg(from, ccMsgInfo, new XWCCMsgManager.OnSendCCMsgListener() {

                            @Override
                            public void onResult(long to, int errCode) {
                                QLog.d(TAG, "sendCCMsg result to: " + to + " errCode: " + errCode);
                            }
                        });
                    }
                } else if (msg.businessName.equals(AIC2CBusiness_SetVolume)) {
                    if (msg.msgBuf != null) {
                        int volume = Integer.valueOf(new String(msg.msgBuf));
                        AIAudioService service = AIAudioService.getInstance();
                        if (service != null) {
                            service.setVolume(volume);
                        }
                    }
                }
            }
        });
*/
//        if (BluetoothAdapter.getDefaultAdapter() != null) {
//            startService(new Intent(this, BLEService.class));
//        }


        // 初始化音视频Service
        // startService(new Intent(this, XWAVChatAIDLService.class));
        AVChatManager.getInstance().init(mContext);
    }

    public static String getDeviceHeadUrl(String strPID) {
        if (TextUtils.isEmpty(strPID)) {
            return "";
        }

        String fullAppid = strPID;
        //容错补零
        if (strPID.length() < 8) {
            String fillZeroString = "00000000";
            fullAppid = (fillZeroString + strPID);
        }

        fullAppid = fullAppid.substring(fullAppid.length() - 8);
        String iconUrl = String.format(URI_DEVICE_ICON_FORMAT, fullAppid.substring(0, 2),
                fullAppid.substring(2, 4), fullAppid.substring(4, 6),
                fullAppid.substring(6, 8),
                strPID);
        QLog.d(TAG, iconUrl);
        return iconUrl;
    }


    private void initListener() {
        XWDeviceBaseManager.setOnDeviceRegisterEventListener(new XWDeviceBaseManager.OnDeviceRegisterEventListener() {
            @Override
            public void onConnectedServer(int errorCode) {
                if (errorCode != 0)
                    showToast("连接服务器失败 " + errorCode);
            }

            @Override
            public void onRegister(int errorCode, int subCode) {
                // errorCode为1需要关注init的参数和配置平台的配置是否都正确。其他错误可以反馈小微。
                if (errorCode != 0)
                    showToast("注册失败 " + subCode + ",请检查网络以及登录的相关信息是否正确。");
            }
        });

        XWDeviceBaseManager.setOnBinderEventListener(new XWDeviceBaseManager.OnBinderEventListener() {

            @Override
            public void onBinderListChange(int error, ArrayList<XWBinderInfo> arrayList) {
                // 刷新MainActivity的列表以及判断是否需要关闭已经打开的Activity
                if (error == 0) {
                    sendBroadcast(ACTION_ON_BINDER_LIST_CHANGE);
                    if (arrayList.size() == 0) {
                        XWeiAudioFocusManager.getInstance().abandonAllAudioFocus();// 解绑了应该停止所有的资源的播放
                    }
                }

                String tmp = "";
                for (XWBinderInfo info : arrayList) {
                    tmp += (info.toString() + ";");
                }
                QLog.i(TAG, "onBinderListChange: error =  " + error + " errorDetail:"
                        + " UUU");
                QAILog.d(TAG, "onBinderListChange() called with: i = [" + error + "], arrayList = [" + tmp + "]");

                mArrayList.clear();
                mArrayList.addAll(arrayList);

                if (error == XWCommonDef.ErrorCode.ERROR_NULL_SUCC) {
                    isOnBinderListChanged = true;
                    sendBinderListChange();
                } else {
                    isOnBinderListChanged = false;
//                    QAIPrompter.getInstance().onBinderListChangeError(error); TODO TODO TODO
                }
            }
        });// 被绑定、列表变化、擦除所有设备了
        XWDeviceBaseManager.setOnDeviceSDKEventListener(new XWDeviceBaseManager.OnDeviceLoginEventListener() {
            @Override
            public void onLoginComplete(int error) {
                QLog.i(TAG, "onLoginComplete: error =  " + error);
                if (error == 0) {
                    isLogined = true;
                    showToastMessage("登录成功");
                    sendBroadcast(ACTION_LOGIN_SUCCESS);

                    boolean useNew = true;
                    if (useNew) {
                        String qrCode = XWDeviceBaseManager.getQRCodeUrl();
                        QAILog.d(TAG, "onLoginComplete: qrcode url:" + QAIUtils.encData(qrCode));
                        mContext.sendStickyBroadcast(QAIUtils.getPidAndIntent(qrCode));
                    }
                } else {
                    sendBroadcast(ACTION_LOGIN_FAILED);
                    showToastMessage("登录失败");
                    CountlyEvents.onLoginCompleteError(error, error+"");
                }
            }

            @Override
            public void onOnlineSuccess() {
                isOnline = true;
                RecordDataManager.getInstance().setHalfWordsCheck(true);

                QLog.i(TAG, "onOnlineSuccess");
                showToastMessage("上线成功");
                sendBroadcast("ONLINE");
            }

            @Override
            public void onOfflineSuccess() {
                isOnline = false;
                RecordDataManager.getInstance().setHalfWordsCheck(false);

                QLog.i(TAG, "onOfflineSuccess ");
                showToastMessage("离线");
                sendBroadcast("OFFLINE");
            }

            @Override
            public void onUploadRegInfo(int error) {
                QLog.i(TAG, "onUploadRegInfoSuccess " + error);
                if (error == 0) {
//                    showToastMessage("上传注册信息成功");
                    isUploadRegInfoSuccess = true;
                } else {
                    isUploadRegInfoSuccess = false;
                }
            }
        });// 登录成功、上下线
    }

    private void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    private void sendBroadcast(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (bundle != null)
            intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
        QLog.d(TAG, "send a broadcast:" + action);
    }

    public static void showToastMessage(final String text) {
        showToastMessage(text, true);
    }

    public static void showToastMessage(final String text, boolean show) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mApplication, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

    public static void showToast(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(mApplication, text, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    public interface InitListener {
        void initComplete();
    }


    // szjy begin
    private ArrayList<XWBinderInfo> mArrayList = new ArrayList<>();

    private void sendBinderListChange() {
        //  TODO TODO  TODO MJ
        /*if (!isUploadRegInfoSuccess || !isOnBinderListChanged) {
            QAILog.i(TAG, "ignore sendBinderListChange: isUploadRegInfoSuccess ="
                    + isUploadRegInfoSuccess + "  isOnBinderListChanged = " + isOnBinderListChanged);
            return;
        }*/

        QAILog.i(TAG, "sendBinderListChange: enter");
        // 如果arrayList长度为0，说明没绑定者，或者解绑了
        boolean bind = false;
        String headUrl = "";
        if (mArrayList.size() > 0) {
            bind = true;
            headUrl = mArrayList.get(0).headUrl;
        }

        String ACTION_TXSDK_BIND_STATE = "kinstalk.com.aicore.action.txsdk.bind_status";
        Intent intent = new Intent();
        intent.setAction(ACTION_TXSDK_BIND_STATE);
        intent.putExtra("bind_status", bind);
        intent.putExtra("head_url", headUrl);
        mContext.sendStickyBroadcast(intent);

        if (mArrayList.size() == 0) {
            CountlyEvents.unBindSucceed();

            DeviceBindStateController.getInst(mContext.getApplicationContext()).onBindStateChange(false);
            DeviceBindStateController.getInst(mContext.getApplicationContext()).setQBinderInfo(null);

            //Fix 11354： 解绑之后，不让唤醒
            QAISpeechStatesMachine.getInstance().setWakeupEnabled(false);

//  TODO TODO TODO          sendBroadcast(ACTION_ON_ERASE_ALL_BINDERS);
        } else {
            CountlyEvents.bindSucceed();

            DeviceBindStateController.QBinderInfo b = new DeviceBindStateController.QBinderInfo();
            b.contactType = mArrayList.get(0).contactType;
            b.remark = mArrayList.get(0).remark;
            b.headUrl = mArrayList.get(0).headUrl;
            b.type = mArrayList.get(0).type;

            //绑定之后，支持唤醒
            QAISpeechStatesMachine.getInstance().setWakeupEnabled(true);

            DeviceBindStateController.getInst(mContext.getApplicationContext()).onBindStateChange(true);
            DeviceBindStateController.getInst(mContext.getApplicationContext()).setQBinderInfo(b);
        }

        BusEventCommon evt = new BusEventCommon();
        evt.eventType = BusEventCommon.BIND_STATE_CHANGE;
        evt.bVar1 = bind;
        evt.strVar1 = headUrl;
        EventBus.getDefault().post(evt);

        // 发送之后，重置状态
        isOnBinderListChanged = false;
    }

    public void getLicenseLocally() {
        QAILog.i(TAG, "getLicenseLocally: enter");

        String eckeyPath = mContext.getFilesDir().getPath() + "/ec_key.pem";
        String licPath = mContext.getFilesDir().getPath() + "/lic.pem";

        AssetCache assets = new AssetCache(mContext, "");
        try {
            assets.getPath("ec_key.pem");
        } catch (IOException e) {
            e.printStackTrace();
        }

        initLocalPidAndKeys();
        // TencentECCEngine engine = new TencentECCEngine();
        //licence = engine.ECDSASignBufferBase16(ec_key, sn);
        // engine.ECDSASignBufferBase16ToLicenceFile(eckeyPath, sn, licPath);

        licence = readFile(licPath);

        QLog.d(TAG, "sn = " + sn);
        QLog.d(TAG, "local get licence = " + licence);
    }

    private String readFile(String inPath) {

        StringBuilder sb = new StringBuilder();

        try {
            Reader r = new FileReader(inPath);
            int c = r.read();

            while (c != -1) {
                sb.append((char) c);
                c = r.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        QAILog.i(TAG, "lic=" + sb.toString());
        return sb.toString();
    }

    public void initLocalPidAndKeys() {
        long pid_2100000151 = 2100000151;//Qlove 云小微上申请的 Columbus/Armstrong
        // From https://xiaowei.qcloud.com/hardware/device-detail/2100000151
        String srvPubKey_2100000151 = "0460DCAFB7C97FE71B9F6D39436A247C5B15EAC4A4494052A6F20647EF42A2A3EABB4EA8DC6BF341929A847E42C5FBEFBF";//2100000151
        /**这是2100000151设备的私钥*/
        String ec_key_2100000151 = "-----BEGIN EC PARAMETERS-----\n" +
                "BgUrgQQACg==\n" +
                "-----END EC PARAMETERS-----\n" +
                "-----BEGIN EC PRIVATE KEY-----\n" +
                "MHQCAQEEIOah5TIkXG4ciQFT3+iD5kmBwu/sIbA1MjvzwvAdMcV5oAcGBSuBBAAK\n" +
                "oUQDQgAEGt6tM23xEbRlI59sJenTucKXsNnMu3kRHnPm0ym3RBlmdur9xIjPJ0pM\n" +
                "nhew/0H6SX7YJfRcChgxy2tjn7iv4Q==\n" +
                "-----END EC PRIVATE KEY-----";

        long pid_2100000229 = 2100000229;//Qlove 云小微上申请的 Magellan
        // From https://xiaowei.qcloud.com/hardware/device-detail/2100000229
        String srvPubKey_2100000229 = "045DF0AB48EF8864D6F1BBA477DA8408C7EFDF69B3265AB3F3D27E66C43D2BF00CBA0BDF9EFAE0DB91B805248F87CF7BF4";//2100000229

        /**这是2100000229设备的私钥*/
        String ec_key_2100000229 = "-----BEGIN EC PARAMETERS-----\n" +
                "BgUrgQQACg==\n" +
                "-----END EC PARAMETERS-----\n" +
                "-----BEGIN EC PRIVATE KEY-----\n" +
                "MHQCAQEEIPCt+KEcB6AsEhzoxl64HDQ6Kehewd7v+NGDF4N2G4z6oAcGBSuBBAAK\n" +
                "oUQDQgAEzUJ5qLx9BVX6eTAv74FFwkDE5NMpK77DXexUxIzoNdYt6U0KX6pCxNFM\n" +
                "oJMt0oeC3bBpetB2klIwtmU2KlVwzw==\n" +
                "-----END EC PRIVATE KEY-----";

        long pid_2100000370_m10_music = 2100000370; // M10 with music PID
        String srvP = "04B3CF7DDB252023A112F94B71048D6DBC06FB1FBCC5945D61C4C3A67E651DF4AADC725C935D1F6452482CE573559955F7";
        String ec = "-----BEGIN EC PARAMETERS-----\n" +
                "BgUrgQQACg==\n" +
                "-----END EC PARAMETERS-----\n" +
                "-----BEGIN EC PRIVATE KEY-----\n" +
                "MHQCAQEEIAwgMbciyZhqLl+JgEzAttZKEhqIYJv4gvjJRuToWN2yoAcGBSuBBAAK\n" +
                "oUQDQgAE2Dyea/NqJPGcXqc7jcwsMt+7Ql+wUJfQZQlkY6Yw4NH3oEiiVp6aZrP+\n" +
                "UNSLeXJlxAdWz83mfnVCAomGGlo/eA==\n" +
                "-----END EC PRIVATE KEY-----";

        switch (QAIConfig.qLoveProductVersionNum) {
            case QAIConfig.MODEL_MAGELLAN_M10:
            case QAIConfig.MODEL_MAGELLAN_M7:
                QAILog.d(TAG, "TXSdkWrapper MODEL_MAGELLAN");
                pid = pid_2100000370_m10_music;
                srvPubKey = srvP;
                ec_key = ec;
                break;
            default:
                QAILog.d(TAG, "TXSdkWrapper MODEL_DEFAULT");
                pid = pid_2100000151;
                srvPubKey = srvPubKey_2100000151;
                ec_key = ec_key_2100000151;
        }
    }

    private void removePidCache(Context context) {
        QAILog.w(TAG, "removePidCache Enter");
        File f1 = new File(context.getFilesDir().getPath() + "/../cache/AppData");
        File f2 = new File(context.getFilesDir().getPath() + "/../cache/SysData");
        QAILog.w(TAG, "removePidCache f1 :" + f1.exists() + " f2:" + f2.exists());
        QAILog.w(TAG, "removePidCache f1:" + f1.getPath() + " f2:" + f2.getPath());

        if (f1.exists() && f2.exists()) {
            boolean cacheUpdated = QAIConfigController.readAicoreEnable(context, QAIConstants.SHARED_PREFERENCE_KEY_UPDATE_CACHE);
            QAILog.w(TAG, "cache updated:" + cacheUpdated);

            if (!cacheUpdated) {
                removeDirectory(f1);
                removeDirectory(f2);

                QAIConfigController.writeAicoreEnable(context, QAIConstants.SHARED_PREFERENCE_KEY_UPDATE_CACHE, true);
            }
        } else {
            QAILog.w(TAG, "f1 or f2 not exist");
        }
    }

    private static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }

            QAILog.w("AI-QAICoreService", "11 removeDirectory:" + dir.getName());

            dir.delete();
        } else {
            dir.delete();
            QAILog.w("AI-QAICoreService", "22 removeDirectory:" + dir.getName());
        }
    }

    public static String getQloveSN() {
        if (!TextUtils.isEmpty(qloveSn)) {
            return qloveSn;
        }
        String qlovesn = SystemPropertiesProxy.getString(mContext, "ro.serialno");
        qloveSn = TextUtils.isEmpty(qlovesn) ? "" : qlovesn;

        return qloveSn;
    }

    public void getSn() {
        String serialNum = getQloveSN();
        boolean isSnGot = false;
        if (!TextUtils.isEmpty(serialNum)) {
            QAILog.d(TAG, "getSn: serialNum = " + serialNum);
            if (serialNum.length() == 18) {
                sn = serialNum.substring(2, 18);
                isSnGot = true;
                QAILog.d(TAG, "getSn: sn = " + sn);
            } else {
                QAILog.e(TAG, "getSn: wrong serial number");
//                CountlyEvents.initTxSdkWrongSN("qlove_sn", serialNum);
            }
        } else {
            QAILog.e(TAG, "getSn: empty serial number ");
//            CountlyEvents.initTxSdkWrongSN("qlove_sn", "empty_sn");
            String macAddr = getLocalMacAddress();
            if (!TextUtils.isEmpty(macAddr)) {
                QAILog.d(TAG, "getSn: mac = " + macAddr);
                if (macAddr.length() == 17) {
                    sn = macAddr.substring(0, 2) + macAddr.substring(3, 17);
                    isSnGot = true;
                    QAILog.d(TAG, "getSn: mac sn = " + sn);
                } else {
                    QAILog.e(TAG, "getSn: wrong mac Addr");
//                    CountlyEvents.initTxSdkWrongSN("qlove_sn_mac", macAddr);
                }
            } else {
//                CountlyEvents.initTxSdkWrongSN("qlove_sn_mac", "empty_mac");
            }
        }

        if (!isSnGot) {
            QLog.e(TAG, "获取sn失败");// 在wifi没打开的时候获取不到mac地址
            /*
            sn = sp.getString("sn_1", null);
            if (TextUtils.isEmpty(sn)) {
                sn = getRandomSN();
                CountlyEvents.initTxSdkWrongSN("qlove_sn_random", sn);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("sn_1", sn);
                editor.commit();
            }
            */
        }
    }

    /**
     * 获得mac当作sn
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String Mac = null;
        try {
            String path = "sys/class/net/wlan0/address";
            if ((new File(path)).exists()) {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    Mac = new String(buffer, 0, byteCount, "utf-8");
                }
                fis.close();
            }

            if (Mac == null || Mac.length() == 0) {
                path = "sys/class/net/eth0/address";
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis.read(buffer_name);
                if (byteCount_name > 0) {
                    Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
                fis.close();
            }

            if (!TextUtils.isEmpty(Mac)) {
                Mac = Mac.substring(0, Mac.length() - 1);
            }
        } catch (Exception io) {
        }

        if (TextUtils.isEmpty(Mac)) {
            WifiManager wifiManager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getMacAddress() != null) {
                return wifiInfo.getMacAddress();// MAC地址
            }
        }
        return TextUtils.isEmpty(Mac) ? "" : Mac;
    }
    // szjy end
}
