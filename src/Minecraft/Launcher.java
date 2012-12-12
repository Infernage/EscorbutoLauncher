/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Minecraft;

import Login.Sources;
import java.applet.Applet;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import net.lingala.zip4j.core.ZipFile;

/**
 *
 * @author Reed
 */
public class Launcher {
    private String userName, latestVersion, downloadTicket, sessionId;
    public Launcher(String args){
        String[] par = args.split(":");
        latestVersion = par[0];
        downloadTicket = par[1];
        userName = par[2];
        sessionId = par[3];
    }
    public Launcher(){}
    public void offline(String user){
        if (Sources.debug) System.out.println("[->OFFLINE set<-]");
        userName = user;
        sessionId = "-";
        latestVersion = "N/A";
        downloadTicket = "N/A";
    }
    public void init(){
        if (Sources.debug) System.out.println("[->Loading necessary files<-]");
        System.out.println("Username: " + userName + "\nSessionID: " + sessionId + "\nLatestVersion: " + 
                latestVersion + "\nDownloadTicket: " + downloadTicket);
        String arg = Sources.Prop.getProperty("user.dir");
        Dimension mc = new Dimension(854, 480);
        String[] jars;
        URL[] urls;
        try{
            System.out.println("Loading jars...");
            jars = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
            urls = new URL[jars.length];
            for (int i = 0; i < urls.length; i++){
                try{
                    File file = new File(new File(arg, "bin"), jars[i]);
                    urls[i] = file.toURI().toURL();
                } catch (Exception ex){
                    Login.Sources.fatalException(ex, "Failed to load jars.", 1);
                }
            }
            System.out.println("Loading natives...");
            if (Sources.OS.equals("linux")){
                if (Sources.debug) System.out.println("[->Linux OS found<-]");
                try{
                    linuxNatives();
                } catch (Exception ex){
                    Sources.fatalException(ex, "Error al parchear en Linux.", 1);
                }
            }
            String natives = new File(new File(arg, "bin"), "natives").toString();
            if (Sources.debug) System.out.println("[->Setting new properties<-]");
            System.setProperty("org.lwjgl.librarypath", natives);
            System.setProperty("net.java.games.input.librarypath", natives);
            System.setProperty("user.dir", Login.Sources.Prop.getProperty("user.dir"));
            System.setProperty("user.home", Login.Sources.Prop.getProperty("user.home"));
            URLClassLoader loader = new URLClassLoader(urls, Launcher.class.getClassLoader());
            Class client = null;
            Class invoker;
            Applet applet;
            MCFrame frame;
            if (Sources.debug) System.out.println("[->Invoking net.minecraft.client.Minecraft<-]");
            try{
                client = loader.loadClass("net.minecraft.client.Minecraft");
                Field field = getMCField(client);
                if (field == null){
                    Login.Sources.fatalException(new Exception("Failed to find Minecraft path."), 
                            "Failed to find Minecraft path.", 1);
                }
                field.setAccessible(true);
                field.set(null, new File(arg));
                System.out.println("Fixed Minecraft Path: Field was " + field.toString());
            } catch (Exception ex){
                Login.Sources.fatalException(ex, "Failed to find Minecraft path.", 1);
            }
            System.setProperty("minecraft.applet.TargetDirectory", arg);
            Object method = client.getMethod("a", new Class[] { String.class }).invoke(null, new Object[] 
            { "minecraft" }).toString();
            System.out.println("MCDIR: " + method + "\nLaunching...");
            try{
                if (Sources.debug) System.out.println("[->Loading applet<-]");
                invoker = loader.loadClass("net.minecraft.client.MinecraftApplet");
                applet = (Applet) invoker.newInstance();
                frame = new MCFrame("Minecraft");
                if (Sources.debug) System.out.println("[->Starting Minecraft<-]");
                frame.start(applet, userName, sessionId, mc);
            } catch (Exception ex){
                Login.Sources.fatalException(ex, "Failed to launch Minecraft.", 1);
            }
        } catch (Exception ex){
            Login.Sources.fatalException(ex, "Failed to load minecraft.", 1);
        }
    }
    private void linuxNatives() throws Exception{
        String path = Sources.Prop.getProperty("user.data") + 
                File.separator + "linux_natives.zip";
        URL download = new URL("https://dl.dropbox.com/s/sb5reu4oecpz1kb/linux_natives.zip?dl=1");
        if (Sources.debug) System.out.println("[->Openning connection<-]");
        HttpURLConnection connection = (HttpURLConnection) download.openConnection();
        if (Sources.debug) System.out.println("[->Settin request properties<-]");
        connection.setRequestProperty("Range", "bytes=" + 0 + "-");
        connection.connect();
        if (Sources.debug) System.out.println("[->Connected<-]");
        RandomAccessFile direct = new RandomAccessFile(path, "rw");
        direct.seek(0);
        if (Sources.debug) System.out.println("[->Started access file<-]");
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        if (Sources.debug) System.out.println("[->Openned inputstream<-]");
        try{
            byte[] buffer = new byte[connection.getContentLength()];
            if (Sources.debug) System.out.println("[->Buffer created<-]");
            int read = in.read(buffer);
            if (Sources.debug) System.out.println("[->Starting transfer<-]");
            while (read > 0){
                direct.write(buffer, 0, read);
                read = in.read(buffer);
            }
            if (Sources.debug) System.out.println("[->Done<-]");
        } finally{
            if (Sources.debug) System.out.println("[->Closing connections<-]");
            if (direct != null){
                direct.close();
            }
            if (in != null){
                in.close();
            }
        }
        if (Sources.debug) System.out.println("[->Decripting files<-]");
        File local = new File(path);
        local.deleteOnExit();
        File natives = new File(Sources.Prop.getProperty("user.dir") + File.separator + "bin", "natives");
        if (natives.exists()){
            if (Sources.debug) System.out.println("[->Patching files<-]");
            File[] files = natives.listFiles();
            if (files.length != 0){
                if (files[0].getName().endsWith("dll")) Sources.IO.borrarFichero(natives);
            }
        } else{
            if (Sources.debug) System.out.println("[->Created directory<-]");
            natives.mkdirs();
        }
        if (Sources.debug) System.out.println("[->Extracting files<-]");
        ZipFile zip = new ZipFile(local);
        zip.extractAll(natives.getAbsolutePath());
        if (Sources.debug) System.out.println("[->Patcher complete<-]");
    }
    public static Field getMCField(Class<?> parameter){
        if (Sources.debug) System.out.println("[->Getting MCField<-]");
        Field[] fields = parameter.getDeclaredFields();
        int i = 0;
        Field res = null;
        while(i < fields.length){
            Field field = fields[i];
            if (field.getType() == File.class){
                if (field.getModifiers() == 10){
                    res = field;
                    i = fields.length;
                }
            }
            i++;
        }
        return res;
    }
}
