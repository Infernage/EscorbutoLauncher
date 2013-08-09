package elr.minecraft;

import elr.core.Loader;
import elr.profiles.Profile;

/**
 *
 * @author Infernage
 */
public class MinecraftEndAction{
    private String action;
    private Process minecraft_process;
    
    public MinecraftEndAction(String act){
        action = act;
    }
    
    public void setProcess(Process process){
        minecraft_process = process;
    }
    
    public void startListening(){
        if (minecraft_process == null) throw new RuntimeException("Process is null");
        if (!action.equals(Profile.actions[2])) Loader.getMainGui().getParentFrame().setVisible(false);
        try {
            minecraft_process.waitFor();
            if (action.equals(Profile.actions[0])){
                System.exit(0);
            } else if (action.equals(Profile.actions[1])){
                Loader.getMainGui().getParentFrame().setVisible(true);
            }
        } catch (Exception e) {
        }
    }
}
