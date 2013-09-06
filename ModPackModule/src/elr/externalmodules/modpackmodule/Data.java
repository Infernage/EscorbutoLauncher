package elr.externalmodules.modpackmodule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elr.modules.compressor.Compressor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Class used to store all values.
 * @author Infernage
 */
public class Data {
    private String elr_user = "", elr_password = "";
    private String dropbox_appKey = "";
    private String mega_user = "", mega_password = "";
    
    private Data(){}
    
    public static String getDropbox(){ return object.dropbox_appKey; }
    
    public static String getMEGA(String field){
        switch(field){
            case "user": return object.mega_user;
            case "password": return object.mega_password;
        }
        return "";
    }
    
    public static String getELR(String field){
        switch(field){
            case "user": return object.elr_user;
            case "password": return object.elr_password;
        }
        return "";
    }
    
    public static void setDropbox(String key){
        object.dropbox_appKey = key;
    }
    
    public static void setMEGA(String field, String data){
        switch(field){
            case "user": object.mega_user = data;
                break;
            case "password": object.mega_password = data;
                break;
        }
    }
    
    public static void setELR(String field, String data){
        switch(field){
            case "user": object.elr_user = data;
                break;
            case "password": object.elr_password = data;
                break;
        }
    }
    
    public static void save(){
        parser.toJson(object, pw);
    }
    
    //Internal functionality
    
    private static PrintWriter pw;
    private static BufferedReader bf;
    private static Gson parser;
    private static Data object;
    private static File src;
    
    static void init(File data) throws IOException{
        parser = new GsonBuilder().setPrettyPrinting().create();
        src = data;
        if (!data.exists()){
            data.createNewFile();
            pw = new PrintWriter(new FileOutputStream(data));
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
            object = new Data();
        } else{
            pw = new PrintWriter(new FileOutputStream(data));
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
            object = parser.fromJson(bf, Data.class);
        }
    }
    
    static void shutdown(){
        save();
        try {
            pw.close();
        } catch (Exception e) {
            //Ignore
        }
        try {
            bf.close();
        } catch (Exception e) {
            //Ignore
        }
        try {
            Compressor.codedCompression(src, Compressor.CompressionLevel.NOCOMPRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        src.delete();
        object = null;
    }
}
