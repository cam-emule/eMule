package uk.ac.cam.cl.emule;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView mTextView;
    Button mTestButton;
    EditText mailTo;
    ViewGroup container;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.test_fragment, container, false);
        mTextView = (TextView) view.findViewById(R.id.textview);
        mTestButton = (Button) view.findViewById(R.id.button_runtest);
        mailTo = (EditText) view.findViewById(R.id.emailTo);
        addListenerOnButton();
        return view;

    }

    public void addListenerOnButton() {

        mTestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                runTest();

            }

        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * Intgeration test of the emule system
     */
    public void runTest() {
        //Test if remote ap is up - get BundleList


        //send email via SMTP to remote ap
        new SendEmailTask().execute(mailTo.getText().toString(),
                "fergus@vap.local",
                "" + MainActivity.remoteIP,
                "fergusradio@gmail.com",
                "emulegateway",
                "1025");
        //Get Bundle LIst again
        //Do we have access to a VAP?


        //Donwload file.
        //Display Contents
        //Check if gateway is up
        //upload file to gateway


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

    class SendEmailTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... params) {
            try {
                return sendEmail(params);


            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(String msg) {
            mTextView.setText(mTextView.getText() + "\n" + msg);
        }

        private String sendEmail(String[] params) {
//            final String username = "fergusradio@gmail.com";
//            final String password = "L33nL33n";
//            final String to="fergusleen@gmail.com";
//
//            Properties props = new Properties();
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
//            props.put("mail.debug", "true");

            final String to = params[0];
            final String from = params[1];
            final String host = params[2];
            final String username = params[3];
            final String password = params[4];
            final String port = params[5];


//            final String username = "fergus@vap.local";
//            final String password = "fergus";
//            final String host = "52.213.102.185";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "false");
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.debug", "true");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));
                message.setSubject("Testing Subject");
                message.setText("Test mail from emule Android APP!,"
                );

//                MimeBodyPart messageBodyPart = new MimeBodyPart();
//
//                Multipart multipart = new MimeMultipart();
//
//                messageBodyPart = new MimeBodyPart();
//                String file = "path of file to be attached";
//                String fileName = "attachmentName";
//                DataSource source = new FileDataSource(file);
//                messageBodyPart.setDataHandler(new DataHandler(source));
//                messageBodyPart.setFileName(fileName);
//                multipart.addBodyPart(messageBodyPart);
//
//                message.setContent(multipart);

                Transport.send(message);

                return "mail sent to " + to + " via " + host;

            } catch (MessagingException e) {
                e.printStackTrace();
                return "Unable to send mail " + e.toString();

            }
        }

    }

}
