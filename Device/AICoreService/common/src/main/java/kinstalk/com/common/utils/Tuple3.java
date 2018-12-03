/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.common.utils;

/**
 * Created by majorxia on 2016/9/26.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */
public class Tuple3<F, S, T> {
    public final F first;
    public final S second;
    public final T third;

    public Tuple3(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
