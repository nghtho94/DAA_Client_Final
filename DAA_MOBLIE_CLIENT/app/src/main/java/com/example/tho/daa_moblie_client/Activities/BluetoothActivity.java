package com.example.tho.daa_moblie_client.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.iface.IDateDialogListener;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.avast.android.dialogs.iface.IMultiChoiceListDialogListener;
import com.avast.android.dialogs.iface.ISimpleDialogCancelListener;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.example.tho.daa_moblie_client.CheckBoxView.library.SmoothCheckBox;
import com.example.tho.daa_moblie_client.Controller.BluetoothService;
import com.example.tho.daa_moblie_client.Controller.Constants;
import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Interfaces.IdentityDownload;
import com.example.tho.daa_moblie_client.Models.DAA.Authenticator;
import com.example.tho.daa_moblie_client.Models.DAA.Issuer;
import com.example.tho.daa_moblie_client.Models.DAA.Verifier;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.Utils.Utils;
import com.example.tho.daa_moblie_client.Models.crypto.BNCurve;
import com.example.tho.daa_moblie_client.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tho.daa_moblie_client.Models.Utils.Config.URL_ISSUER;

public class BluetoothActivity extends AppCompatActivity implements
        ISimpleDialogListener,
        IDateDialogListener,
        ISimpleDialogCancelListener,
        IListDialogListener,
        IMultiChoiceListDialogListener {

    private static final String TAG = "BluetoothActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton, getData;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService mChatService = null;

    //
    Singleton singleton = Singleton.getInstance();
    BNCurve curve = null;
    IdentityData identityData = null;
    String TPM_ECC_BN_P256 = "TPM_ECC_BN_P256";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //Initdata

        curve = new BNCurve(BNCurve.BNCurveInstantiation.valueOf(TPM_ECC_BN_P256));
       // identityData = singleton.getAnonymousIdentity();


        mSendButton = (Button) findViewById(R.id.mSendButton);
        getData = (Button) findViewById(R.id.btnGetDataBlue);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        //TEST
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadIdentityData();
            }
        });


        final SmoothCheckBox scb = (SmoothCheckBox) findViewById(R.id.scb);

        scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                Log.d("SmoothCheckBox", String.valueOf(isChecked));

                Intent intent = new Intent(BluetoothActivity.this,QRScan.class);
                startActivity(intent);
                scb.setVisibility(View.INVISIBLE);
            }
        });


    }

    public void downloadIdentityData() {
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
                Log.d(TAG + "Ano", "Success");
                identityData = identity_Data;

            }

            @Override
            public void onFailure(Call<IdentityData> call, Throwable t) {
                Log.d(TAG, "onResponse" + t.getMessage());
            }
        });
    }




        @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }


    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
//        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
//
//        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
//                View view = getView();
//                if (null != view) {
//                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
//                    String message = textView.getText().toString();
//                    sendMessage(message);
//                }

                singleton.setSesssionID(Utils.createSessionID());
                Log.d(TAG, "sID" + singleton.getSesssionID());

                JSONObject jsonInput = new JSONObject();
                try {
                    jsonInput.put("state", "sessionID");
                    jsonInput.put("sessionID", singleton.getSesssionID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(BluetoothActivity.this, "Send", Toast.LENGTH_SHORT);
                Log.d("Data", jsonInput.toString());
                //sendMessage(jsonInput.toString());

                sendMessage("xxx");
            }
        });

        // Initialize the BluetoothService to perform bluetooth connections
        mChatService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };


    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {

        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {

        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            // mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d("message", readMessage);
                    Toast.makeText(BluetoothActivity.this, readMessage, Toast.LENGTH_LONG).show();
                    try {
                        messageHandle(readMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != BluetoothActivity.this) {

                        Toast.makeText(BluetoothActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                        //******************* TEST

                        String type = msg.getData().getString("secure");
                        if (type == "Secure") {
                            mChatService.tho();
                            String address = mConnectedDeviceName;
                            // Get the BluetoothDevice object
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                            // Attempt to connect to the device
                            mChatService.connect(device, false);
                        }
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != BluetoothActivity.this) {
                        Toast.makeText(BluetoothActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(BluetoothActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(BluetoothActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    public void messageHandle(String messageJSON) throws JSONException {


        JSONObject json = new JSONObject(messageJSON);
        String state = json.getString("state");

        switch (state) {
            case "permission":
                //Get Service sessionID
                String SPSig = json.getString("sig");
                String SPsessionID = json.getString("sessionID");
                String SPermission = json.getString("permission");

                Issuer.IssuerPublicKey ipk = new Issuer.IssuerPublicKey(curve, identityData.getIpk());

                //Verify
                boolean temp = false;
                try {
                    temp = verifyEcDaaSigWrt(ipk, SPSig, singleton.getSesssionID(), "permission", SPermission.getBytes(), singleton.getSesssionID().getBytes());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }



                if (temp == true) {

                    //get Info from permisson
                    Log.d("Verify", "OK");
                    String info = identityData.getLevel_bank();

                    //CreateSig
                    Authenticator.EcDaaSignature signature = createSig(info,
                            identityData.getCredential_level_bank(),
                            identityData.getGsk_level_bank(),
                            SPsessionID,
                            "verification", identityData.getIpk());

                    //Encode Sig
                    String sigString = Utils.bytesToHex(signature.encode(curve));
                    String s = "verification";


                    JSONObject jsonInput = new JSONObject();
                    try {
                        jsonInput.put("state", s);
                        jsonInput.put("info", identityData.getLevel_bank());
                        jsonInput.put("sig", sigString);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //send message
                    sendMessage(jsonInput.toString());

                } else {

                    Log.d("Verify", "Fail");
                    //If verify fail set sessionID = null
                    singleton.setSesssionID(null);
                    JSONObject jsonInput = new JSONObject();
                    try {
                        jsonInput.put("state", "CANCEL");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //send message
                    //sendMessage(jsonInput.toString());
                }

                break;
            case "CANCEL":
                singleton.setSesssionID(null);
                Toast.makeText(this, "Xác thực thất bại",
                        Toast.LENGTH_SHORT).show();
                break;
            case "SUCCESS":
                Toast.makeText(this, "Xác thực thành công",
                        Toast.LENGTH_SHORT).show();


        }

    }

    //VERRIFY
    private boolean verifyEcDaaSigWrt(Issuer.IssuerPublicKey pk, String sig, String message, String basename,
                                      byte[] info, byte[] session) throws NoSuchAlgorithmException {

        Verifier ver = new Verifier(curve);
        Authenticator.EcDaaSignature signature = new Authenticator.EcDaaSignature(
                Utils.hexStringToByteArray(sig), message.getBytes(), curve);
        //compare krd to session
        return ver.verifyWrt(info, session, signature, basename, pk, null);
    }


    //CREATE SIG
    private Authenticator.EcDaaSignature createSig(String info, String cre, String gsk, String sid, String basename, String ipkString) {
        try {
            Issuer.IssuerPublicKey ipk = new Issuer.IssuerPublicKey(curve, ipkString);
            Authenticator au = new Authenticator(curve, ipk, new BigInteger(gsk));
            Issuer.JoinMessage2 jm2 = new Issuer.JoinMessage2(curve, cre);
            au.setJoinState(Authenticator.JoinState.IN_PROGRESS);
            boolean x = au.EcDaaJoin2Wrt(jm2, info);

            if (x == true) {
                Log.d(TAG + "join", "Success");
            } else {
                Log.d(TAG + "join", "Fail");
            }
            Authenticator.EcDaaSignature sig = au.EcDaaSignWrt(info.getBytes(), basename, sid);

            return sig;
        } catch (NoSuchAlgorithmException ex) {


            return null;

        }

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
    }
}