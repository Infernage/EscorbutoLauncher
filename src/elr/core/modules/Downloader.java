package elr.core.modules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * This class downloads a file from an url.
 * @author Infernage
 */
public class Downloader{
    private URL[] url;
    private String[] fileNames;
    private String destPath;
    private JProgressBar progress;
    private int totalSize = 0, offset = 0;
    private List<DownloaderEngine> engines;
    private boolean error = false, started = false;
    
    /**
     * Constructor of the class.
     * @param url The URLs to download.
     */
    public Downloader(String... url){
        try {
            this.url = new URL[url.length];
            for (int i = 0; i < url.length; i++) {
                this.url[i] = new URL(url[i]);
            }
        } catch (Exception e) {
            ExceptionControl.showException(3, e, null);
        }
    }
    
    /**
     * Constructor of the class.
     * @param url The URLs created to download.
     */
    public Downloader(URL... url){
        this.url = url;
    }
    
    /**
     * Gets the URLs loaded.
     * @return The URLs loaded.
     */
    public URL[] getUrl(){ return url; }
    
    /**
     * Assigns a name to each destiny file in a path.
     * @param names The names of destiny files.
     * @param path The path to save the files.
     */
    public void load(String[] names, String path){
        fileNames = names;
        destPath = path;
    }
    
    /**
     * Assigns a path to the destiny files.
     * @param path The path to assign.
     */
    public void load(String path){
        String[] names = new String[url.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = url[i].getFile().substring(url[i].getFile().lastIndexOf("/") + 1);
            if (names[i].contains("?dl=1")){
                names[i] = names[i].replace("?dl=1", "");
            } else if (names[i].contains("/") || names[i].contains("\\") || 
                    names[i].contains(":") || names[i].contains("*") ||
                    names[i].contains("?") || names[i].contains("\"") ||
                    names[i].contains("<") || names[i].contains(">") ||
                    names[i].contains("|")){
                names[i] = "Download" + names[i].substring(path.lastIndexOf(".") + 1);
            }
            if (names[i].contains("%3B")) names[i] = names[i].replace("%3B", ";");
        }
        load(names, path);
    }
    
    /**
     * Assigns the default path and name files.
     */
    public void load(){
        load(Directory.root() + File.separator + "Update");
    }
    
    /**
     * Assigns a JProgressBar.
     * @param prog The JProgressBar to assign.
     */
    public void loadBar(JProgressBar prog){
        if (prog != null){
            progress = prog;
        } else{
            progress = new JProgressBar();
        }
    }
    
    /**
     * Assigns a size to the download.
     * @param size The size of download in bytes.
     */
    public void setSize(int size){ totalSize = size; }
    
    /**
     * Gets the destiny files ubication.
     * @return The destiny files ubication.
     */
    public File[] destinyFiles(){
        File[] files = new File[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            files[i] = new File(destPath, fileNames[i]);
        }
        return files;
    }
    
    /**
     * Breaks all DownloaderEngine classes due to an external error.
     */
    public void breakByError(){ error = true; }
    
    /**
     * Starts the download.
     * @throws RuntimeException if the total size is not defined.
     */
    public void start() throws RuntimeException{
        if (totalSize == 0) throw new RuntimeException("Total size is not defined!");
        engines = new ArrayList<>();
        File path = new File(destPath);
        path.mkdirs();
        ProgressMonitor monitor = new ProgressMonitor();
        monitor.setDaemon(true);
        monitor.start();
        for (int i = 0; i < fileNames.length; i++) {
            File dst = new File(destPath, fileNames[i]);
            System.out.println("Starting protocols");
            try {
                System.out.println("Setting request properties");
                HttpURLConnection connection = (HttpURLConnection) url[i].openConnection();
                connection.setRequestProperty("Range", "bytes=" + 0 + "-");
                connection.connect();
                RandomAccessFile file = new RandomAccessFile(destPath + File.separator + fileNames[i], "rw");
                file.seek(0);
                System.out.println("Starting engines");
                DownloaderEngine engine = new DownloaderEngine(connection.getInputStream(), file, 
                        fileNames[i], connection.getContentLength());
                engine.setDaemon(true);
                engine.start();
                if (!started) started = true;
                engines.add(engine);
            } catch (Exception e) {
                ExceptionControl.showException(3, e, "Transfer failed!");
                dst.delete();
            }
        }
    }
    
    /**
     * Looks if the download is done.
     * @return {@code true} if the download has finished, {@code false} in otherwise.
     */
    public boolean isDone(){
        if (engines.isEmpty()) return false;
        for (DownloaderEngine thread : engines) {
            if(thread.isAlive()) return false;
        }
        return true;
    }
    
    /**
     * Increases the offset.
     * @param lenght The number to add.
     */
    private synchronized void increase(int lenght){
        offset += lenght;
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
            input = stream;
            buffer = new byte[size];
            file = dst;
            name = nameFile;
        }
        
        @Override
        public void run(){
            try {
                System.out.flush();
                System.out.println("Starting transfer");
                int read;
                while((read = input.read(buffer)) > 0 && !error){
                    file.write(buffer, 0, read);
                    increase(read);
                }
                System.out.println("Transfer done");
            } catch (IOException ex) {
                ExceptionControl.showException(3, ex, "Transfer failed!");
                new File(destPath, name).delete();
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
    
    private class ProgressMonitor extends Thread{
        private final int MBYTES = 1048576;
        private double perc = 0, maxSize = totalSize/MBYTES;
        
        @Override
        public void run(){
            try {
                progress.setValue(0);
                progress.setMaximum(100);
                progress.setMinimum(0);
                while(!started) Thread.yield(); //Starts an active wait
                while(perc < 100 && !isDone() && !error){
                    perc = ((offset/MBYTES)/maxSize)*100;
                    progress.setValue((int) perc);
                    progress.setString((int) perc + "%");
                }
            } catch (Exception e) {
            }
        }
    }
}
