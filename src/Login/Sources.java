/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 * @author Reed
 */
public class Sources {
    private static String separatorWin = "\\", separatorLin = "/";
    public static boolean duplicate = false;
    public static String OS;
    /**
     * This gets the password
     */
    public static String pss = "My Pass Phrase";
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
     * This gets the configuration file name of the server
     */
    public static String lsNM = "ALLNM-MC.cfg";
    /**
     * This gets the file name which check if it's the first time that is oppened
     */
    public static String bool = "boolean.txt";
    /**
     * This gets this jar name
     */
    public static String jar = "RUN.jar";
    /**
     * This gets the file name which check if the remember option was used
     */
    public static String rmb = "RMB.txt";
    /**
     * This gets the name of directory and file of installation
     */
    public static String Dirsrc = "inst";
    /**
     * This gets the directory name of libraries used by this jar
     */
    public static String Dirlibs = "lib";
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
     * This method upload a file to the server. This is used to log of Login.
     * @param pathFile The local path name of the file.
     * @param name The name of the file at the server.
     * @return {@code true} If was upload correctly;
     * {@code false} If there was a problem.
     */
    public static boolean upload(String pathFile, String name){
        try{
            OutputStream out = new URL("ftp://minechinchas_zxq:MC-1597328460@minechinchas.zxq.net/Base/"
                    + name + ";type=i").openConnection().getOutputStream();
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
            return false;
        }
    }
    /**
     * This method download a file from the server. It's used to log of Login.
     * @param pathFile The local path name of the file.
     * @param name The name of the file at the server.
     * @return {@code true} If the file was download correctly; {@code false} If there was a problem.
     */
    public static boolean download(String pathFile, String name){
        try{
            InputStream is = new URL("ftp://minechinchas_zxq:MC-1597328460@minechinchas.zxq.net/Base/"
                    + name + ";type=i").openConnection().getInputStream();
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
        System.err.println(name);
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
}
