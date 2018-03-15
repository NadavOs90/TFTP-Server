package bgu.spl171.net.api.bidi;

/**
 * Created by sheld on 1/20/2017.
 */
public interface BidiMessagingProtocol<T>  {

    void start(int connectionId, Connections<T> connections);

    void process(T message);

    /**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}