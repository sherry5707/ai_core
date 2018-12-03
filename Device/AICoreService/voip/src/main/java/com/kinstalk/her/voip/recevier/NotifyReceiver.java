package com.kinstalk.her.voip.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.voip.ui.utils.VoipUtils;


//import static com.tencent.aiaudio.CommonDef.ACTION_ON_FRIEND_LIST_CHANGE;
//import static com.tencent.aiaudio.CommonDef.ACTION_ON_RECEIVE_MSG;
//import static com.tencent.aiaudio.CommonDef.ACTION_ON_RECEIVE_REQUEST_ADD_FRIEND;

public class NotifyReceiver extends BroadcastReceiver {
    public static final String ACTION_ON_CLOSE_CHAT_ACTIVITY = "ACTION_ON_CLOSE_CHAT_ACTIVITY";

    private NotifyDelegate notifyDelegate;

    public interface NotifyDelegate {
        void binderUserChanged();
        void loginOut();
    }

    public NotifyDelegate getNotifyDelegate() {
        return notifyDelegate;
    }

    public void setNotifyDelegate(NotifyDelegate notifyDelegate) {
        this.notifyDelegate = notifyDelegate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(VoipUtils.ACTION_ON_BINDER_LIST_CHANGE)) {
            if (getNotifyDelegate() != null) {
                getNotifyDelegate().binderUserChanged();
            }
            //      } else if (action.equals(ACTION_ON_FRIEND_LIST_CHANGE)) {
            //         if (getNotifyDelegate() != null) {
            //              getNotifyDelegate().binderUserChanged();
        }
 /*       } else if (action.equals(ACTION_ON_ERASE_ALL_BINDERS)) {
            if (getNotifyDelegate() != null) {
                getNotifyDelegate().loginOut();
            }
        } else if (action.equals(ACTION_ON_RECEIVE_REQUEST_ADD_FRIEND)) {
//                TXDeviceFriendInfo contact = intent.getParcelableExtra("contact");
//                String strValidationMsg = intent.getStringExtra("ValidationMsg");
//                long socialNumber = intent.getLongExtra("SocialNumber", 0);
//                synchronized (mNewFriendReqList) {
//                    if (mNewFriendReqList.contains(contact.uin)) {
//                        //同一个好友的连续多次请求只处理一次
//                    } else {
//                        mNewFriendReqList.add(contact.uin);
//                        showNewFriendReqFloatWin(contact, strValidationMsg, socialNumber);
//                    }
//                }
        }else if (action.equals(ACTION_ON_RECEIVE_MSG)) {
//                TXMessageInfo msg = (TXMessageInfo) intent.getSerializableExtra("msg");
//                if (mRedPointNumMap.containsKey(Long.valueOf(msg.sender))) {
//                    mRedPointNumMap.put(Long.valueOf(msg.sender), mRedPointNumMap.get(Long.valueOf(msg.sender)) + 1);
//                } else {
//                    mRedPointNumMap.put(Long.valueOf(msg.sender), 1);
//                }
//                mAdapter.notifyDataSetChanged();
//                mDeviceFriendAdapter.notifyDataSetChanged();
        } else if (action.equals(ACTION_ON_CLOSE_CHAT_ACTIVITY)) {
//                long peerId = intent.getLongExtra("peerId", 0);
//                mRedPointNumMap.remove(peerId);
//                mAdapter.notifyDataSetChanged();
//                mDeviceFriendAdapter.notifyDataSetChanged();
        }
*/

    }

}