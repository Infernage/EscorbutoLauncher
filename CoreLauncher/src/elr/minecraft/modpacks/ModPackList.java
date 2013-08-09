package elr.minecraft.modpacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Infernage
 */
public class ModPackList {
    private Map<String, List<ModPack>> modpacks = new HashMap<>();
    private Map<String, ModPack> latest = new HashMap<>();
    
    public ModPackList(Map modpack, Map last){
        modpacks = modpack;
        latest = last;
    }
    
    public Map<String, List<ModPack>> getAllModPacks(){ return modpacks; }
    
    public Map<String, ModPack> getLatestModPacks(){ return latest; }
}
