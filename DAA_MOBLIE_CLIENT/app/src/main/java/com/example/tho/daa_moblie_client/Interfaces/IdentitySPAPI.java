package com.example.tho.daa_moblie_client.Interfaces;


import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentitySPData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IdentitySPAPI {

    @POST("app")
    @FormUrlEncoded
    Call<IdentitySPData> downloadFile(@Field("appId") Integer appID);
}
