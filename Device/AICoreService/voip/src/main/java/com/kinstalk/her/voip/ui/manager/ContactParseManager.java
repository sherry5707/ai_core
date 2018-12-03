package com.kinstalk.her.voip.ui.manager;

import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
import com.tencent.xiaowei.info.XWContactInfo;

public class ContactParseManager {

    public static ContactEntity parseContactEntity(String peerId){
        ContactEntity entity = new ContactEntity();
        XWContactInfo info = XWDeviceBaseManager.getXWContactInfo(peerId);
        entity.setContactType(info.contactType);
        entity.setType(info.type);
        entity.setName(info.remark);
        entity.setIcon(info.headUrl);
        entity.setOnLine(info.online);
        entity.setUin(info.tinyID);
        return entity;
    }
}
