package com.example.tho.daa_moblie_client.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Interfaces.IdentityDownload;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.Utils.Config;
import com.example.tho.daa_moblie_client.Models.crypto.BNCurve;
import com.example.tho.daa_moblie_client.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_ISSUER;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    SharedPreferences mPrefs = null;

    //View
    CircularProgressButton circularProgressButton;
    EditText password;
  //  MaterialTextField textField;
    Singleton singleton = null;
    private KProgressHUD progressHUD;

    BNCurve curve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefs = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);

        singleton = Singleton.getInstance();
        final String TPM_ECC_BN_P256 = "TPM_ECC_BN_P256";
        curve = new BNCurve(BNCurve.BNCurveInstantiation.valueOf(TPM_ECC_BN_P256));
        singleton.setCurve(curve);

        circularProgressButton = (CircularProgressButton) findViewById(R.id.btnlogin);
        password = (EditText) findViewById(R.id.login_text_edit);
       // textField = (MaterialTextField) findViewById(R.id.login_text_edit1);


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
    protected void onStart() {
        super.onStart();

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


                    // NOTE: In the author's example, he uses an identifier
                    // called searchBar. If setting this code on your EditText
                    // then use v.getWindowToken() as a reference to your
                    // EditText is passed into this callback as a TextView

                    in.hideSoftInputFromWindow(password.getApplicationWindowToken()
                                    ,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    //userValidateEntry();
                    // Must return true here to consume event
                    return true;

                }
                return false;
            }
        });

        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass = password.getText().toString();


                if (checkPassword(pass) == true){

                    if (circularProgressButton.getProgress() == 100){
                        circularProgressButton.setProgress(0);
                    }else {
                        circularProgressButton.setIndeterminateProgressMode(true); // turn on indeterminate progress
                        circularProgressButton.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                circularProgressButton.setProgress(100);

                                //Prepare Data and go to Main screen;
                                prepareData();

                            }
                        }, 300);
                    }

                }else{

                    Toast.makeText(LoginActivity.this,"WRONG PASSWORD", Toast.LENGTH_SHORT).show();

                }



                // set progress to 100 or -1 to indicate complete or error state
                ; // set progress to 0 to switch back to normal state
            }
        });

    }

    public boolean checkPassword(String pd){
        String json = mPrefs.getString("password","");



        if (!json.equals("")){

            Log.d("test", json);
            if (pd == json) {
                return true;
            }else{
                return false;
            }
        }else{

            if (pd.equals("huutho")){

                return true;
            }else{
                return false;
            }
        }


    }

    public void prepareData(){
        final String json = mPrefs.getString("AnoID","");

        progressHUD = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Login")
                .setDetailsLabel("preparing Data");
        progressHUD.show();

        Handler handlerx = new Handler();
        handlerx.postDelayed(new Runnable() {
            @Override
            public void run() {

                if ( json.equals("")){
                    downloadIdentityData();
                }else{
                    Gson gson = new Gson();
                    IdentityData identityData = gson.fromJson(json, IdentityData.class);
                    singleton.setIdentityData(identityData);
                    gotoMainScreen();
                }

            }
        }, 300);



    }

    public void downloadIdentityData(){
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_ISSUER)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IdentityDownload service = retrofit.create(IdentityDownload.class);

        Call<IdentityData> call = service.downloadFile(Config.APP_ID);

        call.enqueue(new Callback<IdentityData>() {
            @Override
            public void onResponse(Call<IdentityData> call, Response<IdentityData> response) {
                IdentityData identity_Data = response.body();
                //initData();
                Log.d("identity", identity_Data.getCredential_level_bank());
                singleton.setIdentityData(identity_Data);
                Log.d(TAG+"Ano", "Success");

                Gson gson1 = new Gson();
                String json = gson1.toJson(identity_Data);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("AnoID",json).commit();


                //Go to main screen
                gotoMainScreen();
            }

            @Override
            public void onFailure(Call<IdentityData> call, Throwable t) {
                Log.d(TAG, "onResponse" + t.getMessage());
            }
        });



    }

    public void gotoMainScreen(){
        //
        Log.d(TAG, "gotoMain");
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);


        String a = mPrefs.getString("AnoID", "");

        if (a.equals("")){
            Log.d(TAG,"gg");
        }else{
            Log.d(TAG,"ok");
        }

        progressHUD.dismiss();
        //Close Activity
        finish();

    }
}
