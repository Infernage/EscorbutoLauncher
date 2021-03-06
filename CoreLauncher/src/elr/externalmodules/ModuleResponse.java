package elr.externalmodules;

/**
 * Class used to control each response and if the module is initialized.
 * @author Infernage
 */
public class ModuleResponse {
    private String response;
    private boolean isInit;
    
    public ModuleResponse(String res, boolean init){
        response = res;
        isInit = init;
    }
    
    public boolean isInitialized(){ return isInit; }
    
    public String getResponse(){ return response; }
}
