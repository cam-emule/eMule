package uk.ac.cam.cl.emule;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.network.EmuleDownloadManager;
import uk.ac.cam.cl.emule.network.EmuleService;
import uk.ac.cam.cl.emule.network.Injector;
import uk.ac.cam.cl.emule.util.LocationUtil;
import uk.ac.cam.cl.emule.util.StatsManager;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static android.util.Log.d;
import static uk.ac.cam.cl.emule.AccessPointFragment.LOG_TAG;
import static uk.ac.cam.cl.emule.network.EmuleDownloadManager.getAvailableFilesBySubstr;


/**
 * The Status Fragment is the 'Home page' of the application
 * It presents status data and calls all sync operations
 */
public class StatusFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "StatusFragment";
    private static final int FILE_CODE = 1;
    TextView mRemoteView;
    TextView mGatewayView;
    TextView mFileView;
    TextView mFileView2;
    TextView mEarnings;
    Button syncButton;
    Button syncRemoteButton;
    ViewGroup thecontainer;
    View mainView;
    EmuleService emuleService;
    List<String> availableBundles;
    String syncDialogText;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainView = inflater.inflate(uk.ac.cam.cl.emule.R.layout.status_fragment, container, false);
        mRemoteView = (TextView) mainView.findViewById(R.id.remoteview);
        mGatewayView = (TextView) mainView.findViewById(R.id.gatewayview);
        mFileView = (TextView) mainView.findViewById(R.id.file_view);
        mFileView2 = (TextView) mainView.findViewById(R.id.file_view2);
        mEarnings = (TextView) mainView.findViewById(R.id.earnings);
        syncRemoteButton = (Button) mainView.findViewById(R.id.button_syncremote);
        syncButton = (Button) mainView.findViewById(R.id.sync_button);
        thecontainer = (ViewGroup) mainView.findViewById(R.id.container);
        addListeners();
        mEarnings.setText("Earnings : " + StatsManager.getInstance().getUserProperties().getEarnings());

        return mainView;
    }

    private void openList() {
        FragmentManager fragmentManager = MainActivity.getInstance().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new AccessPointFragment())
                .addToBackStack(null).commit();

    }

    public void addListeners() {


        mFileView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openList();

            }

        });
        mFileView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                openList();

            }

        });
        mRemoteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                openList();

            }

        });

        syncRemoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                syncRemote();

            }

        });

        syncButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                syncGateway();
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateOnlineText("");
        MainActivity.getInstance().startStatusUpdate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateOnlineText(String text) {
        Log.i("StatusFragment", "Updating");
        String remtext = MainActivity.remoteStatus;
        if (mRemoteView != null) {
            //Search for remote in aplist.
            AccessPoint ap = MainActivity.getInstance().getApBySubdomain(remtext);
            if (ap != null) {
                remtext = ap.getName() + " (" + remtext + ")";
                mRemoteView.setText(Html.fromHtml("Connected to: <b>" + remtext + "</b>"));
            } else

                mRemoteView.setText(Html.fromHtml("Remote Village: <b>" + remtext + "</b>"));
        }
        if (mGatewayView != null) {

            mGatewayView.setText(Html.fromHtml("Internet: " +
                    (MainActivity.gatewayStatus.compareTo(MainActivity.NOT_AVAILABLE) == 0 ?
                            "Offline"
                            :
                            "<b>Online</b>")
            ));
        }

        updateButtonVisibility();
        mainView.invalidate();
        updateFileInfo();


    }

    public void updateButtonVisibility() {
        if (MainActivity.gatewayStatus.compareTo(MainActivity.NOT_AVAILABLE) == 0) {
            syncButton.setEnabled(false);
            syncButton.setBackgroundColor(Color.GRAY);
        } else {
            syncButton.setEnabled(true);
            syncButton.setBackgroundColor(Color.GREEN);
        }
        if (MainActivity.remoteStatus.compareTo(MainActivity.NOT_AVAILABLE) == 0) {
            syncRemoteButton.setEnabled(false);
            syncRemoteButton.setBackgroundColor(Color.GRAY);
        } else {
            syncRemoteButton.setBackgroundColor(Color.GREEN);
            syncRemoteButton.setEnabled(true);

        }
    }

    public void updateFileInfo() {
        String filetextFrom = "No bundles for the Internet";
        String filetextTo = "No bundles for remote villages";
        List<File> files = getAvailableFilesBySubstr("FROM");
        if (!files.isEmpty()) {
            filetextFrom = "<br/><b>Files for Internet</b><br/>";
            for (int i = 0; i < files.size(); i++) {
                filetextFrom += formatFileString(files.get(i));
            }

        }
        files = getAvailableFilesBySubstr("TO");
        if (!files.isEmpty()) {
            filetextTo = "<br/><b>Bundles for Remote Villages</b><br/>";
            for (int i = 0; i < files.size(); i++) {
                filetextTo += formatFileString(files.get(i));
            }

        }
        mFileView.setText(Html.fromHtml(filetextFrom));
        mFileView2.setText(Html.fromHtml(filetextTo));

    }



    public CharSequence formatFileString(File file) {
        int textSize1 = getResources().getDimensionPixelSize(R.dimen.text_size1);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen.text_size2);

        AccessPoint ap = MainActivity.getInstance().getApByFilename(file.getName());
        String[] fileArr = file.getName().split("_");

        SpannableString span1 = new SpannableString(
                LocationUtil.getDateCurrentTimeZone(Long.parseLong(fileArr[2])) + " " + (ap != null ? ap.getName() : file.getName()) + " ("
                        + (Formatter.formatFileSize(MainActivity.getInstance(), file.length())) + ")<br/>"
        );
        span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, span1.length(), SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(file.getName());
        span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, file.getName().length(), SPAN_INCLUSIVE_INCLUSIVE);

    // let's put both spans together with a separator and all // Font size not working.
        return  TextUtils.concat(span1);

    }

    public String getDateFromFilename(String filename) {
        String date = "";
        String[] fileArr = filename.split("_");
        try {
            date = DateFormat.getDateTimeInstance().format(LocationUtil.getDateCurrentTimeZone(Long.parseLong(fileArr[2])));

        } catch (Exception e) {
            Timber.e(e, "unable to parse filename " + filename + Arrays.toString(fileArr));
        }
        return date;
    }


    /**
     * Main Gateway sync method.
     * <p>
     * USer needs to be logged in to sync gateway.
     */
    public void syncGateway() {
        //Check if logged in.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            //we are not logged in. Require login to sync with the gateway transfer to profile screen
            MainActivity.getInstance().openFragment(ProfileFragment.class);


        }else {
            syncDialogText = "";
            emuleService = Injector.provideCacheFreeEmuleService(EmuleDownloadManager.getServiceUrl(MainActivity.gatewayIP));
            //Do we have files to upload?
            List filesToUpload = EmuleDownloadManager.getAvailableFilesBySubstr("FROM");


            uploadFiles(filesToUpload, EmuleDownloadManager.getServiceUrl(MainActivity.gatewayIP) + "/upload/multipart");
            //Show download screen.
            showDownloads();
        }

    }

    public void syncRemote() {
        syncDialogText = "";
        emuleService = Injector.provideCacheFreeEmuleService(EmuleDownloadManager.getServiceUrl(MainActivity.remoteIP));
        //TODO: We should only send the file for this subdomain.
        //Get this from status
        List filesToUpload = EmuleDownloadManager.getAvailableFilesBySubstr("TO_" + MainActivity.remoteStatus);
        uploadFiles(filesToUpload, EmuleDownloadManager.getServiceUrl(MainActivity.remoteIP) + "/upload/multipart");
        MainActivity.getInstance().downloadManager.getDownloadURL(null, emuleService);
    }

    private void uploadFiles(List filesToUpload, String serverUrl) {
        //Do we have files to upload?
        String filesToUploadString = "";
        if (!filesToUpload.isEmpty()) {
            for (int i = 0; i < filesToUpload.size(); i++) {
                d(LOG_TAG, "Uploading to " + serverUrl + " file " + ((File) filesToUpload.get(i)).getName());
                filesToUploadString += ((File) filesToUpload.get(i)).getAbsolutePath() + ",";
            }
            String s = filesToUploadString.substring(0, filesToUploadString.length() - 1);
            d(LOG_TAG, s);
            MainActivity.getInstance().downloadManager.onMultipartUploadClick(s, serverUrl, thecontainer);

        } else {
            MainActivity.getInstance().showToast("No files to upload");
        }

    }

    /**
     * Shows a popup of file available by domain from the gateway
     */
    private void showDownloads() {


        Call<List<String>> jsonCall = emuleService.getAvailableBundles();
        Log.i(LOG_TAG, String.valueOf(jsonCall.toString()));
        jsonCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                String jsonString = response.raw().body().toString();

                availableBundles = response.body();
                if (!availableBundles.isEmpty()) {
                    List<String> bundlesToDisplay = new ArrayList(availableBundles);
                    for (int i = 0; i < bundlesToDisplay.size(); i++) {
                        //add name to each bundle.
                        String s = bundlesToDisplay.get(i);
                        AccessPoint ap = MainActivity.getInstance().getApBySubdomain(s);
                        if (ap != null) {
                            bundlesToDisplay.set(i, ap.getName() + " (" + s + ")");
                        }
                    }
                    showDialog(bundlesToDisplay);
                } else {
                    MainActivity.getInstance().showToast("No Bundles Available");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
                //      ((StatusFragment)fragment).updateOnlineText(" \r No Remote AP Available");
            }
        });

    }

    public void showDialog(List<String> bundlesToDisplay) {
        AvailableBundlesDialogFragment dialogFragment = new AvailableBundlesDialogFragment();
        Bundle bundle = new Bundle();

        CharSequence[] cs = bundlesToDisplay.toArray(new CharSequence[bundlesToDisplay.size()]);
        bundle.putCharSequenceArray("list", cs);
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, DIALOG_FRAGMENT);
        dialogFragment.show(MainActivity.getInstance().getSupportFragmentManager(), "Choose Dialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface

    public void onDialogPositiveClick(DialogFragment dialog) {
        List<Integer> selected = ((AvailableBundlesDialogFragment) dialog).getSelectedItems();

        for (int i = 0; i < selected.size(); i++) {
            MainActivity.getInstance().downloadManager.getDownloadURL(availableBundles.get(selected.get(i)), emuleService);
        }

    }


    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
