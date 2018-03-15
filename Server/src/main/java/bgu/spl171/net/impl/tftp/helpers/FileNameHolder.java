package bgu.spl171.net.impl.tftp.helpers;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sheld on 1/20/2017.
 */
public class FileNameHolder {
    private String name;
    private AtomicInteger readers = new AtomicInteger(0);
    private AtomicBoolean deleted = new AtomicBoolean(false);
    private Runnable runnable = () -> {};


    //TODO remember we must use synchronized on this object when using its methods.
    public FileNameHolder(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void incReaders(){
        this.readers.incrementAndGet();
    }

    public void decrementReaders(){
        if(this.readers.decrementAndGet() <= 0) {
            runnable.run();
            runnable = () -> {};
        }
    }

    public void setRunnable(Runnable runnable){
        this.runnable = runnable;
    }

    public boolean isDeleted(){
        return this.deleted.get();
    }

    public void setDeleted(){
        this.deleted.set(true);
        if(this.readers.get() == 0)
            runnable.run();
    }
    @Override
    public boolean equals(Object other){
        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof FileNameHolder)) return false;
        return this.name.equals(((FileNameHolder)other).name);
    }
}
