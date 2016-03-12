package mx.rdy.android.bingo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import mx.rdy.android.bingo.User;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "Bingo.android.rdy.mx";
    Button loginButton;
    Button detailButton;
    Button creditsButton;

    static final int LOGIN_REQUEST = 1;  // The request code
    static final int LOGIN_TRUE = 10;
    //private static String APIToken = "30576741c0bbc71743d933aff8506fd4a5efbaf4";
    private static String APIToken = "";
    private GetDetailTask mDetailTask = null;

    private boolean isLoged=false;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent login = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(login,LOGIN_REQUEST);

            }
        });

        detailButton = (Button) findViewById(R.id.detailButton);

        detailButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startDetail();
            }
        });


        creditsButton= (Button) findViewById(R.id.creditsButton);
        creditsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mDetailTask = new GetDetailTask(APIToken);
                mDetailTask.execute((Void) null);
            }
        });
        if(APIToken==""){
            startLogin();
        }


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
            Log.e(TAG,"click");
            startDetail();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOGIN_REQUEST)
        {

            if(resultCode==LOGIN_TRUE)
            {
                String token = (String)data.getSerializableExtra("token");

                APIToken = token;
                //loginButton.setVisibility(View.GONE);
                detailButton.setVisibility(View.VISIBLE);
                showProgress(true);
                mDetailTask = new GetDetailTask(APIToken);
                mDetailTask.execute((Void) null);
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
    private void startDetail()
    {
        Intent detail = new Intent(MainActivity.this,DetailActivity.class);
        detail.putExtra("user", user);
        startActivity(detail);
    }
    private void startLogin()
    {
        Intent login = new Intent(MainActivity.this,LoginActivity.class);
        startActivityForResult(login,LOGIN_REQUEST);
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
        outState.putString("key",APIToken);
        outState.putSerializable("user",getUser());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        APIToken = savedInstanceState.getString("key");
        setUserObj( (User)savedInstanceState.getSerializable("user"));

    }
    public void setUser(JSONObject detail)
    {
        user = new User(detail);
    }
    public void setUserObj(User u)
    {
        user= u;
    }
    public User getUser()
    {
        return user;
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
            boolean regresa=getCredit(token);
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

        public boolean getCredit(String token)
        {
            URL url;
            String response = "";
            boolean error=false;

            try
            {
                url = new URL("http://192.168.0.26:3000/login/auth/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Authorization","Token "+token);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> postDataParams=new HashMap<String, String>();
                postDataParams.put("token",token);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = 0;
                try {
                    responseCode = conn.getResponseCode();
                }
                catch(IOException e)
                {
                    responseCode = conn.getResponseCode();
                }

                switch(responseCode)
                {
                    case HttpsURLConnection.HTTP_OK:
                        // TODO: 2/5/16 make a read response function
                        String line;
                        response="";

                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                        while((line=br.readLine()) !=null)
                        {
                            response+=line;
                        }
                        JSONObject detail = new JSONObject(response);

                        setUser(detail);

                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Log.e(TAG,"NOT FOUND");
                        Log.e(TAG,response);
                        break;

                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        Log.e(TAG,"HTTP_UNAUTHORIZED");
                        //mErrorLogin.setText();
                        setErrorText(" ERROR LOGIN");
                        break;

                }


            }
            catch(Exception e)
            {
                setErrorText("Connection Error");
                e.printStackTrace();
            }


            return true;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
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

        private void setErrorText(String msg)
        {
            Log.e(TAG,msg);
        }


    }
}
