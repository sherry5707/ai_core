package com.kinstalk.her.voip.ring;

import android.media.MediaPlayer;

/**
 * Created by siqing on 17/12/26.
 */

public interface RingManager {

    void startRing(String name, int loop, MediaPlayer.OnCompletionListener listener);

    void stopRing();
}
