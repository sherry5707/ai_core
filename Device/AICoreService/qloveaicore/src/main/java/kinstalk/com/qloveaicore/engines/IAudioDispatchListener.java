package kinstalk.com.qloveaicore.engines;

/**
 * Created by lenovo on 2018/3/28.
 */

public interface IAudioDispatchListener {
    //write audio buffer to other Apps, return false if no other APP is waiting for data
    public byte[] write(byte[] buffer);
     //wake up other APPs to receive audio buffer, return false if no other APP is waiting for data
    public boolean onWakeup();

    public void queueWakeupNotification();

    public void onIdle();

    public boolean isOuterRecognizing();
}
