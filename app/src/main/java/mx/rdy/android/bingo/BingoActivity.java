package mx.rdy.android.bingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BingoActivity extends AppCompatActivity {

    private static final String TAG = "Bingo.android.rdy.mx";
    BingoCardAdapter adapter;
    View.OnClickListener cardListenner;
    private GridView bingoItemsContainer;
    String APIToken;
    String currentRoom;
    SocketManager socketManager;
    private TextView bingoGameNumber;
    private static Socket mSocket;
    static final int LOOSE= 100;
    static final int WIN= 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        APIToken = b.getString("token");
        currentRoom = b.getString("room");
        socketManager = new SocketManager(APIToken,this);
        //socketManager.startClient();
        //socketManager.setBingo(this);
        mSocket = socketManager.getSocket();

        mSocket.on("item verified", onItemVerified);
        mSocket.on("new item", onNewItem);
        mSocket.on("you win", onYouWin);
        mSocket.on("you loose", onYouLoose);

        JSONArray cardArray=null;

        try {
            cardArray= new JSONArray(b.getString("card"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cardListenner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"->"+view.getTag());
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("item", view.getTag());
                    jsonObj.put("room", currentRoom);
                    jsonObj.put("token", APIToken);
                    jsonObj.put("gametype", 0);
                    Log.e(TAG,"token"+APIToken);
                    Log.e(TAG,"gametype"+0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("Bingo item", jsonObj);

            }
        };



        bingoItemsContainer = (GridView) findViewById(R.id.BingoGrid);
        bingoGameNumber= (TextView) findViewById(R.id.BingoGameNumber);
        adapter = new BingoCardAdapter(this, cardArray,cardListenner,getLayoutInflater());
        bingoItemsContainer.setAdapter(adapter);
    }

    //safeEmit "Change room", "room",1
    private void safeEmit(String header,String type,int value)
    {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(type, value);
            jsonObj.put("token", APIToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(header, jsonObj);
    }

    public void itemVerified(final int item)
    {
        Log.e(TAG,"itemVerified");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"run");
                View view= bingoItemsContainer.findViewWithTag(item);
                view.setAlpha(0.1f);

            }
        });

    }
    private Emitter.Listener onItemVerified = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, "onItemVerified");
            JSONObject data = (JSONObject) args[0];
            int gametype=0;
            try {
                int item = data.getInt("item");
                gametype=data.getInt("gametype");
                Log.e(TAG, data.getString("win"));
                Log.e(TAG,"item->"+ item);
                itemVerified(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onNewItem = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, "onNewItem");
            JSONObject data = (JSONObject) args[0];
            int gametype=0;
            try {
                final int newitem = data.getInt("item");
                Log.e(TAG,"item->"+ newitem);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG,"run");
                        bingoGameNumber.setText(Integer.toString(newitem));

                    }
                });

                //itemVerified(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onYouWin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, "onYouWin");
            JSONObject data = (JSONObject) args[0];
            Intent returnIntent = new Intent();
            try {
                String price = data.getString("price");
                Log.e(TAG,"PRICE->"+price);
                returnIntent.putExtra("price",price);
                setResult(WIN, returnIntent);
                finish();
                //itemVerified(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onYouLoose = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, "onYouLoose");
            JSONObject data = (JSONObject) args[0];

            Intent returnIntent = new Intent();
            try {
                String winner = data.getString("winner");
                returnIntent.putExtra("winner",winner);
                setResult(LOOSE, returnIntent);
                finish();
                //itemVerified(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
