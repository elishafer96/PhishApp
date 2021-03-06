package tcss450.uw.edu.phishapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.phishapp.blog.BlogPost;
import tcss450.uw.edu.phishapp.model.Credentials;
import tcss450.uw.edu.phishapp.setlist.SetListPost;
import tcss450.uw.edu.phishapp.utils.GetAsyncTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BlogFragment.OnListFragmentInteractionListener,
        BlogPostFragment.OnFragmentInteractionListener,
        SetListFragment.OnListFragmentInteractionListener,
        SetListPostFragment.OnFragmentInteractionListener,
        SuccessFragment.OnFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener {

    private Credentials credentials;
    private String mEmail;
    private String  mJWToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            if (findViewById(R.id.home_fragment_container) != null) {
                Credentials credentials = (Credentials) getIntent()
                        .getSerializableExtra(getString(R.string.credentials_key));
                String emailAddress = mEmail = credentials.getEmail();
                final Bundle args = new Bundle();
                args.putString(getString(R.string.keys_email), emailAddress);

                Fragment fragment;
                if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg),
                                false)) {

                    fragment = new ChatFragment();
                } else {
                    fragment = new SuccessFragment();
                    fragment.setArguments(args);
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.home_fragment_container, fragment)
                        .commit();
            }
        }

        mJWToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwToken));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            SuccessFragment sf = new SuccessFragment();
            Bundle args = new Bundle();
            args.putSerializable(getString(R.string.credentials_key), credentials);
            sf.setArguments(args);
            loadFragment(sf);
        } else if (id == R.id.nav_blogposts) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_blog))
                    .appendPath(getString(R.string.ep_get))
                    .build();

            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleBlogGetOnPostExecute)
                    .addHeaderField("authorization", mJWToken)
                    .build().execute();
        } else if (id == R.id.nav_setlists) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_setlists))
                    .appendPath(getString(R.string.ep_recent))
                    .build();

            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleSetListGetOnPostExecute)
                    .addHeaderField("authorization", mJWToken)
                    .build().execute();
        } else if (id == R.id.nav_globalchat) {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs),
                    Context.MODE_PRIVATE);

            String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            ChatFragment cf = new ChatFragment();
            loadFragment(cf);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        new DeleteTokenAsyncTask().execute();
    }

    private void handleSetListGetOnPostExecute(final String result) {
        // Parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has("response")) {
                JSONObject response = root.getJSONObject("response");
                if (response.has("data")) {
                    JSONArray data = response.getJSONArray("data");
                    List<SetListPost> setLists = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonSetList = data.getJSONObject(i);
                        setLists.add(new SetListPost.Builder(jsonSetList.getString("location"),
                                jsonSetList.getString("long_date"),
                                jsonSetList.getString("venue"))
                                .addURL(jsonSetList.getString("url"))
                                .addSetListNotes(jsonSetList.getString("setlistnotes"))
                                .addSetListData(jsonSetList.getString("setlistdata"))
                                .build());
                    }

                    SetListPost[] setListsAsArray = new SetListPost[setLists.size()];
                    setListsAsArray = setLists.toArray(setListsAsArray);

                    Bundle args = new Bundle();
                    args.putSerializable(SetListFragment.ARG_SETLIST_LIST, setListsAsArray);
                    Fragment frag = new SetListFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    // Notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                // Notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            // Notify user
            onWaitFragmentInteractionHide();
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void handleBlogGetOnPostExecute(final String result) {
        // Parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has("response")) {
                JSONObject response = root.getJSONObject("response");
                if (response.has("data")) {
                    JSONArray data = response.getJSONArray("data");
                    List<BlogPost> blogs = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonBlog = data.getJSONObject(i);
                        blogs.add(new BlogPost.Builder(jsonBlog.getString("pubdate"),
                                jsonBlog.getString("title"))
                                .addTeaser(jsonBlog.getString("teaser"))
                                .addUrl(jsonBlog.getString("url"))
                                .build());
                    }

                    BlogPost[] blogsAsArray = new BlogPost[blogs.size()];
                    blogsAsArray = blogs.toArray(blogsAsArray);

                    Bundle args = new Bundle();
                    args.putSerializable(BlogFragment.ARG_BLOG_LIST, blogsAsArray);
                    Fragment frag = new BlogFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    // Notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                // Notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            // Notify user
            onWaitFragmentInteractionHide();
        }
    }

    @Override
    public void onListFragmentInteraction(BlogPost item) {
        BlogPostFragment bpf = new BlogPostFragment();
        Bundle args = new Bundle();
        args.putSerializable("blogpost", item);
        bpf.setArguments(args);
        loadFragment(bpf);
    }

    @Override
    public void onFullPostClicked(BlogPost blogPost) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(blogPost.getUrl()));
        startActivity(browserIntent);
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.home_fragment_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SetListPost item) {
        SetListPostFragment slpf = new SetListPostFragment();
        Bundle args = new Bundle();
        args.putSerializable("setlistpost", item);
        slpf.setArguments(args);
        loadFragment(slpf);
    }

    @Override
    public void onFullPostClicked(SetListPost setListPost) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(setListPost.getURL()));
        startActivity(browserIntent);
    }

    @Override
    public void onLogOutClicked() {
        logout();
    }

    //     Deleting the InstanceId (Firebase token) must be done asynchronously. Good thing that we
//     have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences prefs = getSharedPreferences(
                    getString(R.string.keys_shared_prefs),
                    Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                Log.e("FCM", "Delete error!");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Close the app
            finishAndRemoveTask();
        }
    }
}
