package com.example.tho.daa_moblie_client.Models.RequestModels.Init;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tho on 11/24/16.
 */

public class JoinData {


    //{"msg":"Authenticated","curveName":"TPM_ECC_BN_P256","nonce":"70572290917041865634075989195185358265178159492617859510970724954282644537888","status":"ok"}
    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("msg")
    @Expose
    String msg;

    @SerializedName("curveName")
    @Expose
    String curveName;

    @SerializedName("nonce")
    @Expose
    String nonce;

    @SerializedName("ipk")
    @Expose
    String ipk;

    public String getStatus() {
        return status;
    }

    public String getNonce() {
        return nonce;
    }

    public String getCurveName() {
        return curveName;
    }

    public String getMsg() {
        return msg;
    }

    public String getIpk() {
        return ipk;
    }
}
