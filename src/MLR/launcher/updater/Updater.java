package MLR.launcher.updater;

import elr.core.Booter;
import elr.core.modules.ExceptionControl;
import elr.core.modules.IO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to support the update of 5.2.1 to 6.0.0 launcher.
 * @author Infernage
 */
public class Updater {
    public static void main(String[] args){
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            //Ignore
        }
        File old = new File(args[0]), update = new File(args[1]);
        try {
            old.delete();
            IO.copy(update, old);
        } catch (Exception e) {
            //Ignore
        }
        List<String> par = new ArrayList<>();
        par.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + 
                (Booter.getStaticOS().equals("windows") ? "javaw" : "java"));
        par.add("-jar");
        par.add(args[0]);
        ProcessBuilder builder = new ProcessBuilder(par);
        try {
            builder.start();
        } catch (Exception e) {
            ExceptionControl.severeExceptionWOStream(4, e, "Failed to update the launcher");
        }
        System.exit(0);
    }
}
