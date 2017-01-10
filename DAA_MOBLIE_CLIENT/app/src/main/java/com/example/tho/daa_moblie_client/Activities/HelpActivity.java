package com.example.tho.daa_moblie_client.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.tho.daa_moblie_client.Interfaces.JoinAPI;
import com.example.tho.daa_moblie_client.Interfaces.JoinMessage2API;
import com.example.tho.daa_moblie_client.Interfaces.ServiceCertAPI;
import com.example.tho.daa_moblie_client.Interfaces.SignAPI;
import com.example.tho.daa_moblie_client.Models.DAA.Authenticator;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.CertificateData;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.JoinData;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.JoinMessage2Data;
import com.example.tho.daa_moblie_client.Models.crypto.BNCurve;
import com.example.tho.daa_moblie_client.R;
import com.example.tho.daa_moblie_client.SQLite.SQLite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Utils.bytesToHex;

public class HelpActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private SecureRandom random;
    private BNCurve curve;
    private Issuer.JoinMessage1 mgs1;
    private final String TPM_ECC_BN_P256 = "TPM_ECC_BN_P256";
    private String url;
    private Issuer.IssuerPublicKey iPK;
    private Issuer.JoinMessage2 mgs2;
    private BigInteger newSK;
    private String nonceString;
    private SQLite db;
    IdentityData identity_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    //
    /** Called when the user clicks the Send button */
    public void goToQR(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, QRCodeActivity.class);
        startActivity(intent);
    }

    public void goToQRScan(View view) {
        Intent intent = new Intent(this, QRScan.class);
        startActivity(intent);
    }

    public void CreateAuthenticator(String nonce){
        try {
            authenticator = new Authenticator(curve, iPK);
            Log.d(">>Authenticator>>>>>>>>",authenticator.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //
        Log.d(">>Authenticator>>>>>>>>",authenticator.toString());


        sendJoinMessage1(nonce);

    }

    public void getNonce() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //APIClient service = retrofit.create(APIClient.class);
        JoinAPI service = retrofit.create(JoinAPI.class);

//        RequestBody m = RequestBody.create(MediaType.parse("text/plain"), "123456");
//        RequestBody appId = RequestBody.create(MediaType.parse("text/plain"), "1");



        Call<JoinData> JoinCall = service.join("LoveCat", 1);

        JoinCall.enqueue(new Callback<JoinData>() {
            @Override
            public void onResponse(Call<JoinData> call, Response<JoinData> response) {

                String status = response.body().getStatus();
                String msg = response.body().getMsg();

                if (status.equals("ok")) {

                    String nonce = response.body().getNonce();
                    nonceString = nonce;
                    Log.d("nonce", nonceString);
                    String ipkJson = response.body().getIpk();
                    iPK = new Issuer.IssuerPublicKey(curve, ipkJson);
                    CreateAuthenticator(nonce);


                } else {
                    Log.d("Get Nonce", "Get Nonce Error");
                }

            }

            @Override
            public void onFailure(Call<JoinData> call, Throwable t) {
                Log.d("Join Message Fail", "onResponse" + t.getMessage());
            }
        });
    }


    /**
     * Send JoinMessage1 to server
     * @param sNonce  BigInterger String
     */
    public void sendJoinMessage1(String sNonce){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        try {
            mgs1 = authenticator.EcDaaJoin1(new BigInteger(sNonce));
            Log.d("jsonMgs1", mgs1.toJson(curve));



        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        String x = mgs1.toJson(curve);
//        JSONObject obj = null;
//
//        try {
//            obj = new JSONObject(x);
//            Log.d("My App", obj.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        JoinMessage2API service = retrofit.create(JoinMessage2API.class);



        Call<JoinMessage2Data> JoinMessage2Call = service.getJoinMessage2(x, "user_name");

        JoinMessage2Call.enqueue(new Callback<JoinMessage2Data>() {
            @Override
            public void onResponse(Call<JoinMessage2Data> call, Response<JoinMessage2Data> response) {
                String status = response.body().getStatus();

                if (status.equals("ok")) {
                    String json = response.body().getJoinMessage2();

                    Log.d("msg2", json);

                    mgs2 = new Issuer.JoinMessage2(curve, json);

                    Log.d("jm2","  OK");

                }else {

                }

            }

            @Override
            public void onFailure(Call<JoinMessage2Data> call, Throwable t) {
                Log.d("Join Message 2 Fail", "onResponse" + t.getMessage());
            }
        });


    }


    public void verifySign() {

//        //Check authentication
//        newSK = this.curve.getRandomModOrder(random);
//        authenticator.setSk(newSK);
        String message = null;
        JSONObject jsonInput = null;
        try {
            jsonInput = new JSONObject("{\"user_name\":\"Thanh Uyen\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("ditconme", jsonInput.toString());



        try {
            authenticator.EcDaaJoin2(mgs2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Authenticator.EcDaaSignature signature = null;







//        try {
//            jsonInput = new JSONObject("{\"user_name\":\"\\\"Thanh Uyen\\\"\"}");
//            //jsonInput.put("user_name", "Thanh Uyen");
//            message = jsonInput.toString();
//            Log.d("message", message);
//        } catch (JSONException e) {
//            //some exception handler code.
//        }

        //jsonInput = new JSONObject();


//        String x = "{\"user_name\":\"\\\"Thanh Uyen\\\"\"}";
//        Log.d("test", x);
//        Log.d("message", message);

        try {
            signature = authenticator.EcDaaSign("user_name", jsonInput.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }




        //byte[] x = new TypedByteArray("application/octet-stream",  byteArray);

        byte signByteArray[] = signature.encode(curve);




        //String krdArrayString = bytesToHex(krd);
        String sigArrayString = bytesToHex(signByteArray);
        //String test = bytesToHex(krd);

        //Log.d("krd", krdArrayString);
        Log.d("signature", sigArrayString);
        Log.d("Nonce", nonceString);
        //Log.d("m", test);



//        RequestBody m = RequestBody.create(MediaType.parse("text/plain"), "Thanh Uyen");
//        RequestBody baseName = RequestBody.create(MediaType.parse("text/plain"), "Thanh Uyen");
//        RequestBody nonce = RequestBody.create(MediaType.parse("text/plain"), nonceString);
//
//        RequestBody signatureByte =  RequestBody.create(MediaType.parse("text/plain"),sigArrayString);


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();



        SignAPI service = retrofit.create(SignAPI.class);
        // Call<VerifyResponse> verifyCall = service.verify(sid,appId,krdByte,signatureByte);
        Call<CertificateData> CertCall = service.getCert(
                sigArrayString,
                nonceString,
                "user_name");

        CertCall.enqueue(new Callback<CertificateData>() {
            @Override
            public void onResponse(Call<CertificateData> call, Response<CertificateData> response) {
                String status = response.body().getStatus();
                String mgs = response.body().getMessage();
                Log.d("Status",status);
                Log.d("mgs", mgs);

                if (status.equals("ok")) {

                    Log.d("Verify", "OK");
                    String cert = response.body().getCert();
                    Log.d("Cert", cert);
                    //  String query = "INSERT OR REPLACE INTO basename(id,basename,gsk,jm2,cert) VALUES(1,'user_name','"+ mgs2.toString() +"','"
                    //         + authenticator.getSk().toString() +"'," +
                    //          " '"+ cert +"')";

                    //   db.QueryData(query);

                }else {
                    Log.d("verify", "Fail");
                }
            }

            @Override
            public void onFailure(Call<CertificateData> call, Throwable t) {
                Log.d("Receive Message", "onResponse" + t.getMessage());
            }
        });



    }

    public void serviceGetCert(){

        String url_verify = "http://10.0.3.2:8080/verifier/";

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_verify)
                .build();


//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url_verify)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();

        ServiceCertAPI service = retrofit.create(ServiceCertAPI.class);
        Call<RequestBody> CertCall = service.getCert();


        CertCall.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                Log.d("testService", response.body().toString());
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("Receive Message", "onResponse" + t.getMessage());
            }
        });





    }

    private class getCS  extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {

                URL u = new URL(params[0]);
                //Log.d("xxx", u.openConnection().getInputStream().toString());
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                InputStream is = urlConnection.getInputStream();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

                //String result = urlConnection.;
                Log.d("xxx", response);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
