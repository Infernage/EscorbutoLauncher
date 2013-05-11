package elr;

import elr.Starter.StaticForms;
import elr.core.Booter;
import elr.core.modules.AES;
import elr.core.modules.IO;
import elr.gui.Splash;
import elr.xz_coder.Decoder;
import elr.xz_coder.Encoder;
import elr.xz_coder.xz.UnsupportedOptionsException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.jvnet.substance.SubstanceLookAndFeel;


/**
 * Initial class of Escorbuto Launcher.
 * @author Infernage
 * @version 6.0.0
 */
public class Starter {
    /**
     * Developer method. Testing to compress a file.
     * @param input The file to compress.
     * @param privileged Choose if the file will be encoded with privileges or not.
     * @param nameDST The zip name.
     * @return The file encoded.
     * @throws ZipException If happens an error while compressing.
     * @throws FileNotFoundException If the file isn't found.
     * @throws UnsupportedOptionsException If the current SO don't support the operation.
     * @throws IOException If happens an I/O error.
     */
    private static File compress(File input, boolean privileged, String nameDST) throws ZipException, 
            FileNotFoundException, UnsupportedOptionsException, IOException{
        String name;
        if (nameDST == null) name = input.getName() + ".zip";
        else name = nameDST;
        File toZip = new File(input.getParent(), name);
        if (toZip.exists()) toZip.delete();
        ZipFile zip = new ZipFile(toZip);
        ZipParameters par = new ZipParameters();
        par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        System.out.println("Compressing");
        if (input.isDirectory()){
            zip.createZipFileFromFolder(input, par, false, 10485760);
        } else{
            zip.createZipFile(input, par, false, 10485760);
        }
        input = zip.getFile();
        File encoded;
        System.out.println("Encoding");
        if(privileged){
            encoded = Encoder.privilegedEncode(new Starter(), input);
        } else{
            encoded = Encoder.encode(new AES(StaticForms.password), input);
        }
        return encoded;
    }
    
    /**
     * Private method which has only use to execute before compiling selected actions.
     */
    private static void executeDeveloperActions(){
        //Only allowed to test before executing.
    }
    
    /**
     * Private method used to execute privileged actions.
     */
    private static void executePrivilegedActions(){
        File root = new File(StaticForms.getConfigurationPath(Booter.getStaticOS()));
        if (!root.exists()) root.mkdirs();
        int jre = Integer.parseInt(System.getProperty("java.version").split("_")[1]);
        int version = Integer.parseInt(System.getProperty("java.version").split("_")[0].split("\\.")[1]);
        if (jre != 21 || version != 7){
            System.out.println("Applying own jre");
            Splash.displayMsg("Applying own jre...");
            try {
                File jreOutput = new File(StaticForms.getConfigurationPath(Booter.getStaticOS()) +
                        File.separator + "jre");
                if (!jreOutput.exists() || jreOutput.listFiles().length < 9){
                    if (jreOutput.exists()) IO.deleteDirectory(jreOutput);
                    File temp = new File(StaticForms.getConfigurationPath(Booter.getStaticOS()) + 
                            File.separator + "JRE_zip-File_.cxz.priv");
                    try (BufferedInputStream input = new BufferedInputStream(Starter.class
                            .getResourceAsStream(
                                 "/elr/resources/JRE_zip-File_.cxz.priv")); 
                            BufferedOutputStream output = new BufferedOutputStream(new 
                                    FileOutputStream(temp))) {
                        IO.copy(input, output);
                    }
                    File customJRE = Decoder.privilegedDecoder(new Starter(), temp);
                    ZipFile zip = new ZipFile(customJRE);
                    zip.extractAll(jreOutput.getParent());
                    temp.delete();
                    customJRE.delete();
                }
                List<String> arg = new ArrayList<>();
                arg.add(jreOutput.getAbsolutePath() + File.separator + "bin" + File.separator + 
                        (Booter.getStaticOS().equals("windows") ? "javaw" : "java"));
                arg.add("-jar");
                arg.add(StaticForms.getStaticCurrentJar());
                Process start = new ProcessBuilder(arg).start();
                Thread.sleep(2000);
                start.exitValue();
            } catch (Exception e) {
                if (e instanceof IllegalThreadStateException){
                    System.exit(0);
                }
                e.printStackTrace();
                int i = JOptionPane.showConfirmDialog(null, "If you continue executing the program with "
                        + "java " + version + " version " + jre + "\nyou can suffer a malfunction.\nDo"
                        + " you want to continue?", "WARNING", JOptionPane.YES_NO_OPTION, 
                        JOptionPane.WARNING_MESSAGE);
                if (i != 0) System.exit(-1);
            }
        }
    }
    
    private Starter(){} //Used only to do a privileged action.
    
    /**
     * Class used to give statics contents which depends of SO.
     * @author Infernage
     */
    public static class StaticForms{
        /**
         * Gets the actual jar in a static function.
         * @return The jar executed.
         * @throws IOException If something went wrong.
         */
        protected static String getStaticCurrentJar() throws IOException{
            return URLDecoder.decode(new File(Starter.class
                        .getProtectionDomain().getCodeSource().getLocation()
                        .getPath()).getCanonicalPath(), "UTF-8");
        }

        /**
         * Gets the folder config path.
         * @return The folder config path.
         */
        protected static String getConfigurationPath(String OS){
            switch (OS) {
                case "windows":
                    return System.getenv("APPDATA") + "\\ELR";
                case "linux":
                    return System.getProperty("user.home") + "/.ELR";
                case "macosx":
                    return System.getProperty("user.home") + "/Library/Application Support/ELR";
            }
            return null;
        }
    
        /**
         * Non-privileged encoding password.
         */
        protected static String password = "Escorbuto_Network";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        executeDeveloperActions();
        JFrame.setDefaultLookAndFeelDecorated(true);
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.BusinessBlackSteelSkin");
        Splash.init();
        Splash.displayMsg("Checking jre...");
        Splash.set(1);
        executePrivilegedActions();
        System.setProperty("java.net.useSystemProxies", "true");
        //SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.NebulaBrickWallSkin");
        //SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.RavenGraphiteGlassSkin");
        System.out.println("Starting boot...");
        JOptionPane.showMessageDialog(null, System.getProperty("java.home"));
        Booter.startBoot();
    }
}
