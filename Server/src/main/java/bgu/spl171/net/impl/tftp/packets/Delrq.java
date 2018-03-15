package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Delrq extends Packet {
    private String fileName;
    private byte endByte = 0;

    public Delrq(){
        this.opCode = 8;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.STRING,MyEnums.DecType.FINISHED};
    }

    public Delrq(String fileName){
        this.opCode = 8;
        this.fileName = fileName;
    }

    public String getFileName(){return this.fileName;}



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
        this.fileName = next;
        location++;
    }

    @Override
    public void setNext(byte[] next) {

    }

    @Override
    public void setNext(short next) {

    }

    @Override
    public void setNext(byte next) {

    }
}

