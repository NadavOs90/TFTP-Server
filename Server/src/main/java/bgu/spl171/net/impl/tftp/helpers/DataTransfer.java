package bgu.spl171.net.impl.tftp.helpers;

import bgu.spl171.net.impl.tftp.packets.Data;

import java.util.Arrays;

/**
 * Created by sheld on 1/21/2017.
 */
public class DataTransfer {
    private int dataSize;
    private byte[] data;
    private short blockNumber = 1;
    private boolean empty = false;

    public DataTransfer(int dataSize, byte[] data){
        this.blockNumber = blockNumber;
        this.dataSize = dataSize;
        this.data = data;
        if(dataSize == 0)
            empty = true;
    }

    public boolean isEmpty(){
        return this.empty;
    }


    public Data getNextPacket(){
        return nextPacket();
    }

    private Data nextPacket() {
        /*Data temp;
        if(dataSize >= 512){
            byte[] send = Arrays.copyOfRange(this.data,0,512);
            temp = new Data((short)512, (short)blockNumber++,send);
            byte[] currentData = Arrays.copyOfRange(this.data,512,dataSize);
            this.data = currentData;
            this.dataSize -= 512;
        }else{
           temp = new Data((short)this.dataSize, (short)this.blockNumber++,this.data);
        }
        return temp;*/
        if (dataSize < 512) {
            //isRemains = false;
            return new Data((short) dataSize, blockNumber++, data);
        }
        //isRemains = true;
        byte[] origin = data;
        byte[] tmp = Arrays.copyOfRange(origin, 0, 512);
        byte[] remains = Arrays.copyOfRange(origin, 512, origin.length);
        dataSize = remains.length;
        data = remains;
        return new Data((short) 512, blockNumber++, tmp);
    }

}
