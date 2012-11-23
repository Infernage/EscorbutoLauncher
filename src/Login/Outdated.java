/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.File;
import java.io.IOException;

/**
 * Clase para hacer soporte a versiones antiguas.
 * @author Reed
 */
public class Outdated {
    public static void ver4xx(){
        File opt = new File(Sources.path("opt.cfg"));
        if (opt.exists()){
            if (!opt.delete()){
                opt.deleteOnExit();
            }
            File data = new File(Sources.path(Sources.DirData()));
            Sources.borrarFichero(data);
        }
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
        File updateSystem = new File(Sources.path("Desktop" + Sources.sep() + "Copia Minecraft"));
        if (updateSystem.exists()){
            try {
                Sources.copyDirectory(updateSystem, new File(Sources.path(Sources.DirData() + Sources.sep() + "Copia Minecraft")));
            } catch (IOException ex) {
                Sources.exception(ex, "Failed to adapt the new copySystem!");
            }
            Sources.borrarFichero(updateSystem);
            updateSystem.delete();
        }
    }
    public static void checkAll(){
        System.out.println("Actual version: " + Mainclass.version);
        ver4xx();
        System.gc();
    }
    /*private static class outECP{
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
    }*/
}
