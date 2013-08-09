package elr.modules.threadsystem;

import elr.core.Loader;
import elr.core.interfaces.Job;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import javax.swing.JProgressBar;

/**
 * Class used to download.
 * @author Infernage
 */
public class DownloadJob implements Job<Downloader, File>{
    private List<Downloader> files = Collections.synchronizedList(new ArrayList<Downloader>());
    private Queue<Downloader> remainingFiles = new ConcurrentLinkedQueue<>();
    private List<Downloader> downloadedFiles = Collections.synchronizedList(new ArrayList<Downloader>());
    private List<Downloader> fails = Collections.synchronizedList(new ArrayList<Downloader>());
    private JProgressBar progress;
    private String name;
    private ProgressMonitor monitor;
    private boolean started = false, empty = false;
    private volatile long offset = 0, totalSize = 0;
    
    /**
     * Default constructor.
     * @param name The name of the job.
     * @param prog The progress bar. Can be {@code null} if is preferrable the console system.
     */
    public DownloadJob(String name, JProgressBar prog){
        progress = prog;
        this.name = name;
    }
    
    /**
     * Adds a download to the job. It's not possible to add a download when the job is running.
     * @param job The download.
     */
    @Override
    public void addJob(Downloader job){
        if (started) throw new RuntimeException("Downloader job already started");
        files.add(job);
        remainingFiles.offer(job);
    }
    
    /**
     * Adds a download collection to the job. It's not possible to add it when the job is running.
     * @param job The download.
     */
    @Override
    public void addListJobs(Collection<Downloader> collection){
        if (started) throw new RuntimeException("Downloader job already started");
        files.addAll(collection);
        remainingFiles.addAll(collection);
    }
    
    /**
     * Starts all scheduled downloads.
     * @return A list of all files.
     * @throws InterruptedException 
     */
    @Override
    public List<Future<File>> startJob() throws InterruptedException{
        if (files.isEmpty()) empty = true;
        List<Future<File>> result = new ArrayList<>();
        for (Downloader file : files) {
            totalSize += file.getSize();
        }
        try {
            if (empty){
                Loader.getMainGui().getConsoleTab().printImportantMessage("Job \"" + name + "\" skipped.");
                return result;
            }
            waitStart();
            started = true;
            monitor = new ProgressMonitor();
            ThreadPool.getInstance().execute(monitor);
            result = ThreadPool.getInstance().invokeAll(files);
            Loader.getMainGui().getConsoleTab().printImportantMessage("Download job " + name + 
                    " finished");
        } finally{
            offset = totalSize;
            Loader.getMainGui().endReady(this);
        }
        return result;
    }
    
    /**
     * Looks if the job has finalized or not.
     * @return {@code true} if the job has finalized.
     */
    public boolean isDone(){
        return remainingFiles.isEmpty();
    }
    
    /**
     * Method used to add a finished download.
     * @param download The download finished.
     */
    void addDownloadedFile(Downloader download){
        synchronized(downloadedFiles){
            downloadedFiles.add(download);
        }
        synchronized(remainingFiles){
            remainingFiles.poll();
        }
    }
    
    /**
     * Method used to add a failed download.
     * @param download The download failed.
     */
    void addFailedDownload(Downloader download){
        fails.add(download);
        synchronized(remainingFiles){
            remainingFiles.poll();
        }
    }
    
    synchronized void addOffset(long percentage){
        offset += percentage;
    }
    
    /**
     * Wait for the progress bar of the main gui.
     */
    private void waitStart(){
        synchronized(Loader.getMainGui()){
            while(!Loader.getMainGui().isReady())
                try {
                    wait();
                } catch (Exception e) {
                    //Ignore
                }
            Loader.getMainGui().getReady("Job \"" + name + "\" in progress...");
        }
    }
    
    /**
     * Monitor of all downloads.
     */
    private class ProgressMonitor implements Runnable{
        private final int MBYTES = 1048576;
        
        @Override
        public void run() {
            if (progress == null) consoleType();
            else guiType();
        }
        
        private void consoleType(){
            System.out.println("Total progress   :|");
            for (int i = 0; i < 10; i++) {
                System.out.print("=");
            }
            System.out.println("|\nFile size: " + (totalSize/MBYTES) + "MB");
            System.out.print("Transfer progress:|");
            int bar = 0;
            while(bar < 10){
                double tmp = ((offset/MBYTES)/(totalSize/MBYTES))*10;
                int temp = (int) (tmp-bar);
                for (int i = 0; i < temp; i++) {
                    System.out.print("=");
                }
            }
            System.out.println("| Finished");
        }
        
        private void guiType(){
            double total = totalSize/MBYTES;
            progress.setValue(0);
            progress.setMinimum(0);
            progress.setMaximum(100);
            while(progress.getValue() < 100){
                double current = offset/MBYTES;
                double tmp = (current/total)*100;
                progress.setValue((int) tmp);
                progress.setString((int) tmp + "%");
            }
        }
    }
}
