package tcss450.uw.edu.phishapp;


import android.content.Context;
import android.content.SharedPreferences;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Credentials mCredentials;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button btn = (Button) view.findViewById(R.id.B_loginfragment_register);
        btn.setOnClickListener(v -> mListener.onRegisterAttempt());

        btn = (Button) view.findViewById(R.id.B_loginfragment_login);
        btn.setOnClickListener(v -> onLoginClicked());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getActivity()
                .getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {
            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");

            EditText emailEdit = getActivity().findViewById(R.id.ET_loginfragment_email);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.ET_loginfragment_password);
            passwordEdit.setText(password);
            onLoginClicked();
        }
    }

    private void onLoginClicked() {
        EditText emailEdit = (EditText) getActivity().findViewById(R.id.ET_loginfragment_email);
        EditText passwordEdit =
                (EditText) getActivity().findViewById(R.id.ET_loginfragment_password);
        boolean isValid = true;

        if (emailEdit.getText().length() == 0) {
            isValid = false;
            emailEdit.setError("Please provide an email address.");
        } else if (!emailEdit.getText().toString().contains("@")) {
            isValid = false;
            emailEdit.setError("The email address you provided is invalid.");
        }
        if (passwordEdit.getText().length() == 0) {
            isValid = false;
            passwordEdit.setError("Please enter your password.");
        }

        if (isValid) {
            Credentials credentials = new Credentials
                    .Builder(emailEdit.getText().toString(), passwordEdit.getText().toString())
                    .build();

            // Build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_login))
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

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        // Store the credentials in SharedPrefs
        prefs.edit().putString(
                getString(R.string.keys_prefs_email),
                credentials.getEmail()).apply();
        prefs.edit().putString(
                getString(R.string.keys_prefs_password),
                credentials.getPassword()).apply();
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

            mListener.onWaitFragmentInteractionHide();
            if (success) {
                // Login was successful. Inform the Activity so it can do its thing.
                saveCredentials(mCredentials);
                mListener.onLoginAttempt(mCredentials);
            } else {
                // Login was unsuccessful. Don’t switch fragments and inform the user
                ((EditText) getView().findViewById(R.id.ET_loginfragment_email))
                        .setError("Log in unsuccessful.");
            }

        } catch (JSONException e) {
            // It appears that the web service didn’t return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((EditText) getView().findViewById(R.id.ET_loginfragment_email))
                    .setError("Log in unsuccessful.");
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
        void onRegisterAttempt();
        void onLoginAttempt(Credentials credentials);
    }

}
