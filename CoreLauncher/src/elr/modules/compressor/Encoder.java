package elr.modules.compressor;

import elr.modules.compressor.xz_coder.xz.LZMA2Options;
import elr.modules.compressor.xz_coder.xz.UnsupportedOptionsException;
import elr.modules.compressor.xz_coder.xz.XZOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Encoder using engine XZ Data Compressor.
 * @author Infernage
 */
class Encoder {
    private static int dicSize = 50 << 20;//1024*1024
    
    /**
     * Encode method using XZ Data Compressor.
     * @param input The file to be encoded.
     * @return The file encoded.
     * @throws UnsupportedOptionsException
     * @throws IOException 
     */
    static File encode(File input) 
            throws UnsupportedOptionsException, IOException{
        File output = new File(input.getParent() + File.separator + input.getName().substring(0, 
                        input.getName().lastIndexOf(".")) + "_" + input.getName().substring(input.getName()
                        .lastIndexOf(".") + 1, input.getName().length()).toLowerCase() + "-File_.cxz");
        byte[] buffer = new byte[8192];
        LZMA2Options options = new LZMA2Options();
        options.setPreset(9);
        options.setDictSize(Math.min(options.getDictSize(), Math.max(LZMA2Options.DICT_SIZE_MIN, 
                dicSize)));
        BufferedInputStream inStream = null;
        XZOutputStream outStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(input));
            outStream = new XZOutputStream(new FileOutputStream(output), options);
            _encode(inStream, outStream, buffer);
        } finally{
            if (inStream != null) inStream.close();
            if (outStream != null){
                outStream.finish();
                outStream.close();
            }
        }
        return output;
    }
    
    /**
     * Engine of the encoder.
     * @param input The inputstream of the file decoded.
     * @param output The outputstream of the file encoded.
     * @param buffer A buffer used to read and write.
     * @throws IOException 
     */
    private static void _encode(BufferedInputStream input, XZOutputStream output, byte[] buffer) 
            throws IOException{
        if ((input == null) || (output == null) || (buffer == null)) throw new NullPointerException(""
                + "Something bad happened! Stream or buffer is null!");
        int left = dicSize, size;
        while((size = input.read(buffer, 0, Math.min(buffer.length, left))) != -1){
            output.write(buffer, 0, size);
            left -= size;
            if (left == 0){
                output.endBlock();
                left = dicSize;
            }
        }
    }
}
