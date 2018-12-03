/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.api;

import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Knight.Xu on 2017/3/22.
 * Modified by Knight.Xu on 2018/1/18.
 */
public interface IApiService {

    @GET()
    Call<ResponseBody> engineVoiceCmd(@Url HttpUrl url);

    @GET()
    Call<ResponseBody> engineMusic(@Url HttpUrl url);

    @GET()
    Call<ResponseBody> engineUnAni(@Url HttpUrl url);

    @POST()
    Call<ResponseBody> engineApi(@Url HttpUrl url, @Body RequestBody body);

    @POST()
    Call<ResponseBody> listsApi(@Url HttpUrl url, @Body RequestBody body);
}
