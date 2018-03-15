package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.tftp.TFTPMessageEncoderDecoder;
import bgu.spl171.net.impl.tftp.TFTPMessagingProtocol;
import bgu.spl171.net.srv.Server;

/**
 * Created by sheld on 1/20/2017.
 */
public class ReactorMain {
    public static void main(String[] args){
        if (args.length != 1) {
            return;
        }
        int port = Integer.parseInt(args[0]);
        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port, //port
                TFTPMessagingProtocol::new, //protocol factory
                TFTPMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
