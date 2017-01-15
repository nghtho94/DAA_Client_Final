package com.example.tho.daa_moblie_client.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.tho.daa_moblie_client.R;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRScan extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private final  String TAG = "QRSCan";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        mScannerView = new ZBarScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

    }

    public void qrScan(View view){

       // Start camera
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }




    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        // Do something with the result here
        Log.v(TAG, result.getContents()); // Prints scan results

        //Log.v(TAG, result.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result.getContents());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            String mode = null;
            try {
                mode = jsonObject.getString("mode");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, jsonObject.toString());


            switch (mode) {
                case "online":
                    String serviceName = null, appID = null;

                    try {
                        serviceName = jsonObject.getString("name");
                        appID = jsonObject.getString("appID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent i = new Intent(QRScan.this, OnlineActivity.class);
                    i.putExtra("name", serviceName);
                    i.putExtra("appID", appID);

                    startActivity(i);
                    finish();
                    break;

                case "offline":
                    String service_name = null;

                    try {
                        service_name = jsonObject.getString("name");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent resultIntent = getIntent();
                    resultIntent.putExtra("QRContent", service_name);
                    setResult(RESULT_OK, resultIntent);

                    finish();


                    break;
                default:
                    Log.d(TAG, "QRContent WRong format");
                    break;
            }
        }


//        //Send back data to parrent activity
//        Intent resultIntent = new Intent();
//        // TODO Add extras or a data URI to this intent as appropriate.
//        resultIntent.putExtra("QRContent", result.getText());
//        setResult(Activity.RESULT_OK, resultIntent);
//        finish();
    }
}
