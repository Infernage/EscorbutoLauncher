/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.XZ;

import MLR.InnerApi;
import MLR.XZ.xz.LZMA2Options;
import MLR.XZ.xz.XZOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Infernage
 */
public class Encoder {
    private int dicSize = 768 << 20;//1024*1024
    private LZMA2Options lzma;
    private XZOutputStream output;
    private BufferedInputStream input;
    private File out;
    private byte[] buffer;
    
    public Encoder(File file){
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            lzma = new LZMA2Options();
            lzma.setPreset(9);
            lzma.setDictSize(Math.min(lzma.getDictSize(), Math.max(LZMA2Options.DICT_SIZE_MIN, dicSize)));
            out = new File(file.getAbsolutePath().substring(0, 
                    file.getAbsolutePath().lastIndexOf(File.separator)) + File.separator + file.getName()
                    .substring(0, file.getName().lastIndexOf(".")) + "_" + file.getName().substring(
                    file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase() + "-File_.cxz");
            output = new XZOutputStream(new FileOutputStream(out), lzma);
            buffer = new byte[8192];
        } catch (Exception e) {
            InnerApi.exception(e, "Failed to initialize encoder!");
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
    
    public File encode() throws IOException{
        try{
            int left = dicSize;
            int size = 0;
            while ((size = input.read(buffer, 0, Math.min(buffer.length, left))) != -1){
                output.write(buffer, 0, size);
                left -= size;
                if (left == 0){
                    output.endBlock();
                    left = dicSize;
                }
            }
        } finally{
            output.finish();
            output.close();
            input.close();
        }
        return out;
    }
}
