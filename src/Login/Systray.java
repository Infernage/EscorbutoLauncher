/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
/**
 *
 * @author Reed
 */
//No usada aún
public class Systray extends Thread{
    private TrayIcon icono;
    private SystemTray tray;
    private boolean exit = false;
    private Vista2 vis;
    private Process minecraft;
    private String pid;
    private File tempo;
    public Systray(Vista2 vista, boolean tmp){
        super("Systray");
        vis = vista;
        if (tmp) try {
            File[] files = null;
            if (Mainclass.OS.equals("windows")){
                files = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Data").listFiles();
            } else if (Mainclass.OS.equals("linux")){
                files = new File(System.getProperty("user.home") + "/.Data").listFiles();
            }
            boolean exit = false;
            int cont = 0;
            while (!exit && cont < files.length){
                if (files[cont].getName().contains("MGF")){
                    exit = true;
                } else{
                    cont++;
                }
            }
            BufferedReader bf = new BufferedReader(new FileReader(files[cont]));
            String line = null;
            List <String> lista = new ArrayList<String>();
            while ((line = bf.readLine()) != null){
                lista.add(line);
            }
            bf.close();
            tempo = files[cont];
            while(!lista.isEmpty()){
                String command = lista.get(0);
                if (command.contains("@")){
                    lista.clear();
                    String[] name = command.split("@");
                    pid = name[0];
                } else{
                    lista.remove(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        else{
            tempo = null;
            pid = null;
        }
    }
    public void addProcess(Process mine){
        minecraft = mine;
    }
    private void init(){
        if (SystemTray.isSupported()){
            tray = SystemTray.getSystemTray();
            Image ic = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Login/5548.png"));
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
                    //vis.res();
                    vis.setVisible(true);
                    vis.setExtendedState(JFrame.NORMAL);
                    vis.repaint();
                }
            };
            ActionListener destruir = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    minecraft.destroy();
                    if (pid != null) try{
                        if (Mainclass.OS.equals("windows")){
                            Process p = Runtime.getRuntime().exec("taskkill /pid " + pid);
                        } else if (Mainclass.OS.equals("linux")){
                            Process p = Runtime.getRuntime().exec("kill -9 " + pid);
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    exit = true;
                    tray.remove(icono);
                    icono = null;
                    tray = null;
                    vis.setVisible(true);
                    /*String cmd = null;
                    if (Mainclass.OS.equals("windows")){
                        cmd = System.getenv("windir") + "\\system32\\tasklist.exe";
                    } else if (Mainclass.OS.equals("linux")){
                        cmd = "ps -ef";
                    }
                    try {
                        Process p = Runtime.getRuntime().exec(cmd);
                        BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        while((line = bf.readLine()) != null){
                            if (line.contains("javaw")){
                                System.err.println(line);
                            } else{
                                System.out.println(line);
                            }
                        }
                        bf.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Systray.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                }
            };
            final JPopupMenu popup = new JPopupMenu();
            JMenuItem re = new JMenuItem("Restablecer");//, new ImageIcon("24-security-lock-open.png"));
            re.addActionListener(restablecer);
            popup.add(re);
            JMenuItem des = new JMenuItem("Kill MC");
            des.addActionListener(destruir);
            popup.add(des);
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
                ex1.printStackTrace();
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
        vis.setVisible(true);
    }
    @Override
    public void run(){
        this.init();
        String cmd = null;
        if (Mainclass.OS.equals("windows")){
            cmd = System.getenv("windir") + "\\system32\\tasklist.exe";
        } else if (Mainclass.OS.equals("linux")){
            cmd = "ps -ef";
        }
        while(!exit){
            try {
                Thread.sleep(500);
                boolean founded = false;
                Process p = Runtime.getRuntime().exec(cmd);
                BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while(!founded && (line = bf.readLine()) != null){
                    if (line.contains("javaw")){
                        founded = true;
                    }
                }
                bf.close();
                if (!founded){
                    this.salir();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (tempo != null) tempo.delete();
    }
}
