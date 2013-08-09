package elr.core.util;

import elr.core.Loader;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Infernage
 */
//Not used yet!
public class Systray{
    private static Systray instance;
    
    private TrayIcon icon;
    private SystemTray container;
    private JPopupMenu menu;
    
    private void init(){
        if (SystemTray.isSupported()){
            container = SystemTray.getSystemTray();
            Image ic = Toolkit.getDefaultToolkit().getImage(getClass()
                    .getResource("/elr/resources/Systray.jpg"));
            ActionListener exit = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
            ActionListener killMC = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //MinecraftLoader.destroyProcess();
                }
            };
            ActionListener bootMode = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Systray.imprMessage("Changed mode");
                }
            };
            menu = new JPopupMenu();
            JMenuItem mode = new JMenu("Change to Normal mode");
            mode.addActionListener(bootMode);
            menu.add(mode);
            JMenuItem kill = new JMenu("Kill MC");
            kill.addActionListener(killMC);
            menu.add(kill);
            JMenuItem exitProgram = new JMenu("Exit program");
            exitProgram.addActionListener(exit);
            menu.add(exitProgram);
            icon = new TrayIcon(ic, Loader.getVersion());
            icon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()){
                        menu.setLocation(e.getX(), e.getY()-50);
                        menu.setInvoker(menu);
                        menu.setVisible(true);
                    }
                }
            });
            try {
                container.add(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            MessageControl.showErrorMessage("SystemTray is not supported in your system.", 
                    this.getClass().getName() + " not supported");
        }
    }
    
    public static void imprMessage(String msg){
        if (instance != null) instance.icon.displayMessage("Message received:", msg, 
                TrayIcon.MessageType.INFO);
    }
}
