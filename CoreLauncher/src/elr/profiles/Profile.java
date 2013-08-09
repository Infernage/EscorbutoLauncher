package elr.profiles;

import elr.core.Configuration;
import elr.core.Loader;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Infernage
 */
public class Profile implements Serializable{
    public static final String[] actions = { "Close launcher", "Reopen launcher", "Keep launcher open" };
    private static final long serialVersionUID = 100264319;
    
    private File instancesPath;
    private List<Instances> instances;
    private Instances selected;
    private String maxRam;
    private String userName;
    private String profileName;
    private String action;
    private String accessToken;
    private String sessionid;
    
    public Profile(String user, File path){
        userName = user;
        profileName = user;
        instancesPath = (path == null) ? new File(Loader.getConfiguration().getMapValue(Configuration.Keys
                .mainELRPath)) : path;
        instances = new ArrayList<>();
        selected = null;
        maxRam = "1 Gbyte";
        accessToken = "NULL";
        sessionid = null;
        action = actions[0];
    }
    
    public Profile(String user, String session, String access, File path){
        this(user, path);
        sessionid = session;
        accessToken = access;
    }
    
    public void changeDir(File newPath){
        instancesPath = newPath;
    }
    
    public void addInstance(Instances instance){
        instances.add(instance);
    }
    
    public void removeInstance(Instances instance){
        instances.remove(instance);
    }
    
    public void setSelected(Instances instance){
        selected = instance;
    }
    
    public void changeName(String newUser){
        if (!isPremium()){
            profileName = newUser;
            userName = newUser;
        } else{
            profileName = newUser;
        }
    }
    
    public void setMaxRam(String ram){
        maxRam = ram;
    }
    
    public void setAction(int index){
        action = actions[index];
    }
    
    public void setAccessToken(String token){
        if (token == null) token = "NULL";
        accessToken = token;
    }
    
    public File getPath(){ return instancesPath; }
    
    public List<Instances> getList(){ return Collections.unmodifiableList(instances); }
    
    public Instances getInstance(Instances instance){
        Instances element = null;
        for (Instances each : instances) {
            if (each.equals(instance)){
                element = new Instances(each);
                break;
            }
        }
        return element;
    }
    
    public Instances getSelected(){ return selected; }
    
    public String getUsername(){ return userName; }
    
    public String getMaxRam(){ return maxRam; }
    
    public String getProfilename(){ return profileName; }
    
    public String getAction(){ return action; }
    
    public String getSessionID(){ return sessionid; }
    
    public String getAccessToken(){ return accessToken; }
    
    public boolean isPremium(){
        return !(accessToken.equalsIgnoreCase("NULL") || sessionid == null);
    }
    
    public String getSessionToken(){
        if (accessToken.equalsIgnoreCase("NULL")) return null;
        try {
            Integer tmp = Integer.parseInt(accessToken);
            return tmp.toString();
        } catch (Exception e) {
            return "token:" + accessToken + ":" + sessionid;
        }
    }
}
