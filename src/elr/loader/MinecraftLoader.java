package elr.loader;

import elr.core.Booter;
import elr.core.Stack;
import elr.core.modules.Directory;
import elr.core.modules.ExceptionControl;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to load Minecraft in a new process.
 * @author Infernage
 */
public class MinecraftLoader {
    private String userName, sessionId;
    public Process minecraft;
    
    /**
     * Finalize all objects loaded.
     */
    private void finalizeAll(){
        Stack.eaq.dispose();
        Stack.eaq = null;
        Stack.editor.dispose();
        Stack.editor = null;
        Stack.error.dispose();
        Stack.error = null;
        Stack.versions.dispose();
        Stack.versions = null;
        Stack.about.dispose();
        Stack.about = null;
        Stack.changelog = null;
        Stack.checker = null;
        Stack.crypter = null;
        Stack.downloader = null;
        Stack.font = null;
        Stack.frame.dispose();
        Stack.frame = null;
        Stack.images = null;
        Stack.login = null;
        Stack.update = null;
        System.gc();
    }
    
    /**
     * Constructor used to launch Minecraft.
     * @param user The username.
     * @param password The session ID.
     */
    public MinecraftLoader(String user, String password){
        userName = user;
        sessionId = password;
    }
    
    /**
     * Starts the load of Minecraft.
     * @param dirMinecraft The directory which Minecraft is ubicated. Can be the instance.
     * @param ram The max RAM to launch. Only has to be an integer.
     */
    public void initMinecraft(String dirMinecraft, String ram){
        if (!dirMinecraft.endsWith(Directory.MINECRAFT) && !dirMinecraft.endsWith(Directory.MINECRAFT + 
                File.separator)) dirMinecraft = dirMinecraft + File.separator + Directory.MINECRAFT;
        String forge = "MinecraftForge.zip";
        StringBuilder builder = new StringBuilder();
        String[] jarBin = { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
        File instancedMods = new File(new File(dirMinecraft).getParent(), "instMods");
        System.out.println("Adding instanced mods to the classpath");
        if (instancedMods.exists() && instancedMods.isDirectory()){
            String[] mods = instancedMods.list();
            Arrays.sort(mods);//Searchs the most recient forge if exists
            for (String mod : mods) {
                if(!mod.equalsIgnoreCase(forge) && mod.contains("forge") && mod.contains("minecraft") 
                        && mod.endsWith(".zip")){
                    if (new File(instancedMods, forge).exists()){
                        if (!new File(instancedMods, forge).equals(new File(instancedMods, mod))){
                            new File(instancedMods, mod).delete();
                        }
                    } else{
                        new File(instancedMods, mod).renameTo(new File(instancedMods, forge));
                    }
                } else if (!mod.equalsIgnoreCase(forge) && (mod.toLowerCase().endsWith(".zip") || 
                        mod.toLowerCase().endsWith(".jar"))){
                    builder.append(System.getProperty("path.separator"));
                    builder.append(new File(instancedMods, mod).getAbsolutePath());
                }
            }
        } else{
            Stack.console.printConfigOption("Forge not found. Launching without mods");
        }
        builder.append(System.getProperty("path.separator"));
        builder.append(new File(instancedMods, forge).getAbsolutePath());
        System.out.println("Adding jar files to the classpath");
        for (String jarFile : jarBin) {
            builder.append(System.getProperty("path.separator"));
            builder.append(new File(dirMinecraft + File.separator + "bin" + File.separator + jarFile)
                    .getAbsolutePath());
        }
        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + 
                "java" + (Booter.getStaticOS().equals("windows") ? "w" : "");
        List<String> arguments = new ArrayList<>();
        arguments.add(javaPath);
        //arguments.add("-d64");
        arguments.add("-Xms256M");
        arguments.add("-Xmx" + ram + "M");
        arguments.add("-XX:+UseConcMarkSweepGC");
        arguments.add("-XX:+ExplicitGCInvokesConcurrent");
        arguments.add("-XX:+CMSIncrementalMode");
        arguments.add("-XX:+AggressiveOpts");
        arguments.add("-XX:+CMSClassUnloadingEnabled");
        arguments.add("-XX:MaxPermSize=128m");
        arguments.add("-cp");
        arguments.add(System.getProperty("java.class.path") + builder.toString());
        arguments.add(MinecraftLoader.class.getCanonicalName());
        arguments.add(dirMinecraft);
        arguments.add(userName);
        arguments.add(sessionId);
        arguments.add(forge);
        ProcessBuilder processCreator = new ProcessBuilder(arguments);
        processCreator.redirectErrorStream(true);
        try {
            finalizeAll();
            minecraft = processCreator.start();
            Stack.console.setInput(minecraft.getInputStream());
            Stack.frame.setVisible(false);
            minecraft.waitFor();
        } catch (IOException | InterruptedException e) {
            ExceptionControl.severeException(2, e, "Failed to initialize Minecraft process");
        }
        System.exit(0);
    }
    
    public static void main(String[] args){
        dir = args[0];
        user = args[1];
        sessionIDLauncher = args[2];
        String forge = args[3];
        try {
            System.out.println("Loading mods files if exist to libraries path");
            String[] jarFiles = { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
            List<File> libraries = new ArrayList<>();
            File instancedModsDir = new File(new File(dir).getParent(), "instMods");
            if (instancedModsDir.exists() && instancedModsDir.isDirectory()){
                for (String mod : instancedModsDir.list()) {
                    if(!mod.equalsIgnoreCase(forge) && (mod.toLowerCase().endsWith(".zip") || 
                            mod.toLowerCase().endsWith(".jar"))){
                        libraries.add(new File(instancedModsDir, mod));
                    }
                }
            }
            System.out.println("Loading forge to libraries path");
            libraries.add(new File(instancedModsDir, forge));
            System.out.println("Loading jar files to libraries path");
            for (String jar : jarFiles) {
                libraries.add(new File(dir + File.separator + "bin" + File.separator + jar));
            }
            System.out.println("Adding URLs to the classpath");
            URL[] urls = new URL[libraries.size()];
            for (int i = 0; i < libraries.size(); i++) {
                urls[i] = libraries.get(i).toURI().toURL();
                System.out.println("Added " + urls[i] + " to the classpath");
            }
            System.out.println("Loading natives");
            String natives = dir + File.separator + "bin" + File.separator + "natives";
            System.out.println("Applying natives and protocols to the system...");
            System.setProperty("org.lwjgl.librarypath", natives);
            System.setProperty("net.java.games.input.librarypath", natives);
            System.setProperty("user.home", new File(dir).getParent());
            loader = new URLClassLoader(urls, MinecraftLoader.class.getClassLoader());
            System.out.println("Loading minecraft classes with ClassLoader");
            try {
                launch(true);
            } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | 
                    NoSuchMethodException | InvocationTargetException | InstantiationException | 
                    IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load Minecraft with applet wrapper. Trying with compatibility mode...");
                launch(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionControl.severeExceptionWOStream(2, e, "Failed to load Minecraft");
        }
    }
    
    /*
     * Support to load Minecraft.
     */
    private static String user, sessionIDLauncher, dir;
    private static URLClassLoader loader = null;
    private static Class minecraftClient = null;
    
    private final static String MINECRAFT_CLASS = "net.minecraft.client.Minecraft", MINECRAFT_APPLET =
            "net.minecraft.client.MinecraftApplet";
    
    /**
     * Start the launch of Minecraft when the process is created to launch it.
     * @param wrapper Selects if has to be launch with wrapper.
     */
    public static void launch (boolean wrapper) throws ClassNotFoundException, IllegalArgumentException, 
            IllegalAccessException, NoSuchMethodException, IllegalAccessException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException, IOException{
        if (loader == null) throw new SecurityException("URLClassLoader can't be null");
        if (minecraftClient == null){
            minecraftClient = loader.loadClass(MINECRAFT_CLASS);
            System.out.println("mc = " + minecraftClient.toString());
            fixMCField(minecraftClient, dir);
            String mcDir = minecraftClient.getMethod("a", new Class[] { String.class }).invoke(null, new Object[] 
            { "minecraft" }).toString();
            System.out.println("MCDIR: " + mcDir);
        }
        if (wrapper){
            System.out.println("Launching with applet wrapper");
            startWithAppletWrapper();
        } else{
            System.out.println("Launching in compatibility mode");
            startInCompatibilityMode();
        }
    }
    
    private static void startInCompatibilityMode() throws ClassNotFoundException, 
            NoSuchMethodException, 
            IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Method main = minecraftClient.getMethod("main", java.lang.String[].class);
        main.invoke(null, new Object[] { new String[] { user, sessionIDLauncher }});
    }
    
    private static void startWithAppletWrapper() throws ClassNotFoundException, InstantiationException, 
            IllegalAccessException, IOException{
        Class applet = loader.loadClass(MINECRAFT_APPLET);
        Applet mcApplet = (Applet) applet.newInstance();
        MinecraftFrame frame = new MinecraftFrame();
        frame.start(mcApplet, user, sessionIDLauncher);
    }
    
    private static void fixMCField(Class<?> parameter, String dir) throws IllegalArgumentException, 
            IllegalAccessException{
        System.out.println("Applying Fields in the class");
        Field[] fields = parameter.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == File.class){
                if (field.getModifiers() == 10){
                    field.setAccessible(true);
                    field.set(null, new File(dir));
                    System.out.println("Fixed Minecraft path: Field was " + field.toString());
                    break;
                }
            }
        }
    }
}
