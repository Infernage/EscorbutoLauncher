package Login;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
/**
 *
 * @author Reed
 */
public class Executer extends Thread{
    private File dst;
    public double tam;
    public double size = 1;
    private boolean exit = false;
    public Executer (File D){
        dst = D;
        tam = 1;
    }
    public void out(){
        exit = true;
    }
    public void buscar(File fil){
        File[] files = fil.listFiles();
        long l = 0;
        if (files != null){
            l = files.length;
        }
        for (int i = 0; i < l; i++){
            if (files[i].isDirectory()){
                buscar(files[i]);
            } else{
                tam = tam + files[i].length();
            }
        }
    }
    @Override
    public void run(){
        double inst = 0;
        double perc = 0;
        if (Mainclass.OS.equals("windows")){
            buscar(new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Data\\.minecraft"));
        } else if (Mainclass.OS.equals("linux")){
            buscar(new File(System.getProperty("user.home") + "/.Data/.minecraft"));
        }
        size = tam/1048576;
        tam = 0;
        while(!exit){
            buscar(dst);
            inst = tam/1048576;
            perc = (inst/size)*100;
            if (perc > 100){
                perc = 100;
            }
            Vista2.jProgressBar1.setValue((int) perc);
            Vista2.jProgressBar1.setString((int) perc + "%");
            tam = 0;
        }
        Vista2.jProgressBar1.setValue(100);
        Vista2.jProgressBar1.setString(100 + "%");
    }
}
