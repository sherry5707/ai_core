package kinstalk.com.qloveaicore;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAILog;


/**
 * Created by majorxia on 2017/6/26.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

/**
 * this class used to write the  bind state to the  shared preference, and if the bind  state
 * changes, send out a broadcast.
 */
public class DeviceBindStateController {
    private static final String TAG = "AI-BindStateCtl";
    private boolean mBindState = false;
    private static DeviceBindStateController sInst = null;
    private Context mContext;
    private final QBinderInfo mBinderInfo = new QBinderInfo();

    public static class QBinderInfo {
        public String headUrl;
        public int type;
        public String remark;
        public int contactType;
        private String sn;

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof QBinderInfo))
                return false;

            QBinderInfo that = (QBinderInfo) obj;
            return (TextUtils.equals(remark, that.remark)
                    && TextUtils.equals(headUrl, that.headUrl));
        }

        public void copyFrom(QBinderInfo that) {
            QAILog.d(TAG, "copyFrom: E");
            this.headUrl = that.headUrl;
            this.remark = that.remark;
            this.type = that.type;
            this.contactType = that.contactType;
        }

        public String toJsonString() {
            JSONObject o = new JSONObject();
            String r = "";
            try {
                o.put(AICoreDef.JSON_OWNERINFO_FIELD_URL, headUrl);
                o.put(AICoreDef.JSON_OWNERINFO_FIELD_REMARK, remark);
                o.put(AICoreDef.JSON_OWNERINFO_FIELD_TYPE, type);
                o.put(AICoreDef.JSON_OWNERINFO_FIELD_CONTACT_TYPE, contactType);
                o.put(AICoreDef.JSON_OWNERINFO_FIELD_SN, sn);
                r = o.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            QAILog.d(TAG, "toJsonString() called, " + r);
            return r;
        }

        @Override
        public String toString() {
            return "QBinderInfo{" +
                    "headUrl='" + headUrl + '\'' +
                    ", type=" + type +
                    ", remark='" + remark + '\'' +
                    ", contactType=" + contactType +
                    '}';
        }
    }

    public static synchronized DeviceBindStateController getInst(Context c) {
        if (sInst == null) {
            sInst = new DeviceBindStateController(c);
        }
        return sInst;
    }

    //since we need to read the state from sp, we should init the controller early
    private DeviceBindStateController(Context c) {
        mContext = c;
        mBindState = QAIConfigController.readBindState(mContext);
        mBinderInfo.copyFrom(QAIConfigController.readBinderInfo(mContext));
    }

    public synchronized void onBindStateChange(boolean bind) {
        QAILog.d(TAG, "onBindStateChange: new:" + bind + " mState:" + mBindState);
        if (bind != mBindState) {
            QAIConfigController.writeBindState(mContext, bind);
            mBindState = bind;
            sendBroadcast(bind);
        }
    }

    public synchronized void setSn(String sn) {
        QAILog.d(TAG, "setSn: E ");
        this.mBinderInfo.sn = sn;
    }

    public String getOwnerInfo() {
        QAILog.d(TAG, "getOwnerInfo: E");
        return mBinderInfo.toJsonString();
    }

    public void setQBinderInfo(QBinderInfo info) {
        QAILog.d(TAG, "setQBinderInfo() called with: info = [" + info + "]");
        if (info == null) {
            mBinderInfo.remark = "";
            mBinderInfo.headUrl = "";
            return;
        }
        if (!this.mBinderInfo.equals(info)) {
            this.mBinderInfo.copyFrom(info);
            QAIConfigController.WriteBinderInfo(mContext, mBinderInfo);
        }
    }

    private void sendBroadcast(boolean bind) {
        QAILog.d(TAG, "sendBroadcast() called with: bind = [" + bind + "]");
        String ACTION_TXSDK_BIND_STATE = AICoreDef.ACTION_TXSDK_BIND_CHANGE;
        Intent intent = new Intent();
        intent.setAction(ACTION_TXSDK_BIND_STATE);
        intent.putExtra(AICoreDef.EXTRA_AICORE_BIND_STATE, bind);
        mContext.sendStickyBroadcast(intent);
    }
}
