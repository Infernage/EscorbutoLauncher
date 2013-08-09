package elr.minecraft.versions;

import elr.core.Loader;
import elr.core.util.Util.OS;

/**
 * Rule to apply to an OS.
 * @author Infernage
 */
public class Rule {
    public static enum Action { allow, disallow }
    
    private Action action = Action.allow;
    private OSAction os;
    
    public Action getAction(){
        if (os != null && os.name != Loader.getConfiguration().getOS()) return null;
        return action;
    }
    
    private class OSAction{
        private OS name;
        private String version;
    }
}
