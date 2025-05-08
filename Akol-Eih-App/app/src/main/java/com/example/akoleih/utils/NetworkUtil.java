package com.example.akoleih.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                boolean isConnected = capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                Log.d(TAG, "Network check: isConnected=" + isConnected);
                return isConnected;
            }
            Log.w(TAG, "ConnectivityManager is null");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to check network: " + e.getMessage());
            return false;
        }
    }
}