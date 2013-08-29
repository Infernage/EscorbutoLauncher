package elr.modules.compressor;

import elr.core.util.MessageControl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class used to crypt with AES algorithm.
 * @author Infernage
 */
class AES{
    private byte[] password;
    private SecretKey sKey;
    private Cipher encrypt;
    private Cipher decrypt;
    private byte[] buffer;
    private String encoded;
    private String code = "UTF-16LE";
    private String ALG = "AES";
    
    AES(){
        buffer = new byte[1024];
        //Default password
        password = new byte[]{ (byte) 0x45, (byte) 0x73, (byte) 0x63, (byte) 0x6F, (byte) 0x72,
                (byte) 0x62, (byte) 0x75, (byte) 0x74, (byte) 0x6F, (byte) 0x5F, (byte) 0x4E,
                (byte) 0x65, (byte) 0x74, (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6B };
        init();
    }
    
    /**
     * Sets a new password.
     * @param password The new password to set.
     */
    void setPassword(String password){
        try {
            this.password = password.getBytes(code);
            init();
        } catch (UnsupportedEncodingException ex) {
            MessageControl.showExceptionMessage(3, ex, "Bytes not supported");
        }
    }
    
    /**
     * Crypts a message.
     * @param msg The message to crypt.
     * @return The message crypted.
     */
    String encryptData(String msg){
        byte[] raw = stringToHex(encoded);
        SecretKeySpec skeyspec = new SecretKeySpec(raw, ALG);
        byte[] crypted;
        try{
            Cipher ciph = Cipher.getInstance(ALG);
            ciph.init(Cipher.ENCRYPT_MODE, skeyspec);
            crypted = ciph.doFinal(msg.getBytes(code));
            String res = hexToString(crypted);
            return res;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
                UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex){
            ex.printStackTrace();
            MessageControl.showExceptionMessage(3, ex, "Error crypting data!");
            return null;
        }
    }
    
    /**
     * Decrypt a message.
     * @param msg The message to decrypt.
     * @return The message decrypted.
     */
    String decryptData(String msg){
        byte[] raw = stringToHex(encoded);
        SecretKeySpec skeyspec = new SecretKeySpec(raw, ALG);
        byte[] decrypted;
        try{
            Cipher ciph = Cipher.getInstance(ALG);
            ciph.init(Cipher.DECRYPT_MODE, skeyspec);
            decrypted = ciph.doFinal(stringToHex(msg));
            char [] aux = new String(decrypted).toCharArray();
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < aux.length; i++){
                if ((aux[i] >= '!') && (aux[i] <= '¡')){
                    res.append(aux[i]);
                }
            }
            return res.toString();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException ex){
            ex.printStackTrace();
            MessageControl.showExceptionMessage(3, ex, "Error decrypting data!");
            return null;
        }
    }
    
    /**
     * Crypts a file.
     * @param src The file to crypt.
     * @param dst The file crypted.
     */
    void encryptFile(File src, File dst){
        try {
            encrypt(new FileInputStream(src), new FileOutputStream(dst), src.getName()
                    .substring(src.getName().lastIndexOf(".") + 1, src.getName().length()));
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageControl.showExceptionMessage(3, ex, "Error crypting the file!");
            dst.delete();
        }
    }
    
    /**
     * Crypts a file without specified ubication.
     * @param src The file to crypt.
     * @return The file crypted.
     */
    File encryptFile(File src){
        File dst = new File(src.getParent(), src.getName().substring(0, src.getName().lastIndexOf("."))
                + ".cry");
        encryptFile(src, dst);
        return dst;
    }
    
    /**
     * Decrypts a file without specified ubication.
     * @param src The file to decrypt.
     * @return The file decrypted.
     */
    File decryptFile(File src){
        String[] tokens = src.getName().split(";");
        if (tokens.length == 3) return outdatedDecryptFile(src, tokens[1]);
        return decryptFile(src, src.getParentFile());
    }
    
    /**
     * Decrypts a file.
     * @param src The file to decrypt.
     * @param parent The folder where will be decrypted.
     * @return The file decrypted.
     */
    File decryptFile(File src, File parent){
        File target = null;
        try {
            CipherInputStream ciph = new CipherInputStream(new FileInputStream(src), decrypt);
            byte[] info = new byte[ciph.read()];
            ciph.read(info);
            String cryptedInfo = new String(info, "utf-8").replace("||", "");
            target = new File(parent, src.getName().substring(0, src.getName().lastIndexOf("."))
                    + "." + cryptedInfo.split("=")[1]);
            FileOutputStream output = new FileOutputStream(target);
            decrypt(ciph, output);
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            MessageControl.showExceptionMessage(3, e, "Error decrypting the file!");
            if (target != null) target.delete();
        }
        return null;
    }
    
    /**
     * Decrypts a file.
     * @param src The file to decrypt.
     * @param nameDST The name of the file decrypted.
     * @return The file decrypted.
     */
    @Deprecated
    File outdatedDecryptFile(File src, String nameDST){
        File dst = new File(src.getParent(), nameDST);
        outdatedDecryptFile(src, dst);
        return dst;
    }
    
    /**
     * Decrypts a file.
     * @param src The file to decrypt.
     * @param dst The file decrypted.
     */
    @Deprecated
    void outdatedDecryptFile(File src, File dst){
        try {
            outdatedDecrypt(new FileInputStream(src), new FileOutputStream(dst));
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageControl.showExceptionMessage(3, ex, "Error decrypting the file!");
            dst.delete();
        }
    }
    
    /**
     * Core method to crypt. The streams will be closed.
     * @param in The inputstream of the source file.
     * @param out The outputstream of the destiny file.
     * @param extension The extension of the source file.
     */
    private void encrypt(InputStream in, OutputStream out, String extension) 
            throws UnsupportedEncodingException, IOException{
        try (CipherOutputStream cout = new CipherOutputStream(out, encrypt); InputStream input = in) {
            byte[] info = ("FileExtension=" + extension + "||").getBytes("utf-8");
            cout.write(info.length);
            cout.write(info);
            int read;
            while ((read = input.read(buffer)) >= 0){
                cout.write(buffer, 0, read);
            }
        }
    }
    /**
     * Core method to decrypt. The streams will be closed.
     * @param cin The cipherinputstream of the source file.
     * @param out The outputstream of the destiny file.
     */
    private void decrypt(CipherInputStream cin, OutputStream out) throws IOException{
        try {
            int read = cin.read(buffer);
            while (read > 0){
                out.write(buffer, 0, read);
                read = cin.read(buffer);
            }
        } finally{
            try {
                cin.close();
            } catch (Exception e) {
                //Ignore
            }
            try {
                out.close();
            } catch (Exception e) {
                //Ignore
            }
        }
    }
    
    /**
     * Core method to decrypt. The streams will be closed.
     * @param in The inputstream of the source file.
     * @param out The outputstream of the destiny file.
     */
    @Deprecated
    private void outdatedDecrypt(InputStream in, OutputStream out) throws IOException{
        try (CipherInputStream cin = new CipherInputStream(in, decrypt); OutputStream output = out) {
            int read = cin.read(buffer);
            while (read >= 0){
                output.write(buffer, 0, read);
                read = cin.read(buffer);
            }
        }
    }
    
    /**
     * Transforms a byte array of hexadecimal characters to a string.
     * @param bytes The byte array of hexadecimal characters.
     * @return The string transformed.
     */
    private String hexToString(byte[] bytes){
        String crypted = "";
        for (int i = 0; i < bytes.length; i++){
            int aux = bytes[i] & 0xff;
            if (aux < 16){
                crypted = crypted.concat("0");
            }
            crypted = crypted.concat(Integer.toHexString(aux));
        }
        return crypted;
    }
    
    /**
     * Transforms a string to a byte array of hexadecimal characters.
     * @param crypted The string to transform.
     * @return The byte array of hexadecimal characters.
     */
    private byte[] stringToHex(String crypted){
        byte[] bytes = new byte[crypted.length()/2];
        for (int i = 0; i < bytes.length; i++){
            int index = i*2;
            String aux = crypted.substring(index, index + 2);
            int v = Integer.parseInt(aux, 16);
            bytes[i] = (byte) v;
        }
        return bytes;
    }

    /**
     * Initializes the object.
     */
    private void init() {
        try{
            KeyGenerator key = KeyGenerator.getInstance(ALG);
            SecureRandom rand = new SecureRandom(password);
            key.init(128, rand);
            sKey = key.generateKey();
            encoded = hexToString(sKey.getEncoded());
            byte [] iv = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
            };
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, sKey, paramSpec);
            decrypt.init(Cipher.DECRYPT_MODE, sKey, paramSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
                InvalidAlgorithmParameterException ex){
            MessageControl.showExceptionMessage(3, ex, "Error attempting to create the crypter!");
        }
    }
}
