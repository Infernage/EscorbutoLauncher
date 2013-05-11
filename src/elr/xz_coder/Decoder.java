package elr.xz_coder;

import elr.Starter;
import elr.core.modules.AES;
import elr.xz_coder.xz.SeekableFileInputStream;
import elr.xz_coder.xz.SeekableXZInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Class used to decode files.
 * @author Infernage
 */
public class Decoder {
    
    /**
     * Decodes a file without permissions.
     * @param decrypter The AES object used to decrypt.
     * @param input The file to decode.
     * @return The file decoded.
     * @throws IOException 
     */
    public static File decode(AES decrypter, File input) throws IOException{
        return commonDecode(decrypter, input, false);
    }
    
    /**
     * Decodes a file with permissions.
     * @param obj An object used to decode.
     * @param input The file to decode.
     * @return The file decoded.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static File privilegedDecoder(Object obj, File input) throws FileNotFoundException, 
            IOException{
        if (obj instanceof Starter){
            AES decrypter = new AES(Starter.class.getCanonicalName());
            return commonDecode(decrypter, input, true);
        }
        return null;
    }
    
    /**
     * The main decode method used with or without permissions.
     * @param decrypter The AES object used to decrypt.
     * @param input The file to be decoded.
     * @param isPrivileged {@code true} if it has permissions, {@code false} if it hasn't.
     * @return The file decoded.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private static File commonDecode(AES decrypter, File input, boolean isPrivileged) 
            throws FileNotFoundException, IOException{
        File decrypted, output = null;
        if (isPrivileged){
            decrypted = decrypter.decryptFile(input, input.getName().replace(".priv", ""));
        } else{
            decrypted = decrypter.decryptFile(input);
        }
        byte[] buffer = new byte[8192];
        SeekableXZInputStream inStream = null;
        BufferedOutputStream outStream = null;
        try {
            inStream = new SeekableXZInputStream(new SeekableFileInputStream(decrypted));
            for (StringTokenizer ext = new StringTokenizer(decrypted.getName(), "_"); ext.hasMoreTokens();) {
                String token = ext.nextToken();
                if (token.contains("-File")){
                    output = new File(decrypted.getParent() + File.separator + decrypted.getName()
                            .substring(0, decrypted.getName().lastIndexOf("_" + token))
                            .replace("REPLACE", "_") + "." + token.split("-")[0]);
                    break;
                }
            }
            outStream = new BufferedOutputStream(new FileOutputStream(output));
            _decode(inStream, outStream, buffer);
        } finally {
            if (inStream != null) inStream.close();
            if (outStream != null) outStream.close();
            if (decrypted != null) decrypted.delete();
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
