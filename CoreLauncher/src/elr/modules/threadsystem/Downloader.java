package elr.modules.threadsystem;

import elr.core.Loader;
import elr.core.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

/**
 * This class downloads a file from an url.
 * @author Infernage
 */
public class Downloader implements Callable<File>{
    private DownloadJob job;
    private URL url;
    private File destiny;
    private volatile long size = -1L;
    private String name = null;
    private boolean error = false, md5, started = false, directory;
    
    /**
     * Default constructor.
     * @param url Link to the file.
     * @param j The job where is called.
     * @param target The local file where will be stored.
     * @param containsMD5 {@code true} if is a MD5 download.
     * @param isDirectory {@code true} if the param target is a directory.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public Downloader(URL url, DownloadJob j, File target, boolean containsMD5, boolean isDirectory) 
            throws MalformedURLException, IOException{
        this.url = url;
        md5 = containsMD5;
        destiny = target;
        job = j;
        directory = isDirectory;
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
    
    private void checkNameFile(){
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
    
    private void normalDownload() throws Exception{
        Exception ex = null;
        RandomAccessFile raf = null;
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + 0 + "-");
            connection.connect();
            raf = new RandomAccessFile(destiny, "rw");
            raf.seek(0);
            byte[] buffer = new byte[(int)size];
            input = connection.getInputStream();
            int read;
            while((read = input.read(buffer)) > 0){
                raf.write(buffer, 0, read);
                job.addOffset(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
            ex = e;
        }
        try {
            input.close();
        } catch (Exception e) {
            //Ignore
        }
        try {
            raf.close();
        } catch (Exception e) {
            //Ignore
        }
        if (error) throw ex;
    }
    
    private void downloadMD5() throws Exception{
        if (destiny.getParentFile() != null && !destiny.getParentFile().isDirectory())
            destiny.mkdirs();
        String md5 = destiny.isFile() ? Util.getMD5(destiny) : null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
            connection.setRequestProperty("Expires", "0");
            connection.setRequestProperty("Pragma", "no-cache");
            if (md5 != null) connection.setRequestProperty("If-None-Match", md5);
            connection.connect();
            int response = connection.getResponseCode();
            int divided = response/100;
            if (response == 304) throw new RuntimeException("Used own copy as it matched etag");
            if (divided == 2){
                try(InputStream input = connection.getInputStream(); FileOutputStream output = 
                        new FileOutputStream(destiny)){
                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    byte[] buffer = new byte[65536];
                    int read = input.read(buffer);
                    while (read > 0){
                        job.addOffset(read);
                        digest.update(buffer, 0, read);
                        output.write(buffer, 0, read);
                        read = input.read(buffer);
                    }
                    md5 = String.format("%1$032x", new Object[] { new BigInteger(1, 
                            digest.digest()) });
                }
                String etag = (connection.getHeaderField("ETag") == null) ? "-" : 
                        connection.getHeaderField("ETag");
                if (etag.startsWith("\"") && etag.endsWith("\"")) etag = etag.substring(1, 
                        etag.length() - 1);
                if (etag.contains("-")) throw new RuntimeException("Etag not found");
                if (etag.equalsIgnoreCase(md5)) return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public File call() throws Exception {
        started = true;
        Loader.getMainGui().getConsoleTab().println("Downloading " + (name == null ? destiny.getName() 
                : name));
        try {
            if (directory){
                destiny.mkdirs();
                if (name == null){
                    name = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                    checkNameFile();
                } else checkNameFile();
                destiny = new File(destiny, name);
            } else destiny.getParentFile().mkdirs();
            if (md5) downloadMD5();
            else normalDownload();
            job.addDownloadedFile(this);
            Loader.getMainGui().getConsoleTab().println("Download of " + (name == null ? destiny
                    .getName() : name) + " finished correctly.");
            return destiny;
        } catch (Exception e) {
            Loader.getMainGui().getConsoleTab().printErr(e, "Failed download: " + (name == null ? 
                    destiny.getName() : name), 2, this.getClass());
            destiny.delete();
            job.addFailedDownload(this);
        }
        return null;
    }
}
