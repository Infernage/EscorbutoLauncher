/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Minecraft;

import Login.Sources;
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
        if (Sources.debug) System.out.println("[->Resources set<-]");
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
        if (Sources.debug) System.out.println("[->Starting applet<-]");
        try{
            applet = new Aplication(ap, new URL("http://www.minecraft.net/game"));
        } catch (Exception ex){
            Login.Sources.fatalException(ex, "Failed to start.", 1);
        }
        if (Sources.debug) System.out.println("[->Setting applet parameters<-]");
        applet.setParameter("username", user);
        applet.setParameter("sessionid", session);
        applet.setParameter("stand-alone", "true");
        if (Sources.debug) System.out.println("[->Setting stub<-]");
        ap.setStub(applet);
        if (Sources.debug) System.out.println("[->Adding applet<-]");
        add(applet);
        applet.setPreferredSize(dimen);
        pack();
        setLocationRelativeTo(null);
        if (Sources.debug) System.out.println("[->Setting in extended state<-]");
        setExtendedState(6);
        if (Sources.debug) System.out.println("[->Validating...<-]");
        validate();
        if (Sources.debug) System.out.println("[->Launching...<-]");
        applet.init();
        applet.start();
        if (Sources.debug) System.out.println("[->DONE<-]");
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
