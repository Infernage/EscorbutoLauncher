package elr.modules.threadsystem;

import elr.core.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Infernage
 */
public class GETEngine extends Downloader{
    private String[] params;
    
    /**
     * Default constructor.
     * @param url Link to the file.
     * @param j The job where is called.
     * @param target The local file where will be stored.
     * @param isDirectory {@code true} if the param target is a directory.
     * @param params (Optional) All pairs "param=value" to use.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public GETEngine(URL url, DownloadJob j, File target, boolean isDirectory, String... params) 
            throws MalformedURLException, IOException{
        super(url, j, target, isDirectory);
        this.params = params;
    }
    
    @Override
    public long getSize(){
        try {
            if (params != null && params.length > 0){
                size = Integer.parseInt(Util.requestGetMethod(url.toString(), "elrfilesize=" 
                        + params[0].split("=")[1]).split("=")[1].replace("<br>", "")
                        .replace("\r", "").replace("\n", ""));
            }
            if (size <= 0)  return super.getSize();
        } catch (Exception e) {
            //Ignore
        }
        return size;
    }
    
    @Override
    protected void download() throws Exception{
        url = new URL(Util.encodeURLGET(url.toString(), params));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        FileOutputStream out = new FileOutputStream(destiny);
        InputStream in = connection.getInputStream();
        try{
            byte[] buffer = new byte[(int)size];
            int read;
            while ((read = in.read(buffer)) > 0){
                job.addOffset(read);
                out.write(buffer, 0, read);
            }
        } catch (Exception ex){
            error = true;
            ex.printStackTrace();
            throw ex;
        } finally{
            try {
                out.close();
            } catch (Exception e) {
                //Ignore
            }
            try {
                in.close();
            } catch (Exception e) {
                //Ignore
            }
        }
    }
    
    @Override
    protected void start() throws Exception{
        if (directory){
            destiny.mkdirs();
            if (name == null){
                if (params != null && params.length > 0){
                    name = params[0].split("=")[1];
                } else{
                    name = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                }
            }
            checkNameFile();
            destiny = new File(destiny, name);
        } else destiny.getParentFile().mkdirs();
        download();
    }
}
