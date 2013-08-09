package elr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JProgressBar;

/**
 *
 * @author Infernage
 */
public class Downloader {
    private MainFrame frame;
    private URL url;
    private String path;
    private String filename;
    private JProgressBar prog;
    private int totalSize;
    private int offset = 0;
    private boolean started = false;
    private DownloaderEngine engine;
    
    public Downloader(String link, MainFrame f){
        frame = f;
        try {
            url = new URL(link);
        } catch (Exception e) {
            frame.printErr(e, "Failed to read the url");
        }
    }
    
    public void load(){
        path = frame.getWorkingDir() + File.separator + "Update";
        String name = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
        if (name.contains("?dl=1")){
            name = name.replace("?dl=1", "");
        } else if (name.contains("/") || name.contains("\\") || 
                name.contains(":") || name.contains("*") ||
                name.contains("?") || name.contains("\"") ||
                name.contains("<") || name.contains(">") ||
                name.contains("|")){
            name = "Download" + name.substring(path.lastIndexOf(".") + 1);
        }
        if (name.contains("%3B")) name = name.replace("%3B", ";");
        filename = name;
    }
    
    public void setBar(JProgressBar bar){
        prog = bar;
    }
    
    public File destinyFile(){ return new File(path, filename); }
    
    public void start(){
        try {
            totalSize = url.openConnection().getContentLength();
        } catch (Exception e) {
            frame.printErr(e, "Failed to obtain content length");
        }
        File dest = new File(path);
        dest.mkdirs();
        ProgressMonitor monitor = new ProgressMonitor();
        monitor.setDaemon(true);
        frame.println("Starting transfer...");
        System.out.println("Starting transfers, please wait...");
        monitor.start();
        File dst = new File(path, filename);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + 0 + "-");
            connection.connect();
            RandomAccessFile file = new RandomAccessFile(dst, "rw");
            file.seek(0);
            engine = new DownloaderEngine(connection.getInputStream(), file, filename, 
                    totalSize);
            engine.setDaemon(true);
            engine.start();
            started = true;
        } catch (Exception e) {
            frame.printErr(e, "Transfer failed");
            dst.delete();
        }
    }
    
    /**
     * Looks if the download is done.
     * @return {@code true} if the download has finished, {@code false} in otherwise.
     */
    public boolean isDone(){
        if (engine.isAlive()) return false;
        return true;
    }
    
    private class ProgressMonitor extends Thread{
        private final int MBYTES = 1048576;
        private double perc = 0, maxSize = totalSize/MBYTES;
        
        public ProgressMonitor(){ super("ProgressMonitor"); }
        
        @Override
        public void run(){
            if (prog == null){
                consoleType();
            } else{
                progressBarType();
            }
        }
        
        private void consoleType(){
            int actual = 0, state;
            System.out.print("Total progress   :|");
            for (int i = 0; i < 10; i++) {
                System.out.print("=");
            }
            System.out.println("|\nFile size: " + maxSize + "MB");
            System.out.print("Transfer progress:|");
            try {
                while(!started) Thread.yield();
                while(actual < 10 && !isDone()){
                    double temp = ((offset/MBYTES)/maxSize)*10;
                    state = (int) temp;
                    if (state != actual){
                        System.out.print("=");
                        System.out.flush();
                        actual++;
                    }
                }
                System.out.println("| Finished");
            } catch (Exception e) {
                frame.printErr(e, "");
            }
        }
        
        private void progressBarType(){
            try {
                prog.setValue(0);
                prog.setMaximum(100);
                prog.setMinimum(0);
                while(!started) Thread.yield(); //Starts an active wait
                while(perc < 100 && !isDone()){
                    perc = ((offset/MBYTES)/maxSize)*100;
                    prog.setValue((int) perc);
                }
            } catch (Exception e) {
                //Ignore
            }
        }
    }
    
    private class DownloaderEngine extends Thread{
        private InputStream input;
        private byte[] buffer;
        private RandomAccessFile file;
        private String name;
        
        /**
         * Constructor of the engine.
         * @param stream The input stream which the engine will read.
         * @param dst The destiny file which will be stored.
         * @param nameFile The name file.
         * @param size The size of the buffer.
         */
        public DownloaderEngine(InputStream stream, RandomAccessFile dst, String nameFile, int size){
            super("DownloaderEngine");
            input = stream;
            buffer = new byte[size];
            file = dst;
            name = nameFile;
        }
        
        @Override
        public void run(){
            try {
                int read;
                while((read = input.read(buffer)) > 0){
                    file.write(buffer, 0, read);
                    offset += read;
                }
            } catch (IOException ex) {
                frame.printErr(ex, "Transfer failed");
                new File(path, name).delete();
            }
            try {
                file.close();
            } catch (Exception e) {
                //Ignore
            }
            try {
                input.close();
            } catch (Exception e) {
                //Ignore
            }
        }
    }
}
