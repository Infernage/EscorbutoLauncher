package elr.core.modules;

import java.io.*;
import javax.swing.JProgressBar;

/**
 * Downloader class has implemented a private class which do exactly the same with no errors.
 * @author Infernage
 */
@Deprecated
public class Progress extends Thread{
    private final int MBYTES = 1048576;
    private File dst, src;
    private JProgressBar progres;
    private double tam, size = 0, offset = 0;
    private boolean exit = false;
    
    public Progress (File D, File S, JProgressBar prog){
        dst = D;
        src = S;
        progres = prog;
        tam = 1;
    }
    
    public void setSize(int size){
        this.size = size/MBYTES;
    }
    
    public void setOffset(int offset){
        this.offset = offset/MBYTES;
    }
    
    public void out(){
        exit = true;
    }
    
    public void check(File fil){
        if (fil.isFile()){
            tam = fil.length();
            return;
        }
        File[] files = fil.listFiles();
        long l = 0;
        if (files != null){
            l = files.length;
        }
        for (int i = 0; i < l; i++){
            if (files[i].isDirectory()){
                check(files[i]);
            } else{
                tam = tam + files[i].length();
            }
        }
    }
    
    @Override
    public void run(){
        double inst, perc;
        progres.setMaximum(100);
        progres.setMinimum(0);
        if (src != null){
            check(src);
            size = tam/MBYTES;
        }
        tam = 0;
        while(!exit){
            check(dst);
            inst = (tam/MBYTES) + offset;
            perc = (inst/size)*100;
            if (perc > 100){
                perc = 100;
            }
            progres.setValue((int) perc);
            progres.setString((int) perc + "%");
            tam = 0;
        }
    }
}
