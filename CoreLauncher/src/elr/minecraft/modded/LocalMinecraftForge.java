package elr.minecraft.modded;

import elr.minecraft.versions.Library;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Class used to abstract an installed Forge.
 * @author Infernage
 */
public class LocalMinecraftForge implements Serializable{
    private static final long serialVersionUID = 105974102;
    
    private File path;
    private String version, mc_version_for;
    private List<Library> libraries;
    
    public LocalMinecraftForge(File p, String v, String mc, List<Library> library){
        path = p;
        version = v;
        mc_version_for = mc;
        libraries = library;
    }
    
    public File getPath(){ return path; }
    public String getForgeVersion(){ return version; }
    public String getMCVersion(){ return mc_version_for; }
    public List<Library> getLibraries(){
        if (libraries == null) return null;
        return Collections.unmodifiableList(libraries);
    }
}
