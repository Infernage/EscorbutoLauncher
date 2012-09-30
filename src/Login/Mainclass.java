package Login;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Reed
 */
public class Mainclass {
    //Versión
    public final static String title = "MineLogin";
    public final static String version = "V3.1.1";
    public static String OS = System.getProperty("os.name");
    public static Map<String, Thread> hilos;
    public static Splash init;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        init = new Splash();
        Thread t = new Thread(init);
        t.start();
        hilos = new HashMap<String, Thread>();
        hilos.put("Splash", t);
        StringTokenizer token = new StringTokenizer(OS, " ");
        OS = token.nextToken().toLowerCase();
        token = null;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Mainclass.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (OS.equals("windows")){
            //Creamos las variables
            StringBuilder path = new StringBuilder(System.getProperty("user.home"));//Path del sistema
            File mine = new File(path.toString() + "\\AppData\\Roaming\\.minecraft");
            if (mine.exists()){
                File[] mines = mine.listFiles();
                File infer = new File(mine.getAbsolutePath() + "\\Infernage.hdn");
                if ((mines.length < 7) || !infer.exists()){
                    System.out.println("Ejecutando instalador...");
                    Installer.Vista.main(args);
                    return;
                }
            } else{
                System.out.println("Ejecutando instalador...");
                Installer.Vista.main(args);
                return;
            }
            File dat = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Data");
            if (!dat.exists()){
                System.out.println("Ejecutando instalador...");
                Installer.Vista.main(args);
                return;
            }
            path.append("\\AppData\\Roaming\\Data");//Agregamos los datos
            File runner = new File(path.toString() + "\\Logger\\RUN.jar");
            File runnerlib = new File(path.toString() + "\\Logger\\lib");
            File actual = new File(System.getProperty("user.dir") + "\\RUN.jar");
            File actuallib = new File(System.getProperty("user.dir") + "\\lib");
            if (!runner.exists() && !runnerlib.exists()){
               File dir = new File(path.toString() + "\\Logger");
               dir.mkdirs();
               try {
                   copy(actual, runner);
                   copyDirectory(actuallib, runnerlib);
               } catch (IOException ex) {
                   System.err.println(ex.getMessage());
               }
           } else if (runner.exists() && runnerlib.exists()){
               System.out.println("Already copied!");
           } else if (!runner.exists() && runnerlib.exists()){
                try {
                    copy(actual, runner);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
           } else if (runner.exists() && !runnerlib.exists()){
                try {
                    copyDirectory(actuallib, runnerlib);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
           }
            String name = "data.cfg", booleano = "boolean.txt";//Nombre de ficheros de datos
            String pss = "My Pass Phrase";//Nombre de frase pass
            String boo = path.toString() + "\\" + booleano;//Path del fichero booleano
            File fich = new File(path.toString());
            fich.mkdirs();
            path.append("\\").append(name);
            File fichero = new File (boo);
            //Controlamos si es la primera vez que se ejecuta y si hay registro o no
            if (fichero.exists()){
                try{
                   //Leemos el fichero si existe
                    BufferedReader bf = new BufferedReader (new FileReader (fichero));
                    boo = bf.readLine();
                } catch (IOException e){
                    
                }
                if (boo.equals("true")){
                    //Si en el fichero hay un true, significa que ya se ha registrado y abrimos la Vista2
                    Vista2 vista = new Vista2(pss);
                    vista.setIconImage(new ImageIcon(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\5547.png").getImage());
                    vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    vista.setTitle(title + " " + version);
                    vista.setLocationRelativeTo(null);
                    vista.setVisible(true);
                    vista.pack();
                } else{
                    //Sino, no se ha registrado, y abrimos la Vista
                    Vista.main(path.toString(), pss, fichero.getAbsolutePath());
                }
            } else {
                //Si no existe el fichero, lo creamos y escribimos en él false
                try{
                   fichero.createNewFile();
                   PrintWriter pw = new PrintWriter(fichero);
                   pw.print("false");
                   pw.close();
                } catch (IOException e){
                        
                }
                //Abrimos Vista
                Vista.main(path.toString(), pss, fichero.getAbsolutePath());
            }
        } else if (OS.equals("linux")){
           //Creamos las variables
           StringBuilder path = new StringBuilder(System.getProperty("user.home"));//Path del sistema
           File mine = new File(path.toString() + "/.minecraft");
            if (mine.exists()){
                File[] mines = mine.listFiles();
                File infer = new File(mine.getAbsolutePath() + "/.Infernage.hdn");
                if ((mines.length < 7) || !infer.exists()){
                    System.out.println("Ejecutando instalador...");
                    Installer.Vista.main(args);
                    return;
                }
            } else{
                System.out.println("Ejecutando instalador...");
                Installer.Vista.main(args);
                return;
            }
            File dat = new File(System.getProperty("user.home") + "/.Data");
            if (!dat.exists()){
                System.out.println("Ejecutando instalador...");
                Installer.Vista.main(args);
                return;
            }
           path.append("/Data");//Agregamos los datos
           File runner = new File(path.toString() + "/Logger/RUN.jar");
           File runnerlib = new File(path.toString() + "/Logger/lib");
           File actual = new File(System.getProperty("user.dir") + "/RUN.jar");
           File actuallib = new File(System.getProperty("user.dir") + "/lib");
           if (!runner.exists() && !runnerlib.exists()){
               File dir = new File(path.toString() + "/Logger");
               dir.mkdirs();
               try {
                   copy(actual, runner);
                   copyDirectory(actuallib, runnerlib);
               } catch (IOException ex) {
                   System.err.println(ex.getMessage());
               }
           } else if (runner.exists() && runnerlib.exists()){
               System.out.println("Already copied!");
           } else if (!runner.exists() && runnerlib.exists()){
                try {
                    copy(actual, runner);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
           } else if (runner.exists() && !runnerlib.exists()){
                try {
                    copyDirectory(actuallib, runnerlib);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
           }
           String name = "data.cfg", booleano = "boolean.txt";//Nombre de ficheros de datos
           String pss = "My Pass Phrase";//Nombre de frase pass
           String boo = path.toString() + "/" + booleano;//Path del fichero booleano
           File fich = new File(path.toString());
           fich.mkdirs();
           path.append("/").append(name);
           File fichero = new File (boo);
           //Controlamos si es la primera vez que se ejecuta y si hay registro o no
           if (fichero.exists()){
                try{
                   //Leemos el fichero si existe
                   BufferedReader bf = new BufferedReader (new FileReader (fichero));
                   boo = bf.readLine();
               } catch (IOException e){
                   
               }
               if (boo.equals("true")){
                   //Si en el fichero hay un true, significa que ya se ha registrado y abrimos la Vista2
                   Vista2 vista = new Vista2(pss);
                   vista.setIconImage(new ImageIcon(System.getProperty("user.home") + "/.minecraft/5547.png").getImage());
                   vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   vista.setTitle(title + " " + version);
                   vista.setLocationRelativeTo(null);
                   vista.setVisible(true);
                   vista.pack();
               } else{
                   //Sino, no se ha registrado, y abrimos la Vista
                   Vista.main(path.toString(), pss, fichero.getAbsolutePath());
               }
           } else {
               //Si no existe el fichero, lo creamos y escribimos en él false
               try{
                   fichero.createNewFile();
                   PrintWriter pw = new PrintWriter(fichero);
                   pw.print("false");
                   pw.close();
               } catch (IOException e){
                   
               }
               //Abrimos Vista
               Vista.main(path.toString(), pss, fichero.getAbsolutePath());
           }
        }
    }
    //Borrar fichero o directorio
    private static void borrarFichero (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory()){
                borrarFichero(ficheros[x]);
            }
            ficheros[x].delete();
        }
    }
    //Copiar directorio de un sitio a otro
    private static void copyDirectory(File srcDir, File dstDir) throws IOException {
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
    private static void copy(File src, File dst) throws IOException { 
        InputStream in = new FileInputStream(src); 
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[4096]; 
        int len; 
        while ((len = in.read(buf)) > 0) { 
            out.write(buf, 0, len); 
        } 
        in.close(); 
        out.close(); 
    }
}
