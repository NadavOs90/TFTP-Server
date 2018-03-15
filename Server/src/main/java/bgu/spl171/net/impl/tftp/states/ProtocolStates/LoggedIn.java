package bgu.spl171.net.impl.tftp.states.ProtocolStates;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.FileManager;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.impl.tftp.helpers.FileNameHolder;
import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;

import static bgu.spl171.net.impl.tftp.TFTPMessagingProtocol.userNames;

/**
 * Created by sheld on 1/20/2017.
 */
public class LoggedIn extends ProtocolState {
    private int expectedAck = -1;

    public LoggedIn(TFTPMessagingProtocol protocol, Connections<Packet> connections, int connectionId) {
        this.protocol = protocol;
        this.connections = connections;
        this.connectionId = connectionId;
    }

    @Override
    public void visit(Rrq packet) {
        if(expectedAck == -1) {
            protocol.changeState(ReadState.class.getName());
            protocol.process(packet);
        }else
            connections.send(connectionId, new Error());//TODO what error?
    }

    @Override
    public void visit(Wrq packet) {
        if(expectedAck == -1) {
            protocol.changeState(WriteState.class.getName());
            protocol.process(packet);
        }else
            connections.send(connectionId, new Error());//TODO what error?
    }

    @Override
    public void visit(Data packet) {
        connections.send(connectionId, new Error((short)0,"Shouldnt get This Data."));
    }

    @Override
    public void visit(Ack packet) {
        if(expectedAck == packet.getBlockNumber())
            expectedAck = -1;
        else
            connections.send(connectionId, new Error((short)0,"Shouldnt get This Data."));
    }

    @Override
    public void visit(Error packet) {
        connections.send(connectionId, new Error((short)0,"Shouldnt get This Data."));
    }

    @Override
    public void visit(Dirq packet) {
        if(expectedAck == -1) {
            protocol.changeState(DirqState.class.getName());
            protocol.process(packet);
        }else
            connections.send(connectionId, new Error());//TODO what error?
    }

    @Override
    public void visit(Logrq packet) {
        connections.send(connectionId, new Error((short)0, "Shoudl Not Get This Data."));
    }

    @Override
    public void visit(Delrq packet) {
        if(expectedAck == -1) {
            FileNameHolder temp = FileManager.getInstance().getLock(packet.getFileName(), false);

            if (temp != null) {
                synchronized (temp) {
                    temp.setRunnable(() -> {
                        FileManager.getInstance().deleteFile(temp);
                        connections.send(connectionId, new Ack((short) 0));
                        protocol.broadCast(new Bcast((byte) 0, temp.getName()));
                    });
                    temp.setDeleted();
                }
            } else
                connections.send(connectionId, new Error((short) 1, "File Not Found, cannot be deleted."));
        }else
            connections.send(connectionId, new Error());//TODO what error?
    }

    @Override
    public void visit(Bcast packet) {
        connections.send(connectionId,new Error((short)0,"As Client You Cannot BroadCast a message."));//TODO is this the right error?
    }

    @Override
    public void visit(Disc packet) {
        if(expectedAck == -1) {

            synchronized (userNames) {
                userNames.remove(connectionId);
            }
            connections.send(connectionId, new Ack((short) 0));
            protocol.terminate();
        }else
            connections.send(connectionId, new Error());//TODO what error?
    }

    @Override
    public void visit(UnknownData packet) {
        connections.send(connectionId, new Error((short)4, "Uknown OpCode Sent To Server."));
    }

    public void expectAck(int expectedAck){
        this.expectedAck = expectedAck;
    }
}
