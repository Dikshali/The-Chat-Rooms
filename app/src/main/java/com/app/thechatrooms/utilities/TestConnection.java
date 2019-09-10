package com.app.thechatrooms.utilities;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class TestConnection {
    public static boolean isConnected(Object systemService) {
        Log.d("demo", "START: isConnected");
        ConnectivityManager connectivityManager = (ConnectivityManager) systemService;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        Log.d("demo", "END: isConnected is " + true);
        return true;
    }
}

