package uk.ac.cam.cl.emule.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import uk.ac.cam.cl.emule.MainActivity;

/**
 * Created by fergus on 02/12/2016.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.i("NetworkChangeReceiver", "NCR "+ intent.getAction());
        MainActivity.getInstance().startStatusUpdate();

    }
}