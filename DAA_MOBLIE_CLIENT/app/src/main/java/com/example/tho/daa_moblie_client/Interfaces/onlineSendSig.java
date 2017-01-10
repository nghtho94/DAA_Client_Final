package com.example.tho.daa_moblie_client.Interfaces;


import com.example.tho.daa_moblie_client.Models.RequestModels.Init.JoinData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface onlineSendSig {

    @FormUrlEncoded
    @POST("")
    Call<JoinData> join(@Field("sig") String m, @Field("infomation") Integer appId);

}
