package mx.rdy.android.bingo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Seca on 3/21/16.
 */
public class SocketManager {

    private static final String TAG = "Bingo.android.rdy.mx";
    private Socket mSocket;
    private String APIToken = "";
    private Config conf;
    private MainActivity parent;
    public SocketManager(String APIToken,MainActivity par) {
        this.APIToken = APIToken;
        this.parent = par;
        conf = new Config();
        startClient();
    }


    public void startClient()
    {

        try {
            //mSocket = IO.socket("http://bingos.herokuapp.com");
            mSocket = IO.socket(conf.getSocketServer()+conf.getSocketServerPort());

            //Log.e(TAG,"http://192.168.2.34");
        } catch (URISyntaxException e) {
            Log.e(TAG, "ERROR -->");
            throw new RuntimeException(e);
        }
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("connect", onConnect);
        mSocket.on("response error", onTest);
        mSocket.on("list rooms", onRoomList);
        mSocket.on("broadcast", onBroadcast);
        mSocket.on("you connect", onYouConnect);
        mSocket.on("user connect", onUserConnect);
        mSocket.on("start game", onStartGame);
        mSocket.connect();
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Log.e(TAG,(String)args[0]);
            Log.e(TAG, args.toString());
            Log.e(TAG, "SE DESCONECTO sd");
        }
    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onConnect");
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("token", APIToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("set token", jsonObj);
        }
    };

    private Emitter.Listener onRoomList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, " onRoomList");
            try {
                addGameRoom((JSONObject) args[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void addGameRoom(JSONObject data) throws JSONException {
        Log.e(TAG,"addGameRoom");
        parent.setRooms(data.getJSONArray("rooms"));

    }
    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            //Log.e(TAG,(String)args[0]);
            JSONObject data = (JSONObject) args[0];

            String username;
            String message;
            try {
                username = data.getString("username");
                message = data.getString("message");
            } catch (JSONException e) {
                return;
            }
            Log.e(TAG, " onUserJoined");
            //JSONObject obj = (JSONObject) args[0];
            /*try {
                Log.e(TAG,""+obj.getString("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    };

    private Emitter.Listener onTest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, " on error");
            JSONObject data = (JSONObject) args[1];
            try {
                Log.e(TAG, data.getString("data"));
                Log.e(TAG, data.getString("total"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener onYouConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, " onYouConnect");
            Log.e(TAG, (String) args[0]);
            JSONObject data = (JSONObject) args[1];
            try {
                Log.e(TAG, data.getString("data"));
                Log.e(TAG, data.getString("total"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onUserConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, " onUserConnect");
            if (args.length > 1) {
                Log.e(TAG, args[1].toString());
                JSONObject data = (JSONObject) args[1];
                try {
                    Log.e(TAG, data.getString("data"));
                    Log.e(TAG, data.getString("total"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, args[0].toString());
            }


        }
    };

    private Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, " onStartGame");
            JSONObject data = (JSONObject) args[1];
            try {
                Log.e(TAG, data.getString("data"));
                Log.e(TAG, data.getString("total"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            Log.e(TAG, " onNewMessage");


        }
    };
    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            try {
                username = data.getString("username");
                message = data.getString("message");
            } catch (JSONException e) {
                return;
            }
            Log.e(TAG, " onUserJoined");
        }
    };


    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            Log.e(TAG, " onUserLeft");
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            Log.e(TAG, " onTyping");
        }
    };
    public void setGameType(Integer gameType)
    {
        safeEmit("gametype","type",gameType);
    }
    //safeEmit "Change room", "room",1
    private void safeEmit(String header,String type,int value)
    {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(type, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(header, jsonObj);
    }
}
