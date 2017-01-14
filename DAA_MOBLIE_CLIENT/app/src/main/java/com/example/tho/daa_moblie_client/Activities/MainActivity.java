package com.example.tho.daa_moblie_client.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Interfaces.IdentityDownload;
import com.example.tho.daa_moblie_client.Models.DAA.Authenticator;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer.IssuerPublicKey;
import com.example.tho.daa_moblie_client.Models.DAA.Verifier;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentitySPData;
import com.example.tho.daa_moblie_client.Models.Utils.Utils;
import com.example.tho.daa_moblie_client.Models.crypto.BNCurve;
import com.example.tho.daa_moblie_client.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import info.hoang8f.widget.FButton;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_ISSUER;
import static com.example.tho.daa_moblie_client.Models.Utils.Utils.hexStringToByteArray;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    //Define
    public static final Integer QRActicity_REQUEST_CODE = 1;

    private BNCurve curve;

    private final String TPM_ECC_BN_P256 = "TPM_ECC_BN_P256";

    private IssuerPublicKey ipk;


    IdentityData identity_Data;
    IdentitySPData identity_sp_data;
    String sessionUser, sessionSP;

    Authenticator.EcDaaSignature signatureSP = null;

    //Bluetooth



    //Views
    Button btnUser_b1, btnSPB1 , btnServiceCert;

    FButton btn_Authentication ;
    FancyButton btn_Profile;
    FancyButton btn_NewCre;
    FancyButton btn_Log;
    FancyButton btnLogout;
    TextView txtname;

    SharedPreferences mPrefs = null;
    Singleton singleton = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Views
        initView();

        mPrefs = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        singleton = Singleton.getInstance();
       // String a = mPrefs.getString("AnoID", "");

        identity_Data = singleton.getIdentityData();






    }

    @Override
    protected void onStart() {
        super.onStart();

        btn_Authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getIdentity();
                Intent i = new Intent(MainActivity.this, ModeActivity.class);
                startActivity(i);
            }
        });

        btn_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(i);
                initData();
               // tam();
            }
        });

        btn_NewCre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                downloadIdentityData();
            }
        });

        btn_Log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, LogActivity.class);
                startActivity(i);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final KProgressHUD progressLogout = KProgressHUD.create(MainActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Logging out");

                progressLogout.show();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 300);


            }
        });


        try {
            txtname.setText(getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() throws JSONException {

        String jsonBank = identity_Data.getLevel_bank();


            JSONObject json = new JSONObject(jsonBank);




        return json.getString("user_name");
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

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
        btn_NewCre = (FancyButton) findViewById(R.id.btn_newCre);
        btn_Log = (FancyButton) findViewById(R.id.btn_Log);
        btnLogout = (FancyButton) findViewById(R.id.btn_LogOut);
        txtname = (TextView) findViewById(R.id.txtName);

        sessionUser = Utils.createSessionID();
    }





    //New Cre

    public void downloadIdentityData(){
        //Show HUD

        final KProgressHUD progressHUD = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Renew Ano-Id")
                .setDetailsLabel("Processing");
        progressHUD.show();

        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_ISSUER)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IdentityDownload service = retrofit.create(IdentityDownload.class);

        Call<IdentityData> call = service.downloadFile(1);

        call.enqueue(new Callback<IdentityData>() {
            @Override
            public void onResponse(Call<IdentityData> call, Response<IdentityData> response) {
                IdentityData identity_Dataxxx = response.body();


                singleton.setIdentityData(identity_Dataxxx);
                Log.d(TAG+"Ano", "Success");

                Gson gson1 = new Gson();
                String json = gson1.toJson(identity_Dataxxx);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("AnoID",json).commit();
                identity_Data = identity_Dataxxx;

                progressHUD.dismiss();



            }

            @Override
            public void onFailure(Call<IdentityData> call, Throwable t) {
                Log.d(TAG, "onResponse" + t.getMessage());
                progressHUD.dismiss();
            }
        });



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
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        IdentitySPAPI service = retrofit.create(IdentitySPAPI.class);
//
//        Call<IdentitySPData> call = service.downloadFile(2);
//
//        call.enqueue(new Callback<IdentitySPData>() {
//            @Override
//            public void onResponse(Call<IdentitySPData> call, Response<IdentitySPData> response) {
//                identity_sp_data = response.body();
//                Log.d("ServiceData", identity_sp_data.getPermission());
//            }
//
//            @Override
//            public void onFailure(Call<IdentitySPData> call, Throwable t) {
//                Log.d("Identity Data SP", "onResponse" + t.getMessage());
//            }
//        });


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























