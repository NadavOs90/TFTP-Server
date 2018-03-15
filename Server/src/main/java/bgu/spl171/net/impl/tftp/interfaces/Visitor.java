package bgu.spl171.net.impl.tftp.interfaces;

import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;

/**
 * Created by sheld on 1/20/2017.
 */
public interface Visitor {
    void visit(Rrq packet);
    void visit(Wrq packet);
    void visit(Data packet);
    void visit(Ack packet);
    void visit(Error packet);
    void visit(Dirq packet);
    void visit(Logrq packet);
    void visit(Delrq packet);
    void visit(Bcast packet);
    void visit(Disc packet);
    void visit(UnknownData packet);
}