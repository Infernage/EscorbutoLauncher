/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR;

import MLR.gui.*;
import MLR.installer.*;
import MLR.launcher.*;
import MLR.launcher.outdated.Outdated;
import MLR.launcher.updater.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author Infernage
 */
public class InnerApi {

    public static String OS;
    public static Properties configuration;
    public static boolean debug = false;
    private static Map<String, String> config = new HashMap<String, String>(); //Internal MLR's configuration

    /**
     * This sets the language of the Desktop
     */
    public static void setLanguage() {
        config.put("es", "Escritorio");
        config.put("en", "Desktop");
    }
    
    public static String getPropertyC(String key){
        return config.get(key);
    }
    
    /**
     * This obtains the language of the Desktop
     * @return The language of the Desktop
     */
    public static String getLanguage(){
        if (OS.equals("linux") && System.getProperty("user.language").toLowerCase().equals("es")){
            return config.get("es");
        } else if (OS.equals("linux") && System.getProperty("user.language").toLowerCase().equals("en")){
            return config.get("en");
        } else if (OS.equals("windows")){
            return config.get("en");
        }
        return config.get("en");
    }
    
    /**
     * Methods to write a message to the Console
     * @param msg The message
     */
    public static void writeMSG(String msg){
        Init.pw.print(msg);
        Init.pw.flush();
    }
    /**
     * Methods to write a message to the Console
     * @param msg The message
     */
    public static void writeMSGln(String msg){
        Init.pw.println(msg);
        Init.pw.flush();
    }
    /**
     * Methods to write a message to the Console
     * @param msg The message
     */
    public static void writeMSG1ln(String msg){
        Init.pw1.println(msg);
        Init.pw1.flush();
    }
    /**
     * Methods to write a message to the Console
     * @param msg The message
     */
    public static void writeMSG1(String msg){
        Init.pw1.print(msg);
        Init.pw1.flush();
    }
    
    /**
     * Stops the streams of the console
     */
    public static void stopAll(){
        Init.pw.close();
        Init.pw1.close();
    }

    /**
     * This method asigns the OS name
     */
    public static void setOS() {
        String aux = System.getProperty("os.name");
        aux = aux.toLowerCase();
        if (aux.contains("win")) {
            OS = "windows";
        } else if (aux.contains("lin") || aux.contains("uni")) {
            OS = "linux";
        } else {
            OS = "NONSUPP";
            System.err.println("[ERROR]Found operative system not supported! Exiting system!");
            JOptionPane.showMessageDialog(null, "Your operative system is not supported! Only are supported"
                    + " Windows and Linux.\nOperative system in this computer: " + InnerApi.OS + "\nIf you are running one "
                    + "of these operative systems, contact with me: Infernage");
            Debug de = new Debug(null, true);
            de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            de.setLocationRelativeTo(null);
            de.setVisible(true);
            System.exit(9);
        }
        System.out.println("OS: " + OS);
    }

    /**
     * This method gets the home path on Windows or Linux.
     *
     * @param name The path name starting from default. It's possible to choose
     * which path name can be returned.
     * @return <ul> <li> {@code null} if the OS is not supported. <li> The
     * default path if {
     * @param name} is {@code null}. <li> The path name starting from default if {
     * @param name} it's not {@code null}. </ul>
     */
    private static String path(String key, String path) {
        if (path != null){
            return config.get(key) + File.separator + path;
        } else{
            return config.get(key);
        }
    }

    /**
     * This method obtains the datapath of MLR
     * @return The datapath of MLR
     */
    public static String dataPath(){
        if (OS.equals("windows")){
            return System.getenv("APPDATA");
        } else if (OS.equals("linux")){
            return System.getProperty("user.home");
        }
        return null;
    }

    /**
     * This method shows an error and exit with an error value if something is
     * wrong.
     *
     * @param ex The exception to be stack.
     * @param msg The message to show.
     * @param num The result of system num{ 1: Error al inicializar Minecraft.
     * 2: Error al comprobar los datos en el método Play() 3: Error en el
     * inicializador del launcher. 4: Error al actualizar el MineLauncher. 9: SO no
     * soportado. }
     */
    public static void fatalException(Exception ex, String msg, int num) {
        JOptionPane.showMessageDialog(null, msg, "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
        Init.error.setError(ex);
        Debug de = new Debug(null, true);
        de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        de.setLocationRelativeTo(null);
        de.setVisible(true);
        System.exit(num);
    }

    /**
     * This method shows an error message if something is wrong. You can also
     * send an error report.
     *
     * @param ex The exception to be stack.
     * @param msg The message to show.
     */
    public static void exception(Exception ex, String msg) {
        int i = JOptionPane.showConfirmDialog(null, msg + "\n¿Quieres enviar el error?",
                "Oops! Ha ocurrido un error.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        Init.error.setError(ex);
        if (i == 0) {
            Debug de = new Debug(null, true);
            de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            de.setLocationRelativeTo(null);
            de.setVisible(true);
        }
    }

    /**
     * This method returns the instance selected of minecraft.
     *
     * @return The instance selected.
     */
    public static String getInstance() {
        File config = new File(InnerApi.Files.Instance());
        BufferedReader bf = null;
        String instance = null;
        try {
            bf = new BufferedReader(new FileReader(config));
            instance = bf.readLine();
        } catch (Exception ex) {
            exception(ex, "Error al obtener la instancia.");
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException ex) {
                }
            }
        }
        return instance;
    }

    /**
     * This method return the avaliable name of the instance
     * @param instance The name to check
     * @return The name checked
     */
    public static String checkInstance(String instance) {
        File inst = new File(Directory.instance(instance));
        if (inst.exists() && (inst.list().length > 0)) {
            if (instance.contains("_")) {
                String[] tmp = instance.split("_");
                int i = Integer.parseInt(tmp[1]);
                i++;
                instance = checkInstance(tmp[0] + "_" + i);
            } else {
                if (instance == null) {
                    instance = checkInstance("Default_1");
                } else {
                    instance = checkInstance(instance + "_1");
                }
            }
        } else if (inst.exists() && (inst.list().length < 1)) {
            inst.delete();
        }
        return instance;
    }
    
    /**
     * This class initialite the program.
     */
    public static class Init {
        private static PrintWriter pw, pw1;
        public final static String title = "MineLauncher";
        public static String version = "V5.2.1";
        public static Map<String, Thread> hilos;
        public static Collector collector;
        /**
         * ****************************
         *********GUI Package*********
         ****************************
         */
        public static About info;
        public static Console consola;
        public static Debug err;
        public static FAQ faq;
        public static MultiMine multiGUI;
        public static Options opt;
        public static Splash image;
        public static Gui mainGUI;
        /**
         * ****************************
         ********Login Package********
         ****************************
         */
        public static AES crypt;
        public static Background background;
        public static CHLG changelog;
        public static VersionChecker client;
        public static ErrStream error;
        public static Logger log;
        public static Systray sys;
        public static Updater update;
        /**
         * ****************************
         ******Installer Package******
         ****************************
         */
        public static Uninstaller unwork;
        public static Installer work;

        /**
         * This initialite all program classes
         */
        public static void start() {
            System.out.println("Loading configurations... ");
            setOS();
            loadConfig();
            loadPaths();
            loadDirs();
            System.out.println("Loading files... ");
            preLoad();
            setConnection();
            System.out.println("Load done!\nStarting classes...");
            try {
                load();
            } catch (Exception ex) {
                System.out.println("Start failed!");
                fatalException(ex, "Error al inicializar los archivos.", 3);
            }
            System.out.println("Classes started!\nChecking resource files");
            try {
                checkSources();
                System.out.println("Files checked");
            } catch (Exception ex) {
                System.out.println("Check failed");
                exception(ex, "Error al comprobar los ficheros fuentes.");
            }
            boolean quizb = false;
            try {
                quizb = searchQuiz();
            } catch (Exception e) {
                exception(e, e.getMessage());
            }
            if (InnerApi.debug) {
                System.out.println("[->Splash finalized<-]");
            }
            image.exit();
            if (quizb){
                System.out.println("Launching Quiz GUI");
                Quiz quiz = new Quiz();
                quiz.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                quiz.setLocationRelativeTo(null);
                quiz.setVisible(true);
            } else{
                System.out.println("Launching main GUI");
                mainGUI.setVisible(true);
            }
        }
        
        private static boolean searchQuiz() throws Exception{
            File quest = new File(config.get("MLR.data") + File.separator + "Quiz.qzx");
            if (!quest.exists()){
                quest.createNewFile();
            }
            BufferedReader bf = new BufferedReader(new FileReader(quest));
            String tmp = bf.readLine();
            bf.close();
            if (tmp == null){
                int i = JOptionPane.showConfirmDialog(null, "¿Quieres realizar la encuesta sobre las mods del server?", "Mods quest", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (i == 0){
                    return true;
                } else{
                    int j = JOptionPane.showConfirmDialog(null, "¿Quieres que se te vuelva a preguntar al volver a iniciar el launcher?", null, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (j != 0){
                        PrintWriter pw = new PrintWriter(quest);
                        pw.print("Done!");
                        pw.close();
                    }
                    return false;
                }
            } else{
                if (!tmp.equals("Done!")){
                    int i = JOptionPane.showConfirmDialog(null, "¿Quieres realizar la encuesta sobre las mods del server?", "Mods quest", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (i == 0){
                        return true;
                    }
                }
                return false;
            }
        }
        
        private static void loadDirs(){
            File tmp = new File(Directory.instance(null));
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            tmp = new File(Directory.logger(null));
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            if (debug) {
                System.out.println("[->Created directories<-]");
            }
            File prop = new File(InnerApi.Files.Instance());
            if (prop.exists()){
                try {
                    BufferedReader bf = new BufferedReader(new FileReader(prop));
                    String inst = bf.readLine();
                    if (inst != null){
                        InnerApi.configuration.setProperty("user.dir", InnerApi.Directory.instance(inst + 
                                File.separator + InnerApi.Directory.MINECRAFT));
                        InnerApi.configuration.setProperty("user.home", InnerApi.Directory.instance(inst));
                    }
                    bf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                try {
                    prop.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            File log = new File(InnerApi.Directory.data("MLR_log_0.log"));
            if (!log.exists()){
                try {
                    log.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                log.delete();
                try {
                    log.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                pw = new PrintWriter(new FileWriter(log, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            File log1 = new File(InnerApi.Directory.data("MLR_log_1.log"));
            if (!log1.exists()){
                try {
                    log1.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                log1.delete();
                try {
                    log1.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                pw1 = new PrintWriter(new FileWriter(log1, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private static String getConfigPath(){
            if (OS.equals("windows")){
                return System.getenv("APPDATA") + "\\Data";
            } else if (OS.equals("linux")){
                return System.getProperty("user.home") + "/.Data";
            }
            return null;
        }

        private static void setConnection() {
            System.out.println("Configurating connection...");
            if (configuration.getProperty("systemProxy").equals("true")) {
                System.setProperty("useSystemProxies", "true");
            }
            System.out.print("Patching connection... ");
            if (debug) {
                System.out.println("[->Patching with preferIPv4Stack=true<-]");
            }
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.out.println("OK");
        }
        
        private static void createConfig(File cfg) throws Exception {
            configuration.load(new FileInputStream(cfg));
            configuration.setProperty("maxRAM", "512");
            configuration.setProperty("systemProxy", "true");
            configuration.setProperty("installPath", "default");
            configuration.setProperty("defaultPath", "true");
            configuration.setProperty("consoleActivated", "true");
            configuration.setProperty("consoleMinimized", "true");
            configuration.setProperty("rememberUser", "false");
            configuration.setProperty("rememberPassword", "false");
            FileOutputStream fos = new FileOutputStream(cfg);
            configuration.store(fos, "Default config");
            fos.close();
        }
        
        private static void loadPaths(){
            String str = configuration.getProperty("installPath");
            String configPath = getConfigPath();
            config.put("MLR.data", configPath);
            if (str.equals("default")){
                str = configPath + File.separator + "Instances";
            }
            config.put("MLR.instances", str);
            str = configPath + File.separator + "MLR.cfg";
            config.put("MLR.config", str);
            str = configPath + File.separator + "Logger";
            config.put("MLR.logger", str);
            configPath = str + File.separator + "RUN.jar";
            config.put("MLR.run", configPath);
            System.out.println("Config loaded");
        }
        
        /**
         * Method used of Options to reload the installation path
         */
        public static void reloadPath(){
            String str = configuration.getProperty("installPath");
            if (str.equals("default")){
                str = getConfigPath() + File.separator + "Instances";
            }
            config.put("MLR.instances", str);
        }

        private static void loadConfig() {
            configuration = new Properties();
            File data = new File(getConfigPath());
            if (!data.exists()){
                data.mkdirs();
            }
            File cfg = new File(getConfigPath() + File.separator + "MLR.cfg");
            if (debug) {
                System.out.println("[->Checking config file<-]");
            }
            if (!cfg.exists()) {
                try {
                    cfg.createNewFile();
                    createConfig(cfg);
                } catch (Exception e) {
                }
            } else {
                try {
                    configuration.load(new FileInputStream(cfg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private static void preLoad() {
            if (debug) {
                System.out.println("[->Created new ThreadMap<-]");
            }
            hilos = new HashMap<String, Thread>();
            if (debug) {
                System.out.println("[->Creating Splash<-]");
            }
            image = new Splash();
            Thread t = new Thread(image, "Splash");
            t.setDaemon(true);
            t.start();
            hilos.put("Splash", t);
            if (debug) {
                System.out.println("[->Splash created<-]");
            }
            System.out.println("Setting OutputStreams");
            consola = new Console();
            if (configuration.getProperty("consoleActivated").equals("true")) {
                consola.setVisible(true);
            }
            try {
                Thread.sleep(300);
            } catch (Exception ex) {
            }
            System.out.println("Console started");
            error = new ErrStream();
            error.setDaemon(true);
            error.start();
            hilos.put("Errors", error);
            System.out.println("Error output created");
            collector = new Collector("Collector");
            collector.setDaemon(true);
            collector.start();
            crypt = new AES(Files.PSS);
            if (debug) {
                System.out.println("[->Created AES crypter<-]");
            }
            background = new Background();
            changelog = new CHLG();
            client = new VersionChecker();
            if (debug) {
                System.out.println("[->Created client<-]");
            }
            log = new Logger();
            if (debug) {
                System.out.println("[->Created logger<-]");
            }
            update = new Updater();
            if (debug) {
                System.out.println("[->Created updater<-]");
            }
            work = new Installer();
            unwork = new Uninstaller();
            mainGUI = new Gui();
            opt = new Options(mainGUI, true);
            multiGUI = new MultiMine();
            faq = new FAQ(mainGUI, true);
            err = new Debug(null, true);
            info = new About(mainGUI, true);
            if (debug) {
                System.out.println("[->Created installer and GUIs<-]");
            }
        }

        private static void load() throws Exception {
            setLanguage();
            if (debug) {
                System.out.println("[->Language set<-]");
            }
            mainGUI.init();
            mainGUI.setIconImage(new ImageIcon(new Init().getClass().getResource("/MLR/resources/5547.png")).getImage());
            mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainGUI.setTitle(title + " " + version);
            mainGUI.setLocationRelativeTo(null);
            mainGUI.pack();
            System.out.println("GUI created");
            multiGUI.setLocationRelativeTo(null);
            multiGUI.pack();
            System.out.println("Installer GUI created");
            System.out.println("Checking for outdated versions...");
            Outdated.startEngines();
        }

        private static void checkSources() throws Exception {
            String current = URLDecoder.decode(new File(InnerApi.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath()).getCanonicalPath(), "UTF-8");
            File last = new File(current);
            File tmp = new File(Files.jar());
            if (InnerApi.debug) {
                System.out.println("[->Checking jar file<-]");
            }
            if (!tmp.exists() && last.exists()) {
                System.out.println("Exporting resource file: " + last.getName());
                IO.copy(last, tmp);
            } else if (tmp.exists() && last.exists()) {
                if (!Installer.check(last, tmp)) {
                    System.out.println("Exporting resource file: " + last.getName());
                    tmp.delete();
                    IO.copy(last, tmp);
                }
            }
            System.gc();
            if (InnerApi.debug) {
                System.out.println("[->Check complete<-]");
            }
        }
    }

    public static class Systray {

        public static void suspend(Gui see) {
            Iterator<String> it = Init.hilos.keySet().iterator();
            String temp;
            while (it.hasNext()) {
                temp = it.next();
                if (!temp.equals("Systray")) {
                    Thread thread = Init.hilos.get(temp);
                    try {
                        if (thread.isAlive()) {
                            thread.wait();
                        } else {
                            System.out.println("Thread " + thread.getName() + " is not active!");
                        }
                    } catch (InterruptedException ex) {
                        Init.error.setError(ex);
                    }
                }
            }
        }
    }

    /**
     * This class implements all IO methods used.
     */
    public static class IO {

        /**
         * This method deletes a file or a directory
         *
         * @param fich The file or directory to delete
         */
        public static void borrarFichero(File fich) {
            File[] ficheros = fich.listFiles();
            for (int x = 0; x < ficheros.length; x++) {
                if (ficheros[x].isDirectory()) {
                    borrarFichero(ficheros[x]);
                }
                ficheros[x].delete();
            }
        }

        /**
         * This method copies a source directory to a destiny directory
         *
         * @param srcDir The source directory
         * @param dstDir The destiny directory
         * @throws IOException
         */
        public static void copyDirectory(File srcDir, File dstDir) throws IOException {
            if (srcDir.isDirectory()) {
                if (!dstDir.exists()) {
                    dstDir.mkdir();
                }
                String[] children = srcDir.list();
                for (int i = 0; i < children.length; i++) {
                    copyDirectory(new File(srcDir, children[i]),
                            new File(dstDir, children[i]));
                }
            } else {
                copy(srcDir, dstDir);
            }
        }

        /**
         * This method copies a source file to a destiny file
         *
         * @param src The source file
         * @param dst The destiny file
         * @throws IOException
         */
        public static void copy(File src, File dst) throws IOException {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
            copy(input, output);
            input.close();
            output.close();
        }

        /**
         * This method copies a source inputstream to a destiny outputstream
         *
         * @param input The source inputstream
         * @param output The destiny outputstream
         */
        public static void copy(BufferedInputStream input, BufferedOutputStream output) throws IOException {
            byte[] buffer = new byte[input.available()];
            input.read(buffer, 0, buffer.length);
            output.write(buffer);
        }

        /**
         * This method installs minecraft
         *
         * @param eti The label state
         * @param source The source file
         * @param destiny The destination file
         * @throws IOException
         */
        public static void installation(JLabel eti, File source, File destiny) throws IOException {
            if (source.isDirectory()) {
                if (!destiny.exists()) {
                    destiny.mkdirs();
                }
                String[] children = source.list();
                for (int i = 0; i < children.length; i++) {
                    installation(eti, new File(source, children[i]),
                            new File(destiny, children[i]));
                }
            } else {
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(source));
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(destiny));
                eti.setText("Extrayendo en " + destiny.getAbsolutePath());
                System.out.println("Extracting " + destiny.getAbsolutePath());
                byte[] buffer = new byte[input.available()];
                input.read(buffer, 0, buffer.length);
                output.write(buffer);
                input.close();
                output.close();
            }
        }
    }

    /**
     * This class returns all directories used.
     */
    public static class Directory {

        /**
         * This gets the name of directory and file of installation
         */
        public static String INST = "inst";

        /**
         * This gets the directory name of data files stored in this PC
         */
        public static String data(String path) {
            return path("MLR.data", path);
        }
        
        /**
         * This gets the directory name of minecraft
         */
        public static String MINECRAFT = ".minecraft";
        
        /**
         * This gets the directory name used to store all jars used by this
         */
        public static String logger(String path){
            return path("MLR.logger", path);
        }
        
        /**
         * This gets the directory name used to store all minecraft instances
         */
        public static String instance(String path){
            return path("MLR.instances", path);
        }
    }

    /**
     * This class return all files used.
     */
    public static class Files {

        /**
         * This method gets the file name of the instance config.
         *
         * @return The absolute path.
         */
        public static String Instance() {
            return Directory.instance("properties.prop");
        }

        /**
         * This gets the jar name.
         *
         * @return The absolute path.
         */
        public static String jar() {
            return config.get("MLR.run");
        }

        /**
         * This gets the password
         */
        private static String PSS = "MineLauncher";

        /**
         * This gets the configuration file name of the instance.
         */
        public static String INSTCONFIG = "Instance.config";

        /**
         * This gets the file name which set the last login.
         *
         * @return The absolute path.
         */
        public static String login() {
            return Directory.data("lstlg.data");
        }
        
        /**
         * This gets the configuration file.
         * @return The absolute path.
         */
        public static String config(){
            return config.get("MLR.config");
        }
    }

    public static class Downloader{
        private URL url;
        private String fileName;
        private String destPath;
        private JProgressBar progress;
        public Downloader(String url){
            try {
                this.url = new URL(url);
            } catch (Exception e) {
            }
        }
        public void load(String name, String path){
            fileName = name;
            destPath = path;
        }
        public void load(String path){
            String name = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
            if (name.contains("?dl=1")){
                name = name.replace("?dl=1", "");
            } else if (name.contains("/") || name.contains("\\") || 
                        name.contains(":") || name.contains("*") ||
                        name.contains("?") || name.contains("\"") ||
                        name.contains("<") || name.contains(">") ||
                        name.contains("|")){
                name = "Download" + name.substring(path.lastIndexOf('.') + 1);
            }
            load(name, path);
        }
        public void load(){
            load(InnerApi.Directory.data("Update"));
        }
        public void loadBar(JProgressBar prog){
            if (prog != null){
                progress = prog;
                progress.setVisible(true);
                progress.setStringPainted(true);
            }
        }
        public File destinyFile(){ return new File(destPath + File.separator + fileName); }
        public void start(){
            File path = new File(destPath);
            path.mkdirs();
            File dst = new File(destPath + File.separator + fileName);
            Progress prog = new Progress(dst, null, progress);
            prog.setDaemon(true);
            System.out.println("Starting protocol");
            InputStream input = null;
            RandomAccessFile file = null;
            try {
                System.out.println("Setting request properties");
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setRequestProperty("Range", "bytes=" + 0 + "-");
                connect.connect();
                file = new RandomAccessFile(destPath + File.separator + fileName, "rw");
                file.seek(0);
                System.out.println("Getting stream");
                input = connect.getInputStream();
                int size = connect.getContentLength();
                prog.setSize(size);
                prog.start();
                System.out.println("Transfering buffer");
                byte[] buffer = new byte[size];
                int read = input.read(buffer);
                while (read > 0){
                    file.write(buffer, 0, read);
                    read = input.read(buffer);
                }
                System.out.println("Transfer done");
            } catch (Exception ex) {
                InnerApi.exception(ex, "Transfer failed!");
                dst.delete();
            } finally{
                if (input != null){
                    try {
                        input.close();
                    } catch (Exception e) {
                    }
                }
                if (file != null){
                    try {
                        file.close();
                    } catch (Exception e) {
                    }
                }
                prog.out();
            }
        }
    }
    
    public static void main(String[] args) {
        //SITE FOR TESTING ONLY
    }
}
