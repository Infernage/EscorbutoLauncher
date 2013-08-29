package elr.modules.compressor;

import elr.modules.compressor.xz_coder.xz.SeekableFileInputStream;
import elr.modules.compressor.xz_coder.xz.SeekableXZInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Decoder using engine XZ Data Compressor.
 * @author Infernage
 */
class Decoder {
    /**
     * The main decode method used with or without permissions.
     * @param decrypter The AES object used to decrypt.
     * @param input The file to be decoded.
     * @param isPrivileged {@code true} if it has permissions, {@code false} if it hasn't.
     * @return The file decoded.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    static File decode(File input) throws FileNotFoundException, IOException{
        File output = null;
        byte[] buffer = new byte[8192];
        SeekableXZInputStream inStream = null;
        BufferedOutputStream outStream = null;
        try {
            inStream = new SeekableXZInputStream(new SeekableFileInputStream(input));
            for (StringTokenizer ext = new StringTokenizer(input.getName(), "_"); ext.hasMoreTokens();) {
                String token = ext.nextToken();
                if (token.contains("-File")){
                    output = new File(input.getParent() + File.separator + input.getName()
                            .substring(0, input.getName().lastIndexOf("_" + token))
                            .replace("REPLACE", "_") + "." + token.split("-")[0]);
                    outStream = new BufferedOutputStream(new FileOutputStream(output));
                    _decode(inStream, outStream, buffer);
                    return output;
                }
            }
            byte[] info = new byte[inStream.read()];
            inStream.read(info);
            String codedInfo = new String(info, "utf-8").replace("||", "");
            output = new File(input.getParent(), input.getName().substring(0, input.getName()
                    .lastIndexOf(".")) + "." + codedInfo.split("=")[1]);
            outStream = new BufferedOutputStream(new FileOutputStream(output));
            _decode(inStream, outStream, buffer);
        } finally {
            if (inStream != null) inStream.close();
            if (outStream != null) outStream.close();
        }
        return output;
    }
    
    /**
     * Engine of the decoder.
     * @param input The inputstream of the encoded file.
     * @param output The outputstream of the decoded file.
     * @param buffer A buffer used to read and write.
     * @throws IOException 
     */
    private static void _decode(SeekableXZInputStream input, BufferedOutputStream output, byte[] buffer) 
            throws IOException{
        if ((input == null) || (output == null) || (buffer == null)) throw new NullPointerException(""
                + "Something bad happened! Stream or buffer is null!");
        int size;
        while((size = input.read(buffer)) != -1){
            output.write(buffer, 0, size);
        }
    }
}
