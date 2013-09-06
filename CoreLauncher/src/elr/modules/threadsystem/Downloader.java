package elr.modules.threadsystem;

import elr.core.Loader;
import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * This class downloads a file from an url.
 * @author Infernage
 */
public abstract class Downloader implements Callable<File>{
    protected DownloadJob job;
    protected URL url;
    protected File destiny;
    protected volatile long size = -1L;
    protected String name = null;
    protected boolean error = false, directory;
    private boolean started = false;
    
    /**
     * Default constructor.
     * @param url Link to the file.
     * @param j The job where is called.
     * @param target The local file where will be stored.
     * @param isDirectory {@code true} if the param target is a directory.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public Downloader(URL url, DownloadJob j, File target, boolean isFolder){
        this.url = url;
        job = j;
        destiny = target;
        directory = isFolder;
    }
    
    public void setSize(long size){
        if (!started) this.size = size;
    }
    
    public void setName(String name){
        if (!started) this.name = name;
    }
    
    public long getSize(){
        try {
            if (size <= 0) size = url.openConnection().getContentLength();
        } catch (Exception e) {
            //Ignore
        }
        return size;
    }
    
    /**
     * Gets the URLs loaded.
     * @return The URLs loaded.
     */
    public URL getUrl(){ return url; }
    
    protected void checkNameFile(){
        if (name.contains("?dl=1")){
            name = name.replace("?dl=1", "");
        }
        if (name.contains("/") || name.contains("\\") || 
                name.contains(":") || name.contains("*") ||
                name.contains("?") || name.contains("\"") ||
                name.contains("<") || name.contains(">") ||
                name.contains("|")){
            name = "Download" + name.substring(destiny.getAbsolutePath().lastIndexOf(".") + 1);
        }
        if (name.contains("%3B")) name = name.replace("%3B", ";");
    }
    
    protected abstract void download() throws Exception;
    
    protected abstract void start() throws Exception;
    
    @Override
    public File call() throws Exception{
        started = true;
        Loader.getMainGui().getConsoleTab().println("Downloading " + (name == null ? destiny.getName() 
                : name));
        try {
            start();
            job.addDownloadedFile(this);
            Loader.getMainGui().getConsoleTab().println("Download of " + (name == null ? destiny
                    .getName() : name) + " finished correctly.");
            return destiny;
        } catch (Exception e) {
            Loader.getMainGui().getConsoleTab().printErr(e, "Failed download: " + (name == null ? 
                    destiny.getName() : name), 2, this.getClass());
            destiny.delete();
            job.addFailedDownload(this);
            e.printStackTrace();
        }
        return null;
    }
}
