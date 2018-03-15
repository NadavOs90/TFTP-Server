package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Ack extends Packet{
    private short blockNumber;


    public Ack(){
        this.opCode = 4;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.SHORT,MyEnums.DecType.FINISHED};
    }

    public Ack(int blockNumber){
        this.opCode = 4;
        this.blockNumber = (short)blockNumber;
    }

    public short getBlockNumber(){
        return this.blockNumber;
    }


    @Override
    public void setNext(short next) {
        this.blockNumber = next;
        location++;
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
    public void setNext(byte next) {
        //nothing to do
    }

    @Override
    public void setNext(String next) {
        //nothing to do
    }

    @Override
    public void setNext(byte[] next) {
        //nothing to do
    }


}
