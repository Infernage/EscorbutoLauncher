/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author Reed
 */
public class LogShafter extends Thread{
    private String user, pass;
    public LogShafter(String U, String P){
        user = U;
        pass = P;
    }
    private void startLog(){
        try {
            URL u = new File(Sources.path(Sources.DirMC + Sources.sep() + Sources.MS)).toURI().toURL();
            URLClassLoader cl = new URLClassLoader(new URL[]{u});
            Class MineClient = cl.loadClass("mineshafter.Mineclient");
            Method loader = MineClient.getDeclaredMethod("init", new Class[]{ java.lang.String.class });
            loader.invoke(MineClient, user, pass);
        } catch (Exception ex) {
            Sources.exception(ex, "Login failed!");
        }
    }
    public void run(){
        startLog();
    }
}
