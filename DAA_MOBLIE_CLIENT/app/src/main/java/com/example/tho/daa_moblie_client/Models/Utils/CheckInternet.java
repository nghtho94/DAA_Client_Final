package com.example.tho.daa_moblie_client.Models.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tho on 1/13/17.
 */

public class CheckInternet {
        public static boolean isConnected(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }


}
