package MLR.launcher.updater;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import MLR.InnerApi;
import MLR.Starter;
import MLR.gui.Gui;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
/**
 *
 * @author Infernage
 */
public class Updater extends Thread{
    private String link;
    public JProgressBar prog;
    public boolean init = false, started = false, finish = false;
    public Updater (){
        super("Updater");
    }
    public void init(String host){
        link = host;
        init = true;
        prog = Gui.jProgressBar1;
    }
    private void selfUpdate(File destinyFile){
        String current = null;
        try {
            current = URLDecoder.decode(new File(Starter.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath()).getCanonicalPath(), "UTF-8"); //Gets the jar ubication
        } catch (Exception e) {
            InnerApi.fatalException(e, "Failed to launch selfUpdater!", 4);
        }
        List args = new ArrayList();
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        args.add(java);
        args.add("-cp");
        args.add(destinyFile.getAbsolutePath());
        args.add(Updater.class.getCanonicalName()); //Executes this main
        args.add(current);
        args.add(destinyFile.getAbsolutePath());
        System.out.println("Updating version!");
        ProcessBuilder proces = new ProcessBuilder(new String[0]);
        proces.command(args);
        try {
            proces.start();
        } catch (Exception e) {
            InnerApi.fatalException(e, "Failed to launch selfUpdater!", 4);
        }
        finish = true;
        System.exit(0);
    }
    //Método de ejecución
    @Override
    public void run(){
        started = true;
        InnerApi.Downloader download = new InnerApi.Downloader(link); //Download class
        download.load(); //Load parameters
        download.loadBar(prog); //Load the progressBar
        download.start(); //Start the download
        selfUpdate(download.destinyFile()); //SelfUpdate method
    }
    
    public static void main(String[] args){
        try {
            Thread.sleep(2000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File current = new File(args[0]), updated = new File(args[1]);
        try {
            current.delete();
            InnerApi.IO.copyDirectory(updated, current);
            updated.delete();
        } catch (Exception e) {
        }
        List arg = new ArrayList();
        arg.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        arg.add("-jar");
        arg.add(args[0]);
        ProcessBuilder builder = new ProcessBuilder(new String[0]);
        builder.command(arg);
        try {
            builder.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to update the launcher!", "Updated failed", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(4);
        }
        System.exit(0);
    }
}