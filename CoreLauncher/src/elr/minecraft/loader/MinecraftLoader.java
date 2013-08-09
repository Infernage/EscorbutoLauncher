package elr.minecraft.loader;

import elr.core.util.MessageControl;
import java.applet.Applet;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Class used to load Minecraft before 1.6.
 * @author Infernage
 */
public class MinecraftLoader {
    private static File gameDir, assetsDir;
    private static String username, sessionid;
    
    /**
     * Starts the Minecraft load. Only available to versions before 1.6.
     * @param args 
     */
    public static void main(String[] args){
        try {
            OptionParser parser = new OptionParser();
            parser.allowsUnrecognizedOptions();
            OptionSpec<String> profileOption = parser.accepts("version", "The version we launched with")
                    .withRequiredArg();
            OptionSpec<File> gameDirOption = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
            OptionSpec<File> assetsDirOption = parser.accepts("assetsDir").withRequiredArg().ofType(File.class);
            OptionSpec<String> tweakClassOption = parser.accepts("tweakClass").withRequiredArg();
            OptionSpec<String> nonOption = parser.nonOptions();
            OptionSet options = parser.parse(args);
            gameDir = options.valueOf(gameDirOption);
            assetsDir = options.valueOf(assetsDirOption);
            String profileName = options.valueOf(profileOption);
            String tweakClassName = options.valueOf(tweakClassOption);
            List<String> arg = options.valuesOf(nonOption);
            username = arg.get(0);
            sessionid = arg.get(1);
            String natives = arg.get(2);
            System.setProperty("org.lwjgl.librarypath", natives);
            System.setProperty("net.java.games.input.librarypath", natives);
            URLClassLoader loader = (URLClassLoader) MinecraftLoader.class.getClassLoader();
            Class minecraftClient = loader.loadClass("net.minecraft.client.Minecraft");
            fixMCField(minecraftClient);
            Class applet = loader.loadClass("net.minecraft.client.MinecraftApplet");
            Applet mcApplet = (Applet) applet.newInstance();
            MinecraftFrame frame = new MinecraftFrame();
            frame.start(mcApplet, username, sessionid);
        } catch (Exception e) {
            e.printStackTrace();
            MessageControl.severeExceptionMessage(2, e, "Failed to load Minecraft");
        }
    }
    
    private static void fixMCField(Class<?> parameter) throws IllegalArgumentException, 
            IllegalAccessException{
        System.out.println("Applying Fields in the class");
        Field[] fields = parameter.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == File.class){
                if (field.getModifiers() == 10){
                    field.setAccessible(true);
                    field.set(null, gameDir);
                    System.out.println("Fixed Minecraft path: Field was " + field.toString());
                    break;
                }
            }
        }
    }
}
