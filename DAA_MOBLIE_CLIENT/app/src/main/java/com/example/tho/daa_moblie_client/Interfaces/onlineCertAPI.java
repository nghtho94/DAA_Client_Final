package com.example.tho.daa_moblie_client.Interfaces;


import com.example.tho.daa_moblie_client.Models.ResponseData.onlineCertData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface onlineCertAPI {

    @GET
    Call<onlineCertData> getCert(@Url String url);
}
