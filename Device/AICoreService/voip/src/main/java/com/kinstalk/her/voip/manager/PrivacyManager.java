package com.kinstalk.her.voip.manager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import com.kinstalk.her.library.view.dialog.MagellanSystemDialog;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.tencent.xiaowei.util.QLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by siqing on 17/10/27.
 */

public class PrivacyManager {

    private static PrivacyManager _instance;
    private Context context;
    private boolean isPrivacy = false;

    public interface PrivacyListener{
        void onPrivacyChange(boolean isPrivacy);
    }

    private List<PrivacyListener> listeners = new ArrayList<>();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isPrivacy = intent.getBooleanExtra("enable", false);
            notifyPrivacyStateChange(isPrivacy);
           QLog.d("PrivacyManager","隐私模式状态监听:isPrivacy =" + isPrivacy);
        }
    };

    private PrivacyManager(Context context) {
        this.context = context.getApplicationContext();
        IntentFilter intentFilter = new IntentFilter("kingstalk.action.privacymode");
        this.context.registerReceiver(broadcastReceiver, intentFilter);
        QLog.d("PrivacyManager","registerReceiver isPrivacy=" + isPrivacy);
    }

    public static PrivacyManager getInstance(Context context) {
        if (_instance == null) {
            synchronized (PrivacyManager.class) {
                if (_instance == null) {
                    _instance = new PrivacyManager(context);
                }
            }
        }
        return _instance;
    }

    /**
     * 给Voip进程用的
     */
    public void destory(){
        QLog.d("PrivacyManager","unregisterReceiver isPrivacy=" + isPrivacy);
        context.unregisterReceiver(broadcastReceiver);
    }

    public boolean isPrivacy() {
        return isPrivacy;
    }

    public void registerPrivacyListener(PrivacyListener listener){
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removePrivacyListener(PrivacyListener listener) {
        listeners.remove(listener);
    }

    private void notifyPrivacyStateChange(boolean isPrivacy){
        for (PrivacyListener listener : listeners) {
            listener.onPrivacyChange(isPrivacy);
        }
    }

    /**
     * 关闭隐私模式
     */
    public void closePrivacy() {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        try {
            Class<NotificationManager> NotificationManagerClass = NotificationManager.class;
            Method method = NotificationManagerClass.getMethod("turnOffPrivacyMode", new Class[]{});
            method.setAccessible(true);
            Object object = method.invoke(mNotificationManager);
            int a = (int) object;
            System.out.println(a);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }

    public void showPrivacyDialog(Context context, String msg, String leftStr, DialogInterface.OnClickListener leftAction, String rightStr, DialogInterface.OnClickListener rightAction ) {
        final MagellanSystemDialog.Builder builder = new MagellanSystemDialog.Builder(context)
                .setMessage(msg)
                .setLeftAction(leftStr, leftAction)
                .setRightAction(rightStr, rightAction)
                .build();
        final PrivacyListener listener = new PrivacyListener() {
            @Override
            public void onPrivacyChange(boolean isPrivacy) {
                if (!isPrivacy) {
                    builder.getDialog().dismiss();
                }
            }
        };
        builder.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                removePrivacyListener(listener);
            }
        });
        registerPrivacyListener(listener);
        builder.show();
    }

    /**
     * Voip 展示隐私模式对话框
     * @param context
     * @param leftAction
     * @param rightAction
     */
    public void showVoipPrivacyDialog(Context context, DialogInterface.OnClickListener leftAction, DialogInterface.OnClickListener rightAction){
        showPrivacyDialog(context, "关闭隐私模式后才能正常使用", "取消", leftAction, "关闭", rightAction);
    }

}
