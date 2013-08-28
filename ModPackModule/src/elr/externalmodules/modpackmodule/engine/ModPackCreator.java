package elr.externalmodules.modpackmodule.engine;

import java.util.Map;

/**
 * This class is uploaded in Json to the server.
 * @author Infernage
 */
public class ModPackCreator {
    private String author;
    private String name;
    private String information;
    private Map<String, String> modPaths;
    private String version;
    private String mc_version;
    private String url;
    
    public ModPackCreator(String auth, String name, String info, String ver, String mc, Map<String,
            String> paths){
        author = auth;
        this.name = name;
        information = info;
        version = ver;
        mc_version = mc;
        modPaths = paths;
        url = null;
    }
    
    public boolean readyToUpload(){ return url != null; }
    
    public void setUrl(String url){
        this.url = url;
    }
}
