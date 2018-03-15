package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.tftp.TFTPMessageEncoderDecoder;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.srv.Server;

/**
 * Created by sheld on 1/21/2017.
 */
public class TPCMain {
    public static void main(String[] args){
        if (args.length != 1) {
            return;
        }
        int port = Integer.parseInt(args[0]);
        Server.threadPerClient(
                port, //port
                TFTPMessagingProtocol::new, //protocol factory
                TFTPMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
