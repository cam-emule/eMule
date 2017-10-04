package uk.ac.cam.cl.emule.network;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import uk.ac.cam.cl.emule.BuildConfig;
import uk.ac.cam.cl.emule.MainActivity;
import uk.ac.cam.cl.emule.R;
import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.models.BundleData;
import uk.ac.cam.cl.emule.util.LocationUtil;

import static android.util.Log.d;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 *
 * Contains the file transfer functions and the UI download progress holders.
 *
 * Fergus Leen fl376@cl.cam.ac.uk January 2017
 */

public class EmuleDownloadManager implements UploadStatusDelegate {


    private static final String USER_AGENT = "emuleApp/" + BuildConfig.VERSION_NAME;
    private static final String LOG_TAG = "EmuleDownloadManager";
    //this is the passed in view which will show the upload bars
    ViewGroup thecontainer;
    Activity mainActivity;
    EmuleService emuleService;
    String downloadUrl;
    private Map<String, UploadProgressViewHolder> uploadProgressHolders = new HashMap<>();

    public EmuleDownloadManager(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public void loadAccessPointData() {
        //Get the gateway...
        EmuleService emuleService = Injector.provideEmuleService("http://" + MainActivity.gatewayIP + ":3000");


        Call<List<AccessPoint>> jsonCall = emuleService.getAccessPoints();
        Log.i(LOG_TAG, String.valueOf(jsonCall.toString()));
        jsonCall.enqueue(new Callback<List<AccessPoint>>() {
            @Override
            public void onResponse(Call<List<AccessPoint>> call, Response<List<AccessPoint>> response) {
                String jsonString = response.raw().body().toString();
                List<AccessPoint> aplist = response.body();

                if (aplist!=null) {
                    MainActivity.getInstance().accessPointDataLoaded(updateAccessPointFiles(aplist));
                }else
                {
                    Timber.e("Unable to retrieve accesspoints!");
                }
            }

            @Override
            public void onFailure(Call<List<AccessPoint>> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
                t.printStackTrace();
                //MainActivity.getInstance().showDialog("Error", "Error in Access Point Data, Please Contact admin");
                //      ((StatusFragment)fragment).updateOnlineText(" \r No Remote AP Available");
            }
        });

    }

    private static List<AccessPoint> updateAccessPointFiles(List<AccessPoint> aplist) {
        //loadfiledata and link to accesspoints.
        AccessPoint ap;
        for (int i = 0; i < aplist.size(); i++) {
            ap = aplist.get(i);
            if (ap.getSubdomain() != null) {
                List<File> f = getAvailableFilesBySubstr(ap.getSubdomain());
                if (!f.isEmpty()) {
                    for (int x = 0; x < f.size(); x++) {
                        File file = f.get(x);
                        if (file.getName().contains("TO")) {
                            ap.setFileTo(file);
                        } else
                            ap.setFileFrom(file);

                    }
                }

            }
        }
        return aplist;
    }

    public static String getServiceUrl(String ipaddress) {
        return "http://" + ipaddress + ":3000";
    }

    /**
     * Method to call getBundleByName
     * bundleToDownload is null if this is a remote.
     */
    public void getDownloadURL(final String bundleToDownload, EmuleService emuleService) {
        Call<List<BundleData>> jsonCall;
        if (bundleToDownload == null) {
            jsonCall = emuleService.getRemoteBundle();
        } else {
            jsonCall = emuleService.getBundleByName(bundleToDownload);
        }
        Log.i(LOG_TAG, String.valueOf(jsonCall.toString()));
        jsonCall.enqueue(new Callback<List<BundleData>>() {
            @Override
            public void onResponse(Call<List<BundleData>> call, Response<List<BundleData>> response) {
                String jsonString = response.raw().body().toString();
                Log.i(LOG_TAG, jsonString);
                List<BundleData> data = response.body();
                if (!data.isEmpty()) {
                    BundleData bdata = data.get(0);
                    downloadUrl(bdata.getUrl());
                    Log.i(LOG_TAG, String.valueOf(data.toString()));
                } else {
                    //Nothing to do.
                    MainActivity.getInstance().showToast("No bundles available for " + (bundleToDownload == null ? "Remote" : bundleToDownload));
                }

            }

            @Override
            public void onFailure(Call<List<BundleData>> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
                MainActivity.getInstance().showToast("No bundles available " + (bundleToDownload == null ? "Remote" : bundleToDownload));
            }
        });
    }

    public void downloadUrl(String _downloadUrl) {

        this.downloadUrl = _downloadUrl;

        if (MainActivity.getInstance().checkPermission()) {
            performDownload();


        }
    }


    //Need to encode gps in filename to record place where picked up.
    public void performDownload() {
        //TODO: Delete previous downloads for this substr e.g. (TO_VAP/FROM_VAP)

        String url = downloadUrl;
        if (url.isEmpty()) {
            MainActivity.getInstance().showFluentSnackbar("URL is null");
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //This is tar.gz, strip it twice.
        String strippedFn = FilenameUtils.getBaseName(Uri.parse(url).getPath());
        String filename = FilenameUtils.getBaseName(strippedFn)+"_"+ LocationUtil.getCurrentGpsAsFileString()+"_.tar.gz";
        //request.setDescription(bdata.getVapname());
        request.setTitle(filename);
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        //request.setDestinationInExternalPublicDir("/eMule", FilenameUtils.getName(Uri.parse(url).getPath()));

        request.setDestinationUri(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/eMule/" + filename)));
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) MainActivity.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);




    }

    ////Upload stuff

    public static File[] getAvailableFiles() {
        String path = Environment.getExternalStorageDirectory().toString() + "/eMule";
        d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d("Files", "Size: " + files.length);
            for (File file : files) {
                d("Files", "FileName:" + file.getName());
            }
        }
        return files;
    }

    public static List<File> getAvailableFilesBySubstr(String substr) {
        File[] files = getAvailableFiles();
        List subFiles = new ArrayList();
        if ((files != null)) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(substr)) {
                    subFiles.add(files[i]);
                }
            }
        }
        return subFiles;
    }

    public void checkServiceUp(String ipaddress, final boolean gateway) {

        EmuleService service = Injector.provideCacheFreeEmuleService(getServiceUrl(ipaddress));
        Call<ResponseBody> callx;
        callx = service.checkServerUp();
        callx.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String data;
                try {
                    data = response.body().string();
                    Log.i(LOG_TAG, String.valueOf(data));
                    MainActivity.getInstance().statusUpdate(data, gateway);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
                MainActivity.getInstance().statusUpdate(MainActivity.NOT_AVAILABLE, gateway);
            }
        });

    }

    private UploadNotificationConfig getNotificationConfig(String filename) {

        return new UploadNotificationConfig()
                .setIcon(R.drawable.ic_upload)
                .setCompletedIcon(R.drawable.ic_upload_success)
                .setErrorIcon(R.drawable.ic_upload_error)
                .setTitle(filename)
                .setInProgressMessage(MainActivity.getInstance().getString(R.string.uploading))
                .setCompletedMessage(MainActivity.getInstance().getString(R.string.upload_success))
                .setErrorMessage(MainActivity.getInstance().getString(R.string.upload_error))
                .setAutoClearOnSuccess(true)
                .setClickIntent(new Intent(MainActivity.getInstance(), MainActivity.class))
                .setClearOnAction(true)
                .setRingToneEnabled(true);
    }

    public void onMultipartUploadClick(String filesToUploadString, String serverUrlString, ViewGroup view) {
        //Store user name here.
        //final String paramNameString = parameterName.getText().toString();
        final String[] filesToUploadArray = filesToUploadString.split(",");

        for (String fileToUploadPath : filesToUploadArray) {
            try {
                final String filename = getFilename(fileToUploadPath);

                MultipartUploadRequest req = new MultipartUploadRequest(MainActivity.getInstance(), serverUrlString)
                        .addFileToUpload(fileToUploadPath, filename)
                        .setNotificationConfig(getNotificationConfig(filename))
                        .setCustomUserAgent(USER_AGENT)
                        .setAutoDeleteFilesAfterSuccessfulUpload(true) //consider the lobster
                        .setUsesFixedLengthStreamingMode(true)
                        .setMaxRetries(3);

                //use utf8;
                req.setUtf8Charset();


                String uploadID = req.setDelegate(this).startUpload();
                thecontainer = view;
                addUploadToList(uploadID, filename);

                // these are the different exceptions that may be thrown
            } catch (FileNotFoundException exc) {
                MainActivity.getInstance().showToast(exc.getMessage());
            } catch (IllegalArgumentException exc) {
                MainActivity.getInstance().showToast("Missing some arguments. " + exc.getMessage());
            } catch (MalformedURLException exc) {
                MainActivity.getInstance().showToast(exc.getMessage());
            }
        }
    }




    /*
     * Upload Progress View Class and updaters
     */

    private String getFilename(String filepath) {
        if (filepath == null)
            return null;

        final String[] filepathParts = filepath.split("/");

        return filepathParts[filepathParts.length - 1];
    }

    private void addUploadToList(String uploadID, String filename) {
        View uploadProgressView = MainActivity.getInstance().getLayoutInflater().inflate(R.layout.view_upload_progress, null);
        UploadProgressViewHolder viewHolder = new UploadProgressViewHolder(uploadProgressView, filename);
        viewHolder.uploadId = uploadID;
        thecontainer.addView(viewHolder.itemView, 0);
        uploadProgressHolders.put(uploadID, viewHolder);
    }

    @Override
    public void onProgress(UploadInfo uploadInfo) {
        Log.i(TAG, String.format(Locale.getDefault(), "ID: %1$s (%2$d%%) at %3$.2f Kbit/s",
                uploadInfo.getUploadId(), uploadInfo.getProgressPercent(),
                uploadInfo.getUploadRate()));
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        uploadProgressHolders.get(uploadInfo.getUploadId())
                .progressBar.setProgress(uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(UploadInfo uploadInfo, Exception exception) {
        Log.e(TAG, "Error with ID: " + uploadInfo.getUploadId() + ": "
                + exception.getLocalizedMessage(), exception);
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        thecontainer.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
        uploadProgressHolders.remove(uploadInfo.getUploadId());
    }

    /**File Successfully Uploaded*/
    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
        Log.i(TAG, String.format(Locale.getDefault(),
                "ID %1$s: completed in %2$ds at %3$.2f Kbit/s. Response code: %4$d, body:[%5$s]",
                uploadInfo.getUploadId(), uploadInfo.getElapsedTime() / 1000,
                uploadInfo.getUploadRate(), serverResponse.getHttpCode(),
                serverResponse.getBodyAsString()));

        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());


        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        thecontainer.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
        uploadProgressHolders.remove(uploadInfo.getUploadId());
        MainActivity.getInstance().startStatusUpdate();
    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {
        Log.i(TAG, "Upload with ID " + uploadInfo.getUploadId() + " is cancelled");
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        thecontainer.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
        uploadProgressHolders.remove(uploadInfo.getUploadId());
    }

    private void logSuccessfullyUploadedFiles(List<String> files) {
        for (String file : files) {
            Log.e(TAG, "Success:" + file);
            MainActivity.statsManager.registerUpload(getFilename(file));
        }
        if (files.size() > 0) {
            MainActivity.getInstance().showDialog("Sync Complete", files.size() + " file(s) uploaded");
        }
    }

    private class UploadProgressViewHolder {
        View itemView;
        TextView uploadTitle;
        ProgressBar progressBar;

        String uploadId;

        UploadProgressViewHolder(View view, String filename) {
            itemView = view;
            uploadTitle = (TextView) view.findViewById(R.id.uploadTitle);
            progressBar = (ProgressBar) view.findViewById(R.id.uploadProgress);
            progressBar.setMax(100);
            progressBar.setProgress(0);
            final Button b = (Button) view.findViewById(R.id.cancelUploadButton);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (uploadId == null)
                        return;

                    UploadService.stopUpload(uploadId);
                }
            });

            uploadTitle.setText(MainActivity.getInstance().getString(R.string.upload_progress, filename));
        }


    }

}
