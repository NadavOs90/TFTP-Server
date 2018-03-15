package bgu.spl171.net.impl.tftp;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.impl.tftp.packets.Packet;
import bgu.spl171.net.impl.tftp.packets.PacketFactory;
import bgu.spl171.net.impl.tftp.states.EncDecStates.DecState;
import bgu.spl171.net.impl.tftp.states.EncDecStates.EncState;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by sheld on 1/20/2017.
 */
public class TFTPMessageEncoderDecoder implements MessageEncoderDecoder<Packet> {
    private final ByteBuffer opCode = ByteBuffer.allocate(2);
    private Packet packet = null;
    private Runnable runnable = ()-> {};

    private DecState decState = new DecState(this);
    private EncState encState = new EncState(this);


    @Override
    public Packet decodeNextByte(byte nextByte) {
        runnable.run(); //TODO remember to change Runnable in this class from decState
        if(packet != null)
            return decState.process(packet,nextByte);

        opCode.put(nextByte);
        if(!opCode.hasRemaining()){
            packet = PacketFactory.getPacket(bytesToShort(opCode.array()));
            return decState.process(packet,nextByte);
        }

        return null;
    }


    @Override
    public byte[] encode(Packet message) throws UnsupportedEncodingException {
        return message.accept(encState);
    }


    public void setDefaultRunnable(){
        runnable = () -> {};
    }

    public void setSpecialRunnable(){
        runnable = () -> {
            packet = null;
            opCode.clear();
            setDefaultRunnable();
        };
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }




}
