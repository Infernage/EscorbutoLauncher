package elr.minecraft.versions;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Infernage
 */
public class ExtractRules {
    private List<String> excludes = new ArrayList<>();
    
    public List<String> getRules(){ return excludes; }
    
    public boolean shouldExtract(String path){
        if (excludes != null)
            for (String exclude : excludes) {
                if (path.startsWith(exclude)) return false;
            }
        return true;
    }
}
