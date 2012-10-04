/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;
import Installer.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/**
 *
 * @author Reed
 */
public class Systray extends Thread{
    private TrayIcon icono;
    private SystemTray tray;
    private boolean exit = false;
    private Vista2 vis;
    private Process minecraft;
    public Systray(Vista2 vista){
        super("Systray");
        vis = vista;
    }
    public void addProcess(Process mine){
        minecraft = mine;
    }
    private void init(){
        if (SystemTray.isSupported()){
            tray = SystemTray.getSystemTray();
            Image ic = null;
            if (Mainclass.OS.equals("windows")){
                ic = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\5548.png");
            } else if (Mainclass.OS.equals("linux")){
                ic = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.home") + "/.minecraft/5548.png");
            }
            ActionListener salida = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    System.out.println("Exiting...");
                    System.exit(0);
                }
            };
            ActionListener restablecer = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    vis.res();
                    vis.setVisible(true);
                    vis.setExtendedState(JFrame.NORMAL);
                    vis.repaint();
                }
            };
            final JPopupMenu popup = new JPopupMenu();
            JMenuItem re = new JMenuItem("Restablecer");//, new ImageIcon("24-security-lock-open.png"));
            re.addActionListener(restablecer);
            popup.add(re);
            JMenuItem ex = new JMenuItem("Exit"); //, new ImageIcon("24-em-cross.png"));
            ex.addActionListener(salida);
            popup.add(ex);
            icono = new TrayIcon(ic, Mainclass.title + " " + Mainclass.version, null);
            icono.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e){
                    if (e.isPopupTrigger()){
                        popup.setLocation(e.getX(), e.getY()-50);
                        popup.setInvoker(popup);
                        popup.setVisible(true);
                    }
                }
            });
            try {
                tray.add(icono);
            } catch (AWTException ex1) {
                System.err.println(ex1);
            }
        } else{
            System.err.println("SysTray is not supported in your system!");
        }
    }
    public void salir(){
        exit = true;
        tray.remove(icono);
        icono = null;
        tray = null;
    }
    @Override
    public void run(){
        this.init();
        while(!exit){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
    }
}
