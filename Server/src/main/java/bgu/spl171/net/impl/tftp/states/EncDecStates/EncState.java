package bgu.spl171.net.impl.tftp.states.EncDecStates;

import bgu.spl171.net.impl.tftp.TFTPMessageEncoderDecoder;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;
import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by sheld on 1/20/2017.
 */
public class EncState implements VisitorEnc {
    private TFTPMessageEncoderDecoder encDec;
    private ByteArrayOutputStream encStream = new ByteArrayOutputStream();
    private Runnable runnable = () -> encStream.reset();


    public EncState(TFTPMessageEncoderDecoder encDec){
        this.encDec = encDec;
    }

    public byte[] visit(Rrq packet) {
        return null;
    }


    public byte[] visit(Wrq packet) {
        return null;
    }


    public byte[] visit(Data packet) {
        runnable.run();
        try {
            encStream.write(encDec.shortToBytes(packet.getOpCode()));
            encStream.write(encDec.shortToBytes(packet.getPacketSize()));
            encStream.write(encDec.shortToBytes(packet.getBlockNum()));
            encStream.write(packet.getData());
            return  encStream.toByteArray();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public byte[] visit(Ack packet) {
        runnable.run();
        try{
            encStream.write(encDec.shortToBytes(packet.getOpCode()));
            encStream.write(encDec.shortToBytes(packet.getBlockNumber()));
            return encStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public byte[] visit(Error packet) {
        runnable.run();
        try{
            encStream.write(encDec.shortToBytes(packet.getOpCode()));
            encStream.write(encDec.shortToBytes(packet.getErrorCode()));
            encStream.write(packet.getErrorMessage().getBytes(Charset.forName("UTF-8")));
            encStream.write(packet.getEndByte());
            return encStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    public byte[] visit(Dirq packet) {
        return null;
    }


    public byte[] visit(Logrq packet) {
        return null;
    }


    public byte[] visit(Delrq packet) {
        return null;
    }


    public byte[] visit(Bcast packet) {
        runnable.run();
        try{
            encStream.write(encDec.shortToBytes(packet.getOpCode()));
            encStream.write(packet.getAction());
            encStream.write(packet.getFileName().getBytes(Charset.forName("UTF-8")));
            encStream.write(packet.getEndByte());
            return encStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public byte[] visit(Disc packet) {
        return null;
    }


    public byte[] visit(UnknownData packet) {
        return null;
    }


}