package mx.rdy.android.bingo;

/**
 * Created by Seca on 3/21/16.
 */
public class Config {
    private final String SocketSever="http://192.168.1.53";
    private final String SocketServerPort=":5000";
    public final int BINGO=0;

    public String getSocketServerPort() {
        return SocketServerPort;
    }


    public String getSocketServer()
    {
        return this.SocketSever;
    }

}
