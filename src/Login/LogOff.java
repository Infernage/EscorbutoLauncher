/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JLabel;

/**
 *
 * @author Reed
 */
public class LogOff extends Thread{
    private URL[] urls;
    private Vista2 vista;
    private JLabel connection;
    private String name;
    public LogOff(String user, Vista2 gui, JLabel status){
        name = user;
        vista = gui;
        connection = status;
    }
    private void log(){
        String dir = Sources.path(Sources.DirMC + Sources.sep() + "bin" + Sources.sep());
        try{
            urls = new URL[]{
            new File(dir + "lwjgl.jar").toURI().toURL(), new File(dir + "jinput.jar").toURI().toURL(), 
            new File(dir + "lwjgl_util.jar").toURI().toURL(), new File(dir + "minecraft.jar").toURI().toURL(),
            new File(dir + "windows_natives.jar").toURI().toURL()
            };
        } catch (Exception ex){
            Sources.fatalException(ex, "Error en la ejecución del minecraft.", 1);
        }
        System.setProperty("org.lwjgl.librarypath", dir + "natives");
        System.setProperty("net.java.games.input.librarypath", dir + "natives");
        File working = new File(Sources.path(Sources.DirMC));
        ClassLoader loader = new URLClassLoader(urls);
        try{
            connection.setText("");
            Class minecraft = loader.loadClass("net.minecraft.client.Minecraft");
            Field[] fs = minecraft.getDeclaredFields();
            for (Field f : fs) {
              if (f.getType() == File.class) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                  Field.setAccessible(new Field[] { f }, true);
                  f.set(null, working);
                  System.out.println("Found Directory Field: " + f.getName());
                  System.out.println("Set Field to:" + working.getAbsolutePath());
                }
              }
            }
            Method[] methods = minecraft.getMethods();
            int i = 0;
            String[] args = new String[]{ name };
            while (i < methods.length){
                if (methods[i].getName().equals("main")){
                    Method launcher = methods[i];
                    launcher.invoke(minecraft, new Object[]{ args });
                    i = methods.length;
                } else{
                    i++;
                }
            }
        } catch (Exception ex){
            Sources.fatalException(ex, "Error en la ejecución del minecraft.", 1);
        }
        //Mainclass.consola.exit();
        vista.dispose();
    }
    @Override
    public void run(){
        log();
    }
}
