package elr.core.modules;

import elr.core.Stack;
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
import java.util.Random;
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
public class AES {
    private SecretKey sKey;
    private Cipher encrypt;
    private Cipher decrypt;
    private byte[] buffer = new byte[1024];
    private byte[] seed;
    private String encoded;
    private String code = "UTF-16LE";
    private String ALG = "AES";
    
    /**
     * Initializes the object with a password.
     * @param password The password.
     */
    public AES(String password){
        if (password == null || password.equals("")){
            if (Stack.console != null) Stack.console.printConfigOption("Default seed initialized");
            seed = new byte[]{
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15
            };
        } else{
            if (Stack.console != null) Stack.console.printConfigOption("New seed applied");
            setPassword(password);
        }
        try{
            if (Stack.console != null) Stack.console.printConfigOption("Key generated from seed");
            KeyGenerator key = KeyGenerator.getInstance(ALG);
            SecureRandom rand = new SecureRandom(seed);
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
            ExceptionControl.showException(3, ex, "Error attempting to create the crypter!");
        }
    }
    
    /**
     * Initializes the object with a password and a secretkey.
     * @param key The secretkey.
     * @param password The password.
     */
    public AES(SecretKey key, String password){
        this(password);
        sKey = key;
        encoded = hexToString(sKey.getEncoded());
    }
    
    /**
     * Initializes the object with default parameters.
     */
    public AES(){
        this(null);
    }
    
    /**
     * Sets a new password.
     * @param password The new password to set.
     */
    public void setPassword(String password){
        try {
            seed = password.getBytes(code);
        } catch (UnsupportedEncodingException ex) {
            ExceptionControl.showException(3, ex, "Bytes not supported");
        }
    }
    
    /**
     * Crypts a message.
     * @param msg The message to crypt.
     * @return The message crypted.
     */
    public String encryptData(String msg){
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
            ExceptionControl.showException(3, ex, "Error crypting data!");
            return null;
        }
    }
    
    /**
     * Decrypt a message.
     * @param msg The message to decrypt.
     * @return The message decrypted.
     */
    public String decryptData(String msg){
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
            ExceptionControl.showException(3, ex, "Error decrypting data!");
            return null;
        }
    }
    
    /**
     * Crypts a file.
     * @param src The file to crypt.
     * @param dst The file crypted.
     */
    public void encryptFile(File src, File dst){
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            encrypt(in, out);
            in.close();
            out.close();
        } catch (Exception ex) {
            ExceptionControl.showException(3, ex, "Error crypting the file!");
        }
    }
    
    /**
     * Crypts a file without specified ubication.
     * @param src The file to crypt.
     * @return The file crypted.
     */
    public File encryptFile(File src){
        StringBuilder build = new StringBuilder();
        Random num = new Random();
        String tmp = null;
        char[] A = new char[4];
        for (int i = 0; i < A.length; i++){
            do{
                tmp = Integer.toHexString(num.nextInt(109449)+1);
            } while(tmp.equals("-") || tmp.equals("_"));
            A[i] = (char) Integer.parseInt(tmp, 16);
        }
        build.append(A)
                .append(";").append(src.getName()).append(";").append(tmp);
        File dst = new File(src.getParent(), build.toString());
        encryptFile(src, dst);
        return dst;
    }
    
    /**
     * Decrypts a file without specified ubication.
     * @param src The file to decrypt.
     * @return The file decrypted.
     */
    public File decryptFile(File src){
        String[] tokens = src.getName().split(";");
        String ext = tokens[1];
        return decryptFile(src, ext);
    }
    
    /**
     * Decrypts a file.
     * @param src The file to decrypt.
     * @param nameDST The name of the file decrypted.
     * @return The file decrypted.
     */
    public File decryptFile(File src, String nameDST){
        File dst = new File(src.getParent(), nameDST);
        decryptFile(src, dst);
        return dst;
    }
    
    /**
     * Decrypts a file.
     * @param src The file to decrypt.
     * @param dst The file decrypted.
     */
    public void decryptFile(File src, File dst){
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            decrypt(in, out);
            in.close();
            out.close();
        } catch (Exception ex) {
            ExceptionControl.showException(3, ex, "Error decrypting the file!");
        }
    }
    
    /**
     * Core method to crypt.
     * @param in The inputstream of the source file.
     * @param out The outputstream of the destiny file.
     */
    private void encrypt(InputStream in, OutputStream out){
        try{
            out = new CipherOutputStream(out, encrypt);
            int read;
            while ((read = in.read(buffer)) >= 0){
                out.write(buffer, 0, read);
            }
            out.close();
        } catch (IOException ex){
            ExceptionControl.showException(3, ex, "Error: Basic encryptation failed!\nImpossible to continue!");
        }
    }
    
    /**
     * Core method to decrypt.
     * @param in The inputstream of the source file.
     * @param out The outputstream of the destiny file.
     */
    private void decrypt(InputStream in, OutputStream out){
        try{
            in = new CipherInputStream(in, decrypt);
            int read;
            while ((read = in.read(buffer)) >= 0){
                out.write(buffer, 0, read);
            }
            out.close();
        } catch (IOException ex){
            ExceptionControl.showException(3, ex, "Error: Basic decryptation failed!\nImpossible to continue!");
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
}
