/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Minecraft;

import Login.Sources;
import java.applet.Applet;
import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

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
        userName = user;
        sessionId = "-";
        latestVersion = "N/A";
        downloadTicket = "N/A";
    }
    public void init(){
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
            String natives = new File(new File(arg, "bin"), "natives").toString();
            System.setProperty("org.lwjgl.librarypath", natives);
            System.setProperty("net.java.games.input.librarypath", natives);
            System.setProperty("user.dir", Login.Sources.Prop.getProperty("user.dir"));
            System.setProperty("user.home", Login.Sources.Prop.getProperty("user.home"));
            URLClassLoader loader = new URLClassLoader(urls, Launcher.class.getClassLoader());
            Class client = null;
            Class invoker;
            Applet applet;
            MCFrame frame;
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
                invoker = loader.loadClass("net.minecraft.client.MinecraftApplet");
                applet = (Applet) invoker.newInstance();
                frame = new MCFrame("Minecraft");
                frame.start(applet, userName, sessionId, mc);
            } catch (Exception ex){
                Login.Sources.fatalException(ex, "Failed to launch Minecraft.", 1);
            }
        } catch (Exception ex){
            Login.Sources.fatalException(ex, "Failed to load minecraft.", 1);
        }
    }
    public static Field getMCField(Class<?> parameter){
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
