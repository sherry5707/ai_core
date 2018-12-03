/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//import kinstalk.com.base.BaseApplication;
import kinstalk.com.common.QAIConfig;

/**
 * Created by knight.xu on 2017/8/4.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public final class SystemTool {
    private static final String TAG = "SystemTool";

    public static final String AUDIO_HW_TYPE_CNXT = "cnxt"; // 科声讯
    public static final String AUDIO_HW_TYPE_SND_CNCT = "snd_cnct";// 先声
    public static final String AUDIO_HW_TYPE_AUDIENCE = "audience";// 原armstrong/columbus的audience设备
    public static final String AUDIO_HW_TYPE_UNKOWN = "unkown"; //未知

    private static String sWifiMacAddress = "";

    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    public static String getDataTime() {
        return getDataTime("HH:mm");
    }

    public static String getPhoneIMEI(Context cxt) {
        TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static void sendSMS(Context cxt, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent("android.intent.action.SENDTO", smsToUri);
        intent.putExtra("sms_body", smsBody);
        cxt.startActivity(intent);
    }

    /**
     * get App versionName
     * wangzhipeng@shuzijiayuan.com
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static boolean checkNet(Context context) {
        if (null == context) return false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return true;
    }

    public static boolean isWiFi(Context cxt) {
        if (cxt == null) return false;
        ConnectivityManager cm = (ConnectivityManager) cxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo.State state = cm.getNetworkInfo(1).getState();
        return (NetworkInfo.State.CONNECTED == state);
    }

    public static void hideKeyBoard(Activity aty) {
        ((InputMethodManager) aty.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(
                        aty.getCurrentFocus().getWindowToken(), 2);
    }


    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(268435456);
        context.startActivity(intent);
    }


    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent("android.intent.action.MAIN");
        mHomeIntent.addCategory("android.intent.category.HOME");
        mHomeIntent.addFlags(270532608);
        context.startActivity(mHomeIntent);
    }


    private static String hexdigest(byte[] paramArrayOfByte) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            int i = 0;
            int j = 0;
            if (i >= 16)
                return new String(arrayOfChar);
            int k = arrayOfByte[i];
            arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
            arrayOfChar[(++j)] = hexDigits[(k & 0xF)];

            ++i;
            ++j;
        } catch (Exception localException) {
        }

        return "";
    }

    public static int getDeviceUsableMemory(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        return (int) (mi.availMem / 1048576L);
    }


    /**
     * Get the device's Universally Unique Identifier (UUID).
     *
     * @return
     */
    public static String getUuid(Context context) {
        String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return uuid;
    }

    public static String getModel() {
        String model = Build.MODEL;
        return model;
    }

    public static String getProductName() {
        String productname = Build.PRODUCT;
        return productname;
    }

    public static String getDisplay() {
        String display = Build.DISPLAY;
        return display;
    }

    public static String getManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return manufacturer;
    }

    public static String getSerialNumber() {
        String serial = Build.SERIAL;
        return serial;
    }

    /**
     * Get the OS version.
     *
     * @return
     */
    public static String getOSVersion() {
        String osversion = Build.VERSION.RELEASE;
        return osversion;
    }

    public static int getSDKVersion() {
        @SuppressWarnings("deprecation")
        int sdkversion = Build.VERSION.SDK_INT;
        return sdkversion;
    }

    public static String getTimeZoneID() {
        TimeZone tz = TimeZone.getDefault();
        return (tz.getID());
    }

    public static boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") ||
                Build.PRODUCT.contains("sdk");
    }

    public static boolean isAudioDumpTest;

    public static boolean isAudioDumpTest(Context context) {
        String propKey = "persist.qlove.ai_wakeup_test";
        isAudioDumpTest = SystemPropertiesProxy.getBoolean(context, propKey, false);
        return isAudioDumpTest;
    }

    public static WakeLock acquireWakeLock(Context c) {
        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "AIWindow");
    }

    public static boolean isUserType() {
        return Build.TYPE.equals("user");
    }

    /**
     * 原property 方式设置的时间可能比较晚，为保证正确读取，在mediaserver里加了一个参数来识别设备。
     *
     * @return String
     */
    public static String getAudioHwType(Context context) {

        String audioHw = SystemTool.AUDIO_HW_TYPE_UNKOWN;
        //TODO monitor bug:10167
//		try {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String sndCardNames = mAudioManager.getParameters("SND_CARD_NAME");
        QAILog.d(TAG, "sndCardNames = " + sndCardNames);

        if (!TextUtils.isEmpty(sndCardNames) && sndCardNames.contains("=")) {
            String[] strs = sndCardNames.split("=");
            if ("SND_CARD_NAME".equalsIgnoreCase(strs[0])) {
                audioHw = strs[1];
            }
        }
//		} catch (Exception e){
//			QAILog.e(TAG, "getAudioHwType error: " + e.getMessage());
//            e.printStackTrace();
//		}
        QAILog.d(TAG, "audioHw = " + audioHw);

        return audioHw;
    }


    public static String getWIFILocalIpAdress(Context context) {

        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }

    public static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }

    /**
     * 获得mac
     *
     * @return
     */
    public static String getLocalMacAddress(Context context) {

        if (!TextUtils.isEmpty(sWifiMacAddress)) {
            return sWifiMacAddress;
        }

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
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getMacAddress() != null) {
                Mac = wifiInfo.getMacAddress();
            }
        }

        QAILog.d(TAG, "wifi Mac = " + Mac);
        sWifiMacAddress = Mac;

        return TextUtils.isEmpty(Mac) ? "" : Mac;
    }

    public static int getQLoveProductVersion(Context context) {
        int versionNum = SystemPropertiesProxy.getInt(context, "ro.boot.hwid", -1);
        QAILog.d(TAG, "getQProductVersion: ver " + versionNum);
        QAIConfig.qLoveProductVersionNum = versionNum;
        //TODO TODO  TODO TODO  XXS
        if (QAIConfig.qLoveProductVersionNum == QAIConfig.MODEL_MAGELLAN_M7) {
            QAIConfig.qLoveProductVersionNum = QAIConfig.MODEL_MAGELLAN_M10;
        }
        return versionNum;
        // 0 :armstrong/discovery
        // 1 :哥伦布
        // 3 :麦哲伦 M10
        // 5 :麦哲伦 M7
        // 104 : M4
    }

    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}