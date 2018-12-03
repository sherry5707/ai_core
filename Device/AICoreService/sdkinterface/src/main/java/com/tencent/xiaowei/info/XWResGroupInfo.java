/*
 * Tencent is pleased to support the open source community by making  XiaoweiSDK Demo Codes available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.tencent.xiaowei.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

//import com.tencent.xiaowei.util.JsonUtil;

/**
 * 播放资源集合，一个资源可能是TTS(TEXT) + URL，TTS + TEXT, TEXT + TEXT等形式，但是UI上只展示URL那个资源信息
 */
public class XWResGroupInfo implements Parcelable {

    public XWResGroupInfo() {
    }

    /**
     * 一个集合包含的资源
     */
    public XWResourceInfo[] resources;

    protected XWResGroupInfo(Parcel in) {
        resources = in.createTypedArray(XWResourceInfo.CREATOR);
    }

    public static final Creator<XWResGroupInfo> CREATOR = new Creator<XWResGroupInfo>() {
        @Override
        public XWResGroupInfo createFromParcel(Parcel in) {
            return new XWResGroupInfo(in);
        }

        @Override
        public XWResGroupInfo[] newArray(int size) {
            return new XWResGroupInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(resources, flags);
    }

    @Override
    public String toString() {
        return "XWResGroupInfo{" +
                "resources=" + Arrays.toString(resources) +
                '}';
    }
}
