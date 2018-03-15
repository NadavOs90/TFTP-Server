package bgu.spl171.net.impl.tftp.states.ProtocolStates;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.FileManager;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.impl.tftp.helpers.DataTransfer;
import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;

import java.util.Arrays;
import java.util.Vector;

/**
 * Created by sheld on 1/20/2017.
 */
public class DirqState  extends ProtocolState {
    private int expectedAck = 0;
    private DataTransfer data = null;

    //TODO could have a problem in this clas... (might not change state fast enough before next message is received).
    public DirqState(TFTPMessagingProtocol protocol, Connections<Packet> connections, int connectionId) {
        this.protocol = protocol;
        this.connections = connections;
        this.connectionId = connectionId;
    }

    @Override
    public void visit(Rrq packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(Wrq packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(Data packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
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

    private void clean(){
        protocol.changeState(LoggedIn.class.getName());
        protocol.LoggedInExpectingAck(expectedAck);
        expectedAck = 0;
        this.data = null;
    }



    @Override
    public void visit(Error packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(Dirq packet) {
        this.data = FileManager.getInstance().getDirectoryFiles();
        if(!data.isEmpty())
            send();
        else {
            protocol.changeState(LoggedIn.class.getName());
            protocol.LoggedInExpectingAck(1);
            expectedAck = 0;
            this.data = null;
            connections.send(connectionId, new Data((short) 0, (short) 1, new byte[0]));
        }
    }

    private void send() {
        Data packet = this.data.getNextPacket();
        expectedAck = packet.getBlockNum();
        if(packet.getPacketSize() < 512) {
            clean();
        }
        connections.send(connectionId, packet);
    }


    @Override
    public void visit(Logrq packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(Delrq packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(Bcast packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(Disc packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }

    @Override
    public void visit(UnknownData packet) {
        connections.send(connectionId, new Error((short)0, "In The Middle Of Something Here..."));
    }
}