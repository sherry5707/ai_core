/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Knight.Xu on 2017/11/10.
 */

public class RequestDataResult implements Parcelable {

    private int code;
    private String voiceId;

    public RequestDataResult(Integer code, String voiceId) {
        this.code = code;
        this.voiceId = voiceId;
    }

    public RequestDataResult() {
    }

    protected RequestDataResult(Parcel in) {
        code = in.readInt();
        voiceId = in.readString();
    }

    public static final Creator<RequestDataResult> CREATOR = new Creator<RequestDataResult>() {
        @Override
        public RequestDataResult createFromParcel(Parcel in) {
            return new RequestDataResult(in);
        }

        @Override
        public RequestDataResult[] newArray(int size) {
            return new RequestDataResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(voiceId);
    }
}
