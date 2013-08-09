package elr.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Class used to update MainELR.
 * @author Infernage
 */
public final class SilentUpdater {
    public static void main(String[] args){
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            //Ignore
        }
        File updated = new File(args[0]), current = new File(args[1]);
        current.delete();
        silentCopy(updated, current);
        updated.delete();
        List<String> arguments = new ArrayList<>();
        arguments.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        arguments.add("-jar");
        arguments.add(new File(current.getParent(), updated.getName()).getPath());
        arguments.add("--deleteFile");
        arguments.add(updated.getPath());
        try {
            new ProcessBuilder(arguments).start();
        } catch (Exception e) {
            String stack = "";
            for (StackTraceElement element : e.getStackTrace()){
                stack += element.toString() + "\n";
            }
            JOptionPane.showMessageDialog(null, e.toString() + "\n" + stack + "\nFailed to relaunch!", 
                    "Exception catched", 0);
            System.exit(4);
        }
        System.exit(0);
    }
    
    private static void silentCopy(File src, File target){
        try(BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(target))){
            byte[] buffer = new byte[input.available()];
            input.read(buffer, 0, buffer.length);
            output.write(buffer);
        } catch(Exception e){
            //Ignore
        }
    }
}
