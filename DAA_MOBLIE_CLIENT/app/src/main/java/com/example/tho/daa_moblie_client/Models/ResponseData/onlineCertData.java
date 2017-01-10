package com.example.tho.daa_moblie_client.Models.ResponseData;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class onlineCertData {
    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("permission")
    @Expose
    String permission;

    @SerializedName("sig")
    @Expose
    String sig;

    @SerializedName("sessionId")
    @Expose
    String sessionId;


    public String getPermission() {
        return permission;
    }

    public String getSig() {
        return sig;
    }

    public String getStatus() {
        return status;


    }

    public String getSessionId() {
        return sessionId;
    }
}
