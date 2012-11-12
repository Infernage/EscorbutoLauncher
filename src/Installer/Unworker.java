package Installer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Login.Sources;
import java.io.*;
import javax.swing.*;
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
        System.out.println("Uninstalling thread execution(OK)");
        //Desinstalación
        pro.setValue(30);
        eti.setText("Preparando desinstalación...");
        Thread.sleep(3000);
        File mine = new File(Sources.path(Sources.DirMC));
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
        File exec = new File(System.getProperty("user.home") + Sources.sep() + "Desktop" + Sources.sep()
                + "RunMinecraft.jar");
        if (exec.exists()){//Si existe el acceso directo, lo borramos
            exec.delete();
        }
        eti.setText("Minecraft desinstalado con éxito!");
        System.out.println("Uninstall complete!");
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

