package kinstalk.com.qloveaicore.engines;

/**
 * Created by shaoyi on 2018/4/17.
 */

public interface  WakeupListener {
    void onInit(int var1);

    void onError(String var1);

    void onReady();

    void onWakeup(String var1,int score);

}
