package com.kinstalk.her.voip.ring;

import android.content.Context;
import android.media.MediaPlayer;


/**
 * Created by siqing on 17/12/26.
 */

public class RingPlayerTencent implements RingManager {

    public RingPlayerTencent(Context context){

    }

    @Override
    public void startRing(String name, int loop, MediaPlayer.OnCompletionListener listener) {
 //       VideoController.getInstance().startRing(name, loop, listener);
    }

    @Override
    public void stopRing() {
        //VideoController.getInstance().stopRing();
    }
}
