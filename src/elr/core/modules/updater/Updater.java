package elr.core.modules.updater;

import elr.core.Booter;
import elr.core.Stack;
import elr.core.modules.Downloader;
import elr.core.modules.ExceptionControl;
import elr.core.modules.Files;
import elr.core.modules.IO;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;


/**
 * Class which updates the launcher.
 * @author Infernage
 */
public class Updater extends Thread{
    private String link;
    public JProgressBar prog;
    public Updater (){
        super("Updater");
    }
    
    /**
     * Starts the updater.
     * @param host The url to connect.
     */
    public void init(String host){
        link = host;
        this.start();
    }
    
    /**
     * Gives a progressbar to show the percentage.
     * @param bar The progressbar.
     */
    public void setBar(JProgressBar bar){
        prog = bar;
    }
    
    /**
     * Do a self update.
     * @param destinyFile The launcher updated. 
     */
    private void selfUpdate(File destinyFile){
        String current = Files.launcher();
        List args = new ArrayList();
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
                + (Booter.getStaticOS().equals("windows") ? "w" : "");
        args.add(java);
        args.add("-cp");
        args.add(destinyFile.getAbsolutePath());
        args.add(Updater.class.getCanonicalName()); //Executes this main
        args.add(current);
        args.add(destinyFile.getAbsolutePath());
        System.out.println("Updating...");
        ProcessBuilder proces = new ProcessBuilder(args);
        try {
            proces.start();
        } catch (Exception e) {
            ExceptionControl.severeException(4, e, "Failed to launch selfUpdater!");
        }
        System.exit(0);
    }
    
    @Override
    public void run(){
        Downloader download = new Downloader(link); //Download class
        download.load(); //Load parameters
        download.loadBar(prog); //Load the progressBar
        Stack.frame.displayUpdateMsg("Transfering...", Color.cyan);
        download.start(); //Start the download
        while(!download.isDone()) Thread.yield();
        selfUpdate(download.destinyFiles()[0]); //SelfUpdate method
    }
    
    /**
     * Main method to re-run the updated launcher.
     * @param args Main args = args[0]: Outdated launcher; args[1]: Updated launcher.
     */
    public static void main(String[] args){
        try {
            Thread.sleep(2000L);
        } catch (Exception e) {
            //Ignore
        }
        File current = new File(args[0]), updated = new File(args[1]);
        try {
            current.delete();
            IO.copyDirectory(updated, current);
            updated.delete();
        } catch (Exception e) {
            //Ignore
        }
        List arg = new ArrayList();
        arg.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        arg.add("-jar");
        arg.add(args[0]);
        ProcessBuilder builder = new ProcessBuilder(arg);
        try {
            builder.start();
        } catch (Exception e) {
            ExceptionControl.severeExceptionWOStream(4, e, "Failed to update the launcher!");
        }
        System.exit(0);
    }
}