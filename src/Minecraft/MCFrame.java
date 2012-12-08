/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Minecraft;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Reed
 */
public class MCFrame extends Frame implements WindowListener{
    private Aplication applet = null;
    public MCFrame(String param){
        super(param);
        try{
            BufferedImage image = ImageIO.read(getClass().getResource("/Resources/5547.png"));
            setIconImage(image);
        } catch (Exception ex){
            Login.Sources.fatalException(ex, "Failed to initialite.", 1);
        }
        super.setVisible(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        addWindowListener(this);
    }
    public void start(Applet ap, String user, String session, Dimension dimen){
        try{
            applet = new Aplication(ap, new URL("http://www.minecraft.net/game"));
        } catch (Exception ex){
            Login.Sources.fatalException(ex, "Failed to start.", 1);
        }
        applet.setParameter("username", user);
        applet.setParameter("sessionid", session);
        applet.setParameter("stand-alone", "true");
        ap.setStub(applet);
        add(applet);
        applet.setPreferredSize(dimen);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(6);
        validate();
        applet.init();
        applet.start();
        setVisible(true);
    }
    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (applet != null){
            applet.stop();
            applet.destroy();
        }
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }
}
