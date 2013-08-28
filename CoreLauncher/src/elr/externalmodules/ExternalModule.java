package elr.externalmodules;

import java.io.File;

/**
 * Interface used to initialize each ExternalModule.
 * @author Infernage
 */
public interface ExternalModule {
    public String getVersion();
    
    public ModuleResponse init(File rootDir);
    
    public void kill();
    
    public void createUniqueID();
    
    public String getUniqueID();
}
