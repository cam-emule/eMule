package uk.ac.cam.cl.emule;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import timber.log.Timber;
import uk.ac.cam.cl.emule.models.AccessPoint;

/**
 * This is the main list fragment for emule. It downloads data from the gateway, using
 * cached data if offline. The accessPointAdaptor formats the list for display.
 */
public class AccessPointFragment extends android.support.v4.app.ListFragment {
    public static final String LOG_TAG = "AccessPointFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private OnFragmentInteractionListener mListener;


    public static AccessPointFragment newInstance(String param1, String param2) {
        return new AccessPointFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.list_fragment, container, false);
        setAdaptor();

        return view;


    }

    public void setAdaptor() {
        if (MainActivity.getApList() != null) try {
            AccessPointAdaptor adaptor = new AccessPointAdaptor(getContext(), R.layout.item_file, MainActivity.getApList());
            setListAdapter(adaptor);
        } catch (Exception e) {
            Timber.e("unable to get access points", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        AccessPoint ap = MainActivity.getApList().get(position);
        // Class activityClass = lookupActivityClass_byName(category);
        //Show about Fragment

        final AccessPointDetailFragment apdFragment = AccessPointDetailFragment.newInstance(position);
        MainActivity.getInstance().fragment = apdFragment;
        MainActivity.getInstance().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, apdFragment, "apDetail")
                .addToBackStack("list")
                .commit();
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
