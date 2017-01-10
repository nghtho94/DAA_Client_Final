package com.example.tho.daa_moblie_client.Interfaces;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by tho on 12/12/16.
 */

public interface ServiceCertAPI {

    @GET("verify")
    Call<RequestBody> getCert();
}
