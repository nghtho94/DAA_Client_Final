package com.example.tho.daa_moblie_client.Interfaces;

import com.example.tho.daa_moblie_client.Models.RequestModels.service.LoginData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by tho on 12/11/16.
 */

public interface ServiceLoginAPI {

    @POST("login")
    @FormUrlEncoded
    Call<LoginData> getNonce(@Field("app_id") Integer app_id, @Field("m") String m);
}
