package elr.core;

import elr.gui.UI;
import elr.gui.utilities.Backgrounds;
import elr.core.modules.Console;
import elr.gui.utilities.MineFont;
import elr.core.modules.configuration.Configuration;
import elr.core.modules.Downloader;
import elr.core.modules.AES;
import elr.core.modules.CHLG;
import elr.core.modules.Logger;
import elr.core.modules.updater.Updater;
import elr.core.modules.updater.VersionChecker;
import elr.gui.About;
import elr.gui.Debug;
import elr.gui.FAQ;
import elr.gui.GuiConsole;
import elr.gui.ModsEditor;
import elr.gui.Versions;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to store all classes for an easy access.
 * @author Infernage
 */
public class Stack {
    private static String versionProgram = "6.0.0";
    public final static String title = "Escorbuto Launcher";
    private static String xmlFiles = "https://dl.dropbox.com/s/1cmxex7nkicrtg6/Files.xml?dl=1", 
            xmlMinecraftVersions = "https://dl.dropbox.com/s/qe1wcqdb08nliq5/MinecraftVersions.xml?dl=1",
            xmlModPacks = "https://dl.dropbox.com/s/kt1d4legtcovv8m/ModPacks.xml?dl=1";
    /*
     * Core classes
     */
    public static Configuration config = null;
    public static Console console = null;
    /*
     * Module classes
     */
    public static AES crypter = null;
    public static MineFont font = null;
    public static Backgrounds images = null;
    public static CHLG changelog = null;
    public static Logger login = null;
    public static Updater update = null;
    public static VersionChecker checker = null;
    public static Downloader downloader = null;
    //public static Systray system = null;
    /*
     * Gui classes
     */
    public static UI frame = null;
    public static About about = null;
    public static Debug error = null;
    public static FAQ eaq = null;
    public static ModsEditor editor = null;
    //public static Quiz quiz = null;
    public static Versions versions = null;
    public static GuiConsole postConsole = null;
    /*
     * Plugin classes
     */
    
    /*
     * Usefull methods
     */
    public static String getProgramVersion(){ return versionProgram; }
    
    public static String getXMLFiles(){ return xmlFiles; }
    
    public static String getXMLMinecraftVersions(){ return xmlMinecraftVersions; }
    
    public static String getXMLModPacks(){ return xmlModPacks; }
    
    //All here is not implemented yet.
    private static void startMap(){
        if (initialized) throw new RuntimeException("Map already created");
        objects = new HashMap<>();
        objects.put(Configuration.class, null);
        objects.put(ModuleLoader.class, null);
        objects.put(Console.class, null);
        objects.put(AES.class, null);
        objects.put(MineFont.class, null);
        objects.put(Backgrounds.class, null);
        objects.put(CHLG.class, null);
        objects.put(Logger.class, null);
        objects.put(Updater.class, null);
        objects.put(VersionChecker.class, null);
        objects.put(Downloader.class, null);
        objects.put(UI.class, null);
        objects.put(About.class, null);
        objects.put(Debug.class, null);
        objects.put(FAQ.class, null);
        objects.put(ModsEditor.class, null);
        objects.put(Versions.class, null);
    }
    private static boolean initialized = false;
    private static Map<Class, Object> objects = null;
}
