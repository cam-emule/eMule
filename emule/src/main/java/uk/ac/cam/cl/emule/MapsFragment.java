package uk.ac.cam.cl.emule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import uk.ac.cam.cl.emule.models.AccessPoint;

import static uk.ac.cam.cl.emule.R.id.map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {


    MapView mapView;

    GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(uk.ac.cam.cl.emule.R.layout.activity_maps_fragment, container, false);

        mapView = (MapView) view.findViewById(map);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        return view;

    }

    @Override
    public void onResume() {
        mapView.onResume();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        // mMap.getUiSettings().setMyLocationButtonEnabled(true);

/*
if (ActivityCompat.checkSelfPermission(this,
android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
return;
}
mMap.setMyLocationEnabled(true);
*/


        mMap.getUiSettings().setZoomControlsEnabled(true);

        populateFromApList(mMap);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(52.205d, 0.119d)));


    }

    /**
     * Use the Aplist to populate the map markers.
     */
    public void populateFromApList(GoogleMap mMap){
        List<AccessPoint> localList = MainActivity.getApList();
        if (localList!=null) {
            AccessPoint ap;
            ap = null;
            for (int i = 0; i < localList.size(); i++) {
                ap = localList.get(i);
                Log.i("GoogMap", ap.toString());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(ap.getLoc().get(1), ap.getLoc().get(0)))
                        .icon(BitmapDescriptorFactory.defaultMarker(((ap.getFileTo() != null) ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN)))
                        .title(ap.getName())
                        .snippet(ap.getDistanceString()));
            }
            if (ap != null){
                // if (ap.getSubdomain().compareTo("vap")==0)
                //moves to lat in camera.
                //  mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(ap.getLoc().get(1), ap.getLoc().get(0))));
            }
        }

    }
}
