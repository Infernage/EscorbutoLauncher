package elr.minecraft.versions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Infernage
 */
public class VersionList {
    private List<Version> versions = new ArrayList<>();
    private Map<String, String> latest = new HashMap<>();
    
    public List<Version> getVersionList(){ return versions; }
    
    public Map<String, String> getLatestVersion(){ return latest; }
}
