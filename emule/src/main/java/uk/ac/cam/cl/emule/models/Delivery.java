package uk.ac.cam.cl.emule.models;

import org.apache.commons.io.FilenameUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import uk.ac.cam.cl.emule.util.LocationUtil;

/**
 * Created by fergus on 17/01/2017.
 */

public class Delivery {

    public static final String GATEWAY = "Internet";
    String uid;
    String filename;
    Long pickupTimestamp;
    Long deliveryTimestamp;

    String accessPoint; //Accesspoint subdomain
    boolean isTo = false; //false = from ie for Internet;


    //Record GPS here for History list. lat,lon
    private List<Double> locStart = new ArrayList<Double>();
    private List<Double> locEnd = new ArrayList<Double>();



    float distance;

    /**
     * builds delivery from filename
     *
     * @param _filename
     */
    public Delivery(String _filename) {
        String[] array = _filename.split("_");
        filename = _filename;
        accessPoint = array[1];
        if (array[0].compareTo("TO") == 0) isTo = true;
        //.tar.gz remove twice
        pickupTimestamp = Long.parseLong(FilenameUtils.removeExtension(FilenameUtils.removeExtension(array[2])));
        deliveryTimestamp = System.currentTimeMillis() / 1000L;
        try {
            locStart = LocationUtil.decodeGpsFromString(array[3]);

            distance = LocationUtil.distanceToHere(locStart.get(1),locStart.get(0));
        } catch (Exception e) {
            Timber.i("Cannot decode GPS from filename " + filename);
            distance =0;
        }

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getPickupTimestamp() {
        return pickupTimestamp;
    }

    public void setPickupTimestamp(Long pickupTimestamp) {
        this.pickupTimestamp = pickupTimestamp;
    }

    public Long getDeliveryTimestamp() {
        return deliveryTimestamp;
    }

    public void setDeliveryTimestamp(Long deliveryTimestamp) {
        this.deliveryTimestamp = deliveryTimestamp;
    }

    public String getFrom() {
        return (!isTo ? accessPoint : GATEWAY);
    }


    public String getTo() {
        return (isTo ? accessPoint : GATEWAY);
    }

    public boolean isTo() {
        return isTo;
    }

    public List<Double> getLocEnd() {
        return locEnd;
    }

    public float getDistance() {
        return distance;
    }

    public void setLocEnd(List<Double> locEnd) {
        this.locEnd = locEnd;
    }

    public List<Double> getLocStart() {
        return locStart;
    }

    public void setLocStart(List<Double> locFrom) {
        this.locStart = locStart;
    }

    public long getDeliveryTime() {
        return deliveryTimestamp - pickupTimestamp;
    }

    public String getAccessPoint() {
        return accessPoint;
    }

    @Override
    public String toString() {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        java.util.Date pickup = new java.util.Date(pickupTimestamp * 1000);
        java.util.Date delivered = new java.util.Date(deliveryTimestamp * 1000);
        return "\nDelivery{\n" +
                "pickupTimestamp=" + dt.format(pickup) +
                ", \ndeliveryTimestamp=" + dt.format(delivered) +
                ", \nfrom='" + getFrom() + '\'' +
                ", \nap='" + accessPoint + '\'' +
                ", \nfilename='" + filename + '\'' +
                '}';
    }

}
