package elr;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import javax.swing.JFrame;
import org.jvnet.substance.SubstanceLookAndFeel;


/**
 * Initial class of Escorbuto Launcher.
 * @author Infernage
 * @version 6.0.0
 */
public class Starter {
    
    /**
     * Sets the operative system.
     */
    private static String setOS(){
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")){
            OS = "windows";
        } else if (OS.contains("lin") || OS.contains("uni")){
            OS = "linux";
        } else if (OS.contains("mac")){
            OS = "macosx";
        } else{
            OS = "unknown";
        }
        return OS;
    }
    
    /**
     * Gets the folder config path.
     * @return The folder config path.
     */
    private static String getWorkingDir(String OS){
        switch (OS) {
            case "windows":
                return System.getenv("APPDATA") + "\\ELR";
            case "linux":
                return System.getProperty("user.home") + "/.ELR";
            case "macosx":
                return System.getProperty("user.home") + "/Library/Application Support/ELR";
            default:
                return System.getProperty("user.home") + "/ELR";
        }
    }
    
    /**
     * Gets the actual jar.
     * @return The jar executed.
     * @throws IOException If something went wrong.
     */
    private static String getCurrentJar() throws IOException{
        return URLDecoder.decode(new File(Starter.class
                    .getProtectionDomain().getCodeSource().getLocation()
                    .getPath()).getCanonicalPath(), "UTF-8");
    }
    
    private Starter(){}
    
    /**
     * Starts ELR.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.net.useSystemProxies", "true");
        JFrame.setDefaultLookAndFeelDecorated(true);
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.BusinessBlackSteelSkin");
        //Creates the root file into the computer.
        String OS = setOS();
        String workingDir = getWorkingDir(OS);
        String currentJar;
        try {
            currentJar = getCurrentJar();
        } catch (Exception ignore) {
            currentJar = "NULL";
        }
        File root = new File(workingDir);
        if (!root.exists()) root.mkdirs();
        MainFrame frame = new MainFrame(OS, workingDir, currentJar);
        frame.load();
    }
}
