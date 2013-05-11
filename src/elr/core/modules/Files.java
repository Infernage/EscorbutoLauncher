package elr.core.modules;

import elr.core.Stack;

/**
 * This class return all files used.
 * @author Infernage
 */
public class Files{
    /**
     * This gets the log.
     */
    public static String logPath(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_logPath.name());
    }
    
    /**
     * This gets the quiz File.
     */
    public static String quizF(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_quiz.name());
    }
    
    /**
     * This gets the changelog File.
     */
    public static String chlog(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_chlog.name());
    }
    
    /**
     * This gets the jar.
     */
    public static String launcher(){
        return Stack.config.getValueInternalConfig(
            Configuration.ConfigVar.elr_currentJar.name());
    }
    
    /**
     * This gets the configuration file name of the instance.
     */
    public static String instanceConfig = "Instance.config";
}