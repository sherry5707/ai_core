package kinstalk.com.qloveaicore;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import kinstalk.com.common.utils.QAILog;
import kinstalk.com.qloveaicore.genericskill.GenericSkillService;


public class BootCompleteReceiver extends BroadcastReceiver {
    public static final String TAG = "AI-BootReceiver";

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        QAILog.d(TAG, "onReceive: Enter");

        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            QAILog.d(TAG, "onReceive: " + intent);

            String action = intent.getAction();
            if (TextUtils.equals(action, "android.intent.action.BOOT_COMPLETED")) {
                startCoreService(context);
                startGenericSkillService(context);
            }

        } else {
            QAILog.d(TAG, "onReceive: null obj, c:" + context + " i:" + intent);
        }
    }

    public void startCoreService(Context c) {
        QAILog.d(TAG, "startCoreService: Enter");
//        Intent i = new Intent();
//        i.setComponent(new ComponentName("kinstalk.com.qloveaicore", "kinstalk.com.qloveaicore.QAICoreService"));
//        c.startService(i);

        Intent i2 = new Intent(c, QAICoreService.class);
        c.startService(i2);

        Intent intent = new Intent(c, QAICoreAudioService.class);
        c.startService(intent);
    }

    private void startGenericSkillService(Context c) {
        QAILog.d(TAG, "startGenericSkillService: Enter");
        String SvcPkg = "kinstalk.com.qloveaicore.genericskill";
        String SvcCls = "kinstalk.com.qloveaicore.genericskill.GenericSkillService";

//        Intent i = new Intent();
//        i.setComponent(new ComponentName(SvcPkg, SvcCls));
        Intent i2 = new Intent(c, GenericSkillService.class);
        c.startService(i2);
    }
}
