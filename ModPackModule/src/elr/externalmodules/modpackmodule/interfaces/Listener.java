/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.externalmodules.modpackmodule.interfaces;

/**
 *
 * @author Infernage
 */
public interface Listener {
    public final static int CONTENT = 0, END = 1;
    public void action (int message, Object data);
}
