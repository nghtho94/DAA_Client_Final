package com.example.tho.daa_moblie_client.Models.RequestModels.Init;

import java.io.Serializable;

/**
 * Created by tho on 1/15/17.
 */

public class Bean implements Serializable {

    public boolean isChecked;


    String name;
    String time;

    public Bean(String name, String time, boolean success){
        this.name = name;
        this.time = time;
        this.isChecked = success;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}
