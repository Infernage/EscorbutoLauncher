package Mineshafter.mineshafter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams
{
  public static int pipeStreams(InputStream in, OutputStream out)
    throws IOException
  {
    byte[] b = new byte[8192];

    int total = 0;
    boolean buc = true;
    while (buc) {
      int read = 0;
      try {
        read = in.read(b);
        if (read != -1);
      }
      catch (IOException e)
      {
      }
      out.write(b, 0, read);
      total += read;
    }
    out.flush();
    return total;
  }
  public static void pipeStreamsActive(final InputStream in, final OutputStream out) {
    Thread thread = new Thread("Active Pipe Thread") {
      public void run() {
        byte[] b = new byte[8192];
        try
        {
          while (true) {
            int count = in.read(b);
            if (count == -1) return;
            out.write(b, 0, count);
            out.flush();
          }
        }
        catch (IOException e)
        {
        }
      }
    };
    thread.start();
  }
}

/* Location:           C:\Users\Reed\Dropbox\GitHub\MineClient\Necessary files\Mineshafter-proxy.jar
 * Qualified Name:     mineshafter.util.Streams
 * JD-Core Version:    0.6.1
 */