package kinstalk.com.qloveaicore.video;

import ly.count.android.sdk.Countly;

/**
 * Created by siqing on 17/7/10.
 */

public class CartoonCountly {

    public static String TAG = "CartoonCountly";


    /**
     * 打开小企鹅乐园
     */
    public static String V_QIE = "v_qie";

    /**
     * 下一集
     */
    public static String V_QIE_NEXT = "v_qie_next";

    /**
     * 上一集
     */
    public static String V_QIE_PREW = "v_qie_prew";

    /**
     * 暂停
     */
    public static String V_QIE_PAUSE = "v_qie_pause";

    /**
     * 播放
     */
    public static String V_QIE_PLAY = "v_qie_play";

    /**
     * 快进/前进
     */
    public static String V_QIE_FORWARD = "v_qie_forward";

    /**
     * 快进/前进3分钟
     */
    public static String V_QIE_FORWARD3 = "v_qie_forward3";

    /**
     * 快进/前进10分钟
     */
    public static String V_QIE_FORWARD10 = "v_qie_forward10";

    /**
     * 后退/快退
     */
    public static String V_QIE_REWIND = "v_qie_rewind";

    /**
     * 后退/快退30秒
     */
    public static String V_QIE_REWIND30S = "v_qie_rewind30s";

    /**
     * 后退/快退10分钟
     */
    public static String V_QIE_REWIND10M = "v_qie_rewind10M";

    /**
     * 停止播放/关闭视频
     */
    public static String V_QIE_STOP = "v_qie_stop";

    /**
     * 关闭小企鹅乐园
     */
    public static String V_QIE_EXIT = "v_qie_exit";

    /**
     * 打开爱奇艺
     */
    public static String V_AIQIYI = "v_aiqiyi";


    public static void recordEvent(String envent) {
        Countly.sharedInstance().recordEvent(envent);
    }

}
