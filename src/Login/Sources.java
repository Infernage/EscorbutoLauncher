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
    public static String pss = "My Pass Phrase";
    public static String infernage = "Infernage.hdn";
    public static String lsNM = "ALLNM-MC.cfg";
    public static String bool = "boolean.txt";
    public static String jar = "RUN.jar";
    public static String Dirlibs = "lib";
    public static String DirData = "Data";
    public static String DirMC = ".minecraft";
    public static String Dirfiles = "Logger";
    public static String DirNM = "Base";
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
    public static void download(String pathFile, String name){
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
        } catch (Exception ex){
            System.err.println(ex.toString());
            System.err.println("[ERROR] Connection failed!");
        }
    }
    public static String path(String name){
        System.err.println(name);
        String Wtmp = System.getProperty("user.home") + "\\AppData\\Roaming";
        String Ltmp = System.getProperty("user.home");
        if (Mainclass.OS.equals("windows") && (name != null)){
            return Wtmp + "\\" + name;
        } else if (Mainclass.OS.equals("linux") && (name != null)){
            return Ltmp + "/" + name;
        } else if (Mainclass.OS.equals("windows") && (name == null)){
            return Wtmp;
        } else if (Mainclass.OS.equals("linux") && (name == null)){
            return Ltmp;
        }
        return null;
    }
    public static String sep(){
        if (Mainclass.OS.equals("windows")){
            return separatorWin;
        } else if (Mainclass.OS.equals("linux")){
            return separatorLin;
        }
        return null;
    }
}
