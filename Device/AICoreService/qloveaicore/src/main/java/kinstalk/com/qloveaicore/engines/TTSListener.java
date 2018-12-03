package kinstalk.com.qloveaicore.engines;

/**
 * Created by shaoyi on 2018/4/17.
 */

public interface  TTSListener {
    void onInit(int var1);

    void onError(String var1, int var2);

    void onReady(String var1);

    void onCompletion(String var1);

    void onProgress(int var1, int var2, boolean var3);
}
