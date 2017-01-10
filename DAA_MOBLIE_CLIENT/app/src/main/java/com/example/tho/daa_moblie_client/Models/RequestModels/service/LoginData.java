package com.example.tho.daa_moblie_client.Models.RequestModels.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tho on 12/11/16.
 */

public class LoginData {

    @SerializedName("nonce number")
    @Expose
    String nonce;

    public String getNonce() {
        return nonce;
    }
}
