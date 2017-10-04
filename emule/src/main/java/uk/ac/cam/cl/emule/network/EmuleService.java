package uk.ac.cam.cl.emule.network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.models.BundleData;

/**
 * This is the retrofit client interface for the emule-webservice
 *
 *
 */

public interface EmuleService {


    @GET("/")
    Call<ResponseBody> checkServerUp();

    @GET("/get-bundle-list")
    Call<List<BundleData>> getRemoteBundle();


    /**
     * This uses the interceptor for caching.
     *
     * @return
     */
    @GET("/accesspoints")
    Call<List<AccessPoint>> getAccessPoints();

    /**
     * The server will return a simple json list of ap strings to download
     *
     * @return
     */
    @GET("/get-available-bundles")
    Call<List<String>> getAvailableBundles();


    /**
     * The server preps the requested bundle, returns a URL.
     *
     * @return
     */
    @GET("/get-bundle-byname")
    Call<List<BundleData>> getBundleByName(@Query("apname") String apName);


    /**
     * Get User Properties.
     *
     */

    /**
     * Confirm a delivery
     * Delivery is confirmed on upload.
     * Username is passed through as a parameter.
     */

    /**
     * Notify of successful download
     * When a download has been received successfully.
     */

}
