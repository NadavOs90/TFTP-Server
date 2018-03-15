package bgu.spl171.net.impl.tftp.interfaces;

/**
 * Created by sheld on 1/20/2017.
 */
public interface Visitable {
    void accept(Visitor visitor);
    byte[] accept(VisitorEnc visitor);
}
