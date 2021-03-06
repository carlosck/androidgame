package mx.rdy.android.bingo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Seca on 3/21/16.
 */
public class SocketManager  {

    private static final String TAG = "Bingo.android.rdy.mx";
    private static Socket mSocket;
    private String APIToken = "";
    private Config conf;
    private MainActivity parent;
    private String currentRoom = "/";
    private BingoActivity bingo;
    public SocketManager(String APIToken,MainActivity par) {
        this.APIToken = APIToken;
        this.parent = par;
        conf = new Config();
        //startClient();
    }
    public SocketManager(String APIToken,BingoActivity par) {
        this.APIToken = APIToken;
        this.bingo = par;
        conf = new Config();
        //startClient();
    }


    public void startClient()
    {

        /*try {
            //mSocket = IO.socket("http://bingos.herokuapp.com");
            mSocket = IO.socket(conf.getSocketServer()+conf.getSocketServerPort());

            //Log.e(TAG,"http://192.168.2.34");
        } catch (URISyntaxException e) {
            Log.e(TAG, "ERROR -->");
            throw new RuntimeException(e);
        }*/
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
        mSocket.on("init game", onInitGame);
        mSocket.on("start game", onStartGame);
        mSocket.on("join room", onJoinRoom);

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
                    Log.e(TAG, data.getString("users"));
                    Log.e(TAG, data.getString("total"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, args[0].toString());
            }


        }
    };

    private Emitter.Listener onJoinRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, "onJoinRoom");
            JSONObject data = (JSONObject) args[0];
            try {
                Log.e(TAG, data.getString("gameroom"));
                currentRoom = data.getString("gameroom");
                Log.e(TAG, "gametype->");
                Log.e(TAG, data.getString("gametype"));
                //currentGame= data.getString("gametype");

                JSONArray players = data.getJSONArray("players");
                for(int i=0;i<players.length();i++)
                {
                    String player= players.getString(i);
                    Log.e(TAG,player);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onInitGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, " onInitGame");
            JSONObject data = (JSONObject) args[0];
            try {
                Integer gametype= data.getInt("game");
                String cardString = "";
                JSONArray card = data.getJSONArray("card");

                switch(gametype)
                {
                    case 0: parent.startBingo(card);
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e(TAG, " onStartGame");
            JSONObject data = (JSONObject) args[0];
            try {
                Log.e(TAG, data.getString("game"));
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
            jsonObj.put("token", APIToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(header, jsonObj);
    }

    public static synchronized Socket getSocket()
    {
        return mSocket;
    }

    public synchronized void setSocket(Socket socket)
    {
        SocketManager.mSocket = socket;
        startClient();
    }

    public String getCurrentRoom()
    {
        return currentRoom;
    }

}
