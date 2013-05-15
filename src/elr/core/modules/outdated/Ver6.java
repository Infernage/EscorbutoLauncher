package elr.core.modules.outdated;

import elr.core.Stack;
import elr.core.modules.configuration.Configuration;
import elr.core.modules.Directory;
import elr.core.modules.ExceptionControl;
import elr.core.modules.IO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Outdated class used to supports versions before 7.
 * @author Infernage
 */
public class Ver6 {
    /**
     * Starts the Outdated Module Version 6.
     * @param version The version installed before.
     */
    public static void startModule(int version){
        if (version < 600) version = 500;
        switch(version){
            case 500: _5xx();
        }
    }
    
    /**
     * Module part which modifies the old configuration before version 6.
     */
    private static void _5xx(){
        String dat = Stack.config.getOS().equals("windows") ? "Data" : ".Data";
        File data = new File(new File(Directory.root()).getParent(), dat);
        File instances = new File(data, "Instances");
        File lstlg = new File(data, "lstlg.data");
        if (instances.list().length > 1){
            for (File instance : instances.listFiles()) {
                try {
                    if (instance.getName().equals("properties.prop")) continue;
                    String name = Stack.frame.checkInstance(instance.getName());
                    System.out.println("Exporting " + instance.getName());
                    File dst = new File(Directory.instances(), name);
                    IO.copyDirectory(instance, dst);
                    new File(dst, ".minecraft").renameTo(new File(dst, Directory.MINECRAFT));
                } catch (Exception e) {
                    ExceptionControl.showExceptionWOStream(e, "Failed to traspase data", 2);
                    //Ignore
                }
            }
        }
        if (lstlg.exists()){
            try {
                try (BufferedReader bf = new BufferedReader(new FileReader(lstlg))) {
                    System.out.println("Exporting data");
                    String user = bf.readLine();
                    String password = bf.readLine();
                    if (user != null && !user.equals("")){
                        Stack.config.setProperty(Configuration.LauncherConfigVar.rememberUser.name(), user);
                    }
                    if (password != null && !password.equals("")){
                        Stack.config.setProperty(Configuration.LauncherConfigVar.rememberPassword.name(), 
                                password);
                    }
                }
            } catch (Exception e) {
                ExceptionControl.showExceptionWOStream(e, "Failed to traspase data", 2);
                //Ignore
            }
        }
        IO.deleteDirectory(data);
        data.delete();
    }
}
