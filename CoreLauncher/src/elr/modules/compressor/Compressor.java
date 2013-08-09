package elr.modules.compressor;

import elr.modules.compressor.xz_coder.xz.UnsupportedOptionsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * Compressor module. Supports basic Zip compression, XZ encode and AES encryptation.
 * @author Infernage
 */
public class Compressor {
    private static final int MAXSPLITLENGTH = 10485760;
    
    /**
     * Selects the compression level.
     */
    public static enum CompressionLevel { FAST, MIN, NORMAL, MAX, ULTRA, NOCOMPRESS }
    
    /**
     * Basic compression.
     * @param src The file to compress.
     * @param dst The Zip file where will be compressed.
     * @param level The compression level.
     * @throws ZipException 
     */
    public static void basicCompression(File src, File dst, CompressionLevel level) throws 
            ZipException{
        if (level == CompressionLevel.NOCOMPRESS) return;
        if (dst.isDirectory()) throw new RuntimeException("Destiny file can't be a directory");
        ZipFile zip = new ZipFile(dst);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        if (level == null) level = CompressionLevel.NORMAL;
        switch(level){
            case FAST: parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
                break;
            case MIN: parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FAST);
                break;
            case NORMAL: parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
                break;
            case MAX: parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
                break;
            case ULTRA: parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
                break;
        }
        File temp = new File(src.getParent(), "Compressed");
        src.renameTo(temp);
        try {
            if (temp.isDirectory()) zip.createZipFileFromFolder(temp, parameters, false, MAXSPLITLENGTH);
            else zip.createZipFile(temp, parameters, false, MAXSPLITLENGTH);
            try {
                Thread.sleep(250);
            } catch (Exception e) {
                //Ignore
            }
        } finally{
            temp.renameTo(src);
        }
    }
    
    /**
     * Crypted compression. Uses AES algorithm to crypt it.
     * @param src The file to compress.
     * @param level The compression level.
     * @param pass The password used to encrypt. (Optional)
     * @return The file crypted.
     * @throws ZipException 
     */
    public static File cryptedCompression(File src, CompressionLevel level, String pass) 
            throws ZipException{
        File dst = new File(src.getParentFile(), src.getName() + ".zip");
        basicCompression(src, dst, level);
        AES crypter = new AES();
        if (pass != null) crypter.setPassword(pass);
        if (level == CompressionLevel.NOCOMPRESS) return crypter.encryptFile(src);
        File crypted = crypter.encryptFile(dst);
        dst.delete();
        return crypted;
    }
    
    /**
     * Coded compression. Uses XZ Data Compressor.
     * @param src The file to compress.
     * @param level The compression level.
     * @return The file encoded.
     * @throws ZipException
     * @throws UnsupportedOptionsException
     * @throws IOException 
     */
    public static File codedCompression(File src, CompressionLevel level) throws
            ZipException, UnsupportedOptionsException, IOException{
        File dst = new File(src.getParentFile(), src.getName() + ".zip");
        basicCompression(src, dst, level);
        if (level == CompressionLevel.NOCOMPRESS) return Encoder.encode(src);
        File encoded = Encoder.encode(dst);
        dst.delete();
        return encoded;
    }
    
    /**
     * Combines all compression types.
     * @param src The file to compress.
     * @param level The compression level.
     * @param pass The password used to encrypt. (Optional)
     * @return The file crypted and encoded.
     * @throws ZipException
     * @throws UnsupportedOptionsException
     * @throws IOException 
     */
    
    public static File secureCompression(File src, CompressionLevel level, String pass) 
            throws ZipException, UnsupportedOptionsException, IOException{
        File dst = new File(src.getParentFile(), src.getName() + ".zip");
        basicCompression(src, dst, level);
        AES crypter = new AES();
        if (pass != null) crypter.setPassword(pass);
        File crypted;
        if (level != CompressionLevel.NOCOMPRESS){
            crypted = crypter.encryptFile(dst);
            dst.delete();
        } else crypted = crypter.encryptFile(src);
        File encoded = Encoder.encode(crypted);
        crypted.delete();
        return encoded;
    }
    
    /**
     * Basic decompression.
     * @param src The Zip file to decompress.
     * @param dst The file where will be extracted.
     * @return The file extracted.
     * @throws ZipException 
     */
    public static File basicDecompression(File src, File dst) throws ZipException{
        ZipFile zip = new ZipFile(src);
        File result;
        if (dst.isDirectory()){
            zip.extractAll(dst.getAbsolutePath());
            result = new File(dst, "Compressed");
        } else if (dst.isFile()){
            zip.extractAll(dst.getParent());
            result = new File(dst.getParent(), "Compressed");
        }
        else throw new ZipException("Output file can not be recognized");
        return result;
    }
    
    /**
     * Crypted decompression. Uses AES algorithm to decrypt it.
     * @param src The file crypted.
     * @param pass The password of the crypted file. (Optional)
     * @return The file extracted.
     * @throws ZipException 
     */
    public static File cryptedDecompression(File src, String pass, boolean isCompress) 
            throws ZipException{
        AES decrypter = new AES();
        if (pass != null) decrypter.setPassword(pass);
        File decrypted = decrypter.decryptFile(src);
        if (!isCompress) return decrypted;
        File dst = new File(src.getParent());
        File decompressed = basicDecompression(decrypted, dst);
        decrypted.delete();
        return decompressed;
    }
    
    /**
     * Coded decompression Uses XZ Data Compressor.
     * @param src The file encoded.
     * @return The file extracted.
     * @throws ZipException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static File codedDecompression(File src, boolean isCompress) throws ZipException, 
            FileNotFoundException, IOException{
        File decoded = Decoder.decode(src);
        if (!isCompress) return decoded;
        File dst = new File(src.getParent());
        File decompressed = basicDecompression(src, dst);
        decoded.delete();
        return decompressed;
    }
    
    /**
     * Combines all decompression types.
     * @param src The file crypted and coded.
     * @param pass The password of the encrypted file. (Optional)
     * @return The file extracted.
     * @throws ZipException
     * @throws UnsupportedOptionsException
     * @throws IOException 
     */
    public static File secureDecompression(File src, String pass, boolean isCompress) 
            throws ZipException, UnsupportedOptionsException, IOException{
        AES decrypter = new AES();
        if (pass != null) decrypter.setPassword(pass);
        File decoded = Decoder.decode(src);
        File decrypted = decrypter.decryptFile(decoded);
        decoded.delete();
        if (!isCompress) return decrypted;
        File dst = new File(src.getParent());
        File decompressed = basicDecompression(decrypted, dst);
        decrypted.delete();
        return decompressed;
    }
}
