package mineshafter;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.JOptionPane;
import Mineshafter.mineshafter.proxy.MineProxy;
import Mineshafter.mineshafter.util.SimpleRequest;
import sun.applet.Main;

public class MineServer
{
  protected static float VERSION = 2.4F;

  public static void main(String[] args) {
    try {
      String verstring = new String(SimpleRequest.get("http://mineshafter.appspot.com/update?name=server"));
      if (verstring.isEmpty()) verstring = "0"; float version;
      try
      {
        version = Float.parseFloat(verstring);
      }
      catch (Exception e)
      {
        version = 0.0F;
      }
      if (verstring == "") verstring = "0";
      System.out.println("Current proxy version: " + VERSION);
      System.out.println("Gotten proxy version: " + version);
      if (VERSION < version) {
        JOptionPane.showMessageDialog(null, "A new version of Mineshafter is available at http://mineshafter.appspot.com/\nGo get it.", "Update Available", -1);
        System.exit(0);
      }
    } catch (Exception e) {
      System.out.println("Error while updating:");
      e.printStackTrace();
    }
try {
      MineProxy proxy = new MineProxy(VERSION);
      proxy.start();
      int proxyPort = proxy.getPort();

      System.setProperty("http.proxyHost", "127.0.0.1");
      System.setProperty("http.proxyPort", Integer.toString(proxyPort));
      String load;
      try {
        load = args[0];
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        load = "minecraft_server.jar";
      }
      Attributes attributes = new JarFile(load).getManifest().getMainAttributes();
      String name = attributes.getValue("Main-Class");

      URLClassLoader cl = null;
      Class cls = null;
      Method main = null;
      try {
        cl = new URLClassLoader(new URL[] { new File(load).toURI().toURL() }, Main.class.getClassLoader());
        cls = cl.loadClass(name);
        main = cls.getDeclaredMethod("main", new Class[] { java.lang.String.class });
      } catch (Exception e) {
        System.out.println("Error loading class " + name + " from jar " + load + ":");
        e.printStackTrace();
        System.exit(1);
      }
      String[] nargs;
      try {
        nargs = new String[args.length - 1];
        System.arraycopy(args, 1, nargs, 0, nargs.length);
      } catch (Exception e) {
        nargs = new String[0];
      }
      main.invoke(cls, new Object[] { nargs });
    } catch (Exception e) {
      System.out.println("Something bad happened:");
      e.printStackTrace();
      System.exit(1);
    }
  }
}

/* Location:           C:\Users\Reed\Dropbox\GitHub\MineClient\Necessary files\Mineshafter-proxy.jar
 * Qualified Name:     mineshafter.MineServer
 * JD-Core Version:    0.6.1
 */