package bgu.spl171.net.impl.tftp.states.ProtocolStates;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.FileManager;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.impl.tftp.helpers.FileNameHolder;
import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;

import java.util.ArrayList;

/**
 * Created by sheld on 1/20/2017.
 */
public class WriteState  extends ProtocolState {
    private FileNameHolder fileLock = null;
    private int expectedData = 0;
    private ArrayList<Byte> data = new ArrayList<>();

    public WriteState(TFTPMessagingProtocol protocol, Connections<Packet> connections, int connectionId) {
        this.protocol = protocol;
        this.connections = connections;
        this.connectionId = connectionId;
    }


    private void processCommand() {
        //data.clear();
        expectedData = 1;
        connections.send(connectionId, new Ack((short)0));
    }


    @Override
    public void visit(Rrq packet) {

    }


    //TODO notice we might receive an error from client since he has lost the file on his side.
    //TODO in that case we should delete the file completely from the toBeAdded vector in FileManager.
    @Override
    public void visit(Wrq packet) {
        fileLock = FileManager.getInstance().getLock(packet.getFileName(),true);
        if(fileLock == null)
            connections.send(connectionId, new Error((short)5,"File Allready Exists."));
        else
            processCommand();
    }



    @Override
    public void visit(Data packet) {
        //  this.data.add(packet.getData());
        if(packet.getBlockNum() != expectedData){
            connections.send(connectionId, new Error((short)0,"Wrong Data Packet Sent. Start Again."));
            clean();//TODO should clear the created fileLock (delete from FileManager ass well).
            protocol.changeState(LoggedIn.class.getName());
        }else{
            byte[] temp = packet.getData();
            for(int i = 0; i < packet.getPacketSize(); i++){
                this.data.add(temp[i]);
            }

            if(packet.getPacketSize() != 512){
                synchronized(fileLock){
                    FileManager.getInstance().writeFile(data,fileLock);//TODO
                    FileManager.getInstance().fileWritted(fileLock);
                }
                protocol.changeState(LoggedIn.class.getName());
                connections.send(connectionId, new Ack(expectedData));
                protocol.broadCast(new Bcast((byte)1,fileLock.getName()));
                clean();
            }else {
                connections.send(connectionId, new Ack(expectedData++));
            }
        }
    }



    private void clean(){
        data.clear();
        fileLock = null;
        expectedData = 0;
    }

    @Override
    public void visit(Ack packet) {

    }

    @Override
    public void visit(Error packet) {

    }

    @Override
    public void visit(Dirq packet) {

    }

    @Override
    public void visit(Logrq packet) {

    }

    @Override
    public void visit(Delrq packet) {

    }

    @Override
    public void visit(Bcast packet) {

    }

    @Override
    public void visit(Disc packet) {

    }

    @Override
    public void visit(UnknownData packet) {

    }
}