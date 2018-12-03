package com.kinstalk.her.voip.activity.manager;

import android.content.Context;
import android.text.TextUtils;

import com.kinstalk.her.voip.model.db.RecordConstant;
import com.kinstalk.her.voip.model.db.RecordDbService;
import com.kinstalk.her.voip.model.entity.RecordEntity;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
import com.tencent.xiaowei.info.XWContactInfo;

public class TXRecordService {
    private Context mContext;
    private RecordDbService recordDbService;
    private RecordEntity mRecordEntity;
    private boolean isReceiver;
    private String peerId;
    private String selfDin;
    private String peerName = "";

    public TXRecordService(Context context) {
        this.mContext = context;
        this.recordDbService = new RecordDbService(mContext);
    }


    public void init(boolean isReceiver, String peerId, String selfDin){
        XWContactInfo info = XWDeviceBaseManager.getXWContactInfo(peerId);
        this.isReceiver = isReceiver;
        this.peerId = peerId;
        if (info != null && !TextUtils.isEmpty(info.remark)) {
            this.peerName = info.remark;
        }
    }

    public void initCreate(){
        mRecordEntity = new RecordEntity();
        mRecordEntity.setCreateTime(System.currentTimeMillis());
        mRecordEntity.setCallType(isReceiver ? RecordConstant.CALL_TYPE_IN : RecordConstant.CALL_TYPE_OUT);
        mRecordEntity.setPeerUid(peerId);
        mRecordEntity.setPeerName(peerName);
        recordDbService.insertRecord(mRecordEntity);
    }

    /**
     * 视频连通
     */
    public void onVideoConnected(){
        if (mRecordEntity == null) {
            initCreate();
        }
        mRecordEntity.setAccept(true);
        mRecordEntity.setAcceptTime(System.currentTimeMillis());
        recordDbService.updateRecord(mRecordEntity);
    }

    /**
     * 通话结束
     */
    public void onVideoEnd(){
        mRecordEntity.setEndTime(System.currentTimeMillis());
        recordDbService.updateRecord(mRecordEntity);
    }

    public void destory(){
        if (recordDbService != null) {
            recordDbService.destory();
        }
    }

}

