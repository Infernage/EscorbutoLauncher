package elr.updater;

import elr.Downloader;
import elr.MainFrame;
import java.io.File;
import javax.swing.JProgressBar;


/**
 * Class which updates the launcher.
 * @author Infernage
 */
class Updater{
    private MainFrame frame;
    private String link;
    public JProgressBar prog;
    
    public Updater (MainFrame f){
        frame = f;
    }
    
    /**
     * Starts the updater.
     * @param host The url to connect.
     */
    public File init(String host){
        link = host;
        return start();
    }
    
    /**
     * Gives a progressbar to show the percentage.
     * @param bar The progressbar.
     */
    public void setBar(JProgressBar bar){
        prog = bar;
    }
    
    public File start(){
        Downloader download = new Downloader(link, frame); //Download class
        download.load(); //Load parameters
        download.setBar(prog); //Load the progressBar
        frame.println("Transfering...");
        download.start(); //Start the download
        while(!download.isDone());
        return download.destinyFile();
    }
}