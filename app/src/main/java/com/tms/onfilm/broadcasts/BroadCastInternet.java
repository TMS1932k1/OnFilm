package com.tms.onfilm.broadcasts;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.tms.onfilm.R;

public class BroadCastInternet extends BroadcastReceiver {
    private Dialog dialogNotInternet = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if(dialogNotInternet == null) {
                setDialogInternet(context);
            }

            if(isNetworkAvailable(context)) {
                dialogNotInternet.cancel();
                dialogNotInternet = null;
            } else {
                dialogNotInternet.show();
            }
        }
    }

    private void setDialogInternet(Context context) {
        dialogNotInternet = new Dialog(context);
        dialogNotInternet.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNotInternet.setContentView(R.layout.layout_not_connect_internet);

        Window window = dialogNotInternet.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialogNotInternet.setCancelable(false);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager == null) {
            return false;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if(network == null) {
                return false;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null
                    && (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));

        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null
                    && ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                    || networkInfo.isConnected());
        }
    }
}
