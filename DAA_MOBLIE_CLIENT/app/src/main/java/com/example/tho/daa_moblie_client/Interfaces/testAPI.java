package com.example.tho.daa_moblie_client.Interfaces;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface testAPI {

    @FormUrlEncoded
    @POST
    Call<String> runpls(@Url String url, @Field("info") RequestBody info);
}
