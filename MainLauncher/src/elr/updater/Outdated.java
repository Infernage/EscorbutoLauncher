package elr.updater;

import elr.MainFrame;
import java.io.File;

/**
 * Class used to check versions installed before the update.
 * @author Infernage
 */
public class Outdated {
    public static void checkOutdated(String version, String update, MainFrame frame){
        if (version.equals("0.0.0")){
            deleteDirectory(new File(frame.getWorkingDir()));
        }
    }
    
    /**
     * This method deletes a file or a directory.
     * @param file The file or directory to delete
     */
    private static void deleteDirectory(File file) {
        File[] list = file.listFiles();
        for (int x = 0; x < list.length; x++) {
            if (list[x].isDirectory()) {
                deleteDirectory(list[x]);
            }
            list[x].delete();
        }
    }
}
