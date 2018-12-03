/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.api.exception;

import android.util.AndroidException;

/**
 * Created by Knight.Xu on 2017/3/22.
 * Modified by Knight.Xu on 2018/1/18.
 */

public class ApiException extends AndroidException {
    public int code;
    public String message;

    public ApiException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
