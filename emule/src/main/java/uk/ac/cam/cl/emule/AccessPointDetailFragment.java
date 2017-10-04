package uk.ac.cam.cl.emule;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import timber.log.Timber;
import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.util.LocationUtil;

/**
 * Created by fergus on 06/01/2017.
 *
 */


public class AccessPointDetailFragment extends Fragment implements OnLocationUpdatedListener, View.OnClickListener {
    private static final String ARGUMENT_IMAGE_RES_ID = "imageResId";
    private static final String ARGUMENT_NAME = "name";
    private static final String ARGUMENT_DESCRIPTION = "description";
    private static final String ARGUMENT_URL = "url";
    private static TextView locationText = null;
    AccessPoint ap;
    Location apLoc;
    Location myLoc;


    public AccessPointDetailFragment() {
    }

    public static AccessPointDetailFragment newInstance(int apPos) {
        final Bundle args = new Bundle();
        final AccessPointDetailFragment fragment = new AccessPointDetailFragment();
        args.putInt("appos", apPos);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_accesspointdetail, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image);
        final TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView descriptionTextView = (TextView) view.findViewById(R.id.description);

        locationText = (TextView) view.findViewById(R.id.locationText);


        final Bundle args = getArguments();
        ap = MainActivity.getApList().get(args.getInt("appos"));
        apLoc = new Location(ap.getName());
        try {
            Picasso.with(getContext()).load(ap.getImageurl()).resize(1000, 1000).into(imageView);
        } catch (Exception e) {
            Timber.e(e);
        }
        nameTextView.setText(ap.getName());

        final String text = String.format(getString(R.string.description_format), ap.getDescription(), formatFiles(ap));
        descriptionTextView.setText(text);

        if (MainActivity.getInstance().checkLocationPermission()) {
            onResume();

        }
        imageView.setOnClickListener(this);
        Timber.d("" + SmartLocation.with(MainActivity.getInstance()).location().state().locationServicesEnabled());


        return view;
    }

    private String formatFiles(AccessPoint ap) {
        String ret = "";
        if ((ap.getFileFrom() == null) && (ap.getFileTo() == null)) {
            return "Carrying no Bundles for " + ap.getName();
        } else {

            if (ap.getFileFrom() != null) {
                ret = "One file for the Internet: " + ap.getFileFrom().getName() + " (" + (ap.getFileFrom().length() / 1024) + "kb)\n\n";
            }
            if (ap.getFileTo() != null) {
                ret += "One file for " + ap.getName() + " - " + ap.getFileTo().getName() + " (" + (ap.getFileTo().length() / 1024) + "kb)\n\n";
            }
        }
        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();
        SmartLocation.with(MainActivity.getInstance()).location()
                .start(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SmartLocation.with(MainActivity.getInstance()).location().stop();

    }


    @Override
    public void onLocationUpdated(Location location) {
        myLoc = location;
        apLoc.setLongitude(ap.getLoc().get(0));
        apLoc.setLatitude(ap.getLoc().get(1));
        float distance = location.distanceTo(apLoc);
        DecimalFormat dec = new DecimalFormat("#.00");

        locationText.setText(dec.format(distance / 1000) + " km. " + LocationUtil.formatBearing(location.bearingTo(apLoc)));
    }


    public void callMapIntent(Double lat, Double lon, String title) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat + "," + lon + "(" + title + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(MainActivity.getInstance().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public void onClick(View v) {
        callMapIntent(ap.getLoc().get(1), ap.getLoc().get(0), ap.getName());
    }

//    @Override
//public void onSensorChanged( SensorEvent event ) {
//
//    // If we don't have a Location, we break out
//    if ( myLoc == null ) return;
//
//    float azimuth = event.values[0];
//    float baseAzimuth = azimuth;
//
//    GeomagneticField geoField = new GeomagneticField( Double
//            .valueOf( myLoc.getLatitude() ).floatValue(), Double
//            .valueOf( myLoc.getLongitude() ).floatValue(),
//            Double.valueOf( myLoc.getAltitude() ).floatValue(),
//            System.currentTimeMillis() );
//
//    azimuth -= geoField.getDeclination(); // converts magnetic north into true north
//
//    // Store the bearingTo in the bearTo variable
//    float bearTo = myLoc.bearingTo( apLoc );
//
//    // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
//    if (bearTo < 0) {
//        bearTo = bearTo + 360;
//    }
//
//    //This is where we choose to point it
//    float direction = bearTo - azimuth;
//
//    // If the direction is smaller than 0, add 360 to get the rotation clockwise.
//    if (direction < 0) {
//        direction = direction + 360;
//    }
//
//    rotateImageView( arrow, R.drawable., direction );
//
//    //Set the field
//    String bearingText = "N";
//
//    if ( (360 >= baseAzimuth && baseAzimuth >= 337.5) || (0 <= baseAzimuth && baseAzimuth <= 22.5) ) bearingText = "N";
//    else if (baseAzimuth > 22.5 && baseAzimuth < 67.5) bearingText = "NE";
//    else if (baseAzimuth >= 67.5 && baseAzimuth <= 112.5) bearingText = "E";
//    else if (baseAzimuth > 112.5 && baseAzimuth < 157.5) bearingText = "SE";
//    else if (baseAzimuth >= 157.5 && baseAzimuth <= 202.5) bearingText = "S";
//    else if (baseAzimuth > 202.5 && baseAzimuth < 247.5) bearingText = "SW";
//    else if (baseAzimuth >= 247.5 && baseAzimuth <= 292.5) bearingText = "W";
//    else if (baseAzimuth > 292.5 && baseAzimuth < 337.5) bearingText = "NW";
//    else bearingText = "?";
//
//    //fieldBearing.setText(bearingText);
//
//}
}
