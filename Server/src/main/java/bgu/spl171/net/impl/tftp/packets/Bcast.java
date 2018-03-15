package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Bcast extends Packet {
    private byte action;
    private String fileName;
    private byte endByte = 0;

    public Bcast(){
        this.opCode = 9;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.BYTE, MyEnums.DecType.STRING,MyEnums.DecType.FINISHED};
    }

    public Bcast(byte action, String fileName){
        this.opCode = 9;
        this.action = action;
        this.fileName = fileName;
    }

    public byte getAction(){
        return this.action;
    }
    public String getFileName(){
        return this.fileName;
    }
    public byte getEndByte(){ return this.endByte; }


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
        this.action = next;
        location++;
    }
}
