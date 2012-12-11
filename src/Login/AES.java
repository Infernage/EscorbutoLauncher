/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;
import java.util.StringTokenizer;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Reed
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
    public AES(String password){
        if (password == null || password.equals("")){
            if (Sources.debug) System.out.println("[->Default seed set<-]");
            seed = new byte[]{
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15
            };
        } else{
            if (Sources.debug) System.out.println("[->Customizing seed<-]");
            setPassword(password);
        }
        try{
            if (Sources.debug) System.out.println("[->Generating key<-]");
            KeyGenerator key = KeyGenerator.getInstance(ALG);
            SecureRandom rand = new SecureRandom(seed);
            key.init(128, rand);
            sKey = key.generateKey();
            encoded = hexToString(sKey.getEncoded());
            byte [] iv = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
            };
            if (Sources.debug) System.out.println("[->Specifying parameters<-]");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, sKey, paramSpec);
            decrypt.init(Cipher.DECRYPT_MODE, sKey, paramSpec);
        } catch (Exception ex){
            Sources.exception(ex, "Error attempting to creating the instance!");
        }
    }
    public AES(SecretKey key, String password){
        this(password);
        sKey = key;
        encoded = hexToString(sKey.getEncoded());
    }
    public AES(){
        this(null);
    }
    public void setPassword(String password){
        try {
            seed = password.getBytes(code);
        } catch (UnsupportedEncodingException ex) {
            Sources.exception(ex, "Bytes not supported!");
        }
    }
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
        } catch (Exception ex){
            Sources.exception(ex, "Error crypting data!");
            return null;
        }
    }
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
        } catch (Exception ex){
            Sources.exception(ex, "Error decrypting data!");
            return null;
        }
    }
    public void encryptFile(File src, File dst){
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            encrypt(in, out);
            in.close();
            out.close();
        } catch (Exception ex) {
            Sources.exception(ex, "Error encrypting the file!");
        }
    }
    public void encryptFile(File src){
        StringBuilder build = new StringBuilder();
        Random num = new Random();
        StringTokenizer token = new StringTokenizer(src.getName(), ".");
        String ext = null;
        while(token.hasMoreTokens()){
            ext = token.nextToken();
        }
        String tmp = null;
        char[] A = new char[4];
        for (int i = 0; i < A.length; i++){
            do{
                tmp = Integer.toHexString(num.nextInt(109449)+1);
            } while(tmp.equals("-") || tmp.equals("_"));
            A[i] = (char) Integer.parseInt(tmp, 16);
        }
        build.append(A)
                .append("-").append(ext).append("_").append(tmp);
        encryptFile(src, new File(src.getParent() + "\\" + build.toString()));
    }
    public void decryptFile(File src){
        String[] tokens = src.getName().split("-");
        tokens = tokens[1].split("_");
        String ext = tokens[0];
        decryptFile(src, "decrypted." + ext);
    }
    public void decryptFile(File src, String nameDST){
        decryptFile(src, new File(src.getParent() + "\\" + nameDST));
    }
    public void decryptFile(File src, File dst){
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            decrypt(in, out);
            in.close();
            out.close();
        } catch (Exception ex) {
            Sources.exception(ex, "Error decrypting the file!");
        }
    }
    private void encrypt(InputStream in, OutputStream out){
        try{
            out = new CipherOutputStream(out, encrypt);
            int read = 0;
            while ((read = in.read(buffer)) >= 0){
                out.write(buffer, 0, read);
            }
            out.close();
        } catch (IOException ex){
            Sources.exception(ex, "Error: Basic encryptation failed!\nImpossible to continue!");
        }
    }
    private void decrypt(InputStream in, OutputStream out){
        try{
            in = new CipherInputStream(in, decrypt);
            int read = 0;
            while ((read = in.read(buffer)) >= 0){
                out.write(buffer, 0, read);
            }
            out.close();
        } catch (IOException ex){
            Sources.exception(ex, "Error: Basic decryptation failed!\nImpossible to continue!");
        }
    }
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
