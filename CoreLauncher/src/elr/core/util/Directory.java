package elr.core.util;

import elr.core.Configuration;
import elr.core.Loader;
import java.io.File;

/**
 * This class returns all directories used.
 * @author Infernage
 */
public class Directory{
    /**
     * This gets the directory name of data files stored in this PC.
     */
    public static String workingDir(){
        return Loader.getConfiguration().getMapValue(Configuration.Keys.workingDir);
    }
    
    /**
     * This gets the directory name of Minecraft resources.
     */
    public static String minecraftResources(){
        return workingDir() + File.separator + Util.MINECRAFT + File.separator + "assets";
    }
    
    /**
     * This gets the actual directory.
     */
    public static String currentPath(){
        return Loader.getConfiguration().getMapValue(Configuration.Keys.mainELRDir);
    }
}