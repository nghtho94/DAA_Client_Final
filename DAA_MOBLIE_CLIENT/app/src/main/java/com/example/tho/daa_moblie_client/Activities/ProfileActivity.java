package com.example.tho.daa_moblie_client.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private final  String TAG = "ProfileActivity";

    TextView txtName, txtBD, txtBank, txtDriver;
    Singleton singleton = null;
    String name, birthday, account, driver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("User Infomation");

        singleton = Singleton.getInstance();

        try {
            getInfo(singleton.getIdentityData());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtName = (TextView) findViewById(R.id.txt_profile_name);
        txtBD = (TextView) findViewById(R.id.txt_profile_bd);
        txtBank = (TextView) findViewById(R.id.txt_profile_bank);
        txtDriver = (TextView) findViewById(R.id.txt_profile_driver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        txtName.setText(name);
        txtBD.setText(birthday);
        txtBank.setText(account);
        txtDriver.setText(driver);


    }

    public void getInfo(IdentityData identityData) throws JSONException {
        String jsonBank = identityData.getLevel_bank();
        String jsonPolice = identityData.getLevel_police();
        JSONObject json = new JSONObject(jsonBank);
        JSONObject json2 = new JSONObject(jsonPolice);

        name = json.getString("user_name");
        account = json.getString("user_account");
        birthday = json2.getString("user_job");
        driver = json2.getString("user_drive_expire");


    }
}
