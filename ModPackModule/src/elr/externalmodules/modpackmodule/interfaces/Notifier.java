/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.externalmodules.modpackmodule.interfaces;

/**
 *
 * @author Infernage
 */
public interface Notifier {
    public void notifyListeners();
    
    public void addListener(Listener listen);
}
