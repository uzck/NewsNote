package com.example.nozoeli.newsnote.util;

import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * Created by nozoeli on 16-4-16.
 */
public class NetworkUtils {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager manager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();

            if (info != null) {
                return info.isAvailable();
            }
        }

        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager manager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();

            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                return info.isAvailable();
            }
        }

        return false;
    }

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 www.baidu.com");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
