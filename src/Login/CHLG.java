package Login;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reed
 */
public class CHLG extends Thread{
    private JTextArea area;
    public CHLG (JTextArea ar){
        super("ChangeLog");
        area = ar;
    }
    private void inicializar(){
        try{
            //Leemos el txt en formato Unicode
            FileInputStream in = null;
            if (Mainclass.OS.equals("windows")){
                in = new FileInputStream(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\mods\\CHLog.txt");
            } else if (Mainclass.OS.equals("linux")){
                in = new FileInputStream(System.getProperty("user.home") + "/.minecraft/mods/CHLog.txt");
            }
            InputStreamReader isr = new InputStreamReader (in, "UTF8");
            BufferedReader bf = new BufferedReader (isr);
            String temp;
            area.append(bf.readLine());
            while ((temp = bf.readLine()) != null){
                area.append("\n");
                area.append(temp);
            }
            in.close();
            isr.close();
            bf.close();
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Error al abrir el ChangeLog.");
            e.printStackTrace(Mainclass.err);
        }
    }
    @Override
    public void run(){
        inicializar();
    }
}
