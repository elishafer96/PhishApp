package tcss450.uw.edu.phishapp;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.phishapp.model.Credentials;
import tcss450.uw.edu.phishapp.utils.SendPostAsyncTask;

import static tcss450.uw.edu.phishapp.MainActivity.MIN_PASSWORD_LENGTH;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Credentials mCredentials;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragment.OnFragmentInteractionListener) {
            mListener = (RegisterFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button button = (Button) view.findViewById(R.id.B_registerfragment_register);
        button.setOnClickListener(v -> onRegisterClicked());

        return view;
    }

    private void onRegisterClicked() {
        EditText firstNameEdit =
                (EditText) getActivity().findViewById(R.id.ET_registerfragment_firstname);
        EditText lastNameEdit =
                (EditText) getActivity().findViewById(R.id.ET_registerfragment_lastname);
        EditText emailEdit =
                (EditText) getActivity().findViewById(R.id.ET_registerfragment_email);
        EditText usernameEdit =
                (EditText) getActivity().findViewById(R.id.ET_registerfragment_username);
        EditText passwordEdit1 =
                (EditText) getActivity().findViewById(R.id.ET_registerfragment_password1);
        EditText passwordEdit2 =
                (EditText) getActivity().findViewById(R.id.ET_registerfragment_password2);
        boolean isValid = true;

        if (firstNameEdit.getText().length() == 0) {
            isValid = false;
            firstNameEdit.setError("Please enter your first name.");
        }
        if (lastNameEdit.getText().length() == 0) {
            isValid = false;
            lastNameEdit.setError("Please enter your last name.");
        }
        if (usernameEdit.getText().length() == 0) {
            isValid = false;
            usernameEdit.setError("Please enter a username.");
        }
        if (emailEdit.getText().length() == 0) {
            isValid = false;
            emailEdit.setError("Please provide an email address.");
        } else if (!emailEdit.getText().toString().contains("@")) {
            isValid = false;
            emailEdit.setError("The email address you provided is invalid.");
        }
        if (passwordEdit1.getText().length() < MIN_PASSWORD_LENGTH) {
            isValid = false;
            passwordEdit1.setError("Please provide a password that is at least "
                    + MIN_PASSWORD_LENGTH + " characters long.");
        }
        if (passwordEdit2.getText().length() < MIN_PASSWORD_LENGTH) {
            isValid = false;
            passwordEdit2.setError("Please provide a password that is at least "
                    + MIN_PASSWORD_LENGTH + " characters long.");
        } else if (isValid && !passwordEdit1.getText().toString()
                .equals(passwordEdit2.getText().toString())) {
            isValid = false;
            passwordEdit2.setError("Please make sure your passwords match.");
        }


        if (isValid) {
            Credentials credentials = new Credentials
                    .Builder(emailEdit.getText().toString(), passwordEdit1.getText().toString())
                    .addFirstName(firstNameEdit.getText().toString())
                    .addLastName(lastNameEdit.getText().toString())
                    .addUsername(usernameEdit.getText().toString())
                    .build();

            // Build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();

            // Build the JSONObject
            JSONObject msg = credentials.asJSONObject();

            mCredentials = credentials;

            // Instantiate and execute the AsyncTask
            // Feel free to add a handler for onPreExecution so that a progress bar
            // is displayed or maybe disable buttons.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleLoginOnPre)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            String token = resultsJSON.getString("token");

            mListener.onWaitFragmentInteractionHide();
            if (success) {
                // Login was successful. Inform the Activity so it can do its thing.
                mListener.onRegisterAttempt(mCredentials, token);
            } else {
                // Login was unsuccessful. Don’t switch fragments and inform the user
                ((EditText) getView().findViewById(R.id.ET_registerfragment_email))
                        .setError("Register unsuccessful");
            }

        } catch (JSONException e) {
            // It appears that the web service didn’t return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((EditText) getView().findViewById(R.id.ET_registerfragment_email))
                    .setError("Register unsuccessful");
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    public interface OnFragmentInteractionListener
            extends WaitFragment.OnFragmentInteractionListener {
        void onRegisterAttempt(Credentials credentials, String jwToken);
    }
}
