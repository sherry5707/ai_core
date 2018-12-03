package com.kinstalk.her.voip.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siqing on 17/6/8.
 */

public class RecordEntity {

    /**
     * 数据ID
     */
    private long id;

    /**
     * 好友ID
     */
    private String peerUid;

    /**
     * 好友名
     */
    private String peerName;

    /**
     * 呼出、呼入
     * {@link com.kinstalk.her.voip.model.db.RecordConstant}
     */
    private int callType;

    /**
     * 是否接通
     */
    private boolean isAccept;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 接听时间
     */
    private long acceptTime;

    /**
     * 结束时间
     */
    private long endTime;

    private int isRead;

    private int viewType;

    private List<RecordEntity> mergeRecords = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPeerUid() {
        return peerUid;
    }

    public void setPeerUid(String peerUid) {
        this.peerUid = peerUid;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int isAccept() {
        return isAccept ? 1 : 0;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(long acceptTime) {
        this.acceptTime = acceptTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int isRead() {
        return isRead;
    }

    public void setRead(int read) {
        isRead = read;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public List<RecordEntity> getMergeRecords() {
        return mergeRecords;
    }

    public void setMergeRecords(List<RecordEntity> mergeRecords) {
        this.mergeRecords = mergeRecords;
    }
}
