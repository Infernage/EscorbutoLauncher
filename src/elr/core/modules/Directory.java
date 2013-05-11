package elr.core.modules;

import elr.core.Stack;

/**
 * This class returns all directories used.
 * @author Infernage
 */
public class Directory{
    public static String MINECRAFT = "Minecraft";
    
    /**
     * This gets the directory name of data files stored in this PC.
     */
    public static String root(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_root.name());
    }
    
    /**
     * This gets the actual directory.
     */
    public static String currentPath(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_currentPath.name());
    }
    
    /**
     * This gets the instances path.
     */
    public static String instances(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_instances.name());
    }
    
    /**
     * This gets the selected instance.
     */
    public static String selectedInstance(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_selectedInstance.name());
    }
}