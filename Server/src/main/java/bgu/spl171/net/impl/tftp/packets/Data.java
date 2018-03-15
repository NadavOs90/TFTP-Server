package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

import java.util.Arrays;

/**
 * Created by sheld on 1/20/2017.
 */
public class Data extends Packet{
    private short packetSize;
    private short blockNum;
    private byte[] data;

    public Data(){
        this.opCode = 3;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.SHORT,MyEnums.DecType.SHORT, MyEnums.DecType.N_BYTES,MyEnums.DecType.FINISHED};
    }

    public Data(short packetSize, short blockNum, byte[] data){
        this.opCode = 3;
        this.packetSize = packetSize;
        this.blockNum = blockNum;
        this.data = data;
    }


    public short getPacketSize(){
        return this.packetSize;
    }
    public short getBlockNum(){
        return this.blockNum;
    }
    public byte[] getData(){
        return this.data;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }



    @Override
    public byte[] accept(VisitorEnc visitor) {
        return visitor.visit(this);
    }

    @Override
    public void setNext(String next) {

    }

    @Override
    public void setNext(byte[] next) {
        this.data = next;
        location++;
    }

    @Override
    public void setNext(short next) {
        if(location++ == 0)
            this.packetSize = next;
        else
            this.blockNum = next;
    }

    @Override
    public void setNext(byte next) {

    }
}
