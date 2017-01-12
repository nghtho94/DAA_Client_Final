package com.example.tho.daa_moblie_client.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tho.daa_moblie_client.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = "Setting";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //ActionBar.setTitle("adsa");
        setTitle(TAG);
    }
}
