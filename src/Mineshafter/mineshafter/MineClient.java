package Mineshafter.mineshafter;

import Login.Sources;
import Mineshafter.mineshafter.proxy.MineProxy;
import Mineshafter.mineshafter.util.Resources;
import Mineshafter.mineshafter.util.SimpleRequest;
import Mineshafter.mineshafter.util.Streams;
import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import sun.applet.Main;

public class MineClient extends Applet
{
  private static final long serialVersionUID = 1L;
  protected static float VERSION = 3.7F;

  protected static String launcherDownloadURL = "https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft.jar";
  protected static String normalLauncherFilename = Sources.path(Sources.DirMC + Sources.sep() + "minecraft.jar");
  protected static String hackedLauncherFilename = Sources.path(Sources.DirMC + Sources.sep() + "minecraft_modified.jar");

  protected static String MANIFEST_TEXT = "Manifest-Version: 1.2\nCreated-By: 1.6.0_22 (Sun Microsystems Inc.)\nMain-Class: net.minecraft.MinecraftLauncher\n";

  private static String username;
  private static String password;
  public static void open() {
    open(null, null);
  }
  public static void open(String U, String P){
      if (U == null || P == null){
          main(new String[0]);
      } else if (U != null && P != null){
          username = U;
          password = P;
          main(new String[0]);
      } else{
          open();
      }
  }

  public static void main(String[] args) {
    try {
      byte[] verdata = SimpleRequest.get("http://mineshafter.appspot.com/update?name=client");
      String verstring = new String();
      if (verdata == null) verstring = "0"; else
        verstring = new String(verdata);
      if (verstring.isEmpty()) verstring = "0";
      float version;
      try
      {
        version = Float.parseFloat(verstring);
      }
      catch (Exception e)
      {
        version = 0.0F;
      }
      System.out.println("Current proxy version: " + VERSION);
      System.out.println("Gotten proxy version: " + version);
      if (VERSION < version) {
        JOptionPane.showMessageDialog(null, "A new version of Mineshafter is available at http://mineshafter.appspot.com/\nGo get it.", "Update Available", -1);
      }
    } catch (Exception e) {
      System.out.println("Error while updating:");
      e.printStackTrace();
    }

    try
    {
      MineProxy proxy = new MineProxy(VERSION);
      proxy.start();
      int proxyPort = proxy.getPort();

      //System.setErr(System.out);

      System.setProperty("http.proxyHost", "127.0.0.1");
      System.setProperty("http.proxyPort", Integer.toString(proxyPort));
      System.setProperty("java.net.preferIPv4Stack", "true");

      File hackedFile = new File(hackedLauncherFilename);
      if (hackedFile.exists()) hackedFile.delete();

      startLauncher(args);
    }
    catch (Exception e) {
      System.out.println("Something bad happened:");
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void startLauncher(String[] args) {
    try {
      if (new File(hackedLauncherFilename).exists()) {
        URL u = new File(hackedLauncherFilename).toURI().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[] { u }, Main.class.getClassLoader());

        Class launcherFrame = cl.loadClass("net.minecraft.LauncherFrame");

        String[] nargs = null;
        if (args.length > 0){
            nargs = new String[args.length];
            System.arraycopy(args, 0, nargs, 0, args.length);
        } else{
            if (username == null || password == null){
                nargs = new String[0];
            }else if (username != null && password != null){
                nargs = new String[2];
                nargs[0] = username;
                nargs[1] = password;
            } else{
                username = null;
                password = null;
                startLauncher(args);
            }
        }
        Method main = launcherFrame.getMethod("main", new Class[] { java.lang.String.class });
        main.invoke(launcherFrame, new Object[] { nargs });
      }  else if (new File(normalLauncherFilename).exists()) {
        editLauncher();
        startLauncher(args);
      }  else {
        try {
          byte[] data = SimpleRequest.get(launcherDownloadURL);
          OutputStream out = new FileOutputStream(normalLauncherFilename);
          out.write(data);
          out.flush();
          out.close();
          startLauncher(args);
        } catch (Exception ex) {
          System.out.println("Error downloading launcher:");
          ex.printStackTrace();
          return;
        }
      }
    } catch (Exception e1) {
      System.out.println("Error starting launcher:");
      e1.printStackTrace();
    }
  }


  public static void editLauncher() {
    try {
      ZipInputStream in = new ZipInputStream(new FileInputStream(normalLauncherFilename));
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(hackedLauncherFilename));
      ZipEntry entry;
      while ((entry = in.getNextEntry()) != null)
      {
        String n = entry.getName();
        if ((!n.contains(".svn")) && 
          (!n.equals("META-INF/MOJANG_C.SF")) && 
          (!n.equals("META-INF/MOJANG_C.DSA")) && 
          (!n.equals("net/minecraft/minecraft.key")) && 
          (!n.equals("net/minecraft/Util$OS.class")))
        {
          out.putNextEntry(entry);
          InputStream dataSource;
          if (n.equals("META-INF/MANIFEST.MF")) { dataSource = new ByteArrayInputStream(MANIFEST_TEXT.getBytes()); }
          else
          {
            if (n.equals("net/minecraft/Util.class")) dataSource = Resources.load("net/minecraft/Util.class"); else
              dataSource = in; 
          }
          Streams.pipeStreams(dataSource, out);
          out.flush();
        }
      }
      in.close();
      out.close();
    } catch (Exception e) {
      System.out.println("Editing launcher failed:");
      e.printStackTrace();
    }
  }
}

/* Location:           C:\Users\Reed\Dropbox\GitHub\MineClient\Necessary files\Mineshafter-proxy.jar
 * Qualified Name:     mineshafter.MineClient
 * JD-Core Version:    0.6.1
 */