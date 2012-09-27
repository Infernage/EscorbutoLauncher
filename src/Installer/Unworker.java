package Installer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.lingala.zip4j.core.ZipFile;
/**
 *
 * @author Reed
 */
public class Unworker extends SwingWorker<Integer, Integer>{
    //Clase de desinstalación
    private JLabel eti;
    private JProgressBar pro;
    private JButton salir, finalizar;
    private JFrame fr;
    //Constructor con todas las variables necesarias
    public Unworker (JLabel A, JProgressBar B, JButton C, JButton D, JFrame G){
        eti = A;
        pro = B;
        finalizar = C;
        salir = D;
        fr = G;
        pro.setValue(0);
        eti.setText("Preparando desinstalación...");
    }
    @Override
    protected Integer doInBackground() throws Exception {
        Thread.sleep(1000);
        //Desinstalación
        pro.setValue(30);
        eti.setText("Preparando desinstalación...");
        Thread.sleep(3000);
        if (Vista.OS.equals("windows")){
            File mine = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft");
            if (mine.exists() && mine.isDirectory()){
                borrarFichero(mine);
                eti.setText("Minecraft desinstalado con éxito.");
                pro.setValue(50);
                Thread.sleep(2000);
                mine.deleteOnExit();
            }
            eti.setText("Recopilando información adicional...");
            Thread.sleep(5000);
            for (int i = 50; i < 90; i++){
                pro.setValue(i+1);
                Thread.sleep(100);
            }
            Thread.sleep(2000);
            File exec = new File(System.getProperty("user.home") + "\\Desktop\\RunMinecraft.jar");
            if (exec.exists()){//Si existe el acceso directo, lo borramos
                exec.delete();
            }
        } else if (Vista.OS.equals("linux")){
            File mine = new File(System.getProperty("user.home") + "/.minecraft");
            if (mine.exists() && mine.isDirectory()){
                borrarFichero(mine);
                eti.setText("Minecraft desinstalado con éxito.");
                pro.setValue(50);
                Thread.sleep(2000);
                mine.deleteOnExit();
            }
            eti.setText("Recopilando información adicional...");
            Thread.sleep(5000);
            for (int i = 50; i < 90; i++){
                pro.setValue(i+1);
                Thread.sleep(100);
            }
            Thread.sleep(2000);
            File exec = new File(System.getProperty("user.home") + "/Desktop/RunMinecraft.jar");
            if (exec.exists()){//Si existe el acceso directo, lo borramos
                exec.delete();
            }
        }
        eti.setText("Minecraft desinstalado con éxito!");
        return 0;
    }
    @Override
    protected void done(){
        finalizar.setEnabled(true);
        finalizar.setVisible(true);
        pro.setValue(100);
        salir.setVisible(false);
        fr.setVisible(true);
    }
    //Borrar fichero o directorio
    private void borrarFichero (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory()){
                borrarFichero(ficheros[x]);
            }
            eti.setText("Borrando... " + ficheros[x].getAbsolutePath());
            ficheros[x].delete();
        }
    }
}

