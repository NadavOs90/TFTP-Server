package bgu.spl171.net.impl.tftp;

import bgu.spl171.net.impl.tftp.helpers.DataTransfer;
import bgu.spl171.net.impl.tftp.helpers.FileNameHolder;
import bgu.spl171.net.impl.tftp.packets.Data;
import bgu.spl171.net.impl.tftp.packets.Packet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by sheld on 1/20/2017.
 */
public class FileManager {
    private static class SingletonHolder{
        private static FileManager instance = new FileManager();
    }

    public static FileManager getInstance(){
        return SingletonHolder.instance;
    }


    private Vector<FileNameHolder> toBeAdded = new Vector<>();
    private Vector<FileNameHolder> inFolder = new Vector<>();
    private Path directory;


    //Constructor
    private FileManager(){
        directory = getPath();
        fillInFolder();
    }

    public void fileWritted(FileNameHolder fileLock) {
        inFolder.add(fileLock);
        toBeAdded.remove(fileLock);
    }

    public void writeFile(ArrayList<Byte> file, FileNameHolder fileHolder){
        Path filePath= Paths.get(directory.toString() + File.separator + fileHolder.getName().trim());
        try {
            BufferedWriter writer = Files.newBufferedWriter(filePath);
            file.forEach((x) -> {
                try {
                    writer.write(x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
            //finished = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataTransfer readFile(FileNameHolder file){
        if (Files.exists(Paths.get(directory.toString()+ File.separator + file.getName().trim()).toAbsolutePath().normalize())) {
            Path filePath = Paths.get(directory.toString() + File.separator + file.getName().trim()).toAbsolutePath().normalize();
            try {
                byte[] data = Files.readAllBytes(filePath);
                DataTransfer ans = new DataTransfer(data.length, data);
                return ans;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void deleteFile(FileNameHolder file){//TODO
        File toDelete = new File(directory.toString() + File.separator + file.getName());
        toDelete.delete();
        inFolder.remove(file);
    }

    public  DataTransfer getDirectoryFiles() {
        String directoryListing = "";
        for(FileNameHolder file: inFolder)
            directoryListing += file.getName() + '\0';
        return new DataTransfer(directoryListing.length(), directoryListing.getBytes());
    }

    //TODO the problem is the read file combined with the delete file. we need to assure that one cannot delete a file before all those who asked to read it have done so.

    private void fillInFolder(){
        Vector<String> files = getFilesInFolder();
        for(String file: files)
            inFolder.add(new FileNameHolder(file));
    }

    private Vector<String> getFilesInFolder(){
        Vector<String> results = new Vector<>();

        File[] files = new File(directory.toString()).listFiles();
        if(files != null)
            for (File file : files) {
                if (file.isFile()) {
                    results.add(file.getName());
                }
            }
        return results;
    }


    public synchronized FileNameHolder getLock(String fileName,boolean write){
        FileNameHolder temp = new FileNameHolder(fileName);
        FileNameHolder lock;
        if(write){
            if(getFileHolder(inFolder,temp) == null && getFileHolder(toBeAdded,temp)==null){
                toBeAdded.add(temp);
                return temp;
            }
            return null;
        }

        return getFileHolder(inFolder,temp);

    }

    private FileNameHolder getFileHolder(Vector<FileNameHolder> vector, FileNameHolder toGet){
        for(FileNameHolder h: vector){
            if(h.equals(toGet))
                return h;
        }
        return null;
    }

    private Path getPath() {
        String folder = "Files";
        try {
            if(Files.exists(Paths.get(folder).toAbsolutePath().normalize())){
                return Paths.get(folder).toAbsolutePath().normalize();
            }else {
                return Files.createDirectory(Paths.get(folder).toAbsolutePath().normalize());}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
