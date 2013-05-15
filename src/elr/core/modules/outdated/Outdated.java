package elr.core.modules.outdated;

import elr.core.Stack;
import elr.core.modules.configuration.Configuration;
import elr.core.modules.Directory;
import java.io.File;

/**
 * Class used to support outdated versions.
 * @author Infernage
 */
public class Outdated {
    /**
     * Gets the old version by refractor (?).
     * @deprecated Not used anymore. Used instead, the configuration to check it.
     */
    public static String getOldVersion(){ return Stack.getProgramVersion(); }
    
    /**
     * Alternative method. Only initialized if the main fails.
     */
    private static void alternativeStart(){
        String data = Stack.config.getOS().equals("windows") ? "Data" : ".Data";
        if (new File(new File(Directory.root()).getParent(), data).exists()){
            Ver6.startModule(599);
        }
    }
    
    /**
     * Starts the specific engine of the outdated version found.
     * @param version The outdated version.
     */
    private static void startEngine(String version){
        if (version.equals(Stack.getProgramVersion().split(" ")[0])) return;
        int ver = Integer.parseInt(version.replace(".", ""));
        if (ver < 700){
            Ver6.startModule(ver);
        }
    }
    
    /**
     * Initializes all engines to check outdated versions.
     */
    public static void startEngines(){
        String oldVersion;
        System.out.println("Actual version: " + Stack.getProgramVersion());
        Stack.console.printConfigOption("Starting first engine method");
        try {
            oldVersion = Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_version.name());
            if (oldVersion == null){
                throw new Exception("Value not found");
            } else if (oldVersion.contains(" ")) oldVersion = oldVersion.split(" ")[0];
            startEngine(oldVersion);
        } catch (Exception ex) {
            Stack.console.setError(ex, 2, Outdated.class);
            Stack.console.printError("Failed to check old versions with first engine.", 0, ex.getClass());
            Stack.console.printConfigOption("Starting alternative engine.");
            alternativeStart();
        }
        Stack.config.setInternalProperty(Configuration.ConfigVar.elr_version.name(), Stack.getProgramVersion());
        Stack.console.printConfigOption("Finalized engines");
    }
}
