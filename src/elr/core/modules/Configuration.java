package elr.core.modules;

import elr.core.Booter;
import elr.core.Stack;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Class used to store the configuration in the system.
 * @author Infernage
 */
public class Configuration implements Serializable{
    
    private HashMap<String, String> launcherConfig, internalConfig;
    private String OS;
    private static final long serialVersionUID = 100113004;
    
    /**
     * Starts loading the Launcher configuration.
     */
    public static void createNewConfig(String os, String current, File root){
        if (Stack.config != null) throw new RuntimeException("Configuration already created");
        Stack.config = new Configuration(root, current, os);
    }
    
    /**
     * Private constructor used when is the first time which is executed.
     * @param root The Directory where will be installed all configuration.
     * @param current The actual path.
     * @param OS The Operative System.
     */
    private Configuration(File root, String current, String OS){
        internalConfig = new HashMap<>();
        internalConfig.put(ConfigVar.elr_selectedInstance.name(), null);
        internalConfig.put(ConfigVar.elr_version.name(), null);
        internalConfig.put(ConfigVar.elr_root.name(), root.getAbsolutePath());
        internalConfig.put(ConfigVar.elr_currentPath.name(), new File(current).getParent());
        internalConfig.put(ConfigVar.elr_currentJar.name(), current);
        File quiz = new File(root, "Quiz.qzx");
        if (!quiz.exists()){
            try {
                quiz.createNewFile();
            } catch (Exception e) {
                ExceptionControl.showExceptionWOStream(e, e.toString(), 0);
            }
        }
        internalConfig.put(ConfigVar.elr_quiz.name(), quiz.getAbsolutePath());
        internalConfig.put(ConfigVar.elr_chlog.name(), root.getAbsolutePath() + 
                File.separator + "CHLog.txt");
        File instances = new File(internalConfig.get(ConfigVar.elr_currentPath.name()), "Instances");
        if (!instances.exists()){
            instances.mkdirs();
        }
        internalConfig.put(ConfigVar.elr_instances.name(), instances.getAbsolutePath());
        internalConfig.put(ConfigVar.elr_logPath.name(), current.substring(0, current.lastIndexOf(File.separator))
                + File.separatorChar + "ELR_log.nfo");
        launcherConfig = initDefaultProperties();
        this.OS = OS;
    }
    
    /**
     * Initializes the default options.
     * @return A HashMap with the default options.
     */
    private HashMap<String, String> initDefaultProperties(){
        HashMap<String, String> launcherConfig = new HashMap<>();
        launcherConfig.put("maxRAM", "1");
        launcherConfig.put("defaultInst", "true");
        launcherConfig.put("defaultLog", "true");
        launcherConfig.put("rememberUser", "false");
        launcherConfig.put("rememberPassword", "false");
        return launcherConfig;
    }
    
    /**
     * Gets the OS.
     * @return The OS.
     */
    public String getOS(){ return OS; }
    
    /**
     * Gets a value from options.
     * @param key The key in the HashMap. It can be:
     * <ul><li> maxRAM: The maximum ram used in minecraft.
     * <li> defaultInst: Boolean to choose default install path (default = userPath/Instances).
     * <li> defaultLog: Boolean to choose default log path (default = userPath/ELR_log.nfo).
     * <li> rememberUser: Boolean to choose if needed to remember the username.
     * <li> rememberPassword: Boolean to choose if needed to remember the password.</ul>
     * @return The value stored.
     */
    public String getValueConfig(String key){
        return launcherConfig.get(key);
    }
    
    /**
     * Changes a property in the HashMap.
     * @param key The key located in the HashMap. It can be:
     * <ul><li> maxRAM: The maximum ram used in minecraft.
     * <li> defaultInst: Boolean to choose default install path (default = userPath/Instances).
     * <li> defaultLog: Boolean to choose default log path (default = userPath/ELR_log.nfo).
     * <li> rememberUser: Boolean to choose if needed to remember the username.
     * <li> rememberPassword: Boolean to choose if needed to remember the password.</ul>
     * @param value The value to replace. It can't be {@code null}.
     */
    public void setProperty(String key, String value){
        if (launcherConfig.containsKey(key)){
            launcherConfig.put(key, value);
        }
    }
    
    /**
     * Changes a property in the Internal HashMap.
     * @param key The key located in the Internal HashMap. It can be:
     * <ul><li> elr_root: Main config directory.
     * <li> elr_version: ELR version loaded from config file.
     * <li> elr_currentPath: Actual path of ELR.
     * <li> elr_instances: Instances path.
     * <li> elr_selectedInstance: Name of the selected instance. {@code null} until is selected.
     * <li> elr_currentJar: Path of the executed Jar file.
     * <li> elr_logPath: Ubication of log file.
     * <li> elr_storage: Userdata file path.
     * <li> elr_quiz: Quiz file path. {@code null} for now. Not used.
     * <li> elr_chlog: Changelog ubication.</ul>
     * @param value The value to replace. It can't be {@code null}.
     */
    public void setInternalProperty(String key, String value){
        if (internalConfig.containsKey(key)){
            internalConfig.put(key, value);
        }
    }
    
    /**
     * Method allowed only from Booter class. Resets a key setting to null.
     */
    public void resetInternalProperty(Object obj, String key){
        if (internalConfig.containsKey(key) && obj instanceof Booter) internalConfig.put(key, null);
    }
    
    /**
     * Gets a value from Internal Config.
     * @param key The key from map. It can be:
     * <ul><li> elr_root: Main config directory.
     * <li> elr_version: ELR version loaded from config file.
     * <li> elr_currentPath: Actual path of ELR.
     * <li> elr_instances: Instances path.
     * <li> elr_selectedInstance: Name of the selected instance. {@code null} until is selected.
     * <li> elr_currentJar: Path of the executed Jar file.
     * <li> elr_logPath: Ubication of log file.
     * <li> elr_storage: Userdata file path.
     * <li> elr_quiz: Quiz file path. {@code null} for now. Not used.
     * <li> elr_chlog: Changelog ubication.</ul>
     * @return The value assigned to the key.
     */
    public String getValueInternalConfig(String key){
        return internalConfig.get(key);
    }
    
    /**
     * Keys of Launcher Configuration.
     */
    public static enum LauncherConfigVar {
        maxRAM, defaultInst, defaultLog, rememberUser, rememberPassword
    }
    /**
     * Keys of Internal Configuration.
     */
    public static enum ConfigVar {
        elr_root, elr_version, elr_currentPath, elr_instances, elr_selectedInstance, 
        elr_currentJar, elr_logPath, elr_quiz, 
        elr_chlog
    }
}