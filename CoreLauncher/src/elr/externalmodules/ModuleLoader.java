/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.externalmodules;

import elr.core.Loader;
import elr.core.util.Directory;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class used to allow external modules to be inyected into the Core classpath.
 * @author Infernage
 */
public class ModuleLoader {
    private static Properties remote;
    private static List<ExternalModule> loaded = new ArrayList<>();
    private static boolean ready = false;
    private URLClassLoader loader = (URLClassLoader) getClass().getClassLoader();
    
    public static Properties getRemoteList(){
        if (ready) return remote;
        else return null;
    }
    
    @SuppressWarnings("Not finished")
    public static void load(){
        Loader.getMainGui().getConsoleTab().println("Getting remote list");
        try {
            remote.loadFromXML(new URL("https://dl.dropbox.com/s/ms9cmr4c4ix6adr/Modules.xml")
                    .openStream());
        } catch (Exception e) {
            remote = new Properties();
        }
        Loader.getMainGui().getConsoleTab().println("Searching for modules to load");
        ModuleLoader load = new ModuleLoader();
        File modules = new File(Directory.workingDir(), "modules");
        if (!modules.exists()){
            modules.mkdirs();
            Loader.getMainGui().getConsoleTab().println("No modules to load");  
        }
        
        ready = true;
    }
}
