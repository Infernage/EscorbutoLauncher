package Mineshafter.mineshafter.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Mineshafter.mineshafter.util.Streams;

public class MineProxyHandler extends Thread
{
  private DataInputStream fromClient;
  private DataOutputStream toClient;
  private Socket connection;
  private MineProxy proxy;
  private static String[] BLACKLISTED_HEADERS = { "Connection", "Proxy-Connection", "Transfer-Encoding" };

  public MineProxyHandler(MineProxy proxy, Socket conn) throws IOException {
    setName("MineProxyHandler Thread");

    this.proxy = proxy;

    connection = conn;
    fromClient = new DataInputStream(conn.getInputStream());
    toClient = new DataOutputStream(conn.getOutputStream());
  }

  public void run() { Map headers = new HashMap();

    String[] requestLine = readUntil(fromClient, '\n').split(" ");
    String method = requestLine[0].trim().toUpperCase();
    String url = requestLine[1].trim();

    System.out.println("Request: " + method + " " + url);
    String header;
    do {
      header = readUntil(fromClient, '\n').trim();

      int splitPoint = header.indexOf(':');
      if (splitPoint != -1)
        headers.put(header.substring(0, splitPoint).toLowerCase().trim(), header.substring(splitPoint + 1).trim());
    }
    while (header.length() > 0);

    Matcher skinMatcher = MineProxy.SKIN_URL.matcher(url);
    Matcher cloakMatcher = MineProxy.CLOAK_URL.matcher(url);
    Matcher getversionMatcher = MineProxy.GETVERSION_URL.matcher(url);
    Matcher joinserverMatcher = MineProxy.JOINSERVER_URL.matcher(url);
    Matcher checkserverMatcher = MineProxy.CHECKSERVER_URL.matcher(url);

    byte[] data = (byte[])null;
    String contentType = null;

    if (skinMatcher.matches()) {
      System.out.println("Skin");

      String username = skinMatcher.group(1);
      if (proxy.skinCache.containsKey(username)) {
        System.out.println("Skin from cache");

        data = (byte[])proxy.skinCache.get(username);
      } else {
        url = "http://" + MineProxy.authServer + "/skin/" + username + ".png";
        System.out.println("To: " + url);

        data = getRequest(url);
        System.out.println("Response length: " + data.length);

        proxy.skinCache.put(username, data);
      }
    }
    else if (cloakMatcher.matches()) {
      System.out.println("Cloak");

      String username = cloakMatcher.group(1);
      if (proxy.cloakCache.containsKey(username)) {
        System.out.println("Cloak from cache");
        data = (byte[])proxy.cloakCache.get(username);
      } else {
        url = "http://" + MineProxy.authServer + "/cloak/get.jsp?user=" + username;
        System.out.println("To: " + url);

        data = getRequest(url);
        System.out.println("Response length: " + data.length);

        proxy.cloakCache.put(username, data);
      }
    }
    else if (getversionMatcher.matches()) {
      System.out.println("GetVersion");

      url = "http://" + MineProxy.authServer + "/game/getversion.jsp?proxy=" + proxy.version;
      System.out.println("To: " + url);
      try
      {
        int postlen = Integer.parseInt((String)headers.get("content-length"));
        char[] postdata = new char[postlen];
        InputStreamReader reader = new InputStreamReader(fromClient);
        reader.read(postdata);

        data = postRequest(url, new String(postdata), "application/x-www-form-urlencoded");
      }
      catch (IOException e)
      {
        System.out.println("Unable to read POST data from getversion request");
        e.printStackTrace();
      }
    }
    else if (joinserverMatcher.matches()) {
      System.out.println("JoinServer");

      String params = joinserverMatcher.group(1);
      url = "http://" + MineProxy.authServer + "/game/joinserver.jsp" + params;
      System.out.println("To: " + url);

      data = getRequest(url);
      contentType = "text/plain";
    }
    else if (checkserverMatcher.matches()) {
      System.out.println("CheckServer");

      String params = checkserverMatcher.group(1);
      url = "http://" + MineProxy.authServer + "/game/checkserver.jsp" + params;
      System.out.println("To: " + url);

      data = getRequest(url);
    }
    else {
      System.out.println("No handler. Piping.");
      try
      {
        if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
          url = "http://" + url;
        }
        URL u = new URL(url);
        if (method.equals("CONNECT")) {
          int port = u.getPort();
          if (port == -1) port = 80;
          Socket sock = new Socket(u.getHost(), port);

          Streams.pipeStreamsActive(sock.getInputStream(), toClient);
          Streams.pipeStreamsActive(connection.getInputStream(), sock.getOutputStream());
        }
        else
        {
          int responseCode;
          Map h;
          List vals;
          if ((method.equals("GET")) || (method.equals("POST"))) {
            HttpURLConnection c = (HttpURLConnection)u.openConnection(Proxy.NO_PROXY);
            c.setRequestMethod(method);
            boolean post = method.equals("POST");
                for (Iterator it = headers.keySet().iterator(); it.hasNext();) {
                    String k = (String) it.next();
                    c.setRequestProperty(k, (String)headers.get(k));
                }

            if (post) {
              c.setDoInput(true);
              c.setDoOutput(true);
              c.setUseCaches(false);
              c.connect();
              int postlen = Integer.parseInt((String)headers.get("content-length"));
              byte[] postdata = new byte[postlen];
              fromClient.read(postdata);

              DataOutputStream os = new DataOutputStream(c.getOutputStream());
              os.write(postdata);
            }

            responseCode = c.getResponseCode();
            String res = "HTTP/1.0 " + responseCode + " " + c.getResponseMessage() + "\r\n";
            res = res + "Connection: close\r\nProxy-Connection: close\r\n";

            h = c.getHeaderFields();
                for (Iterator it = h.keySet().iterator(); it.hasNext();) {
                    String k = (String) it.next();
                    if (k != null) {
                      k = k.trim();
                      for (String forbiddenHeader : BLACKLISTED_HEADERS) {
                        if (k.equalsIgnoreCase(forbiddenHeader))
                          break;
                      }
                      vals = (List)h.get(k);
                        for (Iterator ite = vals.iterator(); ite.hasNext();) {
                            String v = (String) ite.next();
                            res = res + k + ": " + v + "\r\n";
                        }
                    }
                }
            res = res + "\r\n";

            int size = -1;
            if (responseCode / 100 != 5) {
              toClient.writeBytes(res);
              size = Streams.pipeStreams(c.getInputStream(), toClient);
            }

            toClient.close();
            connection.close();

            System.out.println("Piping finished, data size: " + size);
          }
          else if (method.equals("HEAD")) {
            HttpURLConnection c = (HttpURLConnection)u.openConnection(Proxy.NO_PROXY);
            c.setRequestMethod("HEAD");
                for (Iterator it = headers.keySet().iterator(); it.hasNext();) {
                    String k = (String) it.next();
                    c.setRequestProperty(k, (String)headers.get(k));
                }

            String res = "HTTP/1.0 " + c.getResponseCode() + " " + c.getResponseMessage() + "\r\n";
            res = res + "Proxy-Connection: close\r\n";
            h = c.getHeaderFields();
                for (Iterator it = h.keySet().iterator(); it.hasNext();) {
                    String k = (String) it.next();
                    if (k != null) {
                      vals = (List)h.get(k);
                        for (Iterator ite = vals.iterator(); ite.hasNext();) {
                            String v = (String) ite.next();
                            res = res + k + ": " + v + "\r\n";
                        }
                    }
                }
            res = res + "\r\n";

            toClient.writeBytes(res);
            toClient.close();
            connection.close();
          }
          else {
            System.out.println("UNEXPECTED REQUEST TYPE: " + method);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      return;
    }
    try
    {
      if (data != null) {
        toClient.writeBytes("HTTP/1.0 200 OK\r\nConnection: close\r\nProxy-Connection: close\r\nContent-Length: " + data.length + "\r\n");
        if (contentType != null) {
          toClient.writeBytes("Content-Type: " + contentType + "\r\n");
        }
        toClient.writeBytes("\r\n");
        toClient.write(data);
        toClient.flush();
      }
      fromClient.close();
      toClient.close();
      connection.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    } }

  public static byte[] getRequest(String url)
  {
    try {
      HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection(Proxy.NO_PROXY);
      conn.setInstanceFollowRedirects(false);
      Map requestHeaders = conn.getRequestProperties();
      int code = conn.getResponseCode();

      if ((code == 301) || (code == 302) || (code == 303)) {
        System.out.println("Java didn't redirect automatically, going manual: " + Integer.toString(code));
        String l = conn.getHeaderField("location").trim();
        System.out.println("Manual redirection to: " + l);
        return getRequest(l);
      }

      System.out.println("Response: " + code);
      if (code == 403) {
        String s = "403 from req to " + url + "\nRequest headers:\n";
        List vals;
            for (Iterator it = requestHeaders.keySet().iterator(); it.hasNext();) {
                String k = (String) it.next();
                if (k != null) {
                  vals = (List)requestHeaders.get(k);
                    for (Iterator ite = vals.iterator(); ite.hasNext();) {
                        String v = (String) ite.next();
                        s = s + k + ": " + v + "\n";
                    }
                }
            }
        s = s + "Response headers:\n";
        Map responseHeaders = conn.getHeaderFields();
            for (Iterator it = responseHeaders.keySet().iterator(); it.hasNext();) {
                String k = (String) it.next();
                if (k != null) {
                  vals = (List)responseHeaders.get(k);
                    for (Iterator ite = vals.iterator(); ite.hasNext();) {
                        String v = (String) ite.next();
                        s = s + k + ": " + v + "\n";
                    }
                }
            }
        System.out.println(s);
        System.out.println("Contents:\n" + new String(grabData(conn.getErrorStream())));
      }

      if (code / 100 == 4) {
        return new byte[0];
      }
      BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
      return grabData(in);
    }
    catch (MalformedURLException e) {
      System.out.println("Bad URL in getRequest:");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IO error during a getRequest:");
      e.printStackTrace();
    }

    return new byte[0];
  }

  public static byte[] postRequest(String url, String postdata, String contentType) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(out);
    try
    {
      writer.write(postdata);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] rd = postRequest(url, out.toByteArray(), contentType);

    return rd;
  }

  public static byte[] postRequest(String url, byte[] postdata, String contentType) {
    try {
      URL u = new URL(url);

      HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection(Proxy.NO_PROXY);
      c.setDoOutput(true);
      c.setRequestMethod("POST");

      c.setRequestProperty("Host", u.getHost());
      c.setRequestProperty("Content-Length", Integer.toString(postdata.length));
      c.setRequestProperty("Content-Type", contentType);

      BufferedOutputStream out = new BufferedOutputStream(c.getOutputStream());
      out.write(postdata);
      out.flush();
      out.close();

      return grabData(new BufferedInputStream(c.getInputStream()));
    }
    catch (UnknownHostException e)
    {
      System.out.println("Unable to resolve remote host, returning null");
    }
    catch (MalformedURLException e) {
      System.out.println("Bad URL when doing postRequest:");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static byte[] grabData(InputStream in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    boolean buc = true;
    while (buc)
    {
      try
      {
        int len = in.read(buffer);
        if (len != -1);
      }
      catch (IOException e)
      {
      }
      int len = 0;
      out.write(buffer, 0, len);
    }
    return out.toByteArray();
  }

  public static String readUntil(DataInputStream is, String endSequence) {
    return readUntil(is, endSequence.getBytes());
  }

  public static String readUntil(DataInputStream is, char endSequence) {
    return readUntil(is, new byte[] { (byte)endSequence });
  }

  public static String readUntil(DataInputStream is, byte endSequence) {
    return readUntil(is, new byte[] { endSequence });
  }

  public static String readUntil(DataInputStream is, byte[] endSequence) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    String r = null;
    try {
      int i = 0;
      boolean end;
      do { end = false;
        byte b = is.readByte();
        if (b == endSequence[i]) {
          if (i == endSequence.length - 1) {
            end = true;
          }
          i++;
        } else {
          i = 0;
        }

        out.write(b);
      }
      while (!end);
    }
    catch (IOException ex) {
      System.out.println("readUntil unable to read from InputStream, endSeq: " + new String(endSequence));
      ex.printStackTrace();
    }
    try
    {
      r = out.toString("UTF-8");
    } catch (UnsupportedEncodingException ex) {
      System.out.println("readUntil unable to encode data: " + out.toString());
      ex.printStackTrace();
    }

    return r;
  }
}

/* Location:           C:\Users\Reed\Dropbox\GitHub\MineClient\Necessary files\Mineshafter-proxy.jar
 * Qualified Name:     mineshafter.proxy.MineProxyHandler
 * JD-Core Version:    0.6.1
 */