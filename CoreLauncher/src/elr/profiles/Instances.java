package elr.profiles;

import elr.minecraft.modded.LocalMinecraftForge;
import elr.minecraft.versions.CompleteVersion;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to abstract a Minecraft instance.
 * @author Infernage
 */
public class Instances implements Serializable{
    private static final long serialVersionUID = 100236791;
    public final static short JAR = 1, CORE = 2, MOD = 3;
    
    private String name, info;
    private File path;
    private Map<String, File> modList, coremodList, jarmodList;
    private LocalMinecraftForge forge;
    private CompleteVersion mineVersion;
    
    /**
     * Default constructor.
     * @param name The instance name.
     * @param version The Minecraft version.
     * @param path The instance path.
     */
    public Instances(String name, File path, CompleteVersion version){
        this.name = name;
        mineVersion = version;
        this.path = path;
        info = null;
        modList = new HashMap<>();
        coremodList = new HashMap<>();
        jarmodList = new HashMap<>();
        forge = null;
    }
    
    /**
     * Constructor used to copy an instance.
     * @param copy The instance to copy.
     */
    public Instances(Instances copy){
        name = copy.getName();
        mineVersion = copy.getVersion();
        path = copy.getPath();
        info = null;
        modList = copy.modList;
        coremodList = copy.coremodList;
        jarmodList = copy.jarmodList;
    }
    
    /**
     * Changes the instance path.
     * @param newPath The new path.
     */
    public void changePath(File newPath){
        path = newPath;
    }
    
    /**
     * Renames the instance.
     * @param newName The new name.
     */
    public void rename(String newName){
        name = newName;
    }
    
    /**
     * Assigns an info to the instance.
     * @param sequence The information.
     */
    public void setInfo(String sequence){
        info = sequence;
    }
    
    /**
     * Adds a mod to the instance.
     * @param title The mod name.
     * @param path The path.
     * @param type The type of the mod.
     */
    public void addMod(String title, File path, short type){
        switch(type){
            case JAR: jarmodList.put(name, path);
                break;
            case MOD: modList.put(name, path);
                break;
            case CORE: coremodList.put(name, path);
                break;
        }
    }
    
    /**
     * Removes a mod from the instance.
     * @param title The mod name.
     * @param type The type of the mod.
     */
    public void removeMod(String title, short type){
        switch(type){
            case CORE: coremodList.remove(title);
                break;
            case JAR: jarmodList.remove(title);
                break;
            case MOD: modList.remove(title);
                break;
        }
    }
    
    /**
     * Adds a MC Forge to the instance.
     * @param forg The MC Forge to add.
     */
    public void addForge(LocalMinecraftForge forg){
        forge = forg;
    }
    
    /**
     * Removes the MC Forge installed. Is removed only in the object.
     */
    public void removeForge(){
        forge = null;
    }
    
    public boolean isLower1_6(){
        try {
            String[] split = mineVersion.getId().split("\\.");
            int v = Integer.parseInt(split[1]);
            if (split[0].equals("1") && v < 6) return true;
            return false;
        } catch (Exception e) {
            //Ignore
        }
        return false;
    }
    
    public File getPath(){ return path; }
    public String getName(){ return name; }
    public CompleteVersion getVersion(){ return mineVersion; }
    public String getInfo(){ return info; }
    public Map<String, File> getModMap(){ return Collections.unmodifiableMap(modList); }
    public Map<String, File> getJarModMap(){ return Collections.unmodifiableMap(jarmodList); }
    public Map<String, File> getCoreModMap(){ return Collections.unmodifiableMap(coremodList); }
    public LocalMinecraftForge getForge(){ return forge; }
    
    @Override
    public String toString(){
        return "Minecraft instance " + name + " V" + mineVersion.getId() + " with path: " + path.getAbsolutePath();
    }
}
