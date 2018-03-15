package bgu.spl171.net.impl.tftp.states.EncDecStates;

import bgu.spl171.net.impl.tftp.TFTPMessageEncoderDecoder;
import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.packets.Data;
import bgu.spl171.net.impl.tftp.packets.Packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by sheld on 1/20/2017.
 */
public class DecState {
    //implements VisitorDec {
    private TFTPMessageEncoderDecoder encDec;
    private MyEnums.DecType decType = MyEnums.DecType.NOTDEFINED;
    private ByteBuffer big = ByteBuffer.allocate(512);
    private ByteBuffer small = ByteBuffer.allocate(2);
    private ByteBuffer n = null;

    public DecState(TFTPMessageEncoderDecoder encDec){
        this.encDec = encDec;
    }

    public Packet process(Packet packet, byte nextByte){
        if(decType == MyEnums.DecType.NOTDEFINED){
            decType = packet.getNextType();
            if(decType == MyEnums.DecType.FINISHED)
            {
                return finishPacket(packet);
            }
            return null;
        }

        switch(decType){
            case BYTE:
                decodeOneByte(packet,nextByte);
                break;
            case SHORT:
                decodeShort(packet,nextByte);
                break;
            case N_BYTES:
                decodeNBytes(packet,nextByte);
                break;
            case STRING:
                decodeString(packet,nextByte);
                break;
        }

        if(decType == MyEnums.DecType.FINISHED) {
            return finishPacket(packet);
        }
        return null;
    }

    private void decodeNBytes(Packet packet, byte nextByte) {
        if(n == null)
            n = ByteBuffer.allocate(((Data)packet).getPacketSize());
        n.put(nextByte);
        if(!(n.hasRemaining()))
        {
            n.flip();
            packet.setNext(n.array());
            n.clear();
            n = null;
            decType = packet.getNextType();
        }
    }

    private void decodeOneByte(Packet packet, byte nextByte) {
        packet.setNext(nextByte);
        decType = packet.getNextType();
    }
    private void decodeShort(Packet packet, byte nextByte) {
        small.put(nextByte);
        if(!(small.hasRemaining())){
            packet.setNext(encDec.bytesToShort(small.array()));
            small.clear();
            decType = packet.getNextType();
        }
    }
    private void decodeString(Packet packet, byte nextByte) {
        if(nextByte != 0)
            big.put(nextByte);
        else{
            packet.setNext(bufferToString(big));
            big.clear();
            decType = packet.getNextType();
        }
    }

    private String bufferToString(ByteBuffer temp){
        temp.flip();
        byte[] str = new byte[temp.remaining()];
        temp.get(str);
        return new String(str, StandardCharsets.UTF_8);
    }

    private Packet finishPacket(Packet packet){
        encDec.setSpecialRunnable();
        decType = MyEnums.DecType.NOTDEFINED;
        return packet;
    }
}

