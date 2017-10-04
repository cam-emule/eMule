package uk.ac.cam.cl.emule;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.golovin.fluentstackbar.FluentSnackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import timber.log.Timber;
import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.network.EmuleDownloadManager;
import uk.ac.cam.cl.emule.util.StatsManager;



/**
 * The Main Activity for the Emule Android App
 * <p>
 * Fergus Leen
 * fl376@cl.cam.ac.uk
 * January 2017
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, StatusFragment.OnFragmentInteractionListener,
        AccessPointFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener,
        DeliveryFragment.OnFragmentInteractionListener, OnLocationUpdatedListener {

    public static final int WRITE_EXTERNAL_STORAGE_PERMISSON_ID = 1984;
    private static final int LOCATION_PERMISSION_ID = 1001;
    private static final String LOG_TAG = "MainActivity";

    //Ap and Gateway addresses
    //Service port will always be 3000 and http 8080
    public static String remoteIP = "192.168.88.1"; //load this from sharedprefs iniitally rachel offline
    public static String gatewayIP = "34.248.89.252";

    public static final String NOT_AVAILABLE = "Not Available";
    public static String gatewayStatus = NOT_AVAILABLE;
    public static String remoteStatus = NOT_AVAILABLE;

    public static List<AccessPoint> apList;

    public static StatsManager statsManager;
    public EmuleDownloadManager downloadManager;
    static MainActivity mainActivityRunningInstance;
    public Location mLocation;

    public static boolean DEBUG = true;


    Fragment fragment;

    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            onNavigationItemSelected(null);
            statsManager.incDownloads();

        }
    };

    private FluentSnackbar mFluentSnackbar;


    public static MainActivity getInstance() {
        return mainActivityRunningInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Stetho.initializeWithDefaults(this);
        mainActivityRunningInstance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.i("Starting Emule");

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass;
            fragmentClass = StatusFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(null);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = new EmuleDownloadManager(this);
        mFluentSnackbar = FluentSnackbar.create(this);
        statsManager = StatsManager.getInstance();
        startStatusUpdate();
        // Check and if necessary ask for permission for location. If we can't use these services, then fail gracefully,
        if (checkLocationPermission()) {
            onResume();
        }
        SmartLocation.with(this).location()
                .start(this);


    }

    @Override
    protected void onStop() {
        super.onStop();
        SmartLocation.with(this).location().stop();

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(onDownloadComplete);
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        SmartLocation.with(this).location()
                .start(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if ((fragment instanceof StatusFragment) || (fragment instanceof AccessPointDetailFragment)) {
                super.onBackPressed();
            } else goHome();
        }
    }

    public void startStatusUpdate() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance());
        remoteIP = mPrefs.getString("remote_ip_preference", remoteIP);
        downloadManager.loadAccessPointData();
        //check network, remote and gateway
        downloadManager.checkServiceUp(remoteIP, false);
        downloadManager.checkServiceUp(gatewayIP, true);


    }

    static public List<AccessPoint> getApList() {
        return apList;
    }

    public void statusUpdate(String hostname, boolean gateway) {

        if (gateway) {
            gatewayStatus = hostname;
        } else
            remoteStatus = hostname;
        if (fragment instanceof StatusFragment) {
            ((StatusFragment) fragment).updateOnlineText("");


        }
    }

    public void setNavBarText() {
        TextView nameTv = (TextView) findViewById(R.id.nameText);
        TextView emailTv = (TextView) findViewById(R.id.emailText);
        if (nameTv != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {

                nameTv.setText(user.getDisplayName());
                emailTv.setText(user.getEmail());
            } else {
                nameTv.setText(R.string.emule_title);
                emailTv.setText(R.string.not_logged_in);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void accessPointDataLoaded(List<AccessPoint> aplist) {
        apList = aplist;
        //    startStatusUpdate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            //Show about Fragment
            final AboutFragment aboutFragment = AboutFragment.newInstance(R.mipmap.ic_launcher, "eMule", getString(R.string.about_description), getString(R.string.about_url));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContent, aboutFragment, "about")
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.action_refresh) {
            //Show about Fragment
            this.startStatusUpdate();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void goHome() {
        onNavigationItemSelected(null);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = R.id.nav_home;
        if (item != null)
            id = item.getItemId();
        fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_home) {
            fragmentClass = StatusFragment.class;
        } else if (id == R.id.nav_map) {
            fragmentClass = MapsFragment.class;
        } else if (id == R.id.nav_list) {
            fragmentClass = AccessPointFragment.class;
        } else if (id == R.id.nav_manage) {
            fragmentClass = ProfileFragment.class;
        } else if (id == R.id.nav_share) {
            fragmentClass = PrefsFragment.class;
            //} else if (id == R.id.nav_send) {
            //    fragmentClass = TestFragment.class;

        } else if (id == R.id.nav_history) {
            fragmentClass = DeliveryFragment.class;
        }


        //  Log.d(LOG_TAG, "chosen" + item.toString());
        openFragment(fragmentClass);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void openFragment(Class fragmentClass) {
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSON_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted,
                    //URl is set...
                    downloadManager.performDownload();


                } else {

                    // permission denied
                }
                return;
            }
            case LOCATION_PERMISSION_ID: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    onResume();

                }


            }

            // other 'case' lines to check for other
            // permissions this app might request

        }
    }


    public static void requestSystemAlertPermission(Activity context, Fragment fragment, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        final String packageName = context == null ? fragment.getActivity().getPackageName() : context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        if (fragment != null)
            fragment.startActivityForResult(intent, requestCode);
        else
            context.startActivityForResult(intent, requestCode);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        final boolean result = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
        return result;
    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return false;

        }
        return true;
    }

    public boolean checkPermission() {
        // Here, thisActivity is the current activity


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSON_ID);
            return false;
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            //     }
        }
        return true;


    }


    public void showToast(String message) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        showFluentSnackbar(message);
        // showDialog("", message);
    }

    public void showSnackbar(String message) {

        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();


    }

    public void showFluentSnackbar(String message) {

        mFluentSnackbar.create(message)
                .duration(Snackbar.LENGTH_SHORT) // default is Snackbar.LENGTH_LONG
                .important()
                .show();

    }

    public void showDialog(String title, String message) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title);

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public AccessPoint getApBySubdomain(String subdomain) {
        if ((getApList() != null) && (subdomain != null)) {
            //strip out subdomain if necessary
            if (subdomain.contains(".")) {
                String[] array = subdomain.split("\\.");
                subdomain = array[0];
            }
            for (int i = 0; i < getApList().size(); i++) {
                if (apList.get(i).getSubdomain().compareTo(subdomain) == 0) {
                    return apList.get(i);
                }
            }
        }
        return null;
    }

    public AccessPoint getApByFilename(String filename) {
        String[] array = filename.split("_");
        String subdomain = array[1];
        if ((getApList() != null) && (subdomain != null)) {
            for (int i = 0; i < getApList().size(); i++) {
                if (apList.get(i).getSubdomain().compareTo(subdomain) == 0) {
                    return apList.get(i);
                }
            }
        }
        return null;
    }


    @Override
    public void onLocationUpdated(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }
}
