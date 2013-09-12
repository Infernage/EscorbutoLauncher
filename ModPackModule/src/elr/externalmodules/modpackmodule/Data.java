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
import java.nio.channels.FileChannel;

/**
 * Class used to store all values.
 * @author Infernage
 */
public class Data {
    private String elr_user = "", elr_password = "";
    private String dropbox_appKey = "";
    private String mega_user = "", mega_password = "";
    
    private Data(){}
    
    private static String getCrypted(String data){
        if (data == null || data.equals("")) return "";
        else return Compressor.dataDecryptation(data, null);
    }
    
    public static String getDropbox(){ return getCrypted(object.dropbox_appKey); }
    
    public static String getMEGA(String field){
        switch(field){
            case "user": return getCrypted(object.mega_user);
            case "password": return getCrypted(object.mega_password);
        }
        return "";
    }
    
    public static String getELR(String field){
        switch(field){
            case "user": return getCrypted(object.elr_user);
            case "password": return getCrypted(object.elr_password);
        }
        return "";
    }
    
    public static void setDropbox(String key){
        object.dropbox_appKey = Compressor.dataEncryptation(key, null);
    }
    
    public static void setMEGA(String field, String data){
        switch(field){
            case "user": object.mega_user = Compressor.dataEncryptation(data, null);
                break;
            case "password": object.mega_password = Compressor.dataEncryptation(data, null);
                break;
        }
    }
    
    public static void setELR(String field, String data){
        switch(field){
            case "user": object.elr_user = Compressor.dataEncryptation(data, null);
                break;
            case "password": object.elr_password = Compressor.dataEncryptation(data, null);
                break;
        }
    }
    
    public static void save(){
        try {
            channel.position(0);
            parser.toJson(object, pw);
            pw.flush();
        } catch (Exception e) {
            //Ignore
        }
    }
    
    //Internal functionality
    
    private static PrintWriter pw;
    private static BufferedReader bf;
    private static Gson parser;
    private static Data object;
    private static FileChannel channel;
    
    static void init(File data) throws IOException{
        parser = new GsonBuilder().setPrettyPrinting().create();
        if (!data.exists()) data.createNewFile();
        bf = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
        if (data.length() == 0){
            object = new Data();
        } else{
            object = parser.fromJson(bf, Data.class);
        }
        FileOutputStream output = new FileOutputStream(data);
        channel = output.getChannel();
        pw = new PrintWriter(output);
    }
    
    static void shutdown(){
        save();
        try {
            channel.close();
        } catch (Exception e) {
            //Ignore
        }
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
        object = null;
    }
}
