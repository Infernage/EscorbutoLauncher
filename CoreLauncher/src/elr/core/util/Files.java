package elr.core.util;

import elr.core.Configuration;
import elr.core.Loader;

/**
 * This class return all files used.
 * @author Infernage
 */
public class Files{
    /**
     * This gets the log.
     */
    public static String logPath(){
        return Loader.getConfiguration().getMapValue(Configuration.Keys.log);
    }
    
    /**
     * This gets the jar.
     * Not used yet.
     */
    public static String launcher(){
        return Loader.getConfiguration().getMapValue(Configuration.Keys.mainELRPath);
    }
}