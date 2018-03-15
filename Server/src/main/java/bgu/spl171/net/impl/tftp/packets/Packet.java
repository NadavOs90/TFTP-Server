package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitable;

/**
 * Created by sheld on 1/20/2017.
 */
public abstract class Packet implements Visitable {
    protected short opCode;
    protected int location = 0;
    protected MyEnums.DecType[] types;

    public MyEnums.DecType getNextType(){
        return types[location];
    }

    public abstract void setNext(String next);
    public abstract void setNext(byte[] next);
    public abstract void setNext(short next);
    public abstract void setNext(byte next);

    public short getOpCode(){
        return this.opCode;
    }

}
