package uk.ac.cam.cl.emule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Fergus Leen (fl376@cl.cam.ac.uk) 06/01/2017.
 * This can be used to show info using the arguments.
 *
 */


public class AboutFragment extends Fragment {
    private static final String ARGUMENT_IMAGE_RES_ID = "imageResId";
    private static final String ARGUMENT_NAME = "name";
    private static final String ARGUMENT_DESCRIPTION = "description";
    private static final String ARGUMENT_URL = "url";

    public AboutFragment() {
    }

    public static AboutFragment newInstance(int imageResId, String name,
                                            String description, String url) {
        final Bundle args = new Bundle();
        args.putInt(ARGUMENT_IMAGE_RES_ID, imageResId);
        args.putString(ARGUMENT_NAME, name);
        args.putString(ARGUMENT_DESCRIPTION, description);
        args.putString(ARGUMENT_URL, url);
        final AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image);
        final TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView descriptionTextView = (TextView) view.findViewById(R.id.description);

        final Bundle args = getArguments();
        imageView.setImageResource(args.getInt(ARGUMENT_IMAGE_RES_ID));
        nameTextView.setText(args.getString(ARGUMENT_NAME));
        final String text = String.format(getString(R.string.description_format), args.getString
                (ARGUMENT_DESCRIPTION), args.getString(ARGUMENT_URL));
        descriptionTextView.setText(text);
        return view;
    }
}
