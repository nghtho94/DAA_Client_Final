package com.example.tho.daa_moblie_client.Interfaces;


import com.example.tho.daa_moblie_client.Models.RequestModels.Init.JoinMessage2Data;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by tho on 11/25/16.
 */

public interface JoinMessage2API {

    @FormUrlEncoded
    @POST("jm1")
    Call<JoinMessage2Data> getJoinMessage2(@Field("jm1") String jm1, @Field("field") String userName);
}
