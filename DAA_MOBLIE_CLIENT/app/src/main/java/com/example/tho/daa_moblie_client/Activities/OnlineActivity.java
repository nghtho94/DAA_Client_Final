package com.example.tho.daa_moblie_client.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IDateDialogListener;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.avast.android.dialogs.iface.IMultiChoiceListDialogListener;
import com.avast.android.dialogs.iface.ISimpleDialogCancelListener;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.example.tho.daa_moblie_client.CheckBoxView.library.SmoothCheckBox;
import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Interfaces.onlineCertAPI;
import com.example.tho.daa_moblie_client.Interfaces.testAPI;
import com.example.tho.daa_moblie_client.Models.DAA.Authenticator;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer;
import com.example.tho.daa_moblie_client.Models.DAA.Verifier;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.Bean;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.ResponseData.onlineCertData;
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
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_VERIFIER;
import static com.example.tho.daa_moblie_client.Models.Utils.Utils.hexStringToByteArray;

public class OnlineActivity extends AppCompatActivity implements
        ISimpleDialogListener,
        IDateDialogListener,
        ISimpleDialogCancelListener,
        IListDialogListener,
        IMultiChoiceListDialogListener {
    private final String TAG = "OnlineActivity";

    String serviceName = null;
    String appID = null;

    TextView txtName;
    SmoothCheckBox cb1, cb2, cb3, cb4;

    KProgressHUD hud;

    Singleton singleton;
    IdentityData identity_Data;
    BNCurve curve;
    Issuer.IssuerPublicKey ipk;

    SimpleDialogFragment simpleDialogFragment = null;
    SharedPreferences mPrefs = null;
    String userSSIDxx, servicessID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        setTitle("Authenticating");

        mPrefs = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        singleton = Singleton.getInstance();
        identity_Data = singleton.getIdentityData();
        curve = singleton.getCurve();
        ipk = new Issuer.IssuerPublicKey(curve, identity_Data.getIpk());

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            serviceName =(String) b.get("name");
            appID =(String) b.get("appID");

        }

        txtName = (TextView) findViewById(R.id.txt_online_name);
        cb1 = (SmoothCheckBox) findViewById(R.id.cb_s1);
        cb2 = (SmoothCheckBox) findViewById(R.id.cb_s2);
        cb3 = (SmoothCheckBox) findViewById(R.id.cb_s3);
        cb4 = (SmoothCheckBox) findViewById(R.id.cbs4);

        txtName.setText("Service name: "+serviceName);


        //Disable click
        cb1.setClickable(false);
        cb2.setClickable(false);
        cb3.setClickable(false);
        cb4.setClickable(false);

        tam();

    }

    @Override
    protected void onStart() {
        super.onStart();

        hud = KProgressHUD.create(OnlineActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Authenticating");

        hud.show();


    }

    public void tam(){
        final String sessionIDxx = Utils.createSessionID();
        Log.d("UserSesstionID", sessionIDxx);
        userSSIDxx = sessionIDxx;
        // String path = "getCert/"+Service_appID+"/"+sessionID;
        String path = "getCert/"+ appID +"/" +sessionIDxx;

        cb1.setChecked(true);

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

                Log.d("ServiceSIG", data.getSig());
                Log.d("permission", data.getPermission());
                Log.d("status", data.getStatus());
                Log.d("ssID Service", data.getSessionId());
                servicessID = data.getSessionId();


                boolean x = false;
                try {
                    x = verifyEcDaaSigWrt(ipk,data.getSig(),sessionIDxx,"permission"
                            ,data.getPermission().getBytes(),
                            sessionIDxx.getBytes());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


                if (x == true) {
                    Log.d("verity at user", "Success" );
//                    try {
//                        sendSig(data.getSessionId(), sessionIDxx);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    cb2.setChecked(true);

                    hud.dismiss();
                    simpleDialogFragment = (SimpleDialogFragment) SimpleDialogFragment.createBuilder(OnlineActivity.this, getSupportFragmentManager())
                            .setTitle("Notice")
                            .setMessage(serviceName + " would like to know \n - Name. \n - Job.")
                            .setPositiveButtonText("Accept")
                            .setNegativeButtonText("Deny")
                            .setCancelableOnTouchOutside(false)
                            .setRequestCode(4848)
                            .show();
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

    public void test(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cb3.setChecked(true);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cb4.setChecked(true);
                        hand();
                    }
                },1500);
            }
        },2000);
    }

    public void hand(){
        Date date = new Date();
        Bean bean = new Bean(serviceName, date.toString() ,true);
        singleton.addLog(bean);

        Gson gson1 = new Gson();
        String jsonzx = gson1.toJson(singleton.getmList());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("LogList",jsonzx).commit();
        Handler handler = new Handler();
        hud.dismiss();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDialogFragment.createBuilder(OnlineActivity.this, getSupportFragmentManager())
                        .setTitle("Notice")
                        .setMessage("Authentication Success")
                        .setPositiveButtonText("DONE")
                        .setCancelableOnTouchOutside(false)
                        .setRequestCode(9929)
                        .show();
            }
        },300);
    }

    public void sendSig(String sesssionid, String userSs) throws JSONException {
        String url = "verify/"+appID+"/" + userSs;
        Authenticator.EcDaaSignature signatureSPx = createSig(identity_Data.getLevel_service(),
                identity_Data.getCredential_level_service(),
                identity_Data.getGsk_level_service(),
                sesssionid,
                "verification");

        cb3.setChecked(true);
        Log.d("Sig" , Utils.bytesToHex(signatureSPx.encode(curve)));
        Log.d("infomation", identity_Data.getLevel_service());

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("information", identity_Data.getLevel_service());
        jsonObject.put("sig", Utils.bytesToHex(signatureSPx.encode(curve)));
        jsonObject.put("status", "ok");

        //Log.d("Khoa cc", jsonObject.toString());

        RequestBody m = RequestBody.create(MediaType.parse("text/plain"), jsonObject.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_VERIFIER)
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();

        testAPI service = retrofit.create(testAPI.class);
        Call<String> call = service.runpls(url, m);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG + "xx", response.body());
                if (response.body() == "success"){
                    cb4.setChecked(true);
                    Date date = new Date();
                    Bean bean = new Bean(serviceName, date.toString() ,true);
                    singleton.addLog(bean);

                    Gson gson1 = new Gson();
                    String jsonzx = gson1.toJson(singleton.getmList());
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("LogList",jsonzx).commit();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("onlineCert", "onResponse" + t.getMessage());
            }
        });
//        onlineSendSig service = retrofit.create(onlineSendSig.class);
//
//        Call<String> call = service.sendsig(url,Utils.bytesToHex(signatureSPx.encode(curve)),identity_Data.getLevel_service()
//                , "ok");
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//               // Log.d(TAG + "xx", response.body());
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                //Log.d("onlineCert", "onResponse" + t.getMessage());
//            }
//        });

    }


    //Dialog
    private static final int REQUEST_SIMPLE_DIALOG = 42;
    @Override
    public void onPositiveButtonClicked(int requestCode, Date date) {
        //Toast.makeText(this,"cc",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNegativeButtonClicked(int requestCode, Date date) {
        // Toast.makeText(this,"cc",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {

    }

    @Override
    public void onListItemsSelected(CharSequence[] values, int[] selectedPositions, int requestCode) {

    }

    @Override
    public void onCancelled(int requestCode) {

    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SIMPLE_DIALOG) {
            Toast.makeText(this, "Negative button clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {

        if (requestCode == REQUEST_SIMPLE_DIALOG) {
            Toast.makeText(this, "Positive button clicked", Toast.LENGTH_SHORT).show();
        }

        if( requestCode == 4848){
            hud.show();
            try {
                sendSig(servicessID,userSSIDxx);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //test();
        }

        if (requestCode == 9929){
            finish();
        }
    }

    private boolean verifyEcDaaSigWrt(Issuer.IssuerPublicKey pk, String sig, String message, String basename,
                                      byte[] info, byte[] session) throws NoSuchAlgorithmException{

        Verifier ver  = new Verifier(curve);
        Authenticator.EcDaaSignature signature = new Authenticator.EcDaaSignature(
                hexStringToByteArray(sig), message.getBytes(), curve);
        //compare krd to session
        return ver.verifyWrt(info, session, signature, basename , pk, null);
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

}
