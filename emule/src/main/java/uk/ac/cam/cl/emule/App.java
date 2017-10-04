package uk.ac.cam.cl.emule;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.UploadService;

import timber.log.Timber;


/**
 * TODO:
 * Upload service
 * Test activity
 */

public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Set your application namespace to avoid conflicts with other apps
        // using this library
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        // Set upload service debug log messages level
        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.i("Creating our Application");
    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

