package kinstalk.com.common.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import java.util.Hashtable;

import kinstalk.com.qloveaicore.AICoreDef;

import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_BASE;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_LOOP;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_NEXT;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_ORDER;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_PAUSE;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_PREV;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_RANDOM;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_REPEAT;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_RESUME;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_SHARE;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_SINGLE;
import static kinstalk.com.qloveaicore.AICoreDef.AppControlCmd.CONTROL_CMD_STOP;

/**
 * Created by majorxia on 2018/4/2.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class QAIUtils {
    private static final String TAG = "QAIUtils";

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    public static Intent getAICoreServiceIntent() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("kinstalk.com.qloveaicore", "kinstalk.com.qloveaicore.QAICoreService"));
        return i;
    }

    public static Bitmap createImage(String text) {
        Log.d(TAG, "createImage: Enter, " + text);
        final int w = 300;
        final int h = 300;
        return createImage(text, w, h);
    }

    public static Bitmap createImage(String text, int qr_width, int qr_height) {
        Log.d(TAG, "createImage() called with: text = [" + text + "], qr_width = [" + qr_width + "], qr_height = [" + qr_height + "]");
        try {
            // 需要引入core包
            QRCodeWriter writer = new QRCodeWriter();

            if (text == null || "".equals(text) || text.length() < 1) {
                return null;
            }

            // 把输入的文本转为二维码
            BitMatrix martix = null;
            try {
                martix = writer.encode(text, BarcodeFormat.QR_CODE,
                        qr_width, qr_height);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, qr_width, qr_height, hints);
            int[] pixels = new int[qr_width * qr_height];
            for (int y = 0; y < qr_height; y++) {
                for (int x = 0; x < qr_width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * qr_width + x] = 0xff000000;
                    } else {
                        pixels[y * qr_width + x] = 0xffffffff;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(qr_width, qr_height,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, qr_width, 0, 0, qr_width, qr_height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getPropertyIdFromXWRspInfo(XWResponseInfo respInfo) {
        int propId = -1;
        try {
            if (respInfo != null && respInfo.resources != null) {
                for (XWResGroupInfo resGroup : respInfo.resources) {
                    for (XWResourceInfo resource : resGroup.resources) {
                        if (resource.format == 5) {
                            propId = Integer.valueOf(resource.ID);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propId;
    }

    public static Intent getPidAndIntent(String url) {
        Intent i = new Intent();
        i.setAction(AICoreDef.ACTION_TXSDK);
        i.putExtra(AICoreDef.ACTION_TXSDK_EXTRA_QRURL, encData(url));
        return i;
    }

    public static String encData(String data) {
        String e = Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
        return e.substring(0, 2) + "JX" + e.substring(2);
    }

    public static int getQCmdFromProp(int propId) {
        return Qcmd2PropMap.get(propId, CONTROL_CMD_BASE);
    }

    public static String getStringFromAppCtrlCmd(int ctrlCmd) {
        return AppCtrl2StrMap.get(ctrlCmd, "UNKNOWN");
    }

    public static Intent getTTSStopIntent() {
        Intent i = new Intent();
        i.setAction(AICoreDef.ACTION_TXSDK_TTS);
        i.putExtra(AICoreDef.ACTION_TXSDK_EXTRA_TTS_STATE, AICoreDef.ACTION_TXSDK_EXTRA_TTS_STOP);
        return i;
    }

    public static Intent getTTSStartIntent() {
        Intent i = new Intent();
        i.setAction(AICoreDef.ACTION_TXSDK_TTS);
        i.putExtra(AICoreDef.ACTION_TXSDK_EXTRA_TTS_STATE, AICoreDef.ACTION_TXSDK_EXTRA_TTS_START);
        return i;
    }

    private static final SparseArray<String> AppCtrl2StrMap = new SparseArray<>();
    private static final SparseIntArray Qcmd2PropMap = new SparseIntArray();

    static {
        Qcmd2PropMap.put(700003, CONTROL_CMD_RESUME);
        Qcmd2PropMap.put(700004, CONTROL_CMD_PAUSE);
        Qcmd2PropMap.put(700153, CONTROL_CMD_STOP);//fix bug 14865
        Qcmd2PropMap.put(700100, CONTROL_CMD_STOP);
        Qcmd2PropMap.put(700005, CONTROL_CMD_PREV);
        Qcmd2PropMap.put(700006, CONTROL_CMD_NEXT);
        Qcmd2PropMap.put(700103, CONTROL_CMD_RANDOM);
        Qcmd2PropMap.put(700104, CONTROL_CMD_ORDER);
        Qcmd2PropMap.put(700137, CONTROL_CMD_LOOP);
        Qcmd2PropMap.put(700113, CONTROL_CMD_SINGLE);
        Qcmd2PropMap.put(700108, CONTROL_CMD_REPEAT);
        Qcmd2PropMap.put(700126, CONTROL_CMD_SHARE);

        AppCtrl2StrMap.put(CONTROL_CMD_RESUME, "RESUME");
        AppCtrl2StrMap.put(CONTROL_CMD_PAUSE, "PAUSE");
        AppCtrl2StrMap.put(CONTROL_CMD_STOP, "STOP");
        AppCtrl2StrMap.put(CONTROL_CMD_PREV, "PREV");
        AppCtrl2StrMap.put(CONTROL_CMD_NEXT, "NEXT");
        AppCtrl2StrMap.put(CONTROL_CMD_RANDOM, "RANDOM");
        AppCtrl2StrMap.put(CONTROL_CMD_ORDER, "ORDER");
        AppCtrl2StrMap.put(CONTROL_CMD_LOOP, "LOOP");
        AppCtrl2StrMap.put(CONTROL_CMD_SINGLE, "SINGLE");
        AppCtrl2StrMap.put(CONTROL_CMD_REPEAT, "REPEAT");
        AppCtrl2StrMap.put(CONTROL_CMD_SHARE, "SHARE");
    }
}
