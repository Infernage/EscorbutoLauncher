package elr.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elr.core.util.Directory;
import elr.core.util.IO;
import elr.core.util.MessageControl;
import elr.core.util.Util;
import elr.gui.MainGui;
import elr.minecraft.modpacks.ModPackList;
import elr.minecraft.versions.VersionList;
import elr.modules.authentication.Authenticator;
import elr.modules.threadsystem.Downloader;
import elr.modules.compressor.Compressor;
import elr.modules.threadsystem.DownloadJob;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Instances;
import elr.profiles.Profile;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Initial class of CoreELR.
 * @author Infernage
 * @version 7.0.0
 */
public class Loader {
    public static final boolean allowed = getPermission();
    private final static int java_version = 7;
    private final static int java_release = 21;
    private static Loader secure = null;
    private static String version_program = "7.0.0";
    private static ServerSocket unique;
    private static Shutdown kill;
    
    static Configuration config = null;
    
    private File launcher; //CoreLauncher
    private MainGui gui;
    private VersionList versionList = null;
    private ModPackList modpackList = null;
    
    public static VersionList getVersionList(){ return secure.versionList; }
    
    public static ModPackList getModPackList(){ return secure.modpackList; }
    
    public static MainGui getMainGui(){ return secure.gui; }
    
    public static String getVersion(){ return version_program.split(" ")[0]; }
    
    public static Configuration getConfiguration(){ return config; }
    
    public static void saveConfiguration(){
        kill.saveConfig();
    }
        
    /**
     * Method invoked to load the ELR core.
     * @param args args[0] = OS, args[1] = workDir, args[2] = mainJar
     * @param mainFrame The frame where will be all JPanels.
     * @param currentJar Core ELR file.
     * @param prog A progress bar to show the progress of a download.
     * @param stream Custom PrintStream used to inform before remove all containers from the frame.
     */
    public static void load(String[] args, JFrame mainFrame, File currentJar, JProgressBar prog,
            PrintStream stream){
        if (secure != null) throw new RuntimeException("Loader already created");
        if (args[2].equalsIgnoreCase("null")) args[2] = args[1] + File.separator + "MainELR.jar";
        stream.println("Allowed response from server: " + allowed);
        new Thread("Unique_Instance"){
            @Override
            public void run(){
                try {
                    int attempts = 100;
                    int port = 7777;
                    while(attempts > 0){
                        try {
                            unique = new ServerSocket(port);
                            attempts = 0;
                            System.out.println("Internal server successfully created on port " + port);
                        } catch (Exception e) {
                            System.err.println("Failed to assign the port " + port + " to the internal"
                                    + " server... Tries left: " + attempts);
                            attempts--;
                            port++;
                            if (attempts == 0) throw new IOException("Impossible to assign a port. Tried"
                                    + " 100 times.");
                        }
                    }
                    Socket sock;
                    while((sock = unique.accept()) != null){
                        sock.close();
                    }
                } catch (BindException ex){
                    ex.printStackTrace();
                    MessageControl.showErrorMessage("Program is already running", "ERROR");
                    System.exit(1);
                } catch (SocketException ex){
                    System.out.println("Socket closed from outside: " + ex.getMessage());
                } catch (IOException ex){
                    ex.printStackTrace();
                    MessageControl.showErrorMessage("Something failed with the internal server! Cause: "
                            + ex.toString(), "ERROR");
                    System.err.println("Something failed with the internal server!");
                    System.exit(1);
                }
            }
        }.start();
        secure = new Loader(currentJar);
        secure.start(args, prog, stream, mainFrame);
    }
    
    /**
     * Checks the permission of the launcher.
     * @return {@code true} if has permission.
     */
    private static boolean getPermission(){
        try {
            return Boolean.parseBoolean(Util.requestGetMethod("https://dl.dropbox.com/s/kkuyfyvyw39h7tc/AllowedPlay.txt?dl=1").replace("\n", "").replace("\r", ""));
        } catch (Exception e) {
            e.printStackTrace();
            //Ignore
        }
        return false;
    }
    
    /**
     * Default constructor.
     * @param currentJar ELR core file.
     */
    private Loader(File currentJar){
        launcher = currentJar;
    }
    
    /**
     * Invoked from load method. All parameters are the same.
     */
    private void start(String[] args, JProgressBar prog, PrintStream stream, JFrame frame){
        ThreadPool.startThreadPool();
        checkJRE(args, prog, stream);
        File fileConfig = new File(args[1], "ELR.cfg");
        if (fileConfig.exists()) fileConfig.delete();
        for (File file : new File(args[1]).listFiles()) {
            if (file.getName().contains(";ELR.cfg;")){
                try {
                    File dec = Compressor.cryptedDecompression(file, null, false);
                    stream.println("Trying Json read...");
                    try {
                        try (JSonReader reader = new JSonReader(new FileInputStream(dec))) {
                            Configuration.startConfig(args[2], args[2].substring(0, args[2]
                                    .lastIndexOf(File.separator)), args[0], new File(args[1]), reader);
                        }
                        stream.println("Json read completed");
                    } catch (Exception e) {
                        e.printStackTrace(stream);
                        stream.println("Json read failed. Trying with Object read...");
                        try {
                            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(dec))) {
                                Configuration.startConfig(args[2], args[2].substring(0, args[2]
                                        .lastIndexOf(File.separator)), args[0], new File(args[1]), input);
                            }
                            stream.println("Object read completed");
                        } catch (Exception ex) {
                            ex.printStackTrace(stream);
                            stream.println("Object read failed. Not possible to read the "
                                    + "configuration!");
                            file.delete();
                            dec.delete();
                            throw new RuntimeException("Configuration read has failed. Please, try to "
                                    + "reload the launcher.");
                        }
                    }
                    kill = new Shutdown(dec, file);
                    Runtime.getRuntime().addShutdownHook(kill);
                    startAllProtocols(frame);
                    return;
                } catch (Exception e) {
                    e.printStackTrace(stream);
                    return;
                }
            }
        }
        try {
            stream.println("Creating new Configuration...");
            fileConfig.createNewFile();
            Configuration.startConfig(args[2], args[2].substring(0, args[2].lastIndexOf(File.separator)),
                    args[0], new File(args[1]), null);
            File temporal = Compressor.cryptedCompression(fileConfig, Compressor.CompressionLevel
                    .NOCOMPRESS, null);
            kill = new Shutdown(fileConfig, temporal);
            Runtime.getRuntime().addShutdownHook(kill);
        } catch (Exception e) {
            e.printStackTrace(stream);
            return;
        }
        startAllProtocols(frame);
    }
    
    /**
     * Inits all files and jobs.
     * @param frame The JFrame to init.
     */
    private void startAllProtocols(JFrame frame){
        initFrame(frame);
        gui.getConsoleTab().setFileStream();
        ThreadPool.getInstance().submit(new Runnable(){
            @Override
            public void run(){
                DownloadJob resources = new DownloadJob("Resources", gui.getProgressBar());
                Set<Downloader> assets = new HashSet<>();
                File asset = new File(Directory.minecraftResources());
                try {
                    URL resourcesLink = new URL(Util.MINECRAFT_RESOURCES);
                    DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
                    DocumentBuilder build = builder.newDocumentBuilder();
                    Document doc = build.parse(resourcesLink.openStream());
                    NodeList list = doc.getElementsByTagName("Contents");
                    for (int i = 0; i < list.getLength(); i++) {
                        Node node = list.item(i);
                        if (node.getNodeType() == 1){
                            Element element = (Element)node;
                            String key = element.getElementsByTagName("Key").item(0).getChildNodes()
                                    .item(0).getNodeValue();
                            String etag = element.getElementsByTagName("ETag") != null ? 
                                    element.getElementsByTagName("ETag").item(0).getChildNodes().item(0)
                                    .getNodeValue() : "-";
                            long size = Long.parseLong(element.getElementsByTagName("Size").item(0)
                                    .getChildNodes().item(0).getNodeValue());
                            if (size > 0L){
                                File file = new File(asset, key);
                                if (etag.length() > 1){
                                    etag = etag.substring(1, etag.length() - 1);
                                    if (file.isFile() && file.length() == size){
                                        String localMD5 = Util.getMD5(file);
                                        if (localMD5.equals(etag)) continue;
                                    }
                                }
                                URL url = new URL(Util.MINECRAFT_RESOURCES + key);
                                Downloader download = new Downloader(url, resources, file, true, false);
                                download.setName(key.contains("/") ? url.getFile().substring(url
                                        .getFile().lastIndexOf("/") + 1) : key);
                                download.setSize(size);
                                assets.add(download);
                                gui.getConsoleTab().println("Adding " + key);
                            }
                        }
                    }
                    resources.addListJobs(assets);
                    resources.startJob();
                } catch (Exception e) {
                    MessageControl.severeExceptionMessage(2, e, "Failed to download minecraft"
                            + " resources.");
                }
            }
        });
        try {
            Gson tmp = new Gson();
            versionList = tmp.fromJson(Util.requestGetMethod(Util.MINECRAFT_DOWNLOAD_BASE + 
                    "versions/versions.json"), VersionList.class);
            modpackList = tmp.fromJson(Util.requestGetMethod(Util.MODPACKS), ModPackList.class);
        } catch (Exception e) {
            gui.getConsoleTab().printErr(e, "Failed to obtain Minecraft version list/Modpack "
                    + "version list", 3, this.getClass());
        }
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                //Looks if there are profiles.
                if (!config.getProfiles().isEmpty()){
                    for (Profile profile : config.getProfiles()) {
                        //Checks if the profile is premium.
                        if (profile.isPremium()){
                            gui.getConsoleTab().println("Authenticating " + profile.getUsername() + 
                                    "...");
                            try {
                                //Try to authenticate with Yggdrassil method.
                                Authenticator.refresh(profile);
                                gui.getConsoleTab().println(profile.getUsername() + " authenticated.");
                            } catch (Exception ex) {
                                //If failed, the user has to select if relog, or not.
                                gui.getConsoleTab().printErr(ex, "Profile " + profile.getProfilename() 
                                        + " failed to authenticate.", 1, this.getClass());
                                int i = MessageControl.showConfirmDialog("Do you want to login again?", 
                                        "Failed to refresh", 0, 3);
                                try {
                                    //User selects log in.
                                    if (i == 0){
                                        String password = MessageControl.showInputPassword("Input your "
                                                + "password:");
                                        if (password == null || password.equals("")) throw new Exception();
                                        gui.getConsoleTab().println("Login...");
                                        profile.setAccessToken(Authenticator.login(profile.getUsername(),
                                                password, profile.getPath()).getAccessToken());
                                        gui.getConsoleTab().println("Authenticated.");
                                    //User selects no.
                                    } else throw new Exception("BaseProfile");
                                } catch (Exception e) {
                                    /*
                                     * If the login fails or the user selects no, the profile is
                                     * converted into a basic account.
                                     */
                                    e.printStackTrace();
                                    if (!e.toString().contains("BaseProfile")) 
                                        MessageControl.showErrorMessage("Failed to log in. Converting"
                                            + " your\naccount into basic.", "Login failed");
                                    profile.setAccessToken(null);
                                }
                            }
                        }
                    }
                } else gui.getConsoleTab().println("No profiles found in the list");
            }
        });
        for (Profile profile : config.getProfiles()) {
            for (Instances instance : profile.getList()) {
                if (!instance.getPath().exists()) profile.removeInstance(instance);
            }
        }
    }
    
    /**
     * Initializes the main frame removing all its containers.
     */
    private void initFrame(JFrame frame){
        JFrame recreated = new JFrame();
        recreated.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        recreated.setResizable(false);
        recreated.setPreferredSize(new Dimension(636, 395));
        recreated.setTitle("EscorbutoLauncher " + version_program);
        recreated.setIconImage(new ImageIcon(this.getClass().getResource("/elr/resources/5547.png"))
                .getImage());
        gui = new MainGui(recreated);
        recreated.add(gui);
        recreated.pack();
        frame.dispose();
        recreated.setLocationRelativeTo(null);
        recreated.setVisible(true);
    }
    
    /**
     * Private method used to check system JRE.
     */
    private void checkJRE(String[] args, JProgressBar prog, PrintStream stream){
        //Read JVM architecture and warn to the user if it's 32 bit type.
        String architecture = System.getProperty("sun.arch.data.model");
        if (architecture.equals("32")){
            MessageControl.showInfoMessage("It seems that you are running a 32-bit JVM architecture. "
                    + "If you want to\nexecute Minecraft with more than 1Gb, please install the 64-bit"
                    + " JVM version.", "32-bit JVM architecture detected!");
        }
        //Creates the root file into the machine.
        File root = new File(args[1]);
        if (!root.exists()) root.mkdirs();
        //Checks if is installed the same Java version.
        int jre = Integer.parseInt(System.getProperty("java.version").split("_")[1]);
        int version = Integer.parseInt(System.getProperty("java.version").split("_")[0].split("\\.")[1]);
        if (jre != java_release || version != java_version){
            System.out.println("Applying jre");
            stream.println("Applying jre...");
            File jreOutput = new File(root, "jre_" + java_version + "." + java_release), customJRE = null;
            try {
                if (!jreOutput.exists() || jreOutput.listFiles().length < 8){
                    try {
                        /**
                         * Length less than 8 'cause the JRE with less files has 8.
                         */
                        if (jreOutput.exists()) IO.deleteDirectory(jreOutput); //Deletes if it's corrupted.
                        Properties jres = new Properties();
                        jres.loadFromXML(new URL("https://dl.dropbox.com/s/13ojjgdjra49ihn/Jres.xml?dl=1")
                                .openStream());
                        String jreLink;
                        if (args[0].equals("macosx")) jreLink = jres.getProperty("macosx");
                        else jreLink = jres.getProperty(args[0] + "_" + architecture);
                        DownloadJob job = new DownloadJob("JRE job", prog);
                        Downloader download = new Downloader(new URL(jreLink), job, root, false, true);
                        job.addJob(download);
                        List<Future<File>> targets = job.startJob();
                        File temp = targets.get(0).get();
                        stream.println("Decoding jre...");
                        customJRE = Compressor.secureDecompression(temp, null, true);
                        temp.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                        IO.deleteDirectory(jreOutput);
                        throw new RuntimeException(e);
                    }
                }
                List<String> arg = new ArrayList<>();
                arg.add(jreOutput.getAbsolutePath() + File.separator + "bin" + File.separator + 
                        (args[0].equals("windows") ? "javaw" : "java"));
                arg.add("-jar");
                arg.add(args[2]);
                if (customJRE != null) customJRE.renameTo(jreOutput);
                Process start = new ProcessBuilder(arg).start();
                Thread.sleep(2000);
                start.exitValue();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof IllegalThreadStateException){
                    System.exit(0);
                }
                IO.deleteDirectory(jreOutput);
                int i = MessageControl.showConfirmDialog("If you continue executing the program with"
                        + " java " + version + " version " + jre + "\nyou can suffer a malfunction."
                        + "\nDo you want to continue?", "WARNING", 0, 2);
                if (i != 0) System.exit(-1);
            }
        }
    }
    
    private static class JSonReader extends ObjectInputStream{
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private BufferedReader reader;
        
        public JSonReader(InputStream stream) throws FileNotFoundException, IOException{
            super();
            reader = new BufferedReader(new InputStreamReader(stream));
        }
        
        @Override
        public Object readObjectOverride(){
            return gson.fromJson(reader, Configuration.class);
        }
        
        @Override
        public void close() throws IOException{
            reader.close();
        }
    }
    
    private static class JSonWriter extends ObjectOutputStream{
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private PrintWriter writer;
        
        public JSonWriter(OutputStream output) throws IOException{
            super();
            writer = new PrintWriter(output);
        }
        
        @Override
        public void writeObjectOverride(Object obj){
            gson.toJson(obj, writer);
        }
        
        @Override
        public void close(){
            writer.close();
        }
        
        @Override
        public void flush(){
            writer.flush();
        }
    }
    
    private static class Shutdown extends Thread{
        private File decrypt, crypt;
        private FileChannel channel;
        private FileLock lock;
        private ObjectOutputStream output;
        
        public Shutdown(File dec, File cry) throws Exception{
            super("Shutdown");
            decrypt = dec;
            crypt = cry;
            FileOutputStream out = new FileOutputStream(dec);
            //Trying JSON writer...
            try {
                output = new JSonWriter(out);
            } catch (Exception e) {
                e.printStackTrace();
                //JSON failed. Trying with default mode...
                output = new ObjectOutputStream(out);
            }
            channel = out.getChannel();
            lock = channel.lock();
        }
        
        public void saveConfig(){
            try {
                channel.position(0);
                output.writeObject(config);
                output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public void release() throws Exception{
            if (lock != null) lock.release();
            if (channel != null) channel.close();
            if (output != null) output.close();
        }
        
        @Override
        public void run(){
            ThreadPool pool = ThreadPool.getInstance();
            pool.shutdown();
            try {
                saveConfig();
                release();
                crypt.delete();
                Compressor.cryptedCompression(decrypt, Compressor.CompressionLevel.NOCOMPRESS, null);
                if (!pool.isShutdown()) pool.shutdownNow();
                unique.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                decrypt.delete();
            }
        }
    }
}
