/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JOptionPane;

/**
 * Clase para hacer soporte a versiones antiguas.
 * @author Reed
 */
public class Outdated {
    public static void ver410(){
        File RMB = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.rmb));
        if (RMB.exists()){
            RMB.delete();
        }
        File data = new File(Sources.path(Sources.DirData() + Sources.sep()) + "data.cfg");
        String type = null, username = null, password = null, word = null, name = null;
        if (data.exists()){
            try{
                BufferedReader bf = new BufferedReader(new FileReader(data));
                String A = bf.readLine();
                if (A.contains("said_/&/JT&^*$/&(/*Ç_said")){
                    type = "MC";
                    username = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario para "
                            + "confirmar tu identidad:");
                    password = "said_/&/;JT&^_said";
                    word = "said_/*$/&;(/*Ç_said";
                } else if (A.contains("said_/HT&)$^)/%(¨¨Ç_said")){
                    type = "MS";
                    username = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario para "
                            + "confirmar tu identidad:");
                    password = "\"said_/HT;&)$^)_said";
                    word = "said_/%*;^(¨¨Ç_said";
                } else{
                    type = "OFF";
                    outECP B = new outECP();
                    AES C = new AES(Sources.pss);
                    name = B.decrypt(A);
                    username = C.encryptData(B.decrypt(A));
                    password = C.encryptData(B.decrypt(bf.readLine()));
                    word = C.encryptData(B.decrypt(bf.readLine()));
                }
                bf.close();
                data.delete();
                File upload = Vista.createStaticLoginFile(type, name.toLowerCase(), username, password, word);
                if (!Sources.upload(upload.getAbsolutePath(), upload.getName())){
                    throw new IOException("Failed connection to the server!");
                }
                upload.delete();
            } catch (IOException ex){
                Sources.fatalException(ex, "No se pudo sincronizar con el servidor", 3);
            }
        }
        data = null;
    }
    public static void ver421(){
        System.out.println("Version 4.2.2 it's absoultely equals to 4.2.1\nIt isn't necessary to do anything.");
    }
    public static void ver422(){
        File copySystem = new File(System.getProperty("user.home") + Sources.sep() + "Desktop" + 
                Sources.sep() + "Copia Minecraft");
        if (copySystem.exists()){
            System.out.print("Copy system founded! Exporting to the new location... ");
            try {
                Sources.copyDirectory(copySystem, new File(Sources.path(Sources.DirData() + Sources.sep() + "Copia Minecraft")));
                Sources.borrarFichero(copySystem);
                copySystem.delete();
                System.out.println("OK");
            } catch (IOException ex) {
                System.out.println("FAILED");
                Sources.exception(ex, "Failed to adapt the new copySystem!");
            }
        }
        File updateSystem = new File(Sources.path("Desktop"));
        if (updateSystem.exists()){
            Sources.borrarFichero(updateSystem);
            updateSystem.delete();
        }
        File lstlg = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.DirNM + 
                Sources.sep() + Sources.login));
        if (lstlg.exists()){
            File newlstlg = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.login));
            try {
                Sources.copy(lstlg, newlstlg);
            } catch (IOException ex) {
                Sources.exception(ex, "Failed to adapt the new remember system!");
            }
        }
    }
    public static void checkAll(){
        System.out.println("Actual version: " + Mainclass.version);
        ver410();
        ver421();
        ver422();
        System.gc();
    }
    private static class outECP{
        private String key = "My secret key";
        private Cipher ecipher;
        private Cipher dcipher;
        public outECP(){
            byte[] salt = {
                (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
                (byte)0x56, (byte)0x34, (byte)0xE3, (byte)0x03
            };
            int iterationCount = 19;
            try{
                KeySpec keySpec = new PBEKeySpec(key.toCharArray(), salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());
                // Prepare the parameters to the cipther
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (Exception ex){
                ex.printStackTrace(Mainclass.err);
            }
        }
        public String encrypt (String msg){
            try{
                return new sun.misc.BASE64Encoder().encode(ecipher.doFinal(msg.getBytes("UTF8")));
            } catch (Exception ex){
                ex.printStackTrace(Mainclass.err);
                return null;
            }
        }
        public String decrypt (String msg){
            try{
                return new String(dcipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(msg)), "UTF8");
            } catch (Exception ex){
                ex.printStackTrace(Mainclass.err);
                return null;
            }
        }
    }
}
