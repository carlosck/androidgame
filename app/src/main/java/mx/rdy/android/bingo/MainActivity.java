package mx.rdy.android.bingo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;




public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "Bingo.android.rdy.mx";


    static final int LOGIN_REQUEST = 1;  // The request code
    static final int BINGO_REQUEST = 2;  // The request code
    static final int LOGIN_TRUE = 10;
    //private static String APIToken = "30576741c0bbc71743d933aff8506fd4a5efbaf4";
    private static String APIToken = "";
    private GetDetailTask mDetailTask = null;

    public static String PACKAGE_NAME;
    private User user;

    //private Socket mSocket;

    private GridView gameListContainer;
    private Config conf;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;
    private SocketManager socketManager;
    GameRoomsAdapter adapter;
    View.OnClickListener gameRoomsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conf = new Config();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Click");
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });


        gameListContainer = (GridView) findViewById(R.id.gameListContainer);



        if (APIToken == "") {
            startLogin();
            //connectWebSocket();
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_detail) {
            Log.e(TAG, "click");
            startDetail();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST) {

            if (resultCode == LOGIN_TRUE) {
                String token = (String) data.getSerializableExtra("token");

                APIToken = token;
                //loginButton.setVisibility(View.GONE);
                //detailButton.setVisibility(View.VISIBLE);
                showProgress(true);
                connectWebSocket();
                //mDetailTask = new GetDetailTask(APIToken);
                //mDetailTask.execute((Void) null);
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void startDetail() {
        Intent detail = new Intent(MainActivity.this, DetailActivity.class);
        detail.putExtra("user", user);
        startActivity(detail);
    }

    private void startLogin() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(login, LOGIN_REQUEST);
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            */
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", APIToken);
        outState.putSerializable("user", getUser());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        APIToken = savedInstanceState.getString("key");
        setUserObj((User) savedInstanceState.getSerializable("user"));

    }

    public void setUser(JSONObject detail) {
        user = new User(detail);
    }

    public void setUserObj(User u) {
        user = u;
    }

    public User getUser() {
        return user;
    }

    private void connectWebSocket() {
        socketManager = new SocketManager(APIToken,this);
    }

    public void setRooms(JSONArray rooms)
    {
        Object[] arr=new Object[rooms.length()];
        for(int  i=0;i<rooms.length();i++)
        {
            try {
                arr[i]=rooms.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        gameRoomsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"tagss->"+view.getTag());
                socketManager.setGameType((int)view.getTag());
            }
        };
        adapter = new GameRoomsAdapter(this, rooms,gameRoomsListener,getLayoutInflater());


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"run");
                gameListContainer.setAdapter(adapter);
                gameListContainer.setNumColumns(3);

            }
        });
    }




        //gameListContainer.setNumColumns(4);
        //adapter.notifyDataSetChanged();


        /*for(int i =0; i<rooms.length();i++)
        {
            JSONObject object = null;

            object = rooms.getJSONObject(i);
            int id = getResources().getIdentifier(object.getString("name").toLowerCase(), "drawable", getPackageName());
            ImageView imageView = new ImageView(this);
            GridView.LayoutParams vp =
                    new GridView.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                            Toolbar.LayoutParams.WRAP_CONTENT);

            imageView.setLayoutParams(vp);
            imageView.setImageResource(id);

            list.add(object.getString("name"));
            Log.e(TAG,object.getString("name"));
            Log.e(TAG,"val="+object.getInt("value"));

        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        gameListContainer.setAdapter(adapter);*/




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mx.rdy.android.bingo/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mx.rdy.android.bingo/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }
    public void startBingo(String card)
    {
        Intent bingo = new Intent(MainActivity.this, BingoActivity.class);
        bingo.putExtra("token", APIToken);
        bingo.putExtra("card", card);
        startActivityForResult(bingo, BINGO_REQUEST);
    }

    public class GetDetailTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;

        GetDetailTask(String token_) {
            token = token_;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            // TODO: register the new account here.
            boolean regresa = getCredit(token);
            return regresa;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDetailTask = null;
            showProgress(false);

            if (success) {


            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mDetailTask = null;
            showProgress(false);
        }

        public boolean getCredit(String token) {
            URL url;
            String response = "";
            boolean error = false;

            try {
                url = new URL("https://bingos.herokuapp.com/login/auth/");
                //url = new URL(SERVER_IP+"/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Authorization", "Token " + token);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("token", token);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = 0;
                try {
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    responseCode = conn.getResponseCode();
                }

                switch (responseCode) {
                    case HttpsURLConnection.HTTP_OK:
                        // TODO: 2/5/16 make a read response function
                        String line;
                        response = "";

                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                        JSONObject detail = new JSONObject(response);

                        setUser(detail);

                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Log.e(TAG, "NOT FOUND");
                        Log.e(TAG, response);
                        break;

                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        Log.e(TAG, "HTTP_UNAUTHORIZED");
                        //mErrorLogin.setText();
                        setErrorText(" ERROR LOGIN");
                        break;

                }


            } catch (Exception e) {
                setErrorText("Connection Error");
                e.printStackTrace();
            }


            return true;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        private void setErrorText(String msg) {
            Log.e(TAG, msg);
        }


    }

    /*private JSONObject evaluateResult(Object obj)
    {
    return
    }*/
    //sockets




}
