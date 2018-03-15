package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Rrq extends Packet{
    private String fileName;
    private byte endByte = 0;

    public Rrq(){
        this.opCode = 1;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.STRING,MyEnums.DecType.FINISHED};
    }

    public Rrq(String fileName){
        this.opCode = 1;
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
