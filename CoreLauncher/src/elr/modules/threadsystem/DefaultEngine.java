package elr.modules.threadsystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class downloads a file from an url.
 * @author Infernage
 */
public class DefaultEngine extends Downloader{
    
    /**
     * Default constructor.
     * @param url Link to the file.
     * @param j The job where is called.
     * @param target The local file where will be stored.
     * @param isDirectory {@code true} if the param target is a directory.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public DefaultEngine(URL url, DownloadJob j, File target, boolean isDirectory) 
            throws MalformedURLException, IOException{
        super(url, j, target, isDirectory);
    }

    @Override
    protected void download() throws Exception{
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

    @Override
    protected void start() throws Exception {
        if (directory){
            destiny.mkdirs();
            if (name == null) name = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
            checkNameFile();
            destiny = new File(destiny, name);
        } else destiny.getParentFile().mkdirs();
        download();
    }
}
