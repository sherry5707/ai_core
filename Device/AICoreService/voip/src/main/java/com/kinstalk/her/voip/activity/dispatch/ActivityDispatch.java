package com.kinstalk.her.voip.activity.dispatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.kinstalk.her.voip.activity.UserInfoActivity;
import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.kinstalk.her.voip.manager.PrivacyManager;
import com.kinstalk.her.voip.ui.utils.NetUtils;
import com.kinstalk.her.voip.manager.AVChatManager;


public class ActivityDispatch {

    public static void startVideoActivity(Context context, Intent intent) {
//        Intent videoIntent = new Intent(context, VideoActivity.class);
//        videoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//        videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        videoIntent.putExtra("peerid", String.valueOf(intent.getLongExtra("peerid", 0)));
//        videoIntent.putExtra("dinType", intent.getIntExtra("dinType", VideoController.UINTYPE_QQ));
//        context.startActivity(videoIntent);
    }

    public static void onReceiveVideoCall(Context context, String fromUin) {
//        Intent intent = new Intent(context, VideoActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("receive", true);
//        intent.putExtra("peerid", fromUin);
//        context.startActivity(intent);
    }

    /**
     * 启动用户信息页面
     *
     * @param context
     */
    public static void startUserInfoPage(Context context, ContactEntity entity) {
        UserInfoActivity.actionStart(context, entity);
    }

    /**
     * 拨打视频电话
     *
     * @param context
     * @param entity
     */
    public static void callAudio(Context context, ContactEntity entity) {
        if (NetUtils.isNetworkAvailable(context)) {
            if (false == AVChatManager.getInstance().getChatState()) {
  //              Toast.makeText(context, "正在启动", Toast.LENGTH_SHORT).show();
//                TXDeviceAVManager.startAudioChatActivity(entity.getUin(), entity.getContactType());
                AVChatManager.getInstance().startAudioVideoChat(entity.getUin());
            } else {
                Toast.makeText(context, "视频中", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "当前网络不可用，请连接网络", Toast.LENGTH_SHORT).show();
        }
    }

    public static void callVideo(Context context, ContactEntity entity) {
        if (NetUtils.isNetworkAvailable(context)) {
            if (false == AVChatManager.getInstance().getChatState()) {
 //               Toast.makeText(context, "正在启动", Toast.LENGTH_SHORT).show();
//                TXDeviceAVManager.startVideoChatActivity(entity.getUin(), entity.getContactType());
                AVChatManager.getInstance().startAudioVideoChat(entity.getUin());
            } else {
                Toast.makeText(context, "视频中", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "当前网络不可用，请连接网络", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param context
     * @return 隐私模式是否开启
     */
    public static boolean callVideoWithPrivacyInterupt(final Context context, final DialogInterface.OnClickListener cancelAction, final DialogInterface.OnClickListener confirmAction) {
        if (PrivacyManager.getInstance(context).isPrivacy()) {
            PrivacyManager.getInstance(context).showVoipPrivacyDialog(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cancelAction.onClick(dialog, which);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    PrivacyManager.getInstance(context).closePrivacy();
                    confirmAction.onClick(dialog, which);
                }
            });
            return true;
        }
        return false;
    }

    public static void callVideo(Context context, long uin) {
        ContactEntity entity = new ContactEntity();
        entity.setUin(uin);
        callVideo(context, entity);
    }

}
