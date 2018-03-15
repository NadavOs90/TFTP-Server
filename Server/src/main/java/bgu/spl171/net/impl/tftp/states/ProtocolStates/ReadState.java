package bgu.spl171.net.impl.tftp.states.ProtocolStates;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.FileManager;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.impl.tftp.helpers.DataTransfer;
import bgu.spl171.net.impl.tftp.helpers.FileNameHolder;
import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;

import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by sheld on 1/20/2017.
 */
public class ReadState  extends ProtocolState {
    private int expectedAck = 0;
    private DataTransfer data = null;

    public ReadState(TFTPMessagingProtocol protocol, Connections<Packet> connections, int connectionId) {
        this.protocol = protocol;
        this.connections = connections;
        this.connectionId = connectionId;
    }
    @Override
    public void visit(Rrq packet) {
        FileNameHolder fileLock = FileManager.getInstance().getLock(packet.getFileName(),false);
        if(fileLock == null) {
            protocol.changeState(LoggedIn.class.getName());
            connections.send(connectionId, new Error((short) 1, "File Was Not Found."));
            return;
        }

        synchronized(fileLock) {
            if (!fileLock.isDeleted()) {
                fileLock.incReaders();
            } else {
                protocol.changeState(LoggedIn.class.getName());
                connections.send(connectionId, new Error((short) 1, "File Was Deleted."));
                return;
            }
        }

        data = FileManager.getInstance().readFile(fileLock);
        fileLock.decrementReaders();

        if(!data.isEmpty()) {
            send();
        }else
            connections.send(connectionId, new Data((short)0,(short)1,new byte[0]));
    }

    //TODO might have a problem! we might receive next command before changing state.
    //TODO solution: add boolean to see when we are working (could use 'first') and if we receive a different command while in (first == true) we change state and send it to the correct state.


    private void send() {
        Data packet = this.data.getNextPacket();
        expectedAck = packet.getBlockNum();
        if(packet.getPacketSize() < 512) {
            clean();
        }
        connections.send(connectionId, packet);
    }

    private void clean(){
        protocol.changeState(LoggedIn.class.getName());
        protocol.LoggedInExpectingAck(expectedAck);
        expectedAck = 0;
        this.data = null;
    }


    @Override
    public void visit(Wrq packet) {
        //TODO return error
    }

    @Override
    public void visit(Data packet) {
        //TODO return error
    }

    @Override
    public void visit(Ack packet) {
        if(packet.getBlockNumber() != this.expectedAck){
            this.expectedAck = 0;
            this.data = null;
            protocol.changeState(LoggedIn.class.getName());
            connections.send(connectionId, new Error((short)0,"Wrong Ack Number."));//TODO probably should terminate process.
        }else
            send();
    }

    @Override
    public void visit(Error packet) {
        //TODO return error
    }

    @Override
    public void visit(Dirq packet) {
        //TODO return error
    }

    @Override
    public void visit(Logrq packet) {
        //TODO return error
    }

    @Override
    public void visit(Delrq packet) {
        //TODO return error
    }

    @Override
    public void visit(Bcast packet) {
        //TODO return error
    }

    @Override
    public void visit(Disc packet) {
        //TODO return error
    }

    @Override
    public void visit(UnknownData packet) {
        //TODO return error
    }
}
