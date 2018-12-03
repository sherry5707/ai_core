/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.api.exception;

/**
 * Created by Knight.Xu on 2018/1/18.
 */

public interface ErrorDetails {
    String RESPONSE_CODE = "rsp_code_";
    String RESPONSE_EMPTY = "rsp_empty";
    String RESPONSE_BODY_EMPTY = "rsp_body_empty";
    String RSP_EMPTY_CONTENT_ENCODING = "rsp_empty_content_encoding";
    String SRV_CODE = "srv_code_";
    String CONTENT_LENGTH_0 = "content_length_0";
    String ACCESS_KEY_EMPTY = "access_key_empty";
}
