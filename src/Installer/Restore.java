package Installer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.*;
import net.lingala.zip4j.core.ZipFile;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reed
 */
public class Restore extends SwingWorker<Integer, Integer>{
    private SortedMap<String,Set<String>> fich; //Lista de todas las copias de Minecraft
    private File rest, newRest; //Minecraft a restaurar
    private JLabel eti;
    private JProgressBar pro;
    private JButton salir, finalizar;
    private Vista fr;
    public Restore(Vista G, JLabel A, JProgressBar B, JButton C, JButton D){
        eti = A;
        pro = B;
        finalizar = C;
        salir = D;
        fr = G;
        pro.setValue(0);
        eti.setText("Recopilando información de instalaciones anteriores...");
        fich = new TreeMap<String,Set<String>>();
    }
    @Override
    protected Integer doInBackground() throws Exception {
        //Cogemos la base de datos de las copias de seguridad
        if (Vista.OS.equals("windows")){
            File copia = new File(System.getProperty("user.home") + "\\Desktop\\Copia Minecraft");
            ficheros(copia);//Listamos los ficheros que haya en copia
            if (this.isCancelled()){
                return 0;
            }
            Lista vist = new Lista(fr, true, fich);//Creamos un Dialog para ver por cual restauramos
            vist.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            vist.setTitle("Minecraft recovery");
            vist.setLocationRelativeTo(null);
            vist.setVisible(true);
            String fi = vist.copia(); //Obtenemos la seleccionada
            if (fi.equals("null")){
                this.cancel(true);
                return 0;
            }
            StringTokenizer token = new StringTokenizer (fi, "/"); //Separamos la carpeta fecha de la carpeta hora
            String dia = token.nextToken();
            String hora = token.nextToken();
            token = null;
            rest = new File(copia.getAbsolutePath() + "\\" + dia + "\\" + hora + "\\.minecraft"); //Aplicamos el path del antiguo sistema
            newRest = new File(copia.getAbsolutePath() + "\\" + dia + "\\" + hora + "\\data.dat"); //Aplicamos el path del nuevo sistema
            pro.setValue(10);
            int temp = 0;
            if (!rest.exists() && !newRest.exists()){//Si las carpetas no existe, saltamos con error
                JOptionPane.showMessageDialog(null, "Error, no se ha podido encontrar la restauración.");
                eti.setText("Error inesperado al recuperar el archivo.");
                this.cancel(true);
                return 0;
            } else if (rest.exists() && !newRest.exists()){
                temp = 1;
            } else {
                temp = 2;
            }
            eti.setText("Preparando desinstalación...");
            Thread.sleep(3000);
            File mine = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft");
            if (mine.exists() && mine.isDirectory()){
                borrarFichero(mine);//Borramos el Minecraft instalado
                mine.delete();
                eti.setText("Minecraft desinstalado con éxito.");
                pro.setValue(50);
                Thread.sleep(2000);
                eti.setText("Restaurando copia de seguridad de Minecraft...");
                Thread.sleep(1000);
            }
            if (temp == 1){
                mine.mkdirs();
                copyDirectory(rest, mine);//Instalamos la restauración
            } else if (temp == 2){
                //Instalamos la restauración
                ZipFile dat = new ZipFile(newRest);
                if (dat.isEncrypted()){
                    dat.setPassword("Minelogin 3.0.0");
                }
                dat.extractAll(System.getProperty("user.home") + "\\AppData\\Roaming");
            }
            File infer = new File(mine.getAbsolutePath() + "\\Infernage.hdn");
            try{
                if (!infer.exists()){
                    infer.createNewFile();
                }
                Process hide = Runtime.getRuntime().exec("ATTRIB +H " + infer.getAbsolutePath());
            } catch (Exception ex){
                ex.printStackTrace(Login.Mainclass.err);
            }
            eti.setText("Recopilando información adicional...");
            Thread.sleep(3000);
            for (int i = 50; i < 90; i++){
                pro.setValue(i+1);
                Thread.sleep(100);
            }
        } else if (Vista.OS.equals("linux")){
            File copia = new File(System.getProperty("user.home") + "/Desktop/Copia Minecraft");
            ficheros(copia);//Listamos los ficheros que haya en copia
            Lista vist = new Lista(fr, true, fich);//Creamos un Dialog para ver por cual restauramos
            vist.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            vist.setTitle("Minecraft recovery");
            vist.setLocationRelativeTo(null);
            vist.setVisible(true);
            String fi = vist.copia(); //Obtenemos la seleccionada
            if (fi.equals("null")){
                this.cancel(true);
                return 0;
            }
            StringTokenizer token = new StringTokenizer (fi, "/"); //Separamos la carpeta fecha de la carpeta hora
            String dia = token.nextToken();
            String hora = token.nextToken();
            token = null;
            rest = new File(copia.getAbsolutePath() + "/" + dia + "/" + hora + "/.minecraft"); //Aplicamos el path del antiguo sistema
            newRest = new File(copia.getAbsolutePath() + "/" + dia + "/" + hora + "/data.dat"); //Aplicamos el path del nuevo sistema
            pro.setValue(10);
            int temp = 0;
            if (!rest.exists() && !newRest.exists()){//Si las carpetas no existe, saltamos con error
                JOptionPane.showMessageDialog(null, "Error, no se ha podido encontrar la restauración.");
                eti.setText("Error inesperado al recuperar el archivo.");
                this.cancel(true);
                return 0;
            } else if (rest.exists() && !newRest.exists()){
                temp = 1;
            } else {
                temp = 2;
            }
            eti.setText("Preparando desinstalación...");
            Thread.sleep(3000);
            File mine = new File(System.getProperty("user.home") + "/.minecraft");
            if (mine.exists() && mine.isDirectory()){
                borrarFichero(mine);//Borramos el Minecraft instalado
                eti.setText("Minecraft desinstalado con éxito.");
                pro.setValue(50);
                Thread.sleep(2000);
                eti.setText("Restaurando copia de seguridad de Minecraft...");
                Thread.sleep(1000);
            }
            if (temp == 1){
                mine.mkdirs();
                copyDirectory(rest, mine);//Instalamos la restauración
            } else if (temp == 2){
                //Instalamos la restauración
                ZipFile dat = new ZipFile(newRest);
                if (dat.isEncrypted()){
                    dat.setPassword("Minelogin 3.0.0");
                }
                dat.extractAll(System.getProperty("user.home"));
            }
            File infer = new File(mine.getAbsolutePath() + "/.Infernage.hdn");
            try{
                if (!infer.exists()){
                    infer.createNewFile();
                }
            } catch (Exception ex){
                ex.printStackTrace(Login.Mainclass.err);
            }
            eti.setText("Recopilando información adicional...");
            Thread.sleep(3000);
            for (int i = 50; i < 90; i++){
                pro.setValue(i+1);
                Thread.sleep(100);
            }
        }
        eti.setText("Minecraft restaurado con éxito!");
        return 0;
    }
    @Override
    protected void done(){
        finalizar.setEnabled(true);
        finalizar.setVisible(true);
        pro.setValue(100);
        salir.setVisible(false);
        fr.setVisible(true);
        if (this.isCancelled()){
            fr.retry();
        }
    }
    //Método para listar los ficheros copia
    private void ficheros (File copia){
        File [] copias;
        File [] copias2;
        if (copia.exists() && copia.isDirectory()){
            copias = copia.listFiles();
            for (int i = 0; i < copias.length; i++){
                String day = copias[i].getName();
                if (copias[i].isDirectory()){
                    copias2 = copias[i].listFiles();
                    Set<String> hour = new HashSet<String>();
                    for (int j = 0; j < copias2.length; j++){
                        hour.add(copias2[j].getName());
                    }
                    fich.put(day, hour);
                }
            }
        } else{
            JOptionPane.showMessageDialog(null, "No existen copias realizadas.");
            this.cancel(true);
        }
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
    //Copiar directorio de un sitio a otro
    private void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) { 
            if (!dstDir.exists()) { 
                dstDir.mkdir(); 
            }
             
            String[] children = srcDir.list(); 
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]), 
                    new File(dstDir, children[i])); 
            } 
        } else { 
            copy(srcDir, dstDir); 
        } 
    }
    //Copiar fichero de un sitio a otro
    private void copy(File src, File dst) throws IOException { 
        InputStream in = new FileInputStream(src); 
        OutputStream out = new FileOutputStream(dst); 
        eti.setText("Extrayendo en " + dst.getAbsolutePath());
         
        byte[] buf = new byte[4096]; 
        int len; 
        while ((len = in.read(buf)) > 0) { 
            out.write(buf, 0, len); 
        } 
        in.close(); 
        out.close(); 
    } 
}
