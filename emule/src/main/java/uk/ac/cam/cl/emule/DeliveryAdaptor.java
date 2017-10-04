package uk.ac.cam.cl.emule;

/**
 * Created by Fergus Leen (fl376@cl.cam.ac.uk) 10/12/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import uk.ac.cam.cl.emule.models.AccessPoint;
import uk.ac.cam.cl.emule.models.Delivery;

public class DeliveryAdaptor extends ArrayAdapter<Delivery> {

    private Context context;
    private List<Delivery> deliveries;

    public DeliveryAdaptor(Context context, int resource, List<Delivery> objects) {
        super(context, resource, objects);
        this.context = context;
        deliveries = objects;
        //sortList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_file, parent, false);
        Delivery delivery = deliveries.get(position);

        TextView tv = (TextView) view.findViewById(R.id.name);
        TextView infotext = (TextView) view.findViewById(R.id.infotext);
        TextView bountytext = (TextView) view.findViewById(R.id.bountytext);
        TextView gpsText = (TextView) view.findViewById(R.id.gpsText);
        NumberFormat formatter = new DecimalFormat("#0.00");
        DecimalFormat dec = new DecimalFormat("#.00");
        gpsText.setText(" Transit Time: " + DateUtils.formatElapsedTime(delivery.getDeliveryTime()));
        String titleText = delivery.getFrom() + " to " + delivery.getTo();
        //calculate distance.
        infotext.setText("Distance :" +  (dec.format(delivery.getDistance() / 1000) + " km. "));


        AccessPoint ap = MainActivity.getInstance().getApBySubdomain(delivery.getAccessPoint());
        if (ap != null) {
            bountytext.setText("Bounty: " + (formatter.format(ap.getBounty())));
            if (delivery.isTo()) {
                titleText = delivery.getFrom() + " to " + ap.getName();
            }else
                titleText = ap.getName() + " to " + delivery.getTo();


            ImageView img = (ImageView) view.findViewById(R.id.img);

            Picasso.with(getContext()).load(ap.getImageurl()).resize(300, 300).into(img);
        } else {
            infotext.setText("Ap not registered " + delivery.getAccessPoint());
        }
        tv.setText(titleText);
        return view;
    }


    private void sortList() {
//        if (myLoc != null) {
//            this.sort(new Comparator<AccessPoint>() {
//                public int compare(AccessPoint one, AccessPoint other) {
//
//                    if (one.getDistanceMeters() <= other.getDistanceMeters()) {
//                        return -1;
//                    } else {
//                        return 1;
//                    }
//
//                }
//            });
//        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
