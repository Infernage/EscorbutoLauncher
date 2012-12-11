package Installer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Login.Sources;
import java.awt.Color;
import java.io.*;
import java.util.*;
import javax.swing.*;
import net.lingala.zip4j.core.ZipFile;
/**
 *
 * @author Reed
 */
public class Worker extends SwingWorker <String, Integer>{
    private JLabel eti;
    private JProgressBar prog;
    private boolean exito = true;
    private String instance;
    public boolean init = false, started = false, finish = false;
    public void init (JProgressBar pro, String name, JLabel lab){
        init = true;
        prog = pro;
        eti = lab;
        instance = name;
    }
    private void back(){
        File tmp = new File(Sources.Prop.getProperty("user.instance") + File.separator + instance);
        borrarFichero(tmp);
        tmp.delete();
    }
    //Método cuando se produce el this.execute()
    @Override
    protected String doInBackground() throws Exception {
        started = true;
        System.out.println("Install thread execution(OK)");
        String res = null;
        File fichsrc = new File(Sources.Prop.getProperty("user.data") + File.separator + "Update" + 
                File.separator + Sources.Directory.Dirsrc + File.separator + Sources.Directory.Dirsrc + 
                ".dat");
        File fichdst = new File(Sources.Prop.getProperty("user.instance") + File.separator + instance);
        if (!fichdst.exists()){
            fichdst.mkdirs();
        }
        System.out.println("Installing new version");
        eti.setText("Instalando Minecraft...");
        prog.setValue(20);
        Thread.sleep(3000);
        for (int i = 20; i < 70; i++){
            prog.setValue(i+1);
            Thread.sleep(100);
        }
        try{
            ZipFile dat = new ZipFile(fichsrc);
            System.out.println("Decripting data...");
            if (!fichsrc.exists()){
                System.out.println("[ERROR] Data couldnt be found!");
            }
            if (dat.isEncrypted()){
                System.out.print("Setting password... ");
                dat.setPassword("Minelogin 3.0.0");
                System.out.println("OK");
            }
            System.out.print("Extracting installation files... ");
            dat.extractAll(fichdst.getAbsolutePath());
            System.out.println("OK");
            if (Sources.OS.equals("windows") && !new File(Sources.path(Sources.Directory.DirData())).isHidden()){
                Process hidden = Runtime.getRuntime().exec("ATTRIB +H " + Sources.Prop.getProperty("user.data"));
            }
        } catch (IOException e){
            Sources.exception(e, "Error en la instalación. Comprueba que no has borrado "
                    + "ningún archivo de la carpeta.\n" + e.getMessage());
            exito = false;
            this.back();
        }
        if (exito){
            res = "Done!";
        } else{
            res = "Error!";
        }
        System.out.println(res);
        System.out.println("Installation complete!");
        finish = true;
        return res;
    }
    //Cuando termina, salta este método
    protected void done() {
        Sources.Init.multiGUI.reInit();
        if (exito && !this.isCancelled()){
            try{
                eti.setText("Minecraft instalado con éxito");
                Thread.sleep(500);
                prog.setValue(100);
            } catch (Exception ex){
                Sources.exception(ex, ex.getMessage());
            }
        } else if (!exito){
            if (eti != null){
                eti.setText("Minecraft no ha podido ser instalado.");
            } else{
                prog.setForeground(Color.red);
                prog.setString("Error en la instalación.");
            }
        }
    }
    //Borrar fichero o directorio
    private void borrarFichero (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory() && !ficheros[x].getName().equals("stats") && !ficheros[x].getName().equals("saves")){
                borrarFichero(ficheros[x]);
            }
            System.out.println("Deleting: " + ficheros[x].getName());
            eti.setText("Borrando... " + ficheros[x].getAbsolutePath());
            if (!ficheros[x].getName().equals("options.txt") && !ficheros[x].getName().equals("servers.dat")){
                ficheros[x].delete();
            }
        }
    }
    /**
     * Method to check if a file is equal to other one.
     * @param src The source file
     * @param dst The destiny file
     * @return {@code true} if the two files are equals, and {@code false} in otherwise
     */
    public static boolean check(File src, File dst){
        boolean res = false;
        try{
            long A = src.length();
            long B = dst.length();
            String one = src.getName();
            String two = dst.getName();
            BufferedInputStream alpha = new BufferedInputStream(new FileInputStream(src));
            BufferedInputStream beta = new BufferedInputStream(new FileInputStream(dst));
            boolean temp = check(alpha, beta);
            if (A == B && one.equals(two) && temp){
                res = true;
            }
            alpha.close();
            beta.close();
        } catch (Exception ex) {
            Sources.exception(ex, "Error al comprobar los archivos.");
        }
        return res;
    }
    /**
     * Compare two input stream
     * 
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the streams contain the same content, or false otherwise
     * @throws IOException
     * @throws IllegalArgumentException if the stream is null
     */
    public static boolean isSame( InputStream input1,
                                InputStream input2 ){
        boolean error = false;
        try {
            byte[] buffer1 = new byte[1024];
            byte[] buffer2 = new byte[1024];
            int numRead1 = 0;
            int numRead2 = 0;
            while (true) {
                numRead1 = input1.read(buffer1);
                numRead2 = input2.read(buffer2);
                if (numRead1 > -1) {
                    if (numRead2 != numRead1) return false;
                    // Otherwise same number of bytes read
                    if (!Arrays.equals(buffer1, buffer2)) return false;
                    // Otherwise same bytes read, so continue ...
                } else {
                    // Nothing more in stream 1 ...
                    return numRead2 < 0;
                }
            }
        } catch (IOException e) {
            error = true; // this error should be thrown, even if there is an error closing stream 2
            Sources.exception(e, e.getMessage());
        } catch (RuntimeException e) {
            error = true; // this error should be thrown, even if there is an error closing stream 2
            Sources.exception(e, e.getMessage());
        }
        return false;
    }
      
    /**
     * Method to check if an inputstream is equal to another one.
     * @param alpha The source inputstream
     * @param beta The destiny inputstream
     * @return {@code true} if the two inputstream are equals, and {@code false} in otherwise
     */
    public static boolean check (BufferedInputStream alpha, BufferedInputStream beta){
        boolean res = true;
        try{
            int A = alpha.read();
            int B = beta.read();
            while ((A != -1) && (B != -1) && res){
                if (A != B){
                    res = false;
                }
                A = alpha.read();
                B = beta.read();
            }
        } catch (Exception ex){
            Sources.exception(ex, "Error al comprobar los archivos.");
            res = false;
        }
        try {
            alpha.close();
            beta.close();
        } catch (IOException ex) {
            
        }
        return res;
    }
    /**
     * This method checks if a directory is equal to another one.
     * @param srcDir The source directory.
     * @param dstDir The destination directory.
     * @return {@code true} if the two directories are equals, and {@code false} in otherwise
     */
    public static boolean checkDirectory(File srcDir, File dstDir){
        boolean res = true;
        try{
            long A = srcDir.length();
            long B = dstDir.length();
            String one = srcDir.getName();
            String two = dstDir.getName();
            if (A == B && one.equals(two)){
                if (srcDir.isDirectory() && dstDir.isDirectory()){
                    File[] tmp = srcDir.listFiles();
                    File[] temp = dstDir.listFiles();
                    if (tmp.length == temp.length){
                        int i = 0;
                        while((i < tmp.length) && res){
                            res = checkDirectory(tmp[i], temp[i]);
                            i++;
                        }
                    } else{
                        res = false;
                    }
                } else if (srcDir.isFile() && dstDir.isFile()){
                    res = check(srcDir, dstDir);
                } else{
                    res = false;
                }
            } else{
                res = false;
            }
        } catch (Exception ex) {
            Sources.exception(ex, "Error al comprobar los archivos.");
        }
        return res;
    }
    /**
     * Equals to the other {@code checkDirectory}, but this method doesn't compare the names and the length.
     */
    public static boolean checkDirectory(File[] srcList, File[] dstList){
        boolean res = true;
        int i = 0;
        if (srcList.length != dstList.length){
            return false;
        }
        while((i < srcList.length) && res){
            res = checkDirectory(srcList[i], dstList[i]);
            i++;
        }
        return res;
    }
}
