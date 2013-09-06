package elr.externalmodules.modpackmodule.engine;

import com.google.gson.Gson;
import elr.core.Loader;
import elr.core.util.MessageControl;
import static elr.core.util.Util.OS.linux;
import static elr.core.util.Util.OS.osx;
import static elr.core.util.Util.OS.windows;
import elr.externalmodules.modpackmodule.Data;
import elr.externalmodules.modpackmodule.ModPackModule;
import elr.externalmodules.modpackmodule.gui.Loading;
import elr.externalmodules.modpackmodule.gui.ServerHandler;
import elr.externalmodules.modpackmodule.gui.ServerSelector;
import elr.modules.compressor.Compressor;
import elr.modules.threadsystem.ThreadPool;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Infernage
 */
public class Engine extends Thread{
    private static Engine instance = null;
    
    public static void start(ModPackCreator creator, File basePath){
        if (instance == null){
            instance = new Engine(creator, basePath);
            ServerHandler handler = new ServerHandler(Loader.getMainGui().getParentFrame(), false);
            handler.setLocationRelativeTo(null);
            handler.setVisible(true);
            return;
        }
        ThreadPool.getInstance().execute(instance);
    }
    
    private ModPackCreator creator;
    private File basePath;
    
    public Engine(ModPackCreator creator, File basePath){
        this.basePath = basePath;
        this.creator = creator;
    }
    
    @Override
    public void run(){
        try {
            File coded;
            if (!basePath.getName().endsWith(".cxz")){
                Loading.create("Compressing, please wait");
                coded = Compressor.codedCompression(basePath, Compressor.CompressionLevel.ULTRA);
                Loading.close();
            } else{
                coded = basePath;
            }
            switch(Loader.getConfiguration().getOS()){
                case linux: Runtime.getRuntime().exec("xdg-open " + coded.getPath());
                    break;
                case osx: Runtime.getRuntime().exec("open -R " + coded.getPath());
                    break;
                case windows: Runtime.getRuntime().exec("Explorer /select, " + coded.getPath());
                    break;
                default: throw new Exception("Unknown OS detected");
            }
            ServerSelector sel = new ServerSelector(Loader.getMainGui().getParentFrame(), false);
            sel.setLocationRelativeTo(null);
            sel.setVisible(true);
            String url = sel.getURL();
            boolean replace = sel.isReplaced();
            sel.dispose();
            creator.setUrl(url);
            if (creator.readyToUpload()){
                Map<String, String> par = new HashMap<>();
                par.put("auth", Data.getELR("user") + "=" + Data.getELR("password"));
                par.put("json", new Gson().toJson(creator));
                if (replace) par.put("replace", "true");
                if (ServerHandler.doPost(ServerHandler.URL, par)){
                    MessageControl.showInfoMessage("Modpack server was updated successfully", "Success");
                } else{
                    MessageControl.showErrorMessage("Failed to update Modpack server.\nPlease,"
                            + " try again", "Failed");
                }
            } else{
                throw new Exception("URL null?");
            }
        } catch (Exception e) {
            MessageControl.showExceptionMessage(2, e, "Failed to code your files");
        } finally{
            ModPackModule.exit();
        }
    }
}
