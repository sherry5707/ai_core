package kinstalk.com.qloveaicore.engines;

/**
 * Created by shaoyi on 2018/5/2.
 */

public interface ASRListener {

        void onInit(int status);
        void onStop();
        void onCmd(String results);
        void onResults(String results, String answer);
        void onPartialResult(String result, int type);
        void onError(String var1, int var2);
        void onBeginningOfSpeech();
        void onEndOfSpeech();
        void onReady();

}
