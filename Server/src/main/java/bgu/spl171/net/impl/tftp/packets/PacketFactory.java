package bgu.spl171.net.impl.tftp.packets;

/**
 * Created by sheld on 1/20/2017.
 */
public class PacketFactory {
    public static Packet getPacket(short opCode){
        switch(opCode){
            case 1: return new Rrq();
            case 2: return new Wrq();
            case 3: return new Data();
            case 4: return new Ack();
            case 5: return new Error();
            case 6: return new Dirq();
            case 7: return new Logrq();
            case 8: return new Delrq();
            case 9: return new Bcast();
            case 10: return new Disc();
            default: return new UnknownData();
        }
    }
}
