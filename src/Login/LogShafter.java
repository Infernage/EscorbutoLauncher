/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JOptionPane;

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
            Mineshafter.mineshafter.MineClient.open(user, pass);
            Mainclass.consola.exit();
            /*URL u = new File(Sources.path(Sources.DirMC + Sources.sep() + Sources.MS)).toURI().toURL();
            URLClassLoader cl = new URLClassLoader(new URL[]{u});
            Class MineClient = cl.loadClass("mineshafter.MineClient");
            Method[] methods = MineClient.getMethods();
            int i = 0;
            while(i < methods.length){
                if (methods[i].getName().equals("open")){
                    System.out.println("Catched!");
                    Method loader = methods[i];
                    loader.invoke(MineClient, user, pass);
                    i = methods.length;
                } else{
                    System.out.println("Not catched!");
                    i++;
                }
            }
            JOptionPane.showMessageDialog(null, "Hecho!");
            /*Method loader = MineClient.getDeclaredMethod("init", new Class[]{ java.lang.String.class });
            loader.invoke(MineClient, user, pass);*/
            Vista2.see.dispose();
        } catch (Exception ex) {
            Sources.exception(ex, "Login failed!");
        }
    }
    @Override
    public void run(){
        startLog();
    }
}
