package uk.ac.cam.cl.emule;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import uk.ac.cam.cl.emule.models.Delivery;
import uk.ac.cam.cl.emule.models.UserProperties;

/**
 * Created by fergus on 06/01/2017.
 *
 */


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String ARGUMENT_IMAGE_RES_ID = "imageResId";
    private static final String ARGUMENT_NAME = "name";
    private static final String ARGUMENT_DESCRIPTION = "description";
    private static final String ARGUMENT_URL = "url";

    private static final int RC_SIGN_IN = 123;
    Button signOutButton;
    View view;


    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(int imageResId, String name,
                                              String description, String url) {
        final Bundle args = new Bundle();

        final ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
  //      FirebaseApp.initializeApp(MainActivity.getInstance());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile, container, false);

        final Bundle args = getArguments();
        signOutButton = (Button) view.findViewById(R.id.signoutButton);
        signOutButton.setOnClickListener(this);
        updateUi();

        return view;
    }

    public void updateUi() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final TextView bioView = (TextView) view.findViewById(R.id.user_profile_short_bio);

        if (user != null) {
            // Name, email address, and profile photo Url
            String userDisplayName = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            final ImageView imageView = (ImageView) view.findViewById(R.id.image);
            final TextView nameTextView = (TextView) view.findViewById(R.id.user_profile_name);

            Picasso.with(MainActivity.getInstance()).load(photoUrl).into(imageView);
            if (userDisplayName!=null && email!=null) {
                nameTextView.setText(userDisplayName + "\n" + email);
                bioView.setText("You are signed in. You can now Sync IAP and Earn! ");
            }
            signOutButton.setText("Sign Out");

            MainActivity.getInstance().setNavBarText();


        } else {
            signOutButton.setText("Sign In");
            bioView.setText("Sign in with your Google Account to Earn and Sync IAP.");

            MainActivity.getInstance().setNavBarText();
        }
        setStats();

    }

    public void setStats() {
        UserProperties uprops = MainActivity.statsManager.getUserProperties();
        updateTextview(R.id.total_downloads, "" + uprops.getTotalDownloads(), "Total Downloads");
        updateTextview(R.id.successful_downloads, "" + uprops.getSuccessfulTransfers(), "Successful Transfers");
        updateTextview(R.id.earnings, "" + uprops.getEarnings(), "Earnings");
        updateTextview(R.id.success_rate, "" + uprops.getSuccessRate(), "% Success Rate");
        updateTextview(R.id.average_delivery_time, "" + uprops.getAverageDeliveryTimeInSeconds(), " seconds Average Delivery Time");
        if (MainActivity.DEBUG){
            String s="Deliveries:";
            for (Delivery d : uprops.getDeliveries()) {
                s+=d.toString();
            }
            updateTextview(R.id.debugview, "" + uprops.toString()+s, "Debug");

        }

    }

    private void updateTextview(int id, String value, String title) {
        TextView tv = (TextView) view.findViewById(id);
        tv.setVisibility(View.VISIBLE);
        tv.setText(value + " " + title);
    }


    public void onClick(View v) {

        if (v.getId() == R.id.signoutButton) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                updateUi();
                AuthUI.getInstance()
                        .signOut(MainActivity.getInstance())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                MainActivity.getInstance().goHome();
                            }
                        });
            } else {
                // not signed in
                startLogin();

            }


        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    private void startLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(Arrays.asList( //new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                updateUi();
                //TODO: Send user details to server.
//                startActivity(SignedInActivity.createIntent(this, response));
//                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    MainActivity.getInstance().showSnackbar("Sign in Cancelled");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    MainActivity.getInstance().showSnackbar("No Internet Connection");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    MainActivity.getInstance().showSnackbar("Unknown Error");
                    return;
                }
            }

            MainActivity.getInstance().showSnackbar("Unknown Sign In Reponse");
        }
    }

}
