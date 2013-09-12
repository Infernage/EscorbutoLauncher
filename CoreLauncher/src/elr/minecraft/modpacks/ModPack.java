package elr.minecraft.modpacks;

import java.util.Map;

/**
 *
 * @author Infernage
 */
public class ModPack {
    private String author;
    private String name;
    private String information;
    private Map<String, String> modPaths;
    private String version;
    private String mc_version;
    private String url;
    
    public String getAuthor(){ return author; }
    public String getName(){ return name; }
    public String getInformation(){ return information; }
    public String getVersion(){ return version; }
    public String getMinecraftVersion(){ return mc_version; }
    public String getURL(){ return url; }
    public Map<String, String> getModPaths(){ return modPaths; }
}
