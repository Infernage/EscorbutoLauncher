/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.modules.threadsystem;

import elr.core.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

/**
 *
 * @author Infernage
 */
public class MD5Engine extends Downloader{
    
    /**
     * Default constructor.
     * @param url Link to the file.
     * @param j The job where is called.
     * @param target The local file where will be stored.
     * @param isDirectory {@code true} if the param target is a directory.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public MD5Engine(URL url, DownloadJob j, File target, boolean isDirectory) 
            throws MalformedURLException, IOException{
        super(url, j, target, isDirectory);
    }
    
    @Override
    protected void download() throws Exception{
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
