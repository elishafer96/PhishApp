package tcss450.uw.edu.phishapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import tcss450.uw.edu.phishapp.model.Credentials;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    public static final int MIN_PASSWORD_LENGTH = 6;

    private boolean mLoadFromChatNotification = false;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            Log.d(TAG, "type of message: " + getIntent().getExtras().getString("type"));
            mLoadFromChatNotification = "msg".equals(getIntent().getExtras().getString("type"));
        } else {
            Log.d(TAG, "NO MESSAGE");
        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.frame_main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_main_container, new LoginFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onRegisterAttempt() {
        loadFragment(new RegisterFragment());
    }

    private void login(final Credentials credentials, String jwtToken) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable(getString(R.string.credentials_key), credentials);
//        intent.putExtra("args", args);
        intent.putExtra(getString(R.string.credentials_key), credentials);
        intent.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        intent.putExtra(getString(R.string.keys_intent_jwToken), jwtToken);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginAttempt(final Credentials credentials, final String jwtToken) {
        login(credentials, jwtToken);
    }

    @Override
    public void onRegisterAttempt(final Credentials credentials, final String jwtToken) {
        login(credentials, jwtToken);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_main_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }
}
