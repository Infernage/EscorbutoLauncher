/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Reed
 */
public class Sources {
    private static String separatorWin = "\\", separatorLin = "/";
    public static boolean duplicate = false;
    public static String OS;
    /**
     * This gets the Mineshafter jar
     */
    public static String MS = "Mineshafter-proxy.jar";
    /**
     * This gets this jar name
     */
    public static String jar = "RUN.jar";
    /**
     * This gets the direct access name jar
     */
    public static String access = "RunMinecraft.jar";
    /**
     * This gets the temporal jar
     */
    public static String temporal = "Temporal.jar";
    /**
     * This gets the password
     */
    public static String pss = "MineClient";
    /**
     * This gets the file name which check if the installed minecraft is supported by the Login
     */
    public static String infernage(){
        String win = "Infernage.hdn", lin = ".Infernage.hdn";
        if (OS.equals("windows")){
            return win;
        } else if (OS.equals("linux")){
            return lin;
        }
        return null;
    }
    /**
     * This gets the configuration file name of the instance
     */
    public static String infoInst = "Instance.config";
    /**
     * This gets the configuration file name of the server
     */
    public static String lsNM = "ALLNM-MC.cfg";
    /**
     * This gets the file name which check if it's the first time that is oppened
     */
    public static String bool = "boolean.txt";
    /**
     * This gets the file name which check if the remember option was used
     */
    public static String rmb = "RMB.txt";
    /**
     * This gets the file name which set the last login
     */
    public static String login = "lstlg.data";
    /**
     * This gets the name of directory and file of installation
     */
    public static String Dirsrc = "inst";
    /**
     * This gets the directory name of data files stored in this PC
     */
    public static String DirData() {
        String win = "Data", lin = ".Data";
        if (OS.equals("windows")){
            return win;
        } else if (OS.equals("linux")){
            return lin;
        }
        return null;
    }
    /**
     * This gets the directory name of minecraft
     */
    public static String DirMC = ".minecraft";
    /**
     * This gets the directory name used to store all jars used by this
     */
    public static String Dirfiles = "Logger";
    /**
     * This gets the directory name used to store files from the server
     */
    public static String DirNM = "Base";
    /**
     * This gets the directory name used to store temporally files from the server
     */
    public static String DirTMP = "TMP";
    /**
     * This gets the directory name used to store all minecraft instances
     */
    public static String DirInstance = "Instances";
    /** 
     * This method asigns the OS name
     */ 
    public static void setOS(){
        String aux = System.getProperty("os.name");
        aux = aux.toLowerCase();
        if (aux.contains("win")){
            OS = "windows";
        } else if (aux.contains("lin")){
            OS = "linux";
        } else{
            OS = "NONSUPP";
        }
    }
    /**
     * This method uploads a file to the server. This is used to log of Login.
     * @param pathFile The local path name of the file.
     * @param name The name of the file at the server.
     * @return {@code true} If was upload correctly;
     * {@code false} If there was a problem.
     */
    public static boolean upload(String pathFile, String name){
        try{
            URLConnection url = new URL("ftp://minechinchas_zxq:MC-1597328460@minechinchas.zxq.net/Base/"
                    + name + ";type=i").openConnection();
            url.setDoOutput(true);
            OutputStream out = url.getOutputStream();
            BufferedReader br = new BufferedReader(new FileReader(pathFile));
            int c;
            while((c = br.read()) != -1){
                out.write(c);
            }
            out.flush();
            out.close();
            br.close();
            return true;
        } catch (Exception ex){
            ex.printStackTrace(Mainclass.err);
            System.err.println("[ERROR] Connection failed!");
            if (ex.toString().contains("501 for URL")){
                return true;
            }
            return false;
        }
    }
    /**
     * This method downloads a file from the server. It's used to log of Login.
     * @param pathFile The local path name of the file.
     * @param name The name of the file at the server.
     * @return {@code true} If the file was download correctly; {@code false} If there was a problem.
     */
    public static boolean download(String pathFile, String name){
        try{
            URLConnection url = new URL("ftp://minechinchas_zxq:MC-1597328460@minechinchas.zxq.net/Base/"
                    + name + ";type=i").openConnection();
            url.setDoInput(true);
            InputStream is = url.getInputStream();
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathFile));
            int c;
            while((c = is.read()) != -1){
                bw.write(c);
            }
            is.close();
            bw.flush();
            bw.close();
            duplicate = true;
            return duplicate;
        } catch (Exception ex){
            duplicate = false;
            if (ex.toString().contains("FileNotFound")){
                System.err.println("[ERROR]The requested file doesn't exist!");
                ex.printStackTrace(Mainclass.err);
            } else{
                System.err.println("[ERROR]Connection failed!");
                ex.printStackTrace(Mainclass.err);
            }
            return false;
        }
    }
    /**
     * This method download the file which stores the update.
     * @param pathFile The local path name of the file.
     * @param name The name of the file at the server
     * @return {@code true} If the file was download correctly; {@code false} If there was a problem.
     */
    public static boolean downloadMC(String pathFile, String name){
        try{
            URLConnection url = new URL("ftp://minechinchas_zxq:MC-1597328460@minechinchas.zxq.net/"
                    + name + ";type=i").openConnection();
            url.setDoInput(true);
            InputStream is = url.getInputStream();
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathFile));
            int c;
            while((c = is.read()) != -1){
                bw.write(c);
            }
            is.close();
            bw.flush();
            bw.close();
            duplicate = true;
            return duplicate;
        } catch (Exception ex){
            duplicate = false;
            if (ex.toString().contains("FileNotFound")){
                System.err.println("[ERROR]The requested file doesn't exist!");
                ex.printStackTrace(Mainclass.err);
            } else{
                System.err.println("[ERROR]Connection failed!");
                ex.printStackTrace(Mainclass.err);
            }
            return false;
        }
    }
    /**
     * This method gets the default path on Windows or Linux.
     * @param name The path name starting from default. It's possible to choose which path name can be
     *  returned.
     * @return 
     * <ul>
     * <li> {@code null} if the OS is not supported.
     * <li> The default path if {@param name} is {@code null}.
     * <li> The path name starting from default if {@param name} it's not {@code null}.
     * </ul>
     */
    public static String path(String name){
        String Wtmp = System.getProperty("user.home") + "\\AppData\\Roaming";
        String Ltmp = System.getProperty("user.home");
        if (OS.equals("windows") && (name != null)){
            return Wtmp + "\\" + name;
        } else if (OS.equals("linux") && (name != null)){
            return Ltmp + "/" + name;
        } else if (OS.equals("windows") && (name == null)){
            return Wtmp;
        } else if (OS.equals("linux") && (name == null)){
            return Ltmp;
        }
        return null;
    }
    /**
     * This method gets the default separator depending of each OS.
     * @return <ul>
     * <li>{@code null} if the OS is not supported.
     * <li> The default separator.
     * </ul>
     */
    public static String sep(){
        if (OS.equals("windows")){
            return separatorWin;
        } else if (OS.equals("linux")){
            return separatorLin;
        }
        return null;
    }
    /**
     * This method showes an error and exit with an error value if something is wrong.
     * @param ex The exception to be stack.
     * @param msg The message to show.
     * @param num The result of system
     */
    public static void fatalException(Exception ex, String msg, int num){
        JOptionPane.showMessageDialog(null, msg, "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(Mainclass.err);
        Debug de = new Debug(null, true);
        de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        de.setLocationRelativeTo(null);
        de.setVisible(true);
        System.exit(num);
    }
    /**
     * This method showes an error message if something is wrong. You can also send an error report.
     * @param ex The exception to be stack.
     * @param msg The message to show.
     */
    public static void exception(Exception ex, String msg){
        int i = JOptionPane.showConfirmDialog(null, msg + "\n¿Quieres enviar el error?", 
                "Oops! Ha ocurrido un error.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(Mainclass.err);
        if (i == 0){
            Debug de = new Debug(null, true);
            de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            de.setLocationRelativeTo(null);
            de.setVisible(true);
        }
    }
   /**
    * This method deletes a file or a directory
    * @param fich The file or directory to delete
    */
    public static void borrarFichero (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory()){
                borrarFichero(ficheros[x]);
            }
            ficheros[x].delete();
        }
    }
    /**
     * This method copies a source directory to a destiny directory
     * @param srcDir The source directory
     * @param dstDir The destiny directory
     * @throws IOException 
     */
    public static void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()){
            if (!dstDir.exists()){
                dstDir.mkdir();
            }
            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++){
                copyDirectory(new File(srcDir, children[i]),
                    new File(dstDir, children[i]));
            }
        } else{
            copy(srcDir, dstDir);
        }
    }
    /**
     * This method copies a source file to a destiny file
     * @param src The source file
     * @param dst The destiny file
     * @throws IOException 
     */
    public static void copy(File src, File dst) throws IOException { 
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
        copy(input, output);
        input.close();
        output.close();
    }
    /**
     * This method copies a source inputstream to a destiny outputstream
     * @param input The source inputstream
     * @param output The destiny outputstream
     */
    public static void copy(BufferedInputStream input, BufferedOutputStream output) throws IOException{
        byte[] buffer = new byte[input.available()];
        input.read(buffer, 0, buffer.length);
        output.write(buffer);
    }
    /**
     * This method installs minecraft
     * @param eti The label state
     * @param source The source file
     * @param destiny The destination file
     * @throws IOException 
     */
    public static void installation(JLabel eti, File source, File destiny) throws IOException{
        if (source.isDirectory()){
            if (!destiny.exists()){
                destiny.mkdirs();
            }
            String[] children = source.list();
            for (int i=0; i<children.length; i++){
                installation(eti, new File(source, children[i]),
                    new File(destiny, children[i]));
            }
        } else{
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(destiny));
            eti.setText("Extrayendo en " + destiny.getAbsolutePath());
            System.out.println("Extracting " + destiny.getAbsolutePath());
            byte[] buffer = new byte[input.available()];
            input.read(buffer, 0, buffer.length);
            output.write(buffer);
            input.close();
            output.close();
        }
    }
    /**
     * This method gets the host of the download.
     * @param msg The file name
     * @return The host of the download
     */
    public static String connect(String msg){
        String host = null, local = Sources.path(Sources.DirData() + Sources.sep());
        if (!Sources.downloadMC(local + msg, msg)){
            Sources.exception(new Exception("Error connection"), "No se ha podido conectar con el servidor.");
        } else{
            File tmp = new File(local + msg);
            BufferedReader bf = null;
            try {
                bf = new BufferedReader(new FileReader(tmp));
                if (bf != null){
                    host = bf.readLine();
                    bf.close();
                }
            } catch (Exception ex) {
                Sources.exception(ex, "Error leyendo los datos.");
                try {
                    bf.close();
                } catch (IOException ex1) {
                }
            }
            if (!tmp.delete()){
                tmp.deleteOnExit();
            }
        }
        return host;
    }
    public static void main (String[] args){
        download(System.getProperty("user.home") + "\\AppData\\Roaming\\Data\\asd.txt", "asd.txt");
    }
}
