package MLR.installer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import MLR.InnerApi;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author Reed
 */
public class Uninstaller extends SwingWorker<Integer, Integer>{
    //Clase de desinstalación
    private JLabel eti;
    private JProgressBar pro;
    private String file;
    public boolean init = false, started = false, finish = false;
    //Constructor con todas las variables necesarias
    public void init (JLabel A, JProgressBar B, String path){
        init = true;
        eti = A;
        pro = B;
        file = path;
        pro.setValue(0);
    }
    @Override
    protected Integer doInBackground() throws Exception {
        started = true;
        pro.setValue(30);
        eti.setText("Preparando desinstalación...");
        File mine = new File(InnerApi.Directory.instance(file));
        if (mine.exists() && mine.isDirectory()){
            borrarFichero(mine);
            eti.setText("Minecraft desinstalado con éxito.");
            pro.setValue(50);
        }
        if (!mine.delete()){
            mine.deleteOnExit();
        }
        eti.setText("Minecraft desinstalado con éxito!");
        System.out.println("Uninstall complete!");
        finish = true;
        return 0;
    }
    @Override
    protected void done(){
        pro.setValue(100);
        finish = true;
        InnerApi.Init.multiGUI.reInit();
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

