package bgu.spl171.net.impl.tftp.states.ProtocolStates;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.impl.tftp.interfaces.Visitor;
import bgu.spl171.net.impl.tftp.packets.Packet;

/**
 * Created by sheld on 1/20/2017.
 */
public abstract class ProtocolState implements Visitor {
    protected TFTPMessagingProtocol protocol;
    protected int connectionId;
    protected Connections<Packet> connections;

}
