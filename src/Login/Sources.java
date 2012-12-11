/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import Debugger.Parameters;
import Installer.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Reed
 */
public class Sources {
    public static String OS;
    private static String Wtmp = System.getProperty("user.home") + "\\AppData\\Roaming",
            Ltmp = System.getProperty("user.home");
    public static Properties Prop;
    public static boolean debug = false;
    public static void setLanguage(){
        Prop.setProperty("es", "Escritorio");
        Prop.setProperty("en", "Desktop");
    }
    /** 
     * This method asigns the OS name
     */ 
    public static void setOS(){
        String aux = System.getProperty("os.name");
        aux = aux.toLowerCase();
        if (aux.contains("win")){
            OS = "windows";
        } else if (aux.contains("lin")){
            OS = "linux";
        } else{
            OS = "NONSUPP";
        }
    }
    /**
     * This method gets the default path on Windows or Linux.
     * @param name The path name starting from default. It's possible to choose which path name can be
     *  returned.
     * @return 
     * <ul>
     * <li> {@code null} if the OS is not supported.
     * <li> The default path if {@param name} is {@code null}.
     * <li> The path name starting from default if {@param name} it's not {@code null}.
     * </ul>
     */
    public static String path(String name){
        if (OS.equals("windows") && (name != null)){
            return Wtmp + "\\" + name;
        } else if (OS.equals("linux") && (name != null)){
            return Ltmp + "/" + name;
        } else if (OS.equals("windows") && (name == null)){
            return Wtmp;
        } else if (OS.equals("linux") && (name == null)){
            return Ltmp;
        }
        return null;
    }
    /**
     * This method showes an error and exit with an error value if something is wrong.
     * @param ex The exception to be stack.
     * @param msg The message to show.
     * @param num The result of system
     * num{
     *  1: Error al inicializar Minecraft.
     *  2: Error al comprobar los datos en el método Play()
     *  3: Error en el inicializador del login.
     *  4: Error al actualizar el MineLogin.
     *  9: SO no soportado.
     *  10: Error al registrarse en el servidor.
     * }
     */
    public static void fatalException(Exception ex, String msg, int num){
        JOptionPane.showMessageDialog(null, msg, "Oops! Hubo un gran problema!", JOptionPane.ERROR_MESSAGE);
        Init.error.setError(ex);
        Debug de = new Debug(null, true);
        de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        de.setLocationRelativeTo(null);
        de.setVisible(true);
        System.exit(num);
    }
    /**
     * This method showes an error message if something is wrong. You can also send an error report.
     * @param ex The exception to be stack.
     * @param msg The message to show.
     */
    public static void exception(Exception ex, String msg){
        int i = JOptionPane.showConfirmDialog(null, msg + "\n¿Quieres enviar el error?", 
                "Oops! Ha ocurrido un error.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        Init.error.setError(ex);
        if (i == 0){
            Debug de = new Debug(null, true);
            de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            de.setLocationRelativeTo(null);
            de.setVisible(true);
        }
    }
    /**
     * This method returns the instance selected of minecraft.
     * @return The instance selected.
     */
    public static String getInstance(){
        File config = new File(Sources.Files.Instance(true));
        BufferedReader bf = null;
        String instance = null;
        try {
            bf = new BufferedReader(new FileReader(config));
            instance = bf.readLine();
        } catch (Exception ex) {
            exception(ex, "Error al obtener la instancia.");
        } finally{
            if (bf != null){
                try {
                    bf.close();
                } catch (IOException ex) {
                }
            }
        }
        return instance;
    }
    
    public static String checkInstance(String instance){
        File inst = new File(Prop.getProperty("user.instance") + File.separator + instance);
        if (inst.exists() && (inst.list().length > 0)){
            if (instance.contains("_")){
                String[] tmp = instance.split("_");
                int i = Integer.parseInt(tmp[1]);
                i++;
                instance = checkInstance(tmp[0] + "_" + i);
            } else{
                if (instance == null){
                    instance = checkInstance("Default_1");
                } else{
                    instance = checkInstance(instance + "_1");
                }
            }
        } else if (inst.exists() && (inst.list().length < 1)){
            inst.delete();
        }
        return instance;
    }
    /**
     * This class initialite the program.
     */
    public static class Init{
        public final static String title = "MineLogin";
        public static String version = "V5.1.0";
        public static boolean online = true;
        public static Map<String, Thread> hilos;
        public static Map<String, List<Object>> garbage;
        public static Collector collector;
        
        /******************************
         ********Login  Package********
         *****************************/
        public static AES crypt;
        public static Acerca info;
        public static Background background;
        public static CHLG changelog;
        public static Cliente client;
        public static Console consola;
        public static Debug err;
        public static ErrStream error;
        public static FAQ faq;
        public static Logger log;
        public static PassConfirm changer;
        public static Splash image;
        public static Systray sys;
        public static Updater update;
        public static Ventanita vinit;
        public static Vista accountGUI;
        public static Vista2 mainGUI;
        
        /******************************
         ******Installer  Package******
         *****************************/
        public static Restore rest;
        public static Unworker unwork;
        public static MultiMine multiGUI;
        public static Worker work;
        /**
         * This initialite all program classes
         */
        public static void init(){
            System.out.println("Preinitializating... ");
            preLoad();
            setConnection();
            System.out.println("Preload done!\nLoading files...");
            try{
                load();
            } catch (Exception ex){
                System.out.println("Load failed. Exiting now");
                fatalException(ex, "Error al inicializar los archivos.", 3);
            }
            System.out.println("Files loaded\nChecking resource files");
            try{
                checkSources();
                System.out.println("Files checked");
            } catch (Exception ex){
                System.out.println("Check failed");
                exception(ex, "Error al comprobar los ficheros fuentes.");
            }
            if (Sources.debug) System.out.println("[->Splash finalized<-]");
            image.exit();
            System.out.println("Initializing main GUI");
            mainGUI.setVisible(true);
        }
        private static void setConnection(){
            System.out.println("Configurating connection...");
            try {
                System.out.print("Patching connection... ");
                if (debug) System.out.println("[->Patching with preferIPv4Stack=true<-]");
                System.setProperty("java.net.preferIPv4Stack", "true");
                System.out.println("OK");
                Connection.startConnection();
                if (!Connection.isConnected()){
                    throw new Exception("Connection time out");
                }
                File tmp = new File(Sources.path(Sources.Directory.DirData()) + File.separator + "queue.txt");
                tmp.deleteOnExit();
                String temp = null;
                BufferedReader bf = null;
                try{
                    Connection._download(tmp, "Base/queue.txt");
                    bf = new BufferedReader(new FileReader (tmp));
                    temp = bf.readLine();
                } catch (Exception ex){
                    error.setError(ex);
                    System.out.println("ERROR");
                    temp = null;
                } finally{
                    if (Connection.isConnected()){
                        Connection.closeConnection();
                    }
                    if (bf != null){
                        bf.close();
                    }
                }
                System.out.println("Connection result: " + temp);
                bf.close();
                tmp.delete();
                if (temp == null){
                    System.out.println("Trying with alternative form...");
                    Connection.failed = true;
                    Connection.download(tmp, "Base/queue.txt");
                    bf = new BufferedReader(new FileReader(tmp));
                    temp = bf.readLine();
                    System.out.println("Second connection result: " + temp);
                    bf.close();
                    tmp.delete();
                    if (temp == null){
                        throw new Exception("Connection result = null");
                    }
                }
            } catch (Exception ex) {
                online = false;
                System.out.println("ERROR\nConnection disabled");
                exception(ex, "Fallo en la conexión con el servidor. Desactiva el firewall si lo tienes activado.");
            }
        }
        private static void preLoad(){
            setOS();
            System.out.println("OS: " + OS);
            if (!Sources.OS.equals("windows") && !Sources.OS.equals("linux")){ //Si el OS no es soportado lanzamos error
                System.err.println("[ERROR]Found operative system not supported! Exiting system!");
                JOptionPane.showMessageDialog(null, "Your operative system is not supported! Only are supported"
                        + " Windows and Linux.\nOperative system in this computer: " + Sources.OS + "\nIf you are running one "
                        + "of these operative systems, contact with me: Infernage");
                Debug de = new Debug(null, true);
                de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                de.setLocationRelativeTo(null);
                de.setVisible(true);
                System.exit(9);
            }
            if (debug) System.out.println("[->Created new Properties<-]");
            Prop = new Properties();
            if (debug) System.out.println("[->Created new Map<-]");
            hilos = new HashMap<String, Thread>();
            garbage = new HashMap<String, List<Object>>();
            if (debug) System.out.println("[->Creating Splash<-]");
            image = new Splash();
            Thread t = new Thread(image, "Splash");
            t.setDaemon(true);
            t.start();
            hilos.put("Splash", t);
            if (debug) System.out.println("[->Splash created<-]");
            System.out.println("Setting OutputStreams");
            consola = new Console();
            consola.setVisible(true);
            try{
                Thread.sleep(1000);
            } catch (Exception ex){}
            System.out.println("Console created");
            error = new ErrStream();
            error.setDaemon(true);
            error.start();
            hilos.put("Errors", error);
            System.out.println("Error output created");
            File tmp = new File(path(Directory.DirData() + File.separator + Directory.DirInstance));
            if (!tmp.exists()){
                tmp.mkdirs();
            }
            tmp = new File(path(Directory.DirData() + File.separator + Directory.Dirfiles));
            if (!tmp.exists()){
                tmp.mkdirs();
            }
            if (debug) System.out.println("[->Created directories<-]");
            collector = new Collector("Collector");
            collector.setDaemon(true);
            collector.start();
            crypt = new AES(Files.pss);
            if (debug) System.out.println("[->Created AES crypter<-]");
            background = new Background();
            changelog = new CHLG();
            client = new Cliente();
            if (debug) System.out.println("[->Created client<-]");
            log = new Logger();
            if (debug) System.out.println("[->Created logger<-]");
            update = new Updater();
            if (debug) System.out.println("[->Created updater<-]");
            accountGUI = new Vista();
            vinit = new Ventanita(accountGUI, true);
            work = new Worker();
            unwork = new Unworker();
            rest = new Restore();
            mainGUI = new Vista2();
            multiGUI = new MultiMine(mainGUI, true);
            changer = new PassConfirm(mainGUI, true);
            faq = new FAQ(mainGUI, true);
            err = new Debug(null, true);
            info = new Acerca(mainGUI, true);
            if (debug) System.out.println("[->Created installer and GUIs<-]");
        }
        private static void load() throws Exception{
            setLanguage();
            if (debug) System.out.println("[->Language set<-]");
            Prop.setProperty("user.data", Sources.path(Sources.Directory.DirData()));
            if (debug) System.out.println("[->New property set<-]");
            mainGUI.init();
            mainGUI.setIconImage(new ImageIcon(new Init().getClass().getResource("/Resources/5547.png")).getImage());
            mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainGUI.setTitle(title + " " + version);
            mainGUI.setLocationRelativeTo(null);
            mainGUI.pack();
            System.out.println("GUI created");
            multiGUI.setLocationRelativeTo(null);
            multiGUI.pack();
            System.out.println("Installer GUI created");
            System.out.println("Checking for outdated versions...");
            Outdated.checkAll();
            Prop.setProperty("user.dir", Sources.path(Sources.Directory.DirData() + File.separator + 
                    Sources.Directory.DirInstance + File.separator + getInstance() + File.separator + 
                    ".minecraft"));
            Prop.setProperty("user.home", Sources.path(Sources.Directory.DirData() + File.separator + 
                    Sources.Directory.DirInstance + File.separator + getInstance()));
            Prop.setProperty("user.instance", Sources.path(Sources.Directory.DirData() + File.separator + 
                    Sources.Directory.DirInstance));
            System.out.println("New properties set");
        }
        private static void look(File tmp, BufferedInputStream src) throws Exception{
            BufferedInputStream dst = null;
            BufferedOutputStream out = null;
            try{
                if (src == null){
                    throw new Exception("BufferedInputStream can't be null");
                }
                boolean res = false;
                if (tmp.exists()){
                    dst = new BufferedInputStream(new FileInputStream(tmp));
                    res = Worker.isSame(src, dst);
                }
                if (!res || !tmp.exists()){
                    out = new BufferedOutputStream(new FileOutputStream(tmp));
                    tmp.delete();
                    IO.copy(src, out);
                }
            } finally{
                if (dst != null){
                    dst.close();
                }
                if (out != null){
                    out.close();
                }
                if (src != null){
                    src.close();
                }
            }
        }
        private static void checkSources() throws Exception{
            File tmp = new File(Files.jar(true));
            File last = new File(System.getProperty("user.dir") + File.separator + Files.jar(false));
            if (Sources.debug) System.out.println("[->Checking jar file<-]");
            if (!tmp.exists() && last.exists()){
                System.out.println("Exporting resource file: " + last.getName());
                IO.copy(last, tmp);
            } else if (tmp.exists() && last.exists()){
                if (!Worker.check(last, tmp)){
                    System.out.println("Exporting resource file: " + last.getName());
                    tmp.delete();
                    IO.copy(last, tmp);
                }
            }
            if (Sources.debug) System.out.println("[->Checking resource files<-]");
            BufferedInputStream in = null;
            last = null;
            tmp = new File(Files.access(true));
            in = new BufferedInputStream(new Init().getClass().getResourceAsStream("/Resources/" + Files.access(false)));
            look(tmp, in);
            String language = "";
            if (System.getProperty("user.language").toLowerCase().equals("es") && OS.contains("lin")){
                language = Prop.getProperty("es");
            } else if (System.getProperty("user.language").toLowerCase().equals("en") && OS.contains("lin")){
                language = Prop.getProperty("en");
            }
            tmp = new File(System.getProperty("user.home") + File.separator + language + File.separator + Files.access(false));
            in = new BufferedInputStream(new Init().getClass().getResourceAsStream("/Resources/" + Files.access(false)));
            look(tmp, in);
            tmp = new File(Files.temporal(true));
            in = new BufferedInputStream(new Init().getClass().getResourceAsStream("/Resources/" + Files.temporal(false)));
            look(tmp, in);
            tmp = null;
            in = null;
            System.gc();
            if (Sources.debug) System.out.println("[->Check complete<-]");
        }
        
    }
    public static class Systray{
        public static void suspend(Vista2 see){
            Iterator<String> it = Init.hilos.keySet().iterator();
            String temp;
            while(it.hasNext()){
                temp = it.next();
                if (!temp.equals("Systray")){
                    Thread thread = Init.hilos.get(temp);
                    try {
                        if (thread.isAlive()){
                            thread.wait();
                        } else{
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
    public static class IO{
       /**
        * This method deletes a file or a directory
        * @param fich The file or directory to delete
        */
        public static void borrarFichero (File fich){
            File[] ficheros = fich.listFiles();
            for (int x = 0; x < ficheros.length; x++){
                if (ficheros[x].isDirectory()){
                    borrarFichero(ficheros[x]);
                }
                ficheros[x].delete();
            }
        }
        /**
         * This method copies a source directory to a destiny directory
         * @param srcDir The source directory
         * @param dstDir The destiny directory
         * @throws IOException 
         */
        public static void copyDirectory(File srcDir, File dstDir) throws IOException {
                if (srcDir.isDirectory()){
                    if (!dstDir.exists()){
                        dstDir.mkdir();
                }
                String[] children = srcDir.list();
                for (int i=0; i<children.length; i++){
                    copyDirectory(new File(srcDir, children[i]),
                        new File(dstDir, children[i]));
                }
            } else{
                copy(srcDir, dstDir);
            }
        }
        /**
         * This method copies a source file to a destiny file
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
         * @param input The source inputstream
         * @param output The destiny outputstream
         */
        public static void copy(BufferedInputStream input, BufferedOutputStream output) throws IOException{
            byte[] buffer = new byte[input.available()];
            input.read(buffer, 0, buffer.length);
            output.write(buffer);
        }
        /**
         * This method installs minecraft
         * @param eti The label state
         * @param source The source file
         * @param destiny The destination file
         * @throws IOException 
         */
        public static void installation(JLabel eti, File source, File destiny) throws IOException{
            if (source.isDirectory()){
                if (!destiny.exists()){
                    destiny.mkdirs();
                }
                String[] children = source.list();
                for (int i=0; i<children.length; i++){
                    installation(eti, new File(source, children[i]),
                        new File(destiny, children[i]));
                }
            } else{
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
     * This class implements all the connections used.
     */
    public static class Connection{
        private static FTPClient client = new FTPClient();
        private static URL url;
        public static boolean failed = false;
        /**
         * This starts the connection with the FTP server.
         * @throws Exception if any connection error happens.
         */
        private static void startConnection() throws Exception{
            if (debug) System.out.println("[->Started connection<-]");
            client.connect("minechinchas.zxq.net");
            client.login("minechinchas_zxq", new Parameters().getFP());
        }
        private static boolean isConnected() { return client.isConnected(); }
        private static void closeConnection(){
            if (debug) System.out.println("[->Closed connection<-]");
            try{
                client.logout();
                client.disconnect();
            } catch (Exception ex){
                Init.error.setError(ex);
            }
        }
        /**
         * Method to upload a file if the main method fails.
         * @param file The local file.
         * @param pathServer The path to the server.
         * @return {@code true} if the file was upload, {@code false} in otherwise.
         * @throws Exception if something was wrong.
         */
        private static boolean _2upload(File file, String pathServer) throws Exception{
            if (pathServer == null){
                pathServer = "";
            }
            if (!pathServer.startsWith("/") && !pathServer.equals("")){
                pathServer = "/" + pathServer;
            }
            url = new URL("ftp://minechinchas_zxq:" + new Parameters().getFP() + "@minechinchas.zxq.net" +
                    pathServer + ";type=i");
            OutputStream out = url.openConnection().getOutputStream();
            BufferedReader bf = new BufferedReader(new FileReader(file));
            try{
                int i = bf.read();
                while (i != -1){
                    out.write(i);
                    out.flush();
                    i = bf.read();
                }
                return true;
            }finally{
                out.close();
                bf.close();
            }
        }
        /**
         * This method uploads a file to the FTP server.
         * @param file The file to upload.
         * @param pathServer The path to the destiny file.
         * @return {@code true} if the upload was correct, {@code false} if not.
         */
        private static boolean _upload(File file, String pathServer) throws Exception{
            if (!Init.online){
                return false;
            }
            FileInputStream in = null;
            try{
                client.enterLocalPassiveMode();
                client.setFileType(FTP.BINARY_FILE_TYPE);
                in = new FileInputStream(file);
                client.storeFile(pathServer, in);
                return true;
            } finally{
                if (in != null){
                    in.close();
                }
            }
        }
        public static boolean upload(File file, String path){
            boolean res = false;
            if (failed){
                if (debug) System.out.println("[->Uploading with 2nd method<-]");
                try{
                    res = _2upload(file, path);
                } catch (Exception ex){
                    exception(ex, "Fallo al conectar con el servidor.");
                    res = false;
                } finally{
                    return res;
                }
            }
            try{
                if (!client.isConnected() && Init.online){
                    startConnection();
                }
                res = _upload(file, path);
            } catch (Exception ex){
                exception(ex, "Fallo al conectar con el servidor.");
                res = false;
            } finally{
                if (isConnected()){
                    closeConnection();
                }
                return res;
            }
        }
        public static boolean upload(String file, String path){
            return upload(new File(file), path);
        }
        /**
         * Method to download a file if the main method fails.
         * @param file The local file.
         * @param pathServer The path to the server.
         * @return {@code true} if the file was upload, {@code false} in otherwise.
         * @throws Exception if something was wrong.
         */
        private static boolean _2download(File file, String pathServer) throws Exception{
            if (pathServer == null){
                pathServer = "";
            }
            if (!pathServer.startsWith("/") && !pathServer.equals("")){
                pathServer = "/" + pathServer;
            }
            url = new URL("ftp://minechinchas_zxq:" + new Parameters().getFP() + "@minechinchas.zxq.net" +
                    pathServer + ";type=i");
            InputStream in = url.openConnection().getInputStream();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            try{
                int i = in.read();
                while (i != -1){
                    bw.write(i);
                    bw.flush();
                    i = in.read();
                }
                return true;
            } finally{
                in.close();
                bw.close();
            }
        }
        /**
         * This method downloads a file to the FTP server.
         * @param file The local file.
         * @param pathServer The server path to the file.
         * @return {@code true} if the download was correct, {@code false} if not.
         */
        private static boolean _download(File file, String pathServer) throws Exception{
            if (!Init.online){
                return false;
            }
            FileOutputStream out = null;
            try{
                client.enterLocalPassiveMode();
                client.setFileType(FTP.BINARY_FILE_TYPE);
                out = new FileOutputStream(file);
                client.retrieveFile(pathServer, out);
                return true;
            } finally{
                if (out != null){
                    out.close();
                }
            }
        }
        public static boolean download(File file, String path){
            boolean res = false;
            if (failed){
                if (debug) System.out.println("[->Downloading with 2nd method<-]");
                try{
                    res = _2download(file, path);
                } catch (Exception ex){
                    exception(ex, "Fallo al conectar con el servidor.");
                    res = false;
                } finally{
                    return res;
                }
            }
            try{
                if (!client.isConnected() && Init.online){
                    startConnection();
                }
                res = _download(file, path);
            } catch (Exception ex){
                exception(ex, "Fallo al conectar con el servidor.");
                res = false;
            } finally{
                if (isConnected()){
                    closeConnection();
                }
                return res;
            }
        }
        public static boolean download(String file, String path){
            return download(new File(file), path);
        }
        /**
         * This method appends a data to a file.
         * @param data The data written in the local file.
         * @param path The server path file.
         * @throws Exception If an error happens.
         */
        public static void append(String data, String path) throws Exception{
            if (!client.isConnected() && Init.online){
                startConnection();
            }
            DataOutputStream out = new DataOutputStream(client.appendFileStream(path));
            out.writeUTF(data);
            out.close();
        }
        /**
         * Method to append a data to a file if the main method fails.
         * @param file The local file.
         * @param path The server path file.
         * @param data  The data written in the local file.
         */
        public static void append(String file, String path, String data){
            File tmp = new File(path(Directory.DirData() + File.separator + file));
            tmp.deleteOnExit();
            boolean res = download(tmp, path);
            if (res){
                PrintWriter pw = null;
                try{
                    pw = new PrintWriter(new FileWriter(tmp, true));
                    pw.println(data);
                } catch (Exception ex){
                    exception(ex, "Fallo al conectar con el servidor.");
                } finally{
                    if (pw != null){
                        pw.close();
                    }
                }
                upload(tmp, path);
            }
            tmp.delete();
        }
        /**
         * This method adds a log to a file on the server.
         * @param text The username.
         * @throws Exception If an error happens.
         */
        public static void inputLog(String text) throws Exception{
            Calendar C = new GregorianCalendar();
            StringBuilder str = new StringBuilder("Connected at ");
            str.append(C.get(Calendar.DAY_OF_MONTH)).append("/")
                    .append(C.get(Calendar.MONTH) + 1).append("/")
                    .append(C.get(Calendar.YEAR)).append(" ")
                    .append(C.get(Calendar.HOUR_OF_DAY)).append(":")
                    .append(C.get(Calendar.MINUTE)).append(":")
                    .append(C.get(Calendar.SECOND))
                    .append(" with name of ").append(text);
            text = text.toLowerCase();
            if (failed){
                append(text + "NM.dat", "Base/" + text + "NM.dat", str.toString());
                return;
            }
            try{
                append(str.toString(), "Base/" + text + "NM.dat");
            } finally{
                closeConnection();
            }
        }
        private static boolean _checkDuplicated(String accountLower){
            try{
                client.changeWorkingDirectory("Base");
                String[] files = client.listNames();
                int i = 0;
                if (files == null){
                    System.out.println("Error response obtained");
                    return false;
                }
                while (i < files.length){
                    if (files[i].contains(accountLower)){
                        return true;
                    }
                    i++;
                }
                return false;
            } catch (Exception ex){
                Init.error.setError(ex);
                return false;
            }
        }
        /*private static boolean _2checkDuplicated(String accountLower){
            ghdf
        }*/
        /**
         * This method checks if the account is duplicated.
         * @param account The account name.
         * @return {@code true} if the account already exists or {@code false} in otherwise.
         */
        public static boolean checkDuplicated(String account){
            boolean res = false;
            try{
                if (!client.isConnected() && Init.online){
                    startConnection();
                }
                res = _checkDuplicated(account.toLowerCase());
            } catch (Exception ex){
                res = false;
                Init.error.setError(ex);
            } finally{
                closeConnection();
            }
            return res;
        }
    }
    /**
     * This class returns all directories used.
     */
    public static class Directory{
        /**
         * This gets the name of directory and file of installation
         */
        public static String Dirsrc = "inst";
        /**
         * This gets the directory name of data files stored in this PC
         */
        public static String DirData() {
            String win = "Data", lin = ".Data";
            if (OS.equals("windows")){
                return win;
            } else if (OS.equals("linux")){
                return lin;
            }
            return null;
        }
        /**
         * This gets the directory name of minecraft
         */
        public static String DirMC = ".minecraft";
        /**
         * This gets the directory name used to store all jars used by this
         */
        public static String Dirfiles = "Logger";
        /**
         * This gets the directory name used to store files from the server
         */
        public static String DirNM = "Base";
        /**
         * This gets the directory name used to store temporally files from the server
         */
        public static String DirTMP = "TMP";
        /**
         * This gets the directory name used to store all minecraft instances
         */
        public static String DirInstance = "Instances";
    }
    /**
     * This class return all files used.
     */
    public static class Files{
        /**
         * This method gets the file name of the instance config.
         * @param tmp {@code true} if it's required the absolute path to the file, {@code false} if it's
         * required only the name.
         * @return The absolute path or the name.
         */
        public static String Instance(boolean tmp){
            String msg = "properties.prop";
            if (tmp){
                return path(Directory.DirData() + File.separator + Directory.DirInstance + File.separator
                        + msg);
            } else{
                return msg;
            }
        }
        /**
         * This gets the jar name.
         * @param tmp {@code true} if it's required the absolute path to the file, {@code false} if it's
         * required only the name.
         * @return The absolute path or the name.
         */
        public static String jar(boolean tmp){
            String msg = "RUN.jar";
            if (tmp){
                return path(Directory.DirData() + File.separator + Directory.Dirfiles + File.separator + msg);
            } else{
                return msg;
            }
        }
        /**
         * This gets the direct access name jar.
         * @param tmp {@code true} if it's required the absolute path to the file, {@code false} if it's
         * required only the name.
         * @return The absolute path or the name.
         */
        public static String access(boolean tmp){
            String msg = "RunMinecraft.jar";
            if (tmp){
                return path(Directory.DirData() + File.separator + Directory.Dirfiles + File.separator + msg);
            } else{
                return msg;
            }
        }
        /**
         * This gets the temporal jar.
         * @param tmp {@code true} if it's required the absolute path to the file, {@code false} if it's
         * required only the name.
         * @return The absolute path or the name.
         */
        public static String temporal(boolean tmp){
            String msg = "Temporal.jar";
            if (tmp){
                return path(Directory.DirData() + File.separator + Directory.Dirfiles + File.separator + msg);
            } else{
                return msg;
            }
        }
        /**
         * This gets the password
         */
        public static String pss = "MineClient";
        /**
         * This gets the file name which check if the installed minecraft is supported by the Login.
         */
        public static String infernage(){
            String win = "Infernage.hdn", lin = ".Infernage.hdn";
            if (OS.equals("windows")){
                return win;
            } else if (OS.equals("linux")){
                return lin;
            }
            return null;
        }
        /**
         * This gets the configuration file name of the instance.
         */
        public static String infoInst = "Instance.config";
        /**
         * This gets the file name which check if the remember option was used.
         * @param tmp {@code true} if it's required the absolute path to the file, {@code false} if it's
         * required only the name.
         * @return The absolute path or the name.
         */
        public static String rmb(boolean tmp){
            String msg = "RMB.txt";
            if (tmp){
                return path(Directory.DirData() + File.separator + msg);
            } else{
                return msg;
            }
        }
        /**
         * This gets the file name which set the last login.
         * @param tmp {@code true} if it's required the absolute path to the file, {@code false} if it's
         * required only the name.
         * @return The absolute path or the name.
         */
        public static String login(boolean tmp){
            String msg = "lstlg.data";
            if (tmp){
                return path(Directory.DirData() + File.separator + msg);
            } else{
                return msg;
            }
        }
    }
    public static void main(String[] args){
        //SITE FOR TESTING ONLY
    }
}
