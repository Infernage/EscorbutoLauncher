package elr.externalmodules;

import elr.core.Loader;
import elr.core.server.InternalServer;
import elr.core.util.Directory;
import elr.core.util.IO;
import elr.modules.compressor.Compressor;
import elr.modules.compressor.xz_coder.xz.UnsupportedOptionsException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.lingala.zip4j.exception.ZipException;

/**
 * Class used to allow external modules to be inyected into the Core classpath.
 * @author Infernage
 */
public class ModuleLoader extends URLClassLoader{
    public static enum STATE { LOADED, NOT_LOADED };
    private static Properties remote;
    private static List<CompleteModule> modules = new ArrayList<>();
    private static boolean ready = false;
    private static ModuleLoader loader = null;
    private static File root = null, tmp = null, config = null;
    
    /**
     * Closes the classloader.
     * Only allowed when JVM is halting.
     */
    public static void shutdown(){
        if (InternalServer.isHalting() && loader != null){
            try {
                loader.close();
                try {
                    Thread.sleep(400);
                } catch (Exception e) {
                    //Ignore
                }
                IO.deleteDirectory(tmp);
                tmp.delete();
                ready = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Obtains the remote module list.
     * @return A Properties object with {@code key = module name} and {@code value = module url}.
     */
    public static Properties getRemoteList(){
        if (ready) return remote;
        else return null;
    }
    
    /**
     * Obtains all installed modules.
     */
    public static List<CompleteModule> getModules(){ return modules; }
    
    /**
     * Obtains the module root folder, which by default is "./modules".
     */
    public static File getModuleRootFolder(){ return root; }
    
    /**
     * Obtains the module config folder, which by default is "./modules/config".
     */
    public static File getModuleConfigFolder(){ return config; }
    
    /**
     * Obtains the runtime folder, which by default is "./modules/runtime".
     */
    public static File getTmpFolder(){ return tmp; }
    
    /**
     * Checks if the class has finalized the load method.
     * @return {@code true} if has been initialized correctly, {@code false} if had any error.
     */
    public static boolean isReady(){ return ready; }
    
    /**
     * Removes a module from the classpath and from the load method.
     * @param module The module to remove.
     */
    public static void removeModule(String module){
        CompleteModule remove = null;
        for (CompleteModule mod : modules) {
            if (mod.getName().equals(module)) remove = mod;
        }
        if (remove != null){
            modules.remove(remove);
            remove.getSource().delete();
        }
    }
    
    /**
     * Loads a new module into the classpath.
     * @param module The file module in CXZ format.
     * @return {@code true} if the load was correct, {@code false} in otherwise.
     * @throws ZipException
     * @throws UnsupportedOptionsException
     * @throws IOException 
     */
    public static boolean loadModule(File module) throws ZipException, UnsupportedOptionsException,
            IOException{
        if (InternalServer.isHalting() || loader == null || !isReady())
            throw new RuntimeException("ModuleLoader can't do this action now");
        if (!module.isFile() || !module.getName().endsWith(".cxz"))
            throw new RuntimeException("Bad or corrupted module");
        File extract = Compressor.secureDecompression(module, null, true);
        if (extract.getName().endsWith(".jar")){
            File runtime = new File(tmp, extract.getName());
            extract.renameTo(runtime);
            loader.addURL(new URL("jar:file:" + runtime.getPath() + "!/"));
            String name = runtime.getName().replace(".jar", "").replace(" ", ".");
            Loader.getMainGui().getConsoleTab().println("Loading " + name);
            try {
                Class cl = loader.loadClass("elr.externalmodules." + name.toLowerCase() + "." + name);
                Constructor cons = cl.getConstructor();
                cons.setAccessible(true);
                ExternalModule ext = (ExternalModule) cons.newInstance();
                if (ext.isAutoInitialer()) ext.init(config);
                CompleteModule wrapper = new CompleteModule(ext, module);
                ModuleLoader.modules.add(wrapper);
                return true;
            } catch (Exception e) {
                CompleteModule failed = new CompleteModule("elr.externalmodules." + name.toLowerCase());
                ModuleLoader.modules.add(failed);
                Loader.getMainGui().getConsoleTab().println("Failed to load " + name);
                e.printStackTrace();
                return false;
            }
        } else{
            extract.delete();
            return false;
        }
    }
    
    /**
     * Load method which initializes all. Only can be called once.
     * @throws ZipException
     * @throws UnsupportedOptionsException
     * @throws IOException 
     */
    public static void load() throws ZipException, UnsupportedOptionsException, IOException{
        if (loader != null) return;
        Loader.getMainGui().getConsoleTab().println("Getting remote list");
        remote = new Properties();
        try {
            remote.loadFromXML(new URL("https://dl.dropbox.com/s/ms9cmr4c4ix6adr/Modules.xml")
                    .openStream());
        } catch (Exception e) {
            e.printStackTrace();
            remote.clear();
        }
        Loader.getMainGui().getConsoleTab().println("Searching for modules to load");
        root = new File(Directory.workingDir(), "modules");
        config = new File(root, "config");
        if (!root.exists()){
            root.mkdirs();
            Loader.getMainGui().getConsoleTab().println("No modules to load");
        } else{
            if (!config.exists()) config.mkdirs();
            List<File> candidateModules = getCandidates();
            tmp = new File(root, "runtime");
            if (!tmp.exists()) tmp.mkdirs();
            List<File> toLoadModules = getRawModules(candidateModules);
            List<String> inyected = getInyectedModules(toLoadModules);
            loadModules(inyected);
        }
        Loader.getMainGui().getConsoleTab().println("Modules availables: " + remote.size());
        ready = true;
    }
    
    private static void loadModules(List<String> inyected){
        for (String mod : inyected) {
            String inyect = mod.replace(".jar", "").replace(" ", ".");
            try {
                Loader.getMainGui().getConsoleTab().println("Loading " + inyect);
                Class module = loader.loadClass("elr.externalmodules." + inyect.toLowerCase() + "." + 
                        inyect);
                Constructor cons = module.getConstructor();
                cons.setAccessible(true);
                ExternalModule ext = (ExternalModule) cons.newInstance();
                if (ext.isAutoInitialer()) ext.init(config);
                CompleteModule wrapper = new CompleteModule(ext, new File(root, mod.replace(".jar", 
                        ".cxz")));
                ModuleLoader.modules.add(wrapper);
            } catch (Exception e) {
                CompleteModule failed = new CompleteModule("elr.externalmodules." + inyect.toLowerCase());
                ModuleLoader.modules.add(failed);
                Loader.getMainGui().getConsoleTab().println("Failed to load " + inyect);
                e.printStackTrace();
            }
        }
    }
    
    private static List<String> getInyectedModules(List<File> rawModules){
        List<String> result = new ArrayList<>();
        URL[] urls = new URL[rawModules.size()];
        int i = 0;
        for (File module : rawModules) {
            try {
                urls[i] = new URL("jar:file:" + module.getPath() + "!/");
                result.add(module.getName());
            } catch (Exception e) {
                Loader.getMainGui().getConsoleTab().println("Failed to load " + module.getName());
                e.printStackTrace();
            }
        }
        loader = new ModuleLoader(urls);
        return result;
    }
    
    private static List<File> getRawModules(List<File> candidates) throws ZipException, IOException{
        List<File> result = new ArrayList<>();
        for (File candidate : candidates) {
            File extracted = Compressor.secureDecompression(candidate, null, true);
            if (extracted.getName().endsWith(".jar")){
                File module = new File(tmp, extracted.getName());
                extracted.renameTo(module);
                result.add(module);
            } else extracted.delete();
        }
        return result;
    }
    
    private static List<File> getCandidates(){
        List<File> result = new ArrayList<>();
        for (File candidate : root.listFiles()) {
            if (candidate.isDirectory()) continue;
            if (candidate.isFile() && candidate.getName().endsWith(".cxz")){
                //Found candidate
                result.add(candidate);
            }
        }
        return result;
    }
    
    /**
     * Default constructor.
     */
    private ModuleLoader(URL[] urls){
        super(urls, Loader.class.getClassLoader());
    }
}
