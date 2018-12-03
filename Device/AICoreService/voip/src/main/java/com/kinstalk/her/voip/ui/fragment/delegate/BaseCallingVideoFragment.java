package com.kinstalk.her.voip.ui.fragment.delegate;

import android.content.Intent;

import com.kinstalk.her.voip.ui.fragment.BaseFragment;

/**
 * Created by siqing on 17/10/31.
 */

public abstract class BaseCallingVideoFragment extends BaseFragment {


    public static String ACTION_TIME_CHANGE = "com.kinstalk.her.voip.TIME_CHANGE";

    public static String INTENT_TIME = "time";
    public static String INTENT_NAME = "name";

    public void sendTimeBroadCast(String time, String name){
        Intent intent = new Intent(ACTION_TIME_CHANGE);
        intent.putExtra(INTENT_TIME, time);
        intent.putExtra(INTENT_NAME, name);
        getActivity().sendBroadcast(intent);
    }

}
