package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

import java.util.WeakHashMap;

/**
 * Created by sheld on 1/20/2017.
 */
public class ConnectionsIMPL<T> implements Connections<T> {

    private WeakHashMap<Integer,ConnectionHandler<T>> connections = new WeakHashMap<>();

    public void add(int connectionId, ConnectionHandler<T> handler){
        connections.put(connectionId, handler);
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler handler = connections.get(connectionId);
        if(handler == null)
            return false;
        handler.send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        connections.values().forEach(handler -> handler.send(msg));
    }

    @Override
    public void disconnect(int connectionId) {
        try{
            connections.get(connectionId).close();
            connections.remove(connectionId);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
