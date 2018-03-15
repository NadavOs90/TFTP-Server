package bgu.spl171.net.impl.tftp;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.packets.Bcast;
import bgu.spl171.net.impl.tftp.packets.Packet;
import bgu.spl171.net.impl.tftp.states.ProtocolStates.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by sheld on 1/20/2017.
 */
public class TFTPMessagingProtocol implements BidiMessagingProtocol<Packet> {
    private int connectionId;
    private Connections<Packet> connections;
    private HashMap<String, ProtocolState> states = new HashMap<>();
    public static WeakHashMap<Integer,String> userNames = new WeakHashMap<>();
    private String currentState;
    private boolean terminate = false;

    @Override
    public void start(int connectionId, Connections<Packet> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
        this.currentState = NotLoggedIn.class.getName();
        fillStates();
    }

    @Override
    public void process(Packet message) {
        message.accept(states.get(currentState));
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    public void changeState(String state){
        this.currentState = state;
    }

    public void terminate(){
        this.terminate = true;
    }


    private void fillStates(){
        Arrays.asList(
                new LoggedIn(this,connections,connectionId),
                new NotLoggedIn(this,connections,connectionId),
                new ReadState(this,connections,connectionId),
                new WriteState(this,connections,connectionId),
                new DirqState(this,connections,connectionId)
        )
                .forEach(
                        state -> states.put(state.getClass().getName(),state)
                );
    }

    public void LoggedInExpectingAck(int expectedAck){
        ((LoggedIn)this.states.get(LoggedIn.class.getName())).expectAck(expectedAck);
    }

    public void broadCast(Bcast bcast) {
        userNames.forEach(
                (id,name) -> {
                    connections.send(id,bcast);
                }
        );
    }
}
