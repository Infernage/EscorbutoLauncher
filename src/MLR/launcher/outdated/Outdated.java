/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.launcher.outdated;

import MLR.InnerApi;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Clase para hacer soporte a versiones antiguas.
 * @author Reed
 */
public class Outdated {
    public static String getOldVersion(){ return InnerApi.Init.version; }
    private static void alternativeStart(){
        if (new File(InnerApi.dataPath() + File.separator + "opt.cfg").exists()){
            Ver5.before500();
        } else if (new File(InnerApi.Directory.data(null) + File.separator + "boolean.txt").exists()){
            Ver5.ver500();
        } else if (new File(InnerApi.Directory.data(null) + File.separator + "Copia Minecraft").exists()){
            Ver5.ver511();
        }
    }
    private static void startEngine(String version){
        int ver = Integer.parseInt(version.replace(".", ""));
        if (ver < 500) ver = 499;
        switch (ver){
            case 499: Ver5.before500();
                break;
            case 500: Ver5.ver500();
                break;
            case 511: Ver5.ver511();
                break;
        }
    }
    public static void startEngines(){
        String oldVersion = "";
        System.out.println("Actual version: " + InnerApi.Init.version);
        if (InnerApi.debug) System.out.println("[->Starting version engines<-]");
        URLClassLoader loader;
        try {
            URL[] file = new URL[] {new File(InnerApi.Files.jar()).toURI().toURL()};
            loader = new URLClassLoader(file, Outdated.class.getClassLoader());
            Class version = loader.loadClass("MLR.launcher.outdated.Outdated");
            oldVersion = ((String) version.getMethod("getOldVersion").invoke(null)).substring(1);
            System.out.println("Old version founded: " + oldVersion);
            startEngine(oldVersion);
        } catch (Exception ex) {
            InnerApi.Init.error.setError(ex);
            System.out.println("Failed to check old version. Starting alternative method...");
            alternativeStart();
        }
        if (InnerApi.debug) System.out.println("[->Finalizing engines<-]");
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
