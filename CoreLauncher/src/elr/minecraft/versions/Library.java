package elr.minecraft.versions;

import elr.core.Loader;
import elr.core.util.Util;
import elr.core.util.Util.OS;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Infernage
 */
public class Library {
    protected String name;
    private List<Rule> rules;
    private Map<OS, String> natives;
    private ExtractRules extract;
    protected String url;
    
    public String getName(){ return name; }
    public List<Rule> getRules(){ return rules; }
    public Map<OS, String> getNatives(){ return natives; }
    public ExtractRules getExtractRules(){ return extract; }
    public String getURL(){
        if (natives != null){
            String nativ = natives.get(Loader.getConfiguration().getOS());
            if (nativ != null){
                return Util.MINECRAFT_LIBRARIES + getPath(nativ);
            }
            return null;
        } else{
            return Util.MINECRAFT_LIBRARIES + getPath();
        }
    }
    public boolean hasOwnURL(){ return url != null; }
    
    public boolean appliesToCurrentEnvironment(){
        if (rules == null) return true;
        Rule.Action lastAction = Rule.Action.disallow;
        for (Rule rule : rules) {
            Rule.Action action = rule.getAction();
            if (action != null) lastAction = action;
        }
        return lastAction == Rule.Action.allow;
    }
    
    private String getBaseDir(){
        String[] names = name.split(":", 3);
        return String.format("%s/%s/%s", new Object[] { names[0].replaceAll("\\.", "/"), names[1], names[2] });
    }
    
    public String getPath(){ return String.format("%s/%s", new Object[] { getBaseDir(), getFilename() }); }
    
    public String getPath(String classifier){
        return String.format("%s/%s", new Object[] { getBaseDir(), getFilename(classifier) });
    }
    
    private String getFilename(){
        String[] names = name.split(":", 3);
        return String.format("%s-%s.jar", new Object[] { names[1], names[2] });
    }
    
    private String getFilename(String classifier){
        String[] names = name.split(":", 3);
        return String.format("%s-%s-%s.jar", new Object[] { names[1], names[2], classifier });
    }
}
