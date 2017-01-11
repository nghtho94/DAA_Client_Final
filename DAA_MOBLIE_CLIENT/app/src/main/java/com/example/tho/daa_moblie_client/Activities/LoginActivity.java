package com.example.tho.daa_moblie_client.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Interfaces.IdentityDownload;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_ISSUER;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

    //View
    CircularProgressButton circularProgressButton;
    EditText password;
    Singleton singleton = Singleton.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        circularProgressButton = (CircularProgressButton) findViewById(R.id.btnlogin);
        password = (EditText) findViewById(R.id.login_text_edit);
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

        if (json != null){
            if (pd == json) {
                return true;
            }else{
                return false;
            }
        }else{
            if (pd == "Bob"){
                return true;
            }else{
                return false;
            }
        }

    }

    public void prepareData(){
        String json = mPrefs.getString("AnoID","");

        if ( json == null){
            downloadIdentityData();
        }else{
            Gson gson = new Gson();
            IdentityData identityData = gson.fromJson(json, IdentityData.class);
            singleton.setIdentityData(identityData);
        }
    }

    public void downloadIdentityData(){
        Gson gson = new GsonBuilder()
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
                IdentityData identity_Data = response.body();
                //initData();
                Log.d("identity", identity_Data.getCredential_level_bank());
                singleton.setIdentityData(identity_Data);
                Log.d(TAG+"Ano", "Success");

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
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);

        //Close Activity
        finish();
    }
}
