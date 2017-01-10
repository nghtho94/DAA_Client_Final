package com.example.tho.daa_moblie_client.Models.RequestModels.Init;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class CertificateData {

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("msg")
    @Expose
    String message;

    @SerializedName("certificate")
    @Expose
    String cert;

    public String getMessage() {
        return message;
    }

    public String getCert() {
        return cert;
    }

    public String getStatus() {
        return status;
    }
}
