package com.example.tho.daa_moblie_client.Interfaces;

import com.example.tho.daa_moblie_client.Models.RequestModels.Init.CertificateData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



public interface SignAPI {

    @FormUrlEncoded
    @POST("cert")
    Call<CertificateData> getCert(
                                  @Field("sig") String sigString,
                                  @Field("nonce") String nonce,
                                  @Field("basename") String baseName);
}
