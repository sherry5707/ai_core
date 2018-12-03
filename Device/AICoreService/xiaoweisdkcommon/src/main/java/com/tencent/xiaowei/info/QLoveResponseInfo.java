package com.tencent.xiaowei.info;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by majorxia on 2018/4/5.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class QLoveResponseInfo implements Parcelable {
    // 腾讯返回的response信息
    public XWResponseInfo xwResponseInfo;
    // 服务类型名称，定义在AICoreDef.QLServiceType
    public String qServiceType;
    // 是否是控制指令，如果为true，则ctrlCommandInfo里面有控制指令相关的信息
    public boolean isControlCmd = false;
    // 控制指令详细信息，只有当isControlCmd 为 true时才有效
    public QControlCmdInfo ctrlCommandInfo;

    public QLoveResponseInfo(XWResponseInfo xwResponseInfo, String qServiceType) {
        this.xwResponseInfo = xwResponseInfo;
        this.qServiceType = qServiceType;
        this.ctrlCommandInfo = new QControlCmdInfo();
    }

    public QLoveResponseInfo(Parcel in) {
        xwResponseInfo = in.readParcelable(XWResponseInfo.class.getClassLoader());
        qServiceType = in.readString();
        isControlCmd = in.readByte() != 0;
        ctrlCommandInfo = in.readParcelable(QControlCmdInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(xwResponseInfo, flags);
        dest.writeString(qServiceType);
        dest.writeByte((byte) (isControlCmd ? 1 : 0));
        dest.writeParcelable(ctrlCommandInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QLoveResponseInfo> CREATOR = new Creator<QLoveResponseInfo>() {
        @Override
        public QLoveResponseInfo createFromParcel(Parcel in) {
            return new QLoveResponseInfo(in);
        }

        @Override
        public QLoveResponseInfo[] newArray(int size) {
            return new QLoveResponseInfo[size];
        }
    };

    @Override
    public String toString() {
        return "QLoveResponseInfo{" +
                "xwResponseInfo=" + xwResponseInfo +
                ", qServiceType='" + qServiceType + '\'' +
                ", isControlCmd=" + isControlCmd +
                ", ctrlCommandInfo=" + ctrlCommandInfo +
                '}';
    }
}
