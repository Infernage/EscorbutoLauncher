/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.launcher.minecraft;

import MLR.InnerApi;
import java.applet.Applet;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import net.lingala.zip4j.core.ZipFile;

/**
 *
 * @author Reed
 */
public class Launcher {
    private String userName, latestVersion, downloadTicket, sessionId;
    public Process minecraft;
    public Launcher(String args){
        String[] par = args.split(":");
        latestVersion = par[0];
        downloadTicket = par[1];
        userName = par[2];
        sessionId = par[3];
    }
    public Launcher(){}
    public void offline(String user){
        if (InnerApi.debug) System.out.println("[->OFFLINE set<-]");
        userName = user;
        sessionId = "-";
        latestVersion = "N/A";
        downloadTicket = "N/A";
    }
    public void init(){
        System.out.println("Loading natives...");
        if (InnerApi.OS.equals("linux")){
            if (InnerApi.debug) System.out.println("[->Linux OS found<-]");
            try{
                linuxNatives();
            } catch (Exception ex){
                InnerApi.fatalException(ex, "Error al parchear en Linux.", 1);
            }
        }
        String natives = new File(new File(InnerApi.configuration.getProperty("user.dir"), "bin"), 
                "natives").toString();
        if (InnerApi.debug) System.out.println("[->Applying configuration<-]");
        String path = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        List args = new ArrayList();
        args.add(path);
        args.add("-Xms256M");
        args.add("-Xmx" + InnerApi.configuration.getProperty("maxRAM") + "M");
        args.add("-cp");
        args.add(System.getProperty("java.class.path"));
        args.add(Launcher.class.getCanonicalName());
        args.add(userName);
        args.add(sessionId);
        args.add(latestVersion);
        args.add(downloadTicket);
        args.add(natives);
        args.add(MLR.InnerApi.configuration.getProperty("user.dir"));
        args.add(MLR.InnerApi.configuration.getProperty("user.home"));
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);
        InnerApi.Init.collector.exit();
        InnerApi.Init.collector = null;
        try {
            minecraft = builder.start();
            InnerApi.Init.consola.setInput(minecraft.getInputStream());
            Thread.sleep(1000);
            minecraft.waitFor();
            System.exit(0);
        } catch (Exception e) {
            InnerApi.fatalException(e, "Failed to launch minecraft!", 1);
        }
    }
    private void linuxNatives() throws Exception{
        String path = InnerApi.Directory.data("linux_natives.zip");
        URL download = new URL("https://dl.dropbox.com/s/sb5reu4oecpz1kb/linux_natives.zip?dl=1");
        if (InnerApi.debug) System.out.println("[->Openning connection<-]");
        HttpURLConnection connection = (HttpURLConnection) download.openConnection();
        if (InnerApi.debug) System.out.println("[->Settin request properties<-]");
        connection.setRequestProperty("Range", "bytes=" + 0 + "-");
        connection.connect();
        if (InnerApi.debug) System.out.println("[->Connected<-]");
        RandomAccessFile direct = new RandomAccessFile(path, "rw");
        direct.seek(0);
        if (InnerApi.debug) System.out.println("[->Started access file<-]");
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        if (InnerApi.debug) System.out.println("[->Openned inputstream<-]");
        try{
            byte[] buffer = new byte[connection.getContentLength()];
            if (InnerApi.debug) System.out.println("[->Buffer created<-]");
            int read = in.read(buffer);
            if (InnerApi.debug) System.out.println("[->Starting transfer<-]");
            while (read > 0){
                direct.write(buffer, 0, read);
                read = in.read(buffer);
            }
            if (InnerApi.debug) System.out.println("[->Done<-]");
        } finally{
            if (InnerApi.debug) System.out.println("[->Closing connections<-]");
            if (direct != null){
                direct.close();
            }
            if (in != null){
                in.close();
            }
        }
        if (InnerApi.debug) System.out.println("[->Decripting files<-]");
        File local = new File(path);
        local.deleteOnExit();
        File natives = new File(InnerApi.configuration.getProperty("user.dir") + File.separator + 
                "bin", "natives");
        if (natives.exists()){
            if (InnerApi.debug) System.out.println("[->Patching files<-]");
            File[] files = natives.listFiles();
            if (files.length != 0){
                if (files[0].getName().endsWith("dll")) InnerApi.IO.borrarFichero(natives);
            }
        } else{
            if (InnerApi.debug) System.out.println("[->Created directory<-]");
            natives.mkdirs();
        }
        if (InnerApi.debug) System.out.println("[->Extracting files<-]");
        ZipFile zip = new ZipFile(local);
        zip.extractAll(natives.getAbsolutePath());
        if (InnerApi.debug) System.out.println("[->Patcher complete<-]");
    }
    public static Field getMCField(Class<?> parameter){
        System.out.println("Getting MCField");
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
    public static void main(String[] args){
        String userName = args[0], sessionId = args[1], latestVersion = args[2], downloadTicket = args[3],
                natives = args[4];
        System.setProperty("user.dir", args[5]);
        System.setProperty("user.home", args[6]);
        System.out.println("Loading necessary files");
        System.out.println("Username: " + userName + "\nSessionID: " + sessionId + "\nLatestVersion: " + 
                latestVersion + "\nDownloadTicket: " + downloadTicket + "\nNatives: " + natives);
        String arg = System.getProperty("user.dir");
        System.out.println("Dir: " + arg);
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
                    System.out.println(file.getAbsolutePath());
                    urls[i] = file.toURI().toURL();
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Failed to load jars.", 
                            "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            System.out.println("Setting new properties");
            System.setProperty("org.lwjgl.librarypath", natives);
            System.setProperty("net.java.games.input.librarypath", natives);
            URLClassLoader loader = new URLClassLoader(urls, Launcher.class.getClassLoader());
            Class client = null;
            Class invoker;
            Applet applet;
            MCFrame frame;
            System.out.println("Invoking net.minecraft.client.Minecraft");
            try{
                client = loader.loadClass("net.minecraft.client.Minecraft");
                Field field = getMCField(client);
                if (field == null){
                    throw new Exception("Failed to find Minecraft path");
                }
                field.setAccessible(true);
                field.set(null, new File(arg));
                System.out.println("Fixed Minecraft Path: Field was " + field.toString());
            } catch (Exception ex){
                JOptionPane.showMessageDialog(null, "Failed to find Minecraft path.", 
                            "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                System.exit(1);
            }
            System.setProperty("minecraft.applet.TargetDirectory", arg);
            Object method = client.getMethod("a", new Class[] { String.class }).invoke(null, new Object[] 
            { "minecraft" }).toString();
            System.out.println("MCDIR: " + method + "\nLaunching...");
            try{
                System.out.println("Loading applet");
                invoker = loader.loadClass("net.minecraft.client.MinecraftApplet");
                applet = (Applet) invoker.newInstance();
                frame = new MCFrame("Minecraft");
                System.out.println("Starting Minecraft");
                frame.start(applet, userName, sessionId, mc);
            } catch (Exception ex){
                JOptionPane.showMessageDialog(null, "Failed to launch Minecraft.", 
                            "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                System.exit(1);
            }
        } catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Failed to load Minecraft.", 
                        "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
