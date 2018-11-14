package tcss450.uw.edu.phishapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import tcss450.uw.edu.phishapp.model.Credentials;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    public static final int MIN_PASSWORD_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public void onLoginAttempt(final Credentials credentials) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.credentials_key), credentials);
        intent.putExtra("args", args);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterAttempt(final Credentials credentials) {
        onLoginAttempt(credentials);
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
