package elr.core;

import elr.gui.UI;
import elr.gui.utilities.Backgrounds;
import elr.core.modules.Console;
import elr.gui.utilities.MineFont;
import elr.core.modules.configuration.Configuration;
import elr.core.modules.ExceptionControl;
import elr.core.modules.CHLG;
import elr.core.modules.Directory;
import elr.core.modules.Logger;
import elr.core.modules.outdated.Outdated;
import elr.core.modules.updater.Updater;
import elr.core.modules.updater.VersionChecker;
import elr.gui.About;
import elr.gui.Debug;
import elr.gui.FAQ;
import elr.gui.GuiConsole;
import elr.gui.ModsEditor;
import elr.gui.Splash;
import elr.gui.Versions;
import java.io.File;
import javax.swing.ImageIcon;

/**
 * Class used to loads all modules.
 * @author Infernage
 */
class ModuleLoader {
    private static ModuleLoader loader;
    
    private ModuleLoader(){} //Don't let anyone to instance this.
    
    /**
     * Starts all loads of protocols and modules.
     */
    public static void startLoad(){
        if (loader != null) throw new RuntimeException("ModuleLoader already created");
        loader = new ModuleLoader();
        System.out.println("Initializing all modules...");
        Splash.displayMsg("Loading modules...");
        Splash.set(25);
        File inst = new File(Directory.instances());
        if (!inst.exists()) inst.mkdirs();
        loader.preLoadGuiModule();
        loader.loadModuleClasses();
        loader.loadGuiModuleClasses();
        //loader.loadPluginsModuleClasses();
        loader.finishLoads();
    }
    
    private void finishLoads(){
        Splash.displayMsg("Modules loaded");
        if (!Stack.update.isAlive() && !Stack.checker.isAlive() && !Stack.checker.isUpdating()){
            Stack.frame.activate();
        } else{
            new Thread(){
                @Override
                public void run(){
                    while(Stack.update.isAlive() || Stack.checker.isAlive() || Stack.checker.isUpdating()){
                        try {
                            Thread.sleep(200);
                            Thread.yield();
                        } catch (InterruptedException ex) {
                            //Ignore
                        }
                    }
                    Stack.frame.activate();
                }
            }.start();
        }
    }
    
    private void loadPluginsModuleClasses(){
        //Not implemented yet
    }
    
    private void loadGuiModuleClasses(){
        System.out.println("Loading main Gui modules");
        Splash.set(80);
        Stack.versions = new Versions();
        Stack.versions.setIconImage(new ImageIcon(loader.getClass()
                .getResource("/elr/resources/5547.png")).getImage());
        Stack.versions.setTitle("Minecraft installer");
        Splash.set(85);
        Stack.editor = new ModsEditor();
        Stack.editor.setIconImage(new ImageIcon(loader.getClass()
                .getResource("/elr/resources/5547.png")).getImage());
        Stack.editor.setTitle("Minecraft editor");
        Splash.set(90);
        Stack.error = new Debug(Stack.frame, true);
        Stack.error.setTitle("Error ticket");
        Stack.about = new About(Stack.frame, true);
        Stack.eaq = new FAQ(Stack.frame, true);
        Splash.set(95);
    }
    
    private void loadModuleClasses(){
        Splash.set(45);
        Stack.changelog = new CHLG();
        Stack.frame.init();
        Stack.changelog.start();
        System.out.println("Changelog module loaded");
        Splash.set(50);
        Stack.login = new Logger();
        System.out.println("Created login module");
        Splash.set(52);
        Stack.update = new Updater();
        Stack.checker = new VersionChecker();
        Stack.checker.init();
        Splash.set(58);
        System.out.println("Created updater module");
        System.out.println("File system already up");
        System.out.println("IO module ready");
        System.out.println("Starting outdated engine module");
        Splash.set(63);
        Outdated.startEngines();
        Splash.set(70);
        System.out.println("Patching connection");
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        Splash.set(75);
    }
    
    private void preLoadGuiModule(){
        System.out.println("Initializing main UI\nStarting console module");
        try {
            if (Stack.config.getValueConfig(Configuration.LauncherConfigVar.defaultLog.name())
                    .equals("true")){
                Stack.console = Console.createDefault();
            } else{
                String path = Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_logPath
                        .name());
                Stack.console = new Console(new File(path));
            }
            Splash.set(33);
            System.out.println("Console module initialized");
        } catch (Exception e) {
            ExceptionControl.showExceptionWOStream(e, "Failed loading the console!", 0);
        }
        Stack.postConsole = new GuiConsole();
        Stack.postConsole.setTitle("Minecraft console");
        Stack.postConsole.setIconImage(new ImageIcon(loader.getClass()
                .getResource("/elr/resources/5547.png")).getImage());
        Stack.postConsole.setVisible(true);
        Stack.font = new MineFont();
        Splash.set(27);
        System.out.println("Initialized fonts");
        Stack.images = new Backgrounds();
        Splash.set(30);
        System.out.println("Created ContentModule");
        Stack.frame = new UI();
        Splash.set(40);
        Stack.frame.setIconImage(new ImageIcon(loader.getClass()
                .getResource("/elr/resources/5547.png")).getImage());
        Stack.frame.setTitle(Stack.title + " " + Stack.getProgramVersion());
        Stack.frame.setLocationRelativeTo(null);
        System.out.println("Main GUI initialized");
        System.out.println("Loading main protocols and modules");
    }
}
