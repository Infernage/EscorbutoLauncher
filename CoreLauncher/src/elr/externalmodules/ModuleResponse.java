/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.externalmodules;

/**
 *
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
