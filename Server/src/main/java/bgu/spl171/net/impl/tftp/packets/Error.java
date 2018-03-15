package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Error extends Packet {
    private short errorCode;
    private String errorMessage;
    private byte endByte = 0;

    public Error(){
        this.opCode = 5;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.SHORT, MyEnums.DecType.STRING,MyEnums.DecType.FINISHED};
    }

    public Error(short errorCode, String errorMessage){
        this.opCode = 5;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public short getErrorCode() {
        return this.errorCode;
    }
    public String getErrorMessage(){
        return this.errorMessage;
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
        this.errorMessage = next;
        location++;
    }

    @Override
    public void setNext(byte[] next) {

    }

    @Override
    public void setNext(short next) {
        this.errorCode = next;
        location++;
    }

    @Override
    public void setNext(byte next) {

    }
}
