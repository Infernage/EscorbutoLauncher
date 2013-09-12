package elr.externalmodules.modpackmodule;

import elr.core.Loader;
import elr.core.util.MessageControl;
import elr.externalmodules.ExternalModule;
import elr.externalmodules.ModuleResponse;
import elr.externalmodules.modpackmodule.gui.Gui;
import java.io.File;

/**
 * Main module class.
 * @author Infernage
 */
public class ModPackModule implements ExternalModule{
    private String UniqueID;
    private final String version = "1";
    private static ModPackModule instance;
    
    public static void exit(){
        instance.kill();
    }
    
    @Override
    public ModuleResponse init(File configDir) {
        instance = this;
        try {
            Data.init(new File(configDir, "elr_modpack.config"));
            new Gui(Loader.getMainGui().getParentFrame(), false);
        } catch (Exception ex) {
            MessageControl.showExceptionMessage(3, ex, "Failed to init module");
            return new ModuleResponse(ex.toString(), false);
        }
        return new ModuleResponse("OK", true);
    }

    @Override
    public void kill() {
        Data.shutdown();
        instance = null;
    }

    @Override
    public void createUniqueID() {
        UniqueID = "modpack";
    }

    @Override
    public String getUniqueID() {
        return UniqueID;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAutoInitialer() {
        return false;
    }

    @Override
    public String getInformationModule() {
        return "Module used to support a ModPack creation and update it in the server.";
    }
}
