package com.tencent.xiaowei.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by majorxia on 2018/4/27.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

// 通用控制指令相关信息
public class QControlCmdInfo implements Parcelable {
    // 控制指令类型,定义在AICoreDef.AppControlCmd
    /**
     * CONTROL_CMD_RESUME
     * CONTROL_CMD_PAUSE
     * CONTROL_CMD_STOP
     * CONTROL_CMD_PREV
     * CONTROL_CMD_NEXT
     * CONTROL_CMD_RANDOM
     * CONTROL_CMD_ORDER
     * CONTROL_CMD_LOOP
     * CONTROL_CMD_SINGLE
     * CONTROL_CMD_REPEAT
     * CONTROL_CMD_SHARE
     */
    public int command;
    // 控制指令的文本,如"播放","暂停"等
    public String requestText;
    // 控制指令的 skill name
    public String skillName;
    // 控制指令的 skill id
    public String skillId;

    public QControlCmdInfo() {
    }

    protected QControlCmdInfo(Parcel in) {
        command = in.readInt();
        requestText = in.readString();
        skillName = in.readString();
        skillId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(command);
        dest.writeString(requestText);
        dest.writeString(skillName);
        dest.writeString(skillId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QControlCmdInfo> CREATOR = new Creator<QControlCmdInfo>() {
        @Override
        public QControlCmdInfo createFromParcel(Parcel in) {
            return new QControlCmdInfo(in);
        }

        @Override
        public QControlCmdInfo[] newArray(int size) {
            return new QControlCmdInfo[size];
        }
    };

    @Override
    public String toString() {
        return "QControlCmdInfo{" +
                "command=" + command +
                ", requestText='" + requestText + '\'' +
                ", skillName='" + skillName + '\'' +
                ", skillId='" + skillId + '\'' +
                '}';
    }
}
