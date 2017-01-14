package com.example.tho.daa_moblie_client.Interfaces;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface onlineSendSig {

    @FormUrlEncoded
    @POST

    Call<String> sendsig(@Url String url, @Field("sig") String m, @Field("infomation") String info, @Field("status") String status);
    //Call<JoinData> join(@Url String url, @Field("sig") String m, @Field("infomation") String info, @Field("status") String status);

}
