package uk.ac.cam.cl.emule;

/*
 * Created by Fergus Leen (fl376@cl.cam.ac.uk) 10/12/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import timber.log.Timber;
import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.util.LocationUtil;

public class AccessPointAdaptor extends ArrayAdapter<AccessPoint> implements OnLocationUpdatedListener {

    private static  List<AccessPoint> apList;
    Location myLoc;
    private Context context;

    public AccessPointAdaptor(Context context, int resource, List<AccessPoint> objects) {
        super(context, resource, objects);
        this.context = context;
        AccessPointAdaptor.apList = objects;
        sortList();
        SmartLocation.with(MainActivity.getInstance()).location()
                .start(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_file, parent, false);
        AccessPoint ap = apList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.name);
        TextView infotext = (TextView) view.findViewById(R.id.infotext);
        TextView bountytext = (TextView) view.findViewById(R.id.bountytext);
        TextView gpsText = (TextView) view.findViewById(R.id.gpsText);
        tv.setText(ap.getName());
        NumberFormat formatter = new DecimalFormat("#0.00");
        bountytext.setText("Bounty: " + (formatter.format(ap.getBounty())));

        gpsText.setText(ap.getDistanceString());
        ImageView img = (ImageView) view.findViewById(R.id.img);
        try {
            Picasso.with(getContext()).load(ap.getImageurl()).resize(300, 300).into(img);
        } catch (Exception e) {
            Timber.e(e, "Unable to set image");
        }

        int filecount = 0;
        if (ap.getFileFrom() != null) {
            filecount++;
        }
        if (ap.getFileTo() != null) {
            filecount++;
        }
        if (filecount > 0) {
            infotext.setText("Carrying " + filecount + " Bundle" + (filecount > 1 ? "s" : ""));
        }
        return view;
    }

    @Override
    public void onLocationUpdated(Location location) {
        myLoc = location;
        Location apLoc;
        for (int i = 0; i < apList.size(); i++) {
            AccessPoint ap = apList.get(i);
            apLoc = new Location(myLoc);
            apLoc.setLongitude(ap.getLoc().get(0));
            apLoc.setLatitude(ap.getLoc().get(1));
            ap.setDistanceMeters(myLoc.distanceTo(apLoc));
            ap.setDistanceString(LocationUtil.getDistanceAndBearingString(myLoc, apLoc));

        }
        sortList();
        this.notifyDataSetChanged();
    }

    private void sortList() {
        if (myLoc != null) {
            this.sort(new Comparator<AccessPoint>() {
                public int compare(AccessPoint one, AccessPoint other) {

                    if (one.getDistanceMeters() <= other.getDistanceMeters()) {
                        return -1;
                    } else {
                        return 1;
                    }

                }
            });
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
