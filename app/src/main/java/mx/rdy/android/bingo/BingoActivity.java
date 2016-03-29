package mx.rdy.android.bingo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class BingoActivity extends AppCompatActivity {

    private static final String TAG = "Bingo.android.rdy.mx";
    BingoCardAdapter adapter;
    View.OnClickListener cardListenner;
    private GridView bingoItemsContainer;
    String APIToken;
    String currentRoom;
    SocketManager socketManager;
    private static Socket mSocket;
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
        socketManager = new SocketManager(APIToken,null);
        mSocket = socketManager.getSocket();
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

}
