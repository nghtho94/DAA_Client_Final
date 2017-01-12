package com.example.tho.daa_moblie_client.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tho.daa_moblie_client.Interfaces.IdentityDownload;
import com.example.tho.daa_moblie_client.Interfaces.IdentitySPAPI;
import com.example.tho.daa_moblie_client.Interfaces.onlineCertAPI;
import com.example.tho.daa_moblie_client.Models.DAA.Authenticator;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer.IssuerPublicKey;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer.JoinMessage1;
import com.example.tho.daa_moblie_client.Models.DAA.Verifier;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentitySPData;
import com.example.tho.daa_moblie_client.Models.ResponseData.onlineCertData;
import com.example.tho.daa_moblie_client.Models.Utils.Utils;
import com.example.tho.daa_moblie_client.Models.crypto.BNCurve;
import com.example.tho.daa_moblie_client.R;
import com.example.tho.daa_moblie_client.SQLite.SQLite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import info.hoang8f.widget.FButton;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_ISSUER;
import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_VERIFIER;
import static com.example.tho.daa_moblie_client.Models.Utils.Utils.hexStringToByteArray;

public class MainActivity extends AppCompatActivity {

    //Define
    public static final Integer QRActicity_REQUEST_CODE = 1;


    // Crypto
    private Authenticator authenticator;
    private SecureRandom random;
    private Verifier verifier;
    private BNCurve curve;
    private JoinMessage1 mgs1;
    private final String TPM_ECC_BN_P256 = "TPM_ECC_BN_P256";
    private String url;
    private IssuerPublicKey ipk;
    private Issuer.JoinMessage2 mgs2;
    private BigInteger gsk;
    private String Service_appID;

    private String nonceString;
    private SQLite db;
    IdentityData identity_Data;
    IdentitySPData identity_sp_data;
    String sessionUser, sessionSP;

    Authenticator.EcDaaSignature signatureSP = null;

    //Bluetooth



    //Views
    Button btnUser_b1, btnSPB1 , btnServiceCert;

    FButton btn_Authentication ;
    FancyButton btn_Profile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Views
        initView();

        random = new SecureRandom();


        //Create database
        db = new SQLite(this, "data.sqlite", null , 1);

        //Create database table

        db.QueryData(
                "CREATE TABLE IF NOT EXISTS info(id INTEGER PRIMARY KEY AUTOINCREMENT, user_name TEXT, user_job TEXT)"
        );

        db.QueryData(
                "CREATE TABLE IF NOT EXISTS basename(id INTEGER PRIMARY KEY AUTOINCREMENT, basename TEXT, gsk TEXT, jm2 TEXT, cert TEXT)"
        );

        db.QueryData("INSERT OR REPLACE INTO info VALUES(1,'Thanh Uyen', 'Manager')");


//        btnNonce.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getNonce();
//            }
//        });
//
//        btnVerify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifySign();
//            }
//        });
//
//        btnServiceCert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //serviceGetCert();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                            new getCS().execute("http://10.0.3.2:8080/verifier/verify");
//
//
//                    }
//                });
//
//            }
//        });

//        btnUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getIdentity();
//                //getSPIdentity();
//            }
//        });
//
//        btnSPB1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //signPermisstion();
//                tam();
//            }
//        });
//
//        btnUser_b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifyPermission();
//            }
//        });
         btn_Authentication.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                // getIdentity();
             }
         });

        btn_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  tam();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        // unregister the ACTION_FOUND receiver.
      //  unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void initView(){
        //View Init
        btn_Authentication = (FButton) findViewById(R.id.btn_authenticate);
        btn_Profile = (FancyButton) findViewById(R.id.btn_main_profile);
//        btnNonce = (Button) findViewById(R.id.btnGetNonce);
//        btnVerify = (Button) findViewById(R.id.btnVerify);
//        btnServiceCert = (Button) findViewById(R.id.btnSCert);
//        btnUser = (FButton) findViewById(R.id.btn_user_mode);
//        btnUser_b1 = (Button) findViewById(R.id.btn_user_ver);
//        btnSPB1 = (Button) findViewById(R.id.btn_service_sig);
       // sessionSP = Utils.hexStringToByteArray(Utils.createSessionID());
        sessionUser = Utils.createSessionID();
    }






    // NEW META

    public void getIdentity(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_ISSUER)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IdentityDownload service = retrofit.create(IdentityDownload.class);

        Call<IdentityData> call = service.downloadFile(6);

        call.enqueue(new Callback<IdentityData>() {
            @Override
            public void onResponse(Call<IdentityData> call, Response<IdentityData> response) {
                identity_Data = response.body();
                //initData();
                Log.d("identity", identity_Data.getCredential_level_bank());
                initData();
            }

            @Override
            public void onFailure(Call<IdentityData> call, Throwable t) {
                Log.d("Identity Data", "onResponse" + t.getMessage());
            }
        });


    }

    public void getSPIdentity(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IdentitySPAPI service = retrofit.create(IdentitySPAPI.class);

        Call<IdentitySPData> call = service.downloadFile(2);

        call.enqueue(new Callback<IdentitySPData>() {
            @Override
            public void onResponse(Call<IdentitySPData> call, Response<IdentitySPData> response) {
                identity_sp_data = response.body();
                Log.d("ServiceData", identity_sp_data.getPermission());
            }

            @Override
            public void onFailure(Call<IdentitySPData> call, Throwable t) {
                Log.d("Identity Data SP", "onResponse" + t.getMessage());
            }
        });


    }


    public void initData(){
        curve = new BNCurve(BNCurve.BNCurveInstantiation.valueOf(TPM_ECC_BN_P256));
        //curve = new BNCurve(BNCurve.BNCurveInstantiation.valueOf(identity_Data.getCurve()));
        ipk = new Issuer.IssuerPublicKey(curve, identity_Data.getIpk());

//        try {
//            authenticator = new Authenticator(curve,ipk,gsk);
//            Log.d(">>Authenticator>>>>>>>>",authenticator.toString());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        verifier = new Verifier(curve);

    }


    private Authenticator.EcDaaSignature createSig(String info, String cre, String gsk, String sid, String basename) {
        try {
            Authenticator au = new Authenticator(curve, ipk, new BigInteger(gsk));
            Issuer.JoinMessage2 jm2 = new Issuer.JoinMessage2(curve, cre);
            au.setJoinState(Authenticator.JoinState.IN_PROGRESS);
            boolean x = au.EcDaaJoin2Wrt(jm2,info);
            //boolean x = au.EcDaaJoin2Wrt(jm2, info);
            if (x == true) {
                Log.d("join", "Success" );
            }else{
                Log.d("join", "Fail" );
            }
            Authenticator.EcDaaSignature sig = au.EcDaaSignWrt(info.getBytes(), basename, sid);

            return sig;
        } catch (NoSuchAlgorithmException ex) {


            return null;

        }

    }


    public void tam(){
        final String sessionIDxx = Utils.createSessionID();
        Log.d("UserSS", sessionIDxx);
       // String path = "getCert/"+Service_appID+"/"+sessionID;
        String path = "getCert/7/"+sessionIDxx;

        Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_VERIFIER)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        onlineCertAPI service = retrofit.create(onlineCertAPI.class);

        Call<onlineCertData> call = service.getCert(path);

        call.enqueue(new Callback<onlineCertData>() {
            @Override
            public void onResponse(Call<onlineCertData> call, Response<onlineCertData> response) {
                onlineCertData data = response.body();

                Log.d("hahaha", data.getSig());
                Log.d("permission", data.getPermission());
                Log.d("status", data.getStatus());
                Log.d("sessionID", data.getSessionId());



                Log.d("sessionIDUser", sessionIDxx);


//                    boolean x = verifyEcDaaSigWrt(ipk,data.getSig(),sessionIDxx,"permission"
//                            ,data.getPermission().getBytes(),
//                            sessionIDxx.getBytes());
                    boolean x = VerifyUserInfo(sessionIDxx,data.getPermission(),data.getSig());

                    if (x == true) {
                        Log.d("verity at user", "Success" );
                    }else{
                        Log.d("verity at user", "Fail" );
                    }


            }

            @Override
            public void onFailure(Call<onlineCertData> call, Throwable t) {
                Log.d("onlineCert", "onResponse" + t.getMessage());
            }
        });


    }

    public Boolean VerifyUserInfo(String serviceSessionId, String info, String sigString) {
        Verifier verifier = new Verifier(curve);
        // byte[] message
        byte[] information = hexStringToByteArray(info);
        // byte[] session
        byte[] sessionId = hexStringToByteArray(serviceSessionId);
        // EcDaaSignature sig
        Authenticator.EcDaaSignature ecDaaSignature = new Authenticator.EcDaaSignature(hexStringToByteArray(sigString), sessionId, curve);
        // String appId
        String baseName = "permission";
        // IssuerPublicKey ipk

        Boolean resultVerify = false;
        try {
            resultVerify = verifier.verifyWrt(information, sessionId, ecDaaSignature, baseName, ipk, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("Result Verify: "+resultVerify);
        return resultVerify;
    }



    //user

    public void verifyPermission(){
        //verifier = new Verifier(curve);
        //Authenticator.EcDaaSignature sig = new Authenticator.EcDaaSignature(signatureSP.encode(curve),identity_sp_data.getPermission().getBytes(),curve);
        String sig_String = Utils.bytesToHex(signatureSP.encode(curve));
       // Log.d("sig", sig_String);
        Log.d("sessionUser", sessionUser);


        try {

           // boolean x = verifier.verifyWrt(identity_sp_data.getPermission().getBytes(),sessionUser.getBytes(),signatureSP,"permission",ipk,null);
            boolean x = verifyEcDaaSigWrt(ipk, sig_String, sessionUser, "permission", identity_sp_data.getPermission().getBytes(), sessionUser.getBytes());
            if (x == true) {
                Log.d("verity at user", "Success" );
            }else{
                Log.d("verity at user", "Fail" );
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private boolean verifyEcDaaSigWrt(IssuerPublicKey pk, String sig, String message, String basename,
                                      byte[] info, byte[] session) throws NoSuchAlgorithmException{

        Verifier ver  = new Verifier(curve);
        Authenticator.EcDaaSignature signature = new Authenticator.EcDaaSignature(
                hexStringToByteArray(sig), message.getBytes(), curve);
        //compare krd to session
        return ver.verifyWrt(info, session, signature, basename , pk, null);
    }


    //service
    public void signPermisstion(){
//        JSONObject jsonInput = null;
//        try {
//            //jsonInput = new JSONObject("{\"service_permission\":\"user_name,user_job\"}");
//            jsonInput = new JSONObject("user_name,user_job");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        Log.d("test", identity_sp_data.getPermission());
        Log.d("test", identity_sp_data.getCredential_permission());
        Log.d("test", identity_sp_data.getGsk_permission());

        sessionSP = Utils.createSessionID();
        signatureSP = createSig(identity_sp_data.getPermission(),
                identity_sp_data.getCredential_permission(),
                identity_sp_data.getGsk_permission(),
                sessionUser,
                "permission");
        Log.d("sessionUser", sessionUser);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == QRActicity_REQUEST_CODE  && resultCode  == RESULT_OK) {

                String requiredValue = Utils.handleQRContent(data.getStringExtra("QRContent"));
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }


}























