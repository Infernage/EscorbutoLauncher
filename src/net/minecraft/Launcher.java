package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 * Applet used to support the Minecraft loaded.
 * @author Infernage
 */
public class Launcher extends Applet implements AppletStub {
  private Applet wrappedApplet;
  private URL urlGame;
  private boolean active = false;
  private final Map<String, String> params;

  public Launcher(Applet applet, URL url){
    params = new TreeMap();
    setLayout(new BorderLayout());
    add(applet, "Center");
    wrappedApplet = applet;
    urlGame = url;
  }

  public void setParameter(String name, String value) {
    params.put(name, value);
  }

  public void replace(Applet applet) {
    wrappedApplet = applet;
    applet.setStub(this);
    applet.setSize(getWidth(), getHeight());
    setLayout(new BorderLayout());
    add(applet, "Center");
    applet.init();
    active = true;
    applet.start();
    validate();
  }

  @Override
  public String getParameter(String name)
  {
    String param = (String)params.get(name);
    if (param != null)
      return param;
    try
    {
        return super.getParameter(name);
    } catch (Exception ignore) {
    }
    return null;
  }

  @Override
  public boolean isActive()
  {
    return active;
  }

  @Override
  public void appletResize(int width, int height)
  {
    super.setSize(width, height);
    wrappedApplet.setSize(width, height);
  }

  @Override
  public void init()
  {
    if (wrappedApplet != null)
      wrappedApplet.init();
  }

  @Override
  public void start()
  {
    wrappedApplet.start();
    active = true;
  }

  @Override
  public void stop()
  {
    wrappedApplet.stop();
    active = false;
  }

  @Override
  public void destroy() {
    wrappedApplet.destroy();
  }

  @Override
  public URL getCodeBase()
  {
    return wrappedApplet.getCodeBase();
  }

  @Override
  public URL getDocumentBase()
  {
    return urlGame;
  }

  @Override
  public void setVisible(boolean b)
  {
    super.setVisible(b);
    wrappedApplet.setVisible(b);
  }
}