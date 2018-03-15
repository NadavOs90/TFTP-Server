package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Logrq extends Packet {
    private String userName;
    private byte endByte = 0;

    public Logrq(){
        this.opCode = 7;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.STRING,MyEnums.DecType.FINISHED};
    }

    public Logrq(String userName){
        this.opCode = 7;
        this.userName = userName;
    }

    public String getUserName(){return this.userName;}



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
        this.userName = next;
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
