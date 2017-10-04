package uk.ac.cam.cl.emule.util;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.google.gson.Gson;

import timber.log.Timber;
import uk.ac.cam.cl.emule.MainActivity;
import uk.ac.cam.cl.emule.models.Delivery;
import uk.ac.cam.cl.emule.models.UserProperties;

/**
 * Created by fergus on 17/01/2017.
 * <p>
 * This file will manage the calculations and storage of the UserProperties Class.
 * This will be a singleton with a reference in the MainActivity
 */

public class StatsManager {

    static UserProperties userProperties;

    static StatsManager instance;

    SharedPreferences mPrefs;

    private StatsManager() {
        //initialise UserPropertiesClass
        //At this point the user may not be logged in. No problem
        //Load from shareprefs
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance());

        Gson gson = new Gson();
        String json = mPrefs.getString("userprops", "");
        userProperties = gson.fromJson(json, UserProperties.class);
        if (userProperties == null) {
            userProperties = new UserProperties();
            writeUserProperties();
        }

    }

    public static StatsManager getInstance() {
        if (instance == null) {
            instance = new StatsManager();
        }
        return instance;
    }

    public UserProperties getUserProperties() {
        return userProperties;
    }

    private void writeUserProperties() {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userProperties);
        prefsEditor.putString("userprops", json);
        prefsEditor.commit();
    }


    /**
     * These two methods will update all stats.
     */
    public void incDownloads() {
        userProperties.setTotalDownloads(userProperties.getTotalDownloads() + 1);
        writeUserProperties();

    }

    /**
     * award bounty, update successRate,avdeliverytime,number of unique routes.
     *
     * @param file
     */
    public void registerUpload(String file) {
        userProperties.setSuccessfulTransfers(userProperties.getSuccessfulTransfers() + 1);
        try {
            Delivery d = new Delivery(file);

            userProperties.addDelivery(d);
        } catch (Exception e) {
            Timber.e("can't create delivery for " + file, e);
        }
        updateStats();
        awardBounty();
        writeUserProperties();
    }



    public void updateStats() {
        //calculate Successrate = downloads/success*100

        try {


            userProperties.setSuccessRate((userProperties.getSuccessfulTransfers() / userProperties.getTotalDownloads()) * 100.0);
            //average delivery time in seconds
            Long totalTime = 0l;
            for (Delivery d : userProperties.getDeliveries()) {
                totalTime = +d.getDeliveryTimestamp() - d.getPickupTimestamp();
            }
            userProperties.setAverageDeliveryTimeInSeconds((totalTime / userProperties.getDeliveries().size()));

        } catch (Exception e) {
            Timber.e("Unable to updateStats " + userProperties.toString(), e);
        }


    }

    public void awardBounty() {
        userProperties.setEarnings(userProperties.getEarnings() + 0.50d);
        writeUserProperties();
    }



}
