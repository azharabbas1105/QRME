package com.ingentive.qrme.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

/**
 * Created by PC on 30-05-2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private static final String LOG_TAG = "NetworkChangeReceiver";
    public static boolean isConnected=false;
    private int conn = 0;
    @Override
    public void onReceive(final Context context, final Intent intent) {

        getConnectivityStatus(context);
//        if (isOnline() == true && conn == TYPE_MOBILE || conn == TYPE_WIFI) {
//            isConnected=true;
//        }else {
//            isConnected=false;
//        }
        Log.v(LOG_TAG, "Receieved notification about network status  "+isConnected);
    }
    public static int getConnectivityStatus(Context context) {
//        if (isOnline() == true) {
//            isConnected=true;
//        }else {
//            isConnected=false;
//        }

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                isConnected=true;
                return TYPE_WIFI;
            }
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                isConnected=true;
                return TYPE_MOBILE;
            }
        }
        isConnected=false;
        return TYPE_NOT_CONNECTED;
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); return false; }
        catch (InterruptedException e) { e.printStackTrace(); return false; }
    }

}
