package bgu.spl171.net.impl.tftp.interfaces;

import bgu.spl171.net.impl.tftp.packets.*;
import bgu.spl171.net.impl.tftp.packets.Error;
/**
 * Created by sheld on 1/20/2017.
 */
public interface VisitorEnc {
    byte[] visit(Rrq packet);
    byte[] visit(Wrq packet);
    byte[] visit(Data packet);
    byte[] visit(Ack packet);
    byte[] visit(Error packet);
    byte[] visit(Dirq packet);
    byte[] visit(Logrq packet);
    byte[] visit(Delrq packet);
    byte[] visit(Bcast packet);
    byte[] visit(Disc packet);
    byte[] visit(UnknownData packet);
}
