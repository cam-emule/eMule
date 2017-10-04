package uk.ac.cam.cl.emule.util;

import android.location.Location;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;
import uk.ac.cam.cl.emule.MainActivity;

/**
 * Created by Fergus Leen (fl376@cl.cam.ac.uk)  16/01/2017.
 */

public class LocationUtil {

    public static String formatBearing(double bearing) {
        if (bearing < 0 && bearing > -180) {
            // Normalize to [0,360]
            bearing = 360.0 + bearing;
        }
        if (bearing > 360 || bearing < -180) {
            return "Unknown";
        }

        String directions[] = {
                "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW",
                "N"};
        String cardinal = directions[(int) Math.floor(((bearing + 11.25) % 360) / 22.5)];
        return cardinal;
    }

    public static String getDistanceAndBearingString(Location here, Location there) {

        float distance = here.distanceTo(there);
        DecimalFormat dec = new DecimalFormat("#.00");

        return (dec.format(distance / 1000) + " km. " + LocationUtil.formatBearing(here.bearingTo(there)));


    }


    public static String getCurrentGpsAsFileString(){
        //Use underscore and hyphen
        Location loc= MainActivity.getInstance().getLocation();
        String str="";
        if (loc!=null){
             str=""+loc.getLatitude()+"x"+loc.getLongitude();
            str=str.replaceAll("\\.","p");
            Timber.d(str);
        }
        return str;

    }

    /*
        populate start and end GPS for a delivery using Phone GPS.
        Consider using access point gps?
     */

    public static List<Double> decodeGpsFromString(String gpsString){
        if (gpsString.contains("x")){
            //this is one of our strings
            //replace x with comma
            gpsString=gpsString.replace("x",",");
            gpsString=gpsString.replace("p",".");
            String[] latlon= gpsString.split(",");
            ArrayList<Double> retArr=new ArrayList<Double>();
            retArr.add(Double.parseDouble(latlon[1]));
            retArr.add(Double.parseDouble(latlon[0]));
            return retArr;


        }
        return new ArrayList();
    }

    public static float distanceToHere( double endlat, double endlon){
        Location here = MainActivity.getInstance().getLocation();
        Location locationB = new Location("End");
        locationB.setLatitude(endlat);
        locationB.setLongitude(endlon);

        return here.distanceTo(locationB);
    }

//    public static String getDateCurrentTimeZone(long timestamp) {
//        try{
//            Calendar calendar = Calendar.getInstance();
//            TimeZone tz = TimeZone.getDefault();
//            calendar.setTimeInMillis(timestamp * 1000);
//            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
//            SimpleDateFormat sdf = new SimpleDateFormat("d MMM H:m");
//            //DateFormat sdf = DateFormat.getDateTimeInstance();
//            Date currenTimeZone = (Date) calendar.getTime();
//            return sdf.format(currenTimeZone);
//        }catch (Exception e) {
//        }
//        return "";
//    }

    public static String getDateCurrentTimeZone(long timeStamp) {

        try{
            DateFormat sdf = new SimpleDateFormat("d MMM H:m");
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "" + timeStamp;
        }
    }

}


