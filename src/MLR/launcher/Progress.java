package MLR.launcher;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import javax.swing.JProgressBar;
/**
 *
 * @author Infernage
 */
public class Progress extends Thread{
    private File dst, src;
    private JProgressBar progres;
    public double tam;
    public double size = 0;
    private boolean exit = false;
    public Progress (File D, File S, JProgressBar prog){
        dst = D;
        src = S;
        progres = prog;
        tam = 1;
    }
    public void out(){
        exit = true;
    }
    public void setSize(int size){
        this.size = size/1048576;
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
        double inst = 0;
        double perc = 0;
        progres.setMaximum(100);
        progres.setMinimum(0);
        if (src != null){
            check(src);
            size = tam/1048576;
        }
        tam = 0;
        while(!exit){
            check(dst);
            inst = tam/1048576;
            perc = (inst/size)*100;
            if (perc > 100){
                perc = 100;
            }
            progres.setValue((int) perc);
            progres.setString((int) perc + "%");
            tam = 0;
        }
        progres.setValue(100);
        progres.setString(100 + "%");
    }
}
