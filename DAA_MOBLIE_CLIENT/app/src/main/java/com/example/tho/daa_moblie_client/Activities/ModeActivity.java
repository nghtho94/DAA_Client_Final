package com.example.tho.daa_moblie_client.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tho.daa_moblie_client.Models.Utils.CheckInternet;
import com.example.tho.daa_moblie_client.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import mehdi.sakout.fancybuttons.FancyButton;

public class ModeActivity extends AppCompatActivity {
    public final String TAG = "Mode";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    FancyButton btnOnline , btnOffline;
    private KProgressHUD progressHUD;

    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        //InitView
        initView();
        //Set Title
        setTitle(TAG);

        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online();
            }
        });

        btnOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offline();
            }
        });
    }

    public void initView(){
        btnOffline = (FancyButton) findViewById(R.id.btn_mode_offline);
        btnOnline = (FancyButton) findViewById(R.id.btn_mode_online);
    }

    public void online(){


        if (CheckInternet.isConnected(ModeActivity.this) == true){
            //go to QRScan

        }else{
            Toast.makeText(ModeActivity.this, "Wifi not connected. Try again.", Toast.LENGTH_LONG).show();
        }
    }

    public void offline(){
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            }
        }






    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();

                }
        }
    }

    private void scheduleDismiss() {

        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Downloading data");
        progressHUD.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressHUD.dismiss();

            }
        }, 2000);
    }


}
