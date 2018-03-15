package bgu.spl171.net.impl.tftp.packets;

import bgu.spl171.net.impl.tftp.enums.MyEnums;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.interfaces.VisitorEnc;

/**
 * Created by sheld on 1/20/2017.
 */
public class Dirq extends Packet {

    public Dirq(){
        this.opCode = 6;
        this.types = new MyEnums.DecType[] {MyEnums.DecType.FINISHED};
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

    }

    @Override
    public void setNext(short next) {

    }

    @Override
    public void setNext(byte next) {

    }
}
