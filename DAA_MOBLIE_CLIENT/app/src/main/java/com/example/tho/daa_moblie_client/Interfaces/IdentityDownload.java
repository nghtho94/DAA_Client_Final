package com.example.tho.daa_moblie_client.Interfaces;


import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IdentityDownload {

    @POST("app")
    @FormUrlEncoded
    Call<IdentityData> downloadFile(@Field("appId") Integer appID);
}
