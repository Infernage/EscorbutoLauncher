package Mineshafter.mineshafter.proxy;

import java.io.IOException;
import java.io.PrintStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class MineProxy extends Thread
{
  public static String authServer = "mineshafter.appspot.com";

  public static Pattern SKIN_URL = Pattern.compile("http://skins\\.minecraft\\.net/MinecraftSkins/(.+?)\\.png");
  public static Pattern CLOAK_URL = Pattern.compile("http://skins\\.minecraft\\.net/MinecraftCloaks/(.+?)\\.png");
  public static Pattern GETVERSION_URL = Pattern.compile("http://session\\.minecraft\\.net/game/getversion\\.jsp");
  public static Pattern JOINSERVER_URL = Pattern.compile("http://session\\.minecraft\\.net/game/joinserver\\.jsp(.*)");
  public static Pattern CHECKSERVER_URL = Pattern.compile("http://session\\.minecraft\\.net/game/checkserver\\.jsp(.*)");

  public float version = 0.0F;
  private int port = -1;
  public Hashtable<String, byte[]> skinCache;
  public Hashtable<String, byte[]> cloakCache;

  public MineProxy(float version)
  {
    setName("MineProxy Thread");
    try
    {
      this.version = version;

      skinCache = new Hashtable();
      cloakCache = new Hashtable();
    } catch (Exception e) {
      System.out.println("Proxy starting error:");
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      ServerSocket server = null;
      int port = 9010;
      while (port < 12000) {
        try {
          System.out.println("Trying to proxy on port " + port);
          byte[] loopback = { 127, 0, 0, 1 };
          server = new ServerSocket(port, 16, InetAddress.getByAddress(loopback));
          this.port = port;
          System.out.println("Proxying successful");
        }
        catch (BindException ex) {
          port++;
        }
      }
      while (true)
      {
        Socket connection = server.accept();

        MineProxyHandler handler = new MineProxyHandler(this, connection);
        handler.start();
      }
    } catch (IOException e) {
      System.out.println("Error in server accept loop:");
      e.printStackTrace();
    }
  }

  public int getPort() {
    while (port < 0) {
      try {
        sleep(50L);
      } catch (InterruptedException e) {
        System.out.println("Interrupted while waiting for port");
        e.printStackTrace();
      }
    }
    return port;
  }
}

/* Location:           C:\Users\Reed\Dropbox\GitHub\MineClient\Necessary files\Mineshafter-proxy.jar
 * Qualified Name:     mineshafter.proxy.MineProxy
 * JD-Core Version:    0.6.1
 */