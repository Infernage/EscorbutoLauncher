/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.XZ;

import MLR.InnerApi;
import MLR.XZ.xz.SeekableFileInputStream;
import MLR.XZ.xz.SeekableXZInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author Infernage
 */
public class Decoder {
    private SeekableXZInputStream input;
    private BufferedOutputStream output;
    private byte[] buffer;
    private File out;
    
    public Decoder(File file){
        try {
            input = new SeekableXZInputStream(new SeekableFileInputStream(file));
            buffer = new byte[8192];
            StringTokenizer token = new StringTokenizer(file.getName(), "_");
            String str = null;
            while (token.hasMoreTokens()){
                str = token.nextToken();
                if (str.contains("-File")){
                    break;
                }
            }
            out = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(
                    File.separator)) + File.separator + file.getName().substring(0, file.getName()
                    .lastIndexOf("_" + str)) + "." + str.split("-")[0]);
            output = new BufferedOutputStream(new FileOutputStream(out));
        } catch (Exception e) {
            InnerApi.exception(e, "Failed to initialize decoder!");
            if (input != null){
                try {
                    input.close();
                } catch (Exception ex) {
                }
            }
            if (output != null){
                try {
                    output.close();
                } catch (Exception ex) {
                }
            }
        }
    }
    public File decode() throws IOException{
        try {
            int size;
            while ((size = input.read(buffer)) != -1){
                output.write(buffer, 0, size);
            }
        } finally {
            output.close();
            input.close();
        }
        return out;
    }
}
