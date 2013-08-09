package elr.core;

import elr.core.util.IO;
import elr.core.util.Util.OS;
import elr.profiles.Profile;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class used to store the configuration in the system.
 * @author Infernage
 */
public class Configuration implements Serializable{
    private static final long serialVersionUID = 100113004;
    
    private Map<Keys, String> data;
    private List<Profile> profiles;
    private OS os;
    private UUID uuid = UUID.randomUUID();
    
    /**
     * Loads the Configuration class.
     * @param currentJar The actual path of MainELR jar.
     * @param currentDir The actual directory of MainELR jar.
     * @param os The operative system.
     * @param workDir The working directory path.
     * @param input The ObjecInputStream where will be read for.
     * @throws Exception 
     */
    static void startConfig(String currentJar, String currentDir, String os, File workDir, 
            ObjectInputStream input) throws Exception{
        if (Loader.config != null) throw new RuntimeException("Configuration already created");
        Configuration tmp = (Configuration) (input == null ? new Configuration() : input.readObject());
        tmp.os = OS.valueOf(os);
        tmp.data.put(Keys.mainELRPath, currentJar);
        tmp.data.put(Keys.mainELRDir, currentDir);
        tmp.data.put(Keys.workingDir, workDir.getAbsolutePath());
        tmp.data.put(Keys.log, currentDir + File.separator + "ELR.log");
        Loader.config = tmp;
    }
    
    public static enum Keys{
        workingDir, mainELRPath, mainELRDir, log, maxRam
    }
    
    /**
     * Private constructor used when is the first time which is executed.
     */
    private Configuration(){
        data = new HashMap<>();
        profiles = new ArrayList<>();
        data.put(Keys.maxRam, "1G");
    }
    
    /**
     * Gets the OS.
     */
    public OS getOS(){ return os; }
    
    /**
     * Obtains the UUID.
     */
    public UUID getUUID(){ return uuid; }
    
    /**
     * Gets a value from the map.
     * @param key The key in the map.
     * @return The value mapped.
     */
    public String getMapValue(Keys key){
        return data.get(key);
    }
    
    /**
     * Change a value from the map.
     * @param key The key of the value. It has to be in the map.
     * @param value The new value.
     */
    public void changeMapValue(Keys key, String value){
        if (data.containsKey(key)) data.put(key, value);
    }
    
    /**
     * Adds a Profile to the list.
     * @param profile The Profile to add.
     */
    public void addProfile(Profile profile){
        if (!profiles.contains(profile) && !contains(profile)) profiles.add(profile);
    }
    
    /**
     * Removes a Profile from the list. <br>All connected with the Profile will be removed!</br>
     * @param profile The Profile to remove.
     */
    public void removeProfile(Profile profile){
        IO.deleteDirectory(profile.getPath());
        profile.getPath().delete();
        profiles.remove(profile);
    }
    
    /**
     * Gets an unmodifiable list of all profiles created.
     */
    public List<Profile> getProfiles(){ return Collections.unmodifiableList(profiles); }
    
    private boolean contains(Profile profile){
        for (Profile baseProfile : profiles) {
            if (baseProfile.getUsername().equals(profile.getUsername()) ||
                    baseProfile.getProfilename().equals(profile.getProfilename())) return true;
        }
        return false;
    }
}