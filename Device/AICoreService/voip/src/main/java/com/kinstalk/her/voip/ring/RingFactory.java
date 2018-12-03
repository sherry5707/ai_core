package com.kinstalk.her.voip.ring;

import android.content.Context;

/**
 * Created by siqing on 17/12/26.
 */

public class RingFactory {

    public static RingManager createRingManager(Context context, boolean isTencent) {
        if (isTencent) {
            return new RingPlayerTencent(context);
        } else {
            return new RingPlayerM7(context);
        }
    }

}
