package elr.minecraft.modpacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class used to obtain all ModPacks from the server.
 * @author Infernage
 */
public class ModPackList {
    private Map<String, Map<String, List<ModPack>>> modpacks = new HashMap<>();
    private SortedMap<String, ModPack> latest;
    
    public ModPackList(Map modpack, SortedMap last){
        modpacks = modpack;
        latest = last;
    }
    
    /**
     * Obtains all latest ModPacks.
     */
    public void obtainLatest(){
        latest = new TreeMap<>();
        for (Map.Entry<String, Map<String, List<ModPack>>> entry : modpacks.entrySet()) {
            Map<String, List<ModPack>> map = entry.getValue();
            for (Map.Entry<String, List<ModPack>> en : map.entrySet()) {
                String name = en.getKey();
                List<ModPack> versions = en.getValue();
                for (ModPack modPack : versions) {
                    if (latest.containsKey(name)){
                        try {
                            if (Integer.parseInt(latest.get(name).getVersion()) < 
                                    Integer.parseInt(modPack.getVersion())){
                                latest.put(name, modPack);
                            }
                        } catch (Exception e) {
                            //Ignore, only don't add it.
                        }
                    } else latest.put(name, modPack);
                }
            }
        }
    }
    
    public Map<String, Map<String, List<ModPack>>> getAllModPacks(){ return modpacks; }
    
    public SortedMap<String, ModPack> getLatestModPacks(){ return latest; }
}
