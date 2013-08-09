package elr.minecraft.versions;

import elr.core.Loader;
import elr.modules.threadsystem.DownloadJob;
import elr.modules.threadsystem.Downloader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Infernage
 */
public class CompleteVersion extends Version{
    private String minecraftArguments;
    private List<Library> libraries;
    private String mainClass;
    private int minimumLauncherVersion;
    private String incompatibilityReason;
    private List<Rule> rules;
    
    public List<Library> getLibraries(){ return libraries; }
    public List<Rule> getRules(){ return rules; }
    public String getMinecraftArguments(){ return minecraftArguments; }
    public String getMainClass(){ return mainClass; }
    public String getIncompatibilityReason(){ return incompatibilityReason; }
    
    public Collection<Library> getRelevantLibraries(){
        List relevant = new ArrayList();
        for (Library library : libraries) {
            if (library.appliesToCurrentEnvironment()) relevant.add(library);
        }
        return relevant;
    }
    
    public Collection<File> getClassPath(File base){
        Collection<Library> libraries = getRelevantLibraries();
        List classPaths = new ArrayList();
        for (Library library : libraries) {
            if (library.getNatives() == null) classPaths.add(new File(base, "libraries" + File.separator + 
                    library.getPath()));
        }
        classPaths.add(new File(base, "versions" + File.separator + id + File.separator + id + ".jar"));
        return classPaths;
    }
    
    public Set<String> getRequiredFiles(){
        Set files = new HashSet();
        for (Library library : getRelevantLibraries()) {
            if (library.getNatives() != null){
                String natives = library.getNatives().get(Loader.getConfiguration().getOS());
                if (natives != null) files.add("libraries" + File.separator + library.getPath(natives));
            } else files.add("libraries" + File.separator + library.getPath());
        }
        return files;
    }
    
    public Set<Downloader> getRequiredDownloads(File target, DownloadJob job){
        Set downloads = new HashSet();
        for (Library library : getRelevantLibraries()) {
            String file = null;
            if (library.getNatives() != null){
                String natives = library.getNatives().get(Loader.getConfiguration().getOS());
                if (natives != null) file = library.getPath(natives);
            } else{
                file = library.getPath();
            }
            if (file != null){
                File local = new File(target, "libraries" + File.separator + file);
                try {
                    if (!local.isFile() || !library.hasOwnURL()) downloads.add(
                            new Downloader(new URL(library.getURL()), job, local, true, false));
                } catch (Exception e) {
                }
            }
        }
        return downloads;
    }
    
    public boolean appliesToCurrentEnvironment(){
        if (rules == null) return true;
        Rule.Action lastAction = Rule.Action.disallow;
        for (Rule rule : rules) {
            Rule.Action action = rule.getAction();
            if (action != null) lastAction = action;
        }
        return lastAction == Rule.Action.allow;
    }
}
