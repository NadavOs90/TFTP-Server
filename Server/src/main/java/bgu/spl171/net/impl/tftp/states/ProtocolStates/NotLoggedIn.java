package bgu.spl171.net.impl.tftp.states.ProtocolStates;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;

import static bgu.spl171.net.impl.tftp.TFTPMessagingProtocol.userNames;

/**
 * Created by sheld on 1/20/2017.
 */
public class NotLoggedIn extends ProtocolState {

    public NotLoggedIn(TFTPMessagingProtocol protocol, Connections<Packet> connections, int connectionId) {
        this.protocol = protocol;
        this.connections = connections;
        this.connectionId = connectionId;
    }
    @Override
    public void visit(Rrq packet) {
        connections.send(connectionId, new Error((short)2,"You Are Not Logged In - Access Violation."));
    }

    @Override
    public void visit(Wrq packet) {
        connections.send(connectionId, new Error((short)2,"You Are Not Logged In - Access Violation."));
    }

    @Override
    public void visit(Data packet) {
        connections.send(connectionId, new Error((short)6,"User Not Logged In."));
    }

    @Override
    public void visit(Ack packet) {
        connections.send(connectionId, new Error((short)6,"User Not Logged In."));
    }

    @Override
    public void visit(Error packet) {
        connections.send(connectionId, new Error((short)6,"User Not Logged In."));
    }

    @Override
    public void visit(Dirq packet) {
        connections.send(connectionId, new Error((short)6,"User Not Logged In."));
    }

    @Override
    public void visit(Logrq packet) {
        synchronized (userNames){
            if(!userNames.containsValue(packet.getUserName())) {
                userNames.put(connectionId, packet.getUserName());
                protocol.changeState(LoggedIn.class.getName());
                connections.send(connectionId, new Ack(0));
            }else{
                connections.send(connectionId, new Error((short)7,"User Name Is Allready Taken"));
            }
        }
    }

    @Override
    public void visit(Delrq packet) {
        connections.send(connectionId, new Error((short)2,"You Are Not Logged In - Access Violation."));
    }

    @Override
    public void visit(Bcast packet) {
        connections.send(connectionId,new Error((short)6,"User Not Logged In."));
    }

    @Override
    public void visit(Disc packet) {
        protocol.terminate();
        connections.send(connectionId, new Ack((short)0));
    }

    @Override
    public void visit(UnknownData packet) {
        connections.send(connectionId, new Error((short)4, "Unknown OpCode given."));
    }
}

