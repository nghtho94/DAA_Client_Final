package com.example.tho.daa_moblie_client.Models.RequestModels.Init;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IdentityData {



    @SerializedName("curve")
    @Expose
    String curve;

    @SerializedName("appId")
    @Expose
    String appId;

    @SerializedName("ipk")
    @Expose
    String ipk;

    //User name
    @SerializedName("level_service")
    @Expose
    String level_service;

    @SerializedName("epk_level_service")
    @Expose
    String epk_level_service;

    @SerializedName("esk_level_service")
    @Expose
    String esk_level_service;

    @SerializedName("gsk_level_service")
    @Expose
    String gsk_level_service;

    @SerializedName("credential_level_service")
    @Expose
    String credential_level_service;
//- Bank
    @SerializedName("level_bank")
    @Expose
    String level_bank;

    @SerializedName("epk_level_bank")
    @Expose
    String epk_level_bank;

    @SerializedName("esk_level_bank")
    @Expose
    String esk_level_bank;

    @SerializedName("gsk_level_bank")
    @Expose
    String gsk_level_bank;

    @SerializedName("credential_level_bank")
    @Expose
    String credential_level_bank;

    //Police
    @SerializedName("level_police")
    @Expose
    String level_police;

    @SerializedName("epk_level_police")
    @Expose
    String epk_level_police;

    @SerializedName("esk_level_police")
    @Expose
    String esk_level_police;

    @SerializedName("gsk_level_police")
    @Expose
    String gsk_level_police;

    @SerializedName("credential_level_police")
    @Expose
    String credential_level_police;

    public String getIpk() {
        return ipk;
    }

    public String getCurve() {
        return curve;
    }

    public String getAppId() {
        return appId;
    }

    public String getCredential_level_bank() {
        return credential_level_bank;
    }

    public String getCredential_level_police() {
        return credential_level_police;
    }

    public String getCredential_level_service() {
        return credential_level_service;
    }

    public String getEpk_level_bank() {
        return epk_level_bank;
    }

    public String getEpk_level_police() {
        return epk_level_police;
    }

    public String getEpk_level_service() {
        return epk_level_service;
    }

    public String getEsk_level_bank() {
        return esk_level_bank;
    }

    public String getEsk_level_police() {
        return esk_level_police;
    }

    public String getEsk_level_service() {
        return esk_level_service;
    }

    public String getGsk_level_bank() {
        return gsk_level_bank;
    }

    public String getGsk_level_police() {
        return gsk_level_police;
    }

    public String getGsk_level_service() {
        return gsk_level_service;
    }

    public String getLevel_bank() {
        return level_bank;
    }

    public String getLevel_police() {
        return level_police;
    }

    public String getLevel_service() {
        return level_service;
    }
}



