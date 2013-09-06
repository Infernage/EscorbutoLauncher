package elr.externalmodules;

import java.io.File;

/**
 * Class used to abstract an ExternalModule.
 * @author Infernage
 */
public class CompleteModule {
    private ExternalModule internal;
    private String name;
    private ModuleLoader.STATE state;
    private File src;
    private boolean uid = false;
    
    /**
     * Default constructor.
     * @param mod The external module to wrap.
     * @param targetSrc The module location.
     */
    public CompleteModule(ExternalModule mod, File targetSrc){
        internal = mod;
        name = mod.getClass().getName().substring(mod.getClass().getName().lastIndexOf(".") + 1, 
                mod.getClass().getName().length());
        state = ModuleLoader.STATE.LOADED;
        src = targetSrc;
    }
    
    /**
     * Constructor used to mark a module with {@code STATE.NOT_LOADED}
     * @param name The module name.
     */
    public CompleteModule(String name){
        if (!name.startsWith("elr.externalmodules.")) throw new RuntimeException("Bad module");
        internal = null;
        this.name = name;
        state = ModuleLoader.STATE.NOT_LOADED;
    }
    
    public File getSource(){
        return src;
    }
    
    /**
     * Initializes the module.
     * @return A ModuleResponse class.
     */
    public ModuleResponse init(){
        if (!uid){
            internal.createUniqueID();
            uid = true;
        }
        return internal.init(ModuleLoader.getModuleConfigFolder());
    }
    
    public String getVersion(){
        return internal.getVersion();
    }
    
    public String getUniqueID(){
        if (!uid){
            internal.createUniqueID();
            uid = true;
        }
        return internal.getUniqueID();
    }
    
    public String getName(){
        return name;
    }
    
    public ModuleLoader.STATE getState(){
        return state;
    }
    
    public String getInfo(){
        return internal.getInformationModule();
    }
}
