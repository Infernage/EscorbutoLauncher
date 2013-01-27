/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.launcher.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Infernage
 */
public class Aplication extends Applet implements AppletStub {
    private Applet applet;
    private URL game;
    private boolean active = false;
    private final Map<String, String> params;
    public Aplication(Applet aplet, URL url){
        params = new TreeMap();
        setLayout(new BorderLayout());
        add(aplet, "Center");
        applet = aplet;
        game = url;
    }
    public void setParameter(String key, String value){
        params.put(key, value);
    }
    public void replace(Applet aplet){
        applet = aplet;
        aplet.setStub(this);
        aplet.setSize(getWidth(), getHeight());
        setLayout(new BorderLayout());
        add(aplet, "Center");
        aplet.init();
        active = true;
        aplet.start();
        validate();
    }
    @Override
    public String getParameter(String key){
        String res = params.get(key);
        if (res != null){
            return res;
        }
        try{
            return super.getParameter(key);
        } catch (Exception ex){
            return null;
        }
    }
    @Override
    public boolean isActive(){
        return active;
    }
    @Override
    public void appletResize(int witdh, int height){
        applet.resize(witdh, height);
    }
    @Override
    public void resize(int witdh, int height){
        applet.resize(witdh, height);
    }
    @Override
    public void resize(Dimension dimension){
        applet.resize(dimension);
    }
    @Override
    public void init(){
        if (applet != null){
            applet.init();
        }
    }
    @Override
    public void start(){
        applet.start();
        active = true;
    }
    @Override
    public void stop(){
        applet.stop();
        active = false;
    }
    @Override
    public void destroy(){
        applet.destroy();
    }
    @Override
    public URL getCodeBase(){
        return applet.getCodeBase();
    }
    @Override
    public URL getDocumentBase(){
        return game;
    }
    @Override
    public void setVisible(boolean tmp){
        super.setVisible(tmp);
        applet.setVisible(tmp);
    }
    @Override
    public void update(Graphics g){
        
    }
    @Override
    public void paint(Graphics g){
        
    }
}
