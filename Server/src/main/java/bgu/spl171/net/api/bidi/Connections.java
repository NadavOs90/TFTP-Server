package bgu.spl171.net.api.bidi;

/**
 * Created by sheld on 1/20/2017.
 */
public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);
}

