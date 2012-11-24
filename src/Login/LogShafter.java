/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author Reed
 */
public class LogShafter extends Thread{
    private String[] args;
    public LogShafter(String U, String P){
        args = new String[]{ U, P };
    }
    private void startLog(){
        /*URLClassLoader cl = null;
        try {
            URL u = new File(Sources.path(Sources.DirMC + Sources.sep() + Sources.MS)).toURI().toURL();
            cl = new URLClassLoader(new URL[]{u});
            Class MineClient = cl.loadClass("mineshafter.MineClient");
            Method[] methods = MineClient.getMethods();
            int i = 0;
            while(i < methods.length){
                System.out.println(methods[i].getName());
                if (methods[i].getName().equals("main")){
                    if (Mainclass.consola.isVisible()){
                        Mainclass.consola.exit();
                    }
                    System.out.println("Catched!");
                    Method loader = methods[i];
                    loader.invoke(MineClient, new Object[]{ args });
                    i = methods.length;
                } else{
                    System.out.println("Not catched!");
                    i++;
                }
            }
            /*Method loader = MineClient.getDeclaredMethod("init", new Class[]{ java.lang.String.class });
            loader.invoke(MineClient, user, pass);*/
        /*    Vista2.see.dispose();
        } catch (Exception ex) {
            Sources.exception(ex, "Login failed!");
        } finally{
            try {
                cl.close();
            } catch (IOException ex) {
                
            }
        }*/
        Desktop dest = Desktop.getDesktop();
        try {
            if (Mainclass.consola.isVisible()){
                Mainclass.consola.exit();
            }
            Vista2.see.dispose();
            dest.open(new File(Sources.path(Sources.DirMC + Sources.sep() + Sources.MS)));
        } catch (IOException ex) {
            Sources.fatalException(ex, "Login failed!", 1);
        }
    }
    @Override
    public void run(){
        startLog();
    }
}
