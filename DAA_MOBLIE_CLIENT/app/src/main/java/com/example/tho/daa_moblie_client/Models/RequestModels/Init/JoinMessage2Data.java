package com.example.tho.daa_moblie_client.Models.RequestModels.Init;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tho on 11/25/16.
 */

public class JoinMessage2Data{

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("msg")
    @Expose
    String msg;

    @SerializedName("jm2")
    @Expose
    String joinMessage2;



    public String getStatus() {
            return status;
        }

    public String getJoinMessage2() {
        return joinMessage2;
    }

    public String getMsg() {
        return msg;
    }





    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
